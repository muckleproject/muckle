package org.sh.muckle.runtime;

/*
Copyright 2013 The Muckle Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class SessionsRunner {
	
	NioClientSocketChannelFactory channelFactory;
	ScheduledExecutorService scheduler;
	ExecutorService boss;
	ExecutorService workers;
	
	int connectTimeoutMillis;
	
	public SessionsRunner(int connectTimeoutMillis) {
		this.connectTimeoutMillis = connectTimeoutMillis;
		boss = Executors.newCachedThreadPool();
		workers = Executors.newCachedThreadPool();
		channelFactory = new NioClientSocketChannelFactory(boss, workers);
		scheduler = Executors.newScheduledThreadPool(1);
	}
	
	
	public IHttpSessionSequenceStatus[] run(int count, double startupRate, IHttpConnectionInfo connectionInfo, IHttpRunHandlerFactory handlerFactory, 
			IHttpTimerListenerFactory listenerFactory, IHttpSequenceStatusChecker checker) throws Exception {
		
		if(!scheduler.isShutdown()){
			CountDownLatch latch = new CountDownLatch(count);
			ArrayList<HttpSessionSequencer> sessions = new ArrayList<HttpSessionSequencer>();
			
			for(int i=0; i<count; i++){
				HttpServiceImpl httpService  = new HttpServiceImpl(channelFactory, connectionInfo, connectTimeoutMillis);
				listenerFactory.addListeneners(httpService);
				sessions.add(new HttpSessionSequencer(scheduler, latch, handlerFactory.buildHandler(), httpService));
			}
			
			if(startupRate > 0){
				int millisecondsStartupFactor = (int)(1000d/Math.abs(startupRate));
				for(int i=0; i<sessions.size(); i++){
					sessions.get(i).start(i*millisecondsStartupFactor);
				}
				
				while(!latch.await(checker.getTimeout(), TimeUnit.MILLISECONDS)){
					if(!checker.continueWith(sessions)){
						for(int i=0; i<sessions.size(); i++) {
							sessions.get(i).abort();
						}
						break;
					}
				}
				
				IHttpSessionSequenceStatus[] status = new IHttpSessionSequenceStatus[sessions.size()];
				for(int i=0; i<sessions.size(); i++) {
					HttpSessionSequencer seq = sessions.get(i);
					status[i] = seq.hadError() ? new HttpSessionHadError(seq.getError()) : HttpSessionOK.STATIC;
				}
				
				return status;
			}
			else {
				throw new RuntimeException("Startup rate must be greater than zero.");
			}
		}
		else {
			throw new IllegalStateException("Run called after shutdown.");
		}
	}
	
	public void shutdown(){
		scheduler.shutdownNow();
		channelFactory.shutdown();
		boss.shutdownNow();
		workers.shutdownNow();
	}
	
}

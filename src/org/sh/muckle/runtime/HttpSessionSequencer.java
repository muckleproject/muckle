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


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class HttpSessionSequencer implements IHttpServiceCallback {

	ScheduledExecutorService service;
	CountDownLatch latch;
	IHttpRunHandler source;
	IHttpService httpService;
	
	boolean hadError;
	Throwable error;
	
	boolean started;
	AtomicBoolean finished = new AtomicBoolean(false);
	int stepCount;
	
	HttpRequestDescriptor currentRequest;
	
	public HttpSessionSequencer(ScheduledExecutorService service, CountDownLatch latch, IHttpRunHandler source, IHttpService httpService) {
		this.service = service;
		this.latch = latch;
		this.source = source;
		this.httpService = httpService;
	}
	
	public boolean isStarted(){
		return started;
	}

	public boolean isFinished(){
		return finished.get();
	}

	public int getStepCount(){
		return stepCount;
	}

	public boolean hadError() {
		return hadError;
	}

	public Throwable getError() {
		return error;
	}

	public void start(int delay) {
		service.schedule(new Starter(), delay, TimeUnit.MILLISECONDS);
	}
	
	public void abort(){
		if(!finished.get()){
			// yes there is a race condition here but as we are aborting all we want is to shut down processing
			setError(new RuntimeException("Aborted!"));
		}
	}
	
	//----- IHttpServiceCallback methods ---------
	
	public void responseReceived(HttpResponse resp) {
		try {
			stepCount++;
			source.handleResponse(resp);
			scheduleNextRequest();
		}
		catch(Throwable e){
			setError(e);
		}
	}

	public void error(final EHttpCommsError error) {
		try {
			EHttpErrorAction action = source.handleError(error);
			action.accept(new IHttpErrorActionVisitor() {
				public Object visitRetry() {
					service.schedule(new Retry(currentRequest.getRequest()), 10, TimeUnit.MILLISECONDS);
					return null;
				}
				
				public Object visitContinue() {
					scheduleNextRequest();
					return null;
				}
				
				public Object visitAbort() {
					setError(new RuntimeException("Http session aborted on error. " + error.toString()));
					return null;
				}
			});
		}
		catch(Throwable e){
			setError(e);
		}
	}
	
	//--- end IHttpServiceCallback methods -------
	
	void scheduleNextRequest() {
		try {
			 currentRequest = source.nextRequest();
			 if(currentRequest == null){
				 finished();
			 }
			 else {
				 service.schedule(new Request(currentRequest.getRequest()), currentRequest.getDelay(), TimeUnit.MILLISECONDS);
			 }
		} 
		catch (Throwable e) {
			setError(e);
		}
	}
	
	void setError(Throwable e){
		hadError = true;
		error = e;
		finished();
	}
	
	void finished(){
		// ensure we only decrement latch once
		if(finished.compareAndSet(false, true)){
			httpService.finished();
			latch.countDown();
		}
	}
	
	//---------------------------------------------
	
	class Starter implements Runnable {
		public void run() {
			started = true;
			scheduleNextRequest();
		}
	}
	
	class Request implements Runnable {
		HttpRequest req;
		
		Request(HttpRequest req){
			this.req = req;
		}
		
		public void run() {
			if(!finished.get()){
				try {
					httpService.request(req, HttpSessionSequencer.this);
				}
				catch(Throwable t){
					setError(t);
				}
			}
		}
	}
	
	class Retry implements Runnable {
		HttpRequest req;
		
		Retry(HttpRequest req){
			this.req = req;
		}
		
		public void run() {
			if(!finished.get()){
				try {
					httpService.retry(req, HttpSessionSequencer.this);
				}
				catch(Throwable t){
					setError(t);
				}
			}
		}
	}

}

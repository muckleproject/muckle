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


import java.net.InetSocketAddress;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.ssl.SslHandler;

public class HttpServiceImpl extends ClientBootstrap implements ChannelPipelineFactory, IHttpService {

	HttpRequest req;
	IHttpServiceCallback callback;
	Channel currentChannel;
	CookieManager cookieJar;
	
	IHttpConnectionInfo connectionInfo;
	
	ArrayList<IHttpTransactionEventsListener> transactionEventsListeners;
	boolean isRetry = false;

	public HttpServiceImpl(ChannelFactory channelFactory, IHttpConnectionInfo connectionInfo, int connectTimeoutMillis) {
		super(channelFactory);
		transactionEventsListeners = new ArrayList<>();
		cookieJar = new CookieManager();
		
		this.connectionInfo = connectionInfo;
		
	    setOption("keepAlive", false);
	    setOption("tcpNoDelay", true);
	    setOption("reuseAddress", true);
	    setOption("connectTimeoutMillis", connectTimeoutMillis);
	    
	    setPipelineFactory(this);
	}
	
	public ChannelPipeline getPipeline() {
        ChannelPipeline pipeline = Channels.pipeline();
        if (connectionInfo.isSecure()) {
        	try {
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(null, AcceptAnythingTrustManager.MANAGERS, null);
				SSLEngine engine = context.createSSLEngine();
				engine.setUseClientMode(true);
				pipeline.addLast("ssl", new SslHandler(engine));
			} 
        	catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
        }
        pipeline.addLast("codec", new HttpClientCodec());
        pipeline.addLast("inflater", new HttpContentDecompressor());
        pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
        pipeline.addLast("handler", new HttpHandler());
		
        return pipeline;
	}

	
	//-------- IHttpService methods ---------
	
	public void finished() {
		closeChannel();
	}

	public void retry(HttpRequest req, IHttpServiceCallback callback) {
	    request(req, callback, true);
	}

	public void request(HttpRequest req, IHttpServiceCallback callback) {
	    request(req, callback, false);
	}

	public void addTransactionEventsListener(IHttpTransactionEventsListener listener) {
		transactionEventsListeners.add(listener);
	}
	
	//---- end IHttpService methods ---------

	void request(HttpRequest req, IHttpServiceCallback callback, boolean retry) {
		isRetry = retry;
	    this.req = req;
        this.callback = callback;

		setHeaderIfNotSet(HttpHeaders.Names.HOST, connectionInfo.getHostString(), req);
	    setHeaderIfNotSet(HttpHeaders.Names.ACCEPT_ENCODING, "gzip,deflate", req);
	    cookieJar.setRequestCookies(req);
	    
	    setUriIfNecessary(req);

		if(currentChannel != null && currentChannel.isConnected()){
			sendStart(req);
			currentChannel.write(req);
		}
		else {
			connectStart();
			connect(connectionInfo.isProxied()? connectionInfo.getProxyAddress() : connectionInfo.getRemoteAddress());
		}
	}

	void setUriIfNecessary(HttpRequest req){
	    if(connectionInfo.isProxied()){
	    	String uri = req.getUri();
	    	if(!(uri.startsWith("http://") || uri.startsWith("https://")) ){
	    		StringBuilder sb = new StringBuilder();
	    		sb.append("http");
	    		if(connectionInfo.isSecure()){
	    			sb.append("s");
	    		}
	    		sb.append("://");
	    		InetSocketAddress remote = connectionInfo.getRemoteAddress();
	    		sb.append(remote.getHostString());
	    		sb.append(":");
	    		sb.append(remote.getPort());
	    		sb.append(uri);
		    	req.setUri(sb.toString());
	    	}
	    }
	}
	
	void setHeaderIfNotSet(String name, String value, HttpRequest req){
	    if(HttpHeaders.getHeader(req, name) == null){
	    	req.setHeader(name, value);
	    }
	}

	void connectStart(){
		int len = transactionEventsListeners.size();
		if(len > 0){
			for(int i=0; i<len; i++){
				transactionEventsListeners.get(i).connectStart();
			}
		}
	}

	void connected(){
		int len = transactionEventsListeners.size();
		if(len > 0){
			for(int i=0; i<len; i++){
				transactionEventsListeners.get(i).connected();
			}
		}
	}	

	void sendStart(HttpRequest request){
		int len = transactionEventsListeners.size();
		if(len > 0){
			for(int i=0; i<len; i++){
				if(!isRetry){
					transactionEventsListeners.get(i).sendStart(request);
				}
				else {
					transactionEventsListeners.get(i).retry(request);
				}
			}
		}
	}

	void responseReceieved(HttpResponse response){
		int len = transactionEventsListeners.size();
		if(len > 0){
			for(int i=0; i<len; i++){
				transactionEventsListeners.get(i).responseReceived(response);
			}
		}
	}

	void error(EHttpCommsError error){
		int len = transactionEventsListeners.size();
		if(len > 0){
			for(int i=0; i<len; i++){
				transactionEventsListeners.get(i).error(error);
			}
		}
	}
	
	void closeChannel(){
		if(currentChannel != null){
			currentChannel.close();
			currentChannel = null;
		}
	}
	
	//------------------------------------
	
	class HttpHandler extends SimpleChannelUpstreamHandler {
		
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e){
			closeChannel();
			error(EHttpCommsError.Connect);
			callback.error(EHttpCommsError.Connect);
		}
		
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			currentChannel = ctx.getChannel();
			connected();
			sendStart(req);
			currentChannel.write(req);
		}
		
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			HttpResponse resp = (HttpResponse)e.getMessage();
			cookieJar.getCookiesFromResponse(resp, req);
			
			if(!HttpHeaders.isKeepAlive(resp)){
				closeChannel();
			}
			responseReceieved(resp);
			callback.responseReceived(resp);
		}

	}


}

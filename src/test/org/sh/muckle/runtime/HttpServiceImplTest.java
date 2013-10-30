package test.org.sh.muckle.runtime;

/*
*	Copyright 2013 The Muckle Project
*
*	Licensed under the Apache License, Version 2.0 (the "License");
*	you may not use this file except in compliance with the License.
*	You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*	Unless required by applicable law or agreed to in writing, software
*	distributed under the License is distributed on an "AS IS" BASIS,
*	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*	See the License for the specific language governing permissions and
*	limitations under the License.
*/


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.sh.muckle.runtime.EHttpCommsError;
import org.sh.muckle.runtime.HttpServiceImpl;
import org.sh.muckle.runtime.IHttpConnectionInfo;
import org.sh.muckle.runtime.IHttpServiceCallback;
import org.sh.muckle.runtime.IHttpTransactionEventsListener;


public class HttpServiceImplTest extends MockObjectTestCase {

	TestImpl impl;
	ChannelFactory cf;
	SocketAddress remoteAddress;
	IHttpServiceCallback callback;

	HttpRequest req;
	Channel channel;
	ChannelConfig config;
	ChannelFuture future;
	ChannelHandlerContext ctx;
	
	IHttpConnectionInfo info;
	
	public void testSecure(){
		info = mock(IHttpConnectionInfo.class, "insecure");
		checking(new Expectations(){{
			allowing(info).getRemoteAddress(); will(returnValue(remoteAddress));
			allowing(info).getHostString(); will(returnValue(remoteAddress.toString()));
			allowing(info).isSecure(); will(returnValue(true));
			allowing(info).isProxied(); will(returnValue(false));
		}});
		impl = new TestImpl(cf, info);
		configureForRequest();
		impl.request(req, callback);
		assertNotNull(impl.handler);
	}

	public void testProxiedNotSecure(){
		info = mock(IHttpConnectionInfo.class, "insecure");
		checking(new Expectations(){{
			allowing(info).getRemoteAddress(); will(returnValue(remoteAddress));
			allowing(info).getHostString(); will(returnValue(remoteAddress.toString()));
			allowing(info).isSecure(); will(returnValue(false));
			allowing(info).isProxied(); will(returnValue(true));
		}});
		impl = new TestImpl(cf, info);
		configureForRequest();
		checking(new Expectations(){{
			one(req).getUri(); will(returnValue("/"));
			one(req).setUri("http://TEST:999/"); 
			one(info).getProxyAddress(); will(returnValue(remoteAddress));
		}});
		impl.request(req, callback);
		assertNotNull(impl.handler);
	}

	public void testProxiedSecure(){
		info = mock(IHttpConnectionInfo.class, "insecure");
		checking(new Expectations(){{
			allowing(info).getRemoteAddress(); will(returnValue(remoteAddress));
			allowing(info).getHostString(); will(returnValue(remoteAddress.toString()));
			allowing(info).isSecure(); will(returnValue(true));
			allowing(info).isProxied(); will(returnValue(true));
		}});
		impl = new TestImpl(cf, info);
		configureForRequest();
		checking(new Expectations(){{
			one(req).getUri(); will(returnValue("/"));
			one(req).setUri("https://TEST:999/"); 
			one(info).getProxyAddress(); will(returnValue(remoteAddress));
		}});
		impl.request(req, callback);
		assertNotNull(impl.handler);
	}

	public void testProxiedURIStartsWithHttp(){
		info = mock(IHttpConnectionInfo.class, "insecure");
		checking(new Expectations(){{
			allowing(info).getRemoteAddress(); will(returnValue(remoteAddress));
			allowing(info).getHostString(); will(returnValue(remoteAddress.toString()));
			allowing(info).isSecure(); will(returnValue(true));
			allowing(info).isProxied(); will(returnValue(true));
		}});
		impl = new TestImpl(cf, info);
		configureForRequest();
		checking(new Expectations(){{
			one(req).getUri(); will(returnValue("http://fred/"));
			one(info).getProxyAddress(); will(returnValue(remoteAddress));
		}});
		impl.request(req, callback);
		assertNotNull(impl.handler);
	}

	public void testProxiedURIStartsWithHttps(){
		info = mock(IHttpConnectionInfo.class, "insecure");
		checking(new Expectations(){{
			allowing(info).getRemoteAddress(); will(returnValue(remoteAddress));
			allowing(info).getHostString(); will(returnValue(remoteAddress.toString()));
			allowing(info).isSecure(); will(returnValue(true));
			allowing(info).isProxied(); will(returnValue(true));
		}});
		impl = new TestImpl(cf, info);
		configureForRequest();
		checking(new Expectations(){{
			one(req).getUri(); will(returnValue("https://fred/"));
			one(info).getProxyAddress(); will(returnValue(remoteAddress));
		}});
		impl.request(req, callback);
		assertNotNull(impl.handler);
	}

	public void testOptionsSet(){
	    assertFalse((Boolean)impl.getOption("keepAlive"));
	    assertTrue((Boolean)impl.getOption("tcpNoDelay"));
	    assertTrue((Boolean)impl.getOption("reuseAddress"));
	    assertEquals(20000, impl.getOption("connectTimeoutMillis"));
	}
	
	public void testIsPipleineFactory(){
		assertTrue(impl == impl.getPipelineFactory());
	}
	
	public void testGetPipeline(){
		ChannelPipeline cp = impl.getPipeline();
		List<String> names = cp.getNames();
		assertEquals(4, names.size());
	}
	
	public void testRequest(){
		configureForRequest();
		impl.request(req, callback);
		assertNotNull(impl.handler);
	}
	
	public void testFinishedhNotConnected(){
		impl.finished();
	}
	
	public void testFinishedhConnected() throws Exception{
		configureForRequest();
		impl.request(req, callback);
		callConnected();
		
		checking(new Expectations(){{
			one(channel).close();
		}});
		impl.finished();
	}
	
	public void testError() throws Exception{
		configureForRequest();
		impl.request(req, callback);
		checking(new Expectations(){{
			one(callback).error(EHttpCommsError.Connect);
		}});
		impl.handler.exceptionCaught(ctx, new ExceptionEvent() {
			public ChannelFuture getFuture() {
				return null;
			}
			
			public Channel getChannel() {
				return null;
			}
			
			public Throwable getCause() {
				return new RuntimeException();
			}
		});
	}
	
	public void testRequestAlreadyConnected() throws Exception{
		configureForRequest();
		impl.request(req, callback);
		callConnected();
		
		checking(new Expectations(){{
			one(req).getHeader(HttpHeaders.Names.HOST); will(returnValue(null));
			one(req).setHeader(HttpHeaders.Names.HOST, remoteAddress.toString());
			one(req).getHeader(HttpHeaders.Names.ACCEPT_ENCODING); will(returnValue(null));
			one(req).setHeader(HttpHeaders.Names.ACCEPT_ENCODING, "gzip,deflate");
			one(channel).isConnected(); will(returnValue(true));
			one(channel).write(req);
		}});
		impl.request(req, callback);
	}
	
	public void testRetryAlreadyConnected() throws Exception{
		configureForRequest();
		impl.retry(req, callback);
		callConnected();
		
		checking(new Expectations(){{
			one(req).getHeader(HttpHeaders.Names.HOST); will(returnValue(null));
			one(req).setHeader(HttpHeaders.Names.HOST, remoteAddress.toString());
			one(req).getHeader(HttpHeaders.Names.ACCEPT_ENCODING); will(returnValue(null));
			one(req).setHeader(HttpHeaders.Names.ACCEPT_ENCODING, "gzip,deflate");
			one(channel).isConnected(); will(returnValue(true));
			one(channel).write(req);
		}});
		impl.request(req, callback);
	}
	
	public void testRequestConnectionClosed() throws Exception{
		configureForRequest();
		impl.request(req, callback);
		callConnected();
		
		configureForRequest();
		checking(new Expectations(){{
			one(channel).isConnected(); will(returnValue(false));
		}});
		impl.request(req, callback);
	}
	
	public void testMessageRecieved() throws Exception{
		final MessageEvent ev = mock(MessageEvent.class);
		final HttpResponse resp = mock(HttpResponse.class);
		checking(new Expectations(){{
			one(resp).getHeaders(HttpHeaders.Names.SET_COOKIE); will(returnValue(new ArrayList<String>()));
			one(resp).getHeader("Connection"); will(returnValue(null));
			one(resp).getProtocolVersion(); will(returnValue(HttpVersion.HTTP_1_1));
			one(ev).getMessage(); will(returnValue(resp));
			one(callback).responseReceived(resp);
		}});
		
		configureForRequest();
		impl.request(req, callback);
		
		impl.handler.messageReceived(ctx, ev);
	}
	
	public void testMessageRecievedConnectionClosed() throws Exception{
		final MessageEvent ev = mock(MessageEvent.class);
		final HttpResponse resp = mock(HttpResponse.class);
		checking(new Expectations(){{
			one(resp).getHeaders(HttpHeaders.Names.SET_COOKIE); will(returnValue(new ArrayList<String>()));
			one(resp).getHeader("Connection"); will(returnValue("close"));
			one(ev).getMessage(); will(returnValue(resp));
			one(callback).responseReceived(resp);
		}});
		
		configureForRequest();
		impl.request(req, callback);
		
		impl.handler.messageReceived(ctx, ev);
	}
	
	public void testRequestCallsListener() throws Exception{
		final IHttpTransactionEventsListener l = mock(IHttpTransactionEventsListener.class);
		impl.addTransactionEventsListener(l);
		checking(new Expectations(){{
			one(l).connectStart();
			one(l).connected();
			one(l).sendStart(req);
		}});
		
		configureForRequest();
		impl.request(req, callback);
		callConnected();
	}
	
	public void testRetryCallsListener() throws Exception{
		final IHttpTransactionEventsListener l = mock(IHttpTransactionEventsListener.class);
		impl.addTransactionEventsListener(l);
		checking(new Expectations(){{
			one(l).connectStart();
			one(l).connected();
			one(l).retry(req);
		}});
		
		configureForRequest();
		impl.retry(req, callback);
		callConnected();
	}
	
	public void testResponseCallsListener() throws Exception{
		final MessageEvent ev = mock(MessageEvent.class);
		final HttpResponse resp = mock(HttpResponse.class);
		checking(new Expectations(){{
			one(ev).getMessage(); will(returnValue(resp));
			one(resp).getHeaders(HttpHeaders.Names.SET_COOKIE); will(returnValue(new ArrayList<String>()));
			one(resp).getHeader("Connection"); will(returnValue(null));
			one(resp).getProtocolVersion(); will(returnValue(HttpVersion.HTTP_1_1));
			one(callback).responseReceived(resp);
		}});
		
		final IHttpTransactionEventsListener l = mock(IHttpTransactionEventsListener.class);
		impl.addTransactionEventsListener(l);
		checking(new Expectations(){{
			one(l).connectStart();
			one(l).responseReceived(resp);
		}});
		
		configureForRequest();
		impl.request(req, callback);
		
		impl.handler.messageReceived(ctx, ev);
	}
	
	public void testRequestAlreadyConnectedCallsListener() throws Exception{
		final IHttpTransactionEventsListener l = mock(IHttpTransactionEventsListener.class);
		impl.addTransactionEventsListener(l);
		checking(new Expectations(){{
			one(l).connectStart();
			one(l).connected();
			one(l).sendStart(req);
			one(l).sendStart(req);
		}});
		
		configureForRequest();
		impl.request(req, callback);
		callConnected();
		
		checking(new Expectations(){{
			one(req).getHeader(HttpHeaders.Names.HOST); will(returnValue(null));
			one(req).setHeader(HttpHeaders.Names.HOST, remoteAddress.toString());
			one(req).getHeader(HttpHeaders.Names.ACCEPT_ENCODING); will(returnValue("gzip"));
			one(channel).isConnected(); will(returnValue(true));
			one(channel).write(req);
		}});
		impl.request(req, callback);
	}
	
	
	public void testErrorCallsListener() throws Exception{
		final IHttpTransactionEventsListener l = mock(IHttpTransactionEventsListener.class);
		impl.addTransactionEventsListener(l);
		
		checking(new Expectations(){{
			one(l).connectStart();
		}});

		configureForRequest();
		impl.request(req, callback);
		
		checking(new Expectations(){{
			one(callback).error(EHttpCommsError.Connect);
			one(l).error(with(EHttpCommsError.Connect));
		}});
		impl.handler.exceptionCaught(ctx, new ExceptionEvent() {
			public ChannelFuture getFuture() {
				return null;
			}
			
			public Channel getChannel() {
				return null;
			}
			
			public Throwable getCause() {
				return new RuntimeException();
			}
		});
	}
	
	//-------------------------------------------------
	
	protected void setUp() throws Exception {
		super.setUp();
		callback = mock(IHttpServiceCallback.class);
		cf = mock(ChannelFactory.class);
		req = mock(HttpRequest.class);
		channel = mock(Channel.class);
		config = mock(ChannelConfig.class);
		future = mock(ChannelFuture.class);
		ctx = mock(ChannelHandlerContext.class);
		remoteAddress = new InetSocketAddress("TEST", 999);
		info = mock(IHttpConnectionInfo.class);
		checking(new Expectations(){{
			allowing(info).getRemoteAddress(); will(returnValue(remoteAddress));
			allowing(info).getHostString(); will(returnValue(remoteAddress.toString()));
			allowing(info).isSecure(); will(returnValue(false));
			allowing(info).isProxied(); will(returnValue(false));
		}});
		impl = new TestImpl(cf, info);
	}
	
	@SuppressWarnings("unchecked")
	void configureForRequest(){
		checking(new Expectations(){{
			one(req).getHeader(HttpHeaders.Names.HOST); will(returnValue(null));
			one(req).setHeader(HttpHeaders.Names.HOST, remoteAddress.toString());
			one(req).getHeader(HttpHeaders.Names.ACCEPT_ENCODING); will(returnValue("gzip"));
			one(cf).newChannel(with(any(ChannelPipeline.class))); will(returnValue(channel));
			one(channel).getConfig(); will(returnValue(config));
			one(config).setOptions(with(any(Map.class)));
			one(channel).connect(remoteAddress); will(returnValue(future));
		}});
	}
	
	void callConnected() throws Exception{
		checking(new Expectations(){{
			one(ctx).getChannel(); will(returnValue(channel));
			one(channel).write(req); will(returnValue(future));
		}});
		impl.handler.channelConnected(ctx, null);
	}
	
	//--------------------------------------------------
	
	
	class TestImpl extends HttpServiceImpl {

		SimpleChannelUpstreamHandler handler;
		
		public TestImpl(ChannelFactory channelFactory,	IHttpConnectionInfo info) {
			super(channelFactory, info, 20000);
		}
		
		public ChannelPipeline getPipeline() {
			ChannelPipeline pipeline = super.getPipeline();
			handler = (SimpleChannelUpstreamHandler)pipeline.getLast();
			return pipeline;
		}

	}

}

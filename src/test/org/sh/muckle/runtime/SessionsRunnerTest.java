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


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.sh.muckle.runtime.EHttpCommsError;
import org.sh.muckle.runtime.EHttpErrorAction;
import org.sh.muckle.runtime.HttpRequestDescriptor;
import org.sh.muckle.runtime.HttpSessionHadError;
import org.sh.muckle.runtime.HttpSessionOK;
import org.sh.muckle.runtime.HttpSessionSequencer;
import org.sh.muckle.runtime.IHttpConnectionInfo;
import org.sh.muckle.runtime.IHttpRunHandler;
import org.sh.muckle.runtime.IHttpRunHandlerFactory;
import org.sh.muckle.runtime.IHttpSequenceStatusChecker;
import org.sh.muckle.runtime.IHttpService;
import org.sh.muckle.runtime.IHttpSessionSequenceStatus;
import org.sh.muckle.runtime.IHttpTimerListenerFactory;
import org.sh.muckle.runtime.SessionsRunner;



public class SessionsRunnerTest extends JettyTestCase {

	SessionsRunner runner;
	
	IHttpConnectionInfo info;
	
	public void testRunOne() throws Exception {
		IHttpSessionSequenceStatus[] results = runner.run(1, 1, info, new HandlerFactory(), new NoListenerFactory(), new WaitForever());
		assertEquals(1, results.length);
		assertTrue(results[0] instanceof HttpSessionOK);
	}
	
	public void testRunMinusOne() throws Exception {
		try {
			runner.run(-1, 1, info, new HandlerFactory(), new NoListenerFactory(), new WaitForever());
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testStartupRateZero() throws Exception {
		try {
			runner.run(1, 0, info, new HandlerFactory(), new NoListenerFactory(), new WaitForever());
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testStartupRateNegative() throws Exception {
		try {
			runner.run(1, -2, info, new HandlerFactory(), new NoListenerFactory(), new WaitForever());
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testListenerFactoryCalled() throws Exception {
		NoListenerFactory f = new NoListenerFactory();
		runner.run(1, 1, info, new HandlerFactory(), f,  new WaitForever());
		assertEquals(1, f.count);
	}
	
	public void testRunOneFails() throws Exception {
		jetty.stop();
		IHttpSessionSequenceStatus[] results = runner.run(1, 1, info, new HandlerFactory(), new NoListenerFactory(), new WaitForever());
		assertEquals(1, results.length);
		assertTrue(results[0] instanceof HttpSessionHadError);
	}
	
	public void testStartupRate() throws Exception {
		long start = System.currentTimeMillis();
		IHttpSessionSequenceStatus[] results = runner.run(100, 50, info, new HandlerFactory(), new NoListenerFactory(),  new WaitForever());
		long elapsed = System.currentTimeMillis()-start;
		assertTrue(elapsed > 1500);
		assertTrue(elapsed < 2500);
		assertEquals(100, results.length);
		for(int i=0; i<results.length; i++){
			assertTrue(results[0] instanceof HttpSessionOK);
		}
	}
	
	public void testTimeout() throws Exception {
		IHttpSequenceStatusChecker chk = new IHttpSequenceStatusChecker() {
			boolean second;
			public long getTimeout() {				
				return 10;
			}

			public boolean continueWith(List<HttpSessionSequencer> sessions) {
				if(second){
					return false;
				}
				else {
					second = true;
					return true;
				}
			}
		};

		// startup 5 at 1 per second
		IHttpSessionSequenceStatus[] results = runner.run(5, 1, info, new HandlerFactory(), new NoListenerFactory(), chk);
		assertEquals(5, results.length);
	}
	
	public void testRunAfterShutdown() throws Exception{
		runner.shutdown();
		try {
			runner.run(100, 50, info, new HandlerFactory(), new NoListenerFactory(),  new WaitForever());
			fail();
		}
		catch(IllegalStateException e){}
	}
	
	//------------------------------------------
	
	protected void setUp() throws Exception {
		super.setUp();
		info = new Info(new InetSocketAddress("localhost", jettyPort));
		runner = new SessionsRunner(5000);
	}
	
	//-----------------------------------------------

	class Info implements  IHttpConnectionInfo {
		InetSocketAddress address;
		
		public Info(InetSocketAddress inetSocketAddress) {
			this.address = inetSocketAddress;
		}

		public boolean isSecure() {
			return false;
		}
		
		public InetSocketAddress getRemoteAddress() {
			return address;
		}
		
		public String getHostString() {
			return address.toString();
		}

		public boolean isProxied() {
			return false;
		}

		public InetSocketAddress getProxyAddress() {
			return null;
		}
	};
	
	class TestHttpHandler implements IHttpRunHandler {
		boolean first = true;
		HttpRequestDescriptor des = new HttpRequestDescriptor(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"));
		public HttpRequestDescriptor nextRequest() throws IOException {
			if(first){
				first = false;
				return des;
			}
			else {
				return null;
			}
			
		}

		public void handleResponse(HttpResponse resp) {
		}

		public EHttpErrorAction handleError(EHttpCommsError error) {
			return EHttpErrorAction.Abort;
		}
	}
	
	class HandlerFactory implements IHttpRunHandlerFactory {
		public IHttpRunHandler buildHandler() throws IOException {
			return new TestHttpHandler();
		}
	}
	
	class NoListenerFactory implements IHttpTimerListenerFactory {
		int count = 0;
		public void addListeneners(IHttpService service) {
			count++;
		}
	}
	
	class WaitForever implements IHttpSequenceStatusChecker {
		public long getTimeout() {
			return Integer.MAX_VALUE;
		}

		public boolean continueWith(List<HttpSessionSequencer> sessions) {
			return false;
		}
	}

	//-----------------------------------------------
	
	Handler[] getHandlers() {
		return new Handler[] { new TestHandler() };
	}
	
	
	class TestHandler extends AbstractHandler {
		AtomicInteger count = new AtomicInteger();
		String content;
		
		TestHandler(){
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<1000; i++){
				sb.append("0123456789");
			}
			content = sb.toString();
		}
		
		public void handle(String target, Request baseRequest, HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException, ServletException {
			httpResp.setContentType("text/html");
			httpResp.setStatus(HttpServletResponse.SC_OK);
			httpResp.setContentLength(content.length());
			httpResp.getWriter().write(content);
			baseRequest.setHandled(true);
		}
		
	}
	}

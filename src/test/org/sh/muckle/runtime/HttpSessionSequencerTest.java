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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.sh.muckle.runtime.EHttpCommsError;
import org.sh.muckle.runtime.EHttpErrorAction;
import org.sh.muckle.runtime.HttpRequestDescriptor;
import org.sh.muckle.runtime.HttpSessionSequencer;
import org.sh.muckle.runtime.IHttpRunHandler;
import org.sh.muckle.runtime.IHttpService;
import org.sh.muckle.runtime.IHttpServiceCallback;
import org.sh.muckle.runtime.IHttpTransactionEventsListener;


public class HttpSessionSequencerTest extends MockObjectTestCase {

	HttpSessionSequencer seq;
	CountDownLatch latch;
	IHttpRunHandler source;
	ScheduledExecutorService service;
	MockService httpService;
	
	public void testStartNoDelayNoRequest() throws Exception {
		checking(new Expectations(){{
			one(source).nextRequest(); will(returnValue(null));
		}});
		seq.start(0);
		latch.await(50, TimeUnit.MILLISECONDS);
		assertTrue(seq.isStarted());
		assertTrue(seq.isFinished());
		assertEquals(0, seq.getStepCount());
		assertEquals(0, latch.getCount());
		assertFalse(seq.hadError());
		assertEquals(1, httpService.finsihedCount);
	}
	
	public void testAbort() throws Exception {
		seq.abort();
		latch.await(50, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertTrue(seq.hadError());
		Throwable t = seq.getError();
		assertEquals(1, httpService.finsihedCount);
		
		seq.abort();
		assertTrue(seq.hadError());
		assertTrue(t == seq.getError());
	}
	
	public void testStartAfterAbort() throws Exception {
		
		final HttpRequest req = mock(HttpRequest.class);
		final HttpRequestDescriptor desc = new HttpRequestDescriptor(req);
		desc.setDelay(0);
		
		
		checking(new Expectations(){{
			one(source).nextRequest(); will(returnValue(desc));
		}});
		
		seq.abort();
		seq.start(0);
		//sleep to let request be aborted
		Thread.sleep(20);
		
		assertEquals(0, latch.getCount());
		assertTrue(seq.hadError());
		assertEquals(1, httpService.finsihedCount);
	}
	
	public void testStartRequestThrowsError() throws Exception {
		final IOException e = new IOException();
		checking(new Expectations(){{
			one(source).nextRequest(); will(throwException(e));
		}});
		seq.start(0);
		latch.await(50, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertTrue(seq.hadError());
		assertTrue(e == seq.getError());
		assertEquals(1, httpService.finsihedCount);
	}
	
	public void testStartWithDelayNoRequest() throws Exception {
		checking(new Expectations(){{
			one(source).nextRequest(); will(returnValue(null));
		}});
		long start = System.currentTimeMillis();
		seq.start(100);
		latch.await(250, TimeUnit.MILLISECONDS);
		assertTrue(System.currentTimeMillis() - start > 80);
		assertEquals(0, latch.getCount());
		assertFalse(seq.hadError());
		assertEquals(1, httpService.finsihedCount);
	}
	
	public void testStartWithRequest() throws Exception {
		final HttpRequest req = mock(HttpRequest.class);
		final HttpRequestDescriptor desc = new HttpRequestDescriptor(req);
		desc.setDelay(0);
		
		final HttpResponse resp = mock(HttpResponse.class);
		httpService.responder = new IServiceResponder() {
			public void doCallback(IHttpServiceCallback callback) {
				callback.responseReceived(resp);
			}
		};
		
		checking(new Expectations(){{
			one(source).nextRequest(); will(returnValue(desc));
			one(source).handleResponse(resp);
			one(source).nextRequest(); will(returnValue(null));
		}});
		
		seq.start(0);
		latch.await(50, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertEquals(1, httpService.requestCount);
		assertFalse(seq.hadError());
		assertEquals(1, httpService.finsihedCount);
		assertEquals(1, seq.getStepCount());
	}
	
	public void testStartWithRequestThrowsError() throws Exception {
		httpService = new ErrorService();
		seq = new HttpSessionSequencer(service, latch, source, httpService);

		final HttpRequest req = mock(HttpRequest.class);
		final HttpRequestDescriptor desc = new HttpRequestDescriptor(req);
		desc.setDelay(0);
		
		checking(new Expectations(){{
			one(source).nextRequest(); will(returnValue(desc));
		}});
		
		seq.start(0);
		latch.await(50, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertTrue(seq.hadError());
		assertEquals(1, httpService.finsihedCount);
	}
	
	
	public void testStartWithRequestResponseHandlerThrowsException() throws Exception {
		final HttpRequest req = mock(HttpRequest.class);
		final HttpRequestDescriptor desc = new HttpRequestDescriptor(req);
		desc.setDelay(0);
		
		final HttpResponse resp = mock(HttpResponse.class);
		httpService.responder = new IServiceResponder() {
			public void doCallback(IHttpServiceCallback callback) {
				callback.responseReceived(resp);
			}
		};
		
		checking(new Expectations(){{
			one(source).nextRequest(); will(returnValue(desc));
			one(source).handleResponse(resp); will(throwException(new RuntimeException()));
		}});
		
		seq.start(0);
		latch.await(50, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertEquals(1, httpService.requestCount);
		assertTrue(seq.hadError());
		assertEquals(1, httpService.finsihedCount);
	}
	
	public void testStartWithRequestHasErrorAbortRequested() throws Exception {
		final HttpRequest req = mock(HttpRequest.class);
		final HttpRequestDescriptor desc = new HttpRequestDescriptor(req);
		desc.setDelay(0);
		
		final EHttpCommsError error = EHttpCommsError.Connect;
		httpService.responder = new IServiceResponder() {
			public void doCallback(IHttpServiceCallback callback) {
				callback.error(error);
			}
		};
		
		checking(new Expectations(){{
			one(source).nextRequest(); will(returnValue(desc));
			one(source).handleError(error); will(returnValue(EHttpErrorAction.Abort));
		}});
		
		seq.start(0);
		latch.await(50, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertEquals(1, httpService.requestCount);
		assertTrue(seq.hadError());
		assertEquals(1, httpService.finsihedCount);
	}
	
	public void testStartWithRequestHasErrorContinueRequested() throws Exception {
		final HttpRequest req = mock(HttpRequest.class);
		final HttpRequestDescriptor desc = new HttpRequestDescriptor(req);
		desc.setDelay(0);
		
		final EHttpCommsError error = EHttpCommsError.Connect;
		httpService.responder = new IServiceResponder() {
			public void doCallback(IHttpServiceCallback callback) {
				callback.error(error);
			}
		};
		
		checking(new Expectations(){{
			one(source).nextRequest(); will(returnValue(desc));
			one(source).handleError(error); will(returnValue(EHttpErrorAction.Continue));
			one(source).nextRequest(); will(returnValue(null));
		}});
		
		seq.start(0);
		latch.await(50, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertEquals(1, httpService.requestCount);
		assertFalse(seq.hadError());
		assertEquals(1, httpService.finsihedCount);
	}
	
	public void testStartWithRequestHasErrorRetryRequested() throws Exception {
		final HttpRequest req = mock(HttpRequest.class);
		final HttpRequestDescriptor desc = new HttpRequestDescriptor(req);
		desc.setDelay(0);
		
		final EHttpCommsError error = EHttpCommsError.Connect;
		httpService.responder = new IServiceResponder() {
			boolean first = true;
			public void doCallback(IHttpServiceCallback callback) {
				if(first){
					callback.error(error);
					first = false;
				}
				else {
					callback.responseReceived(null);
				}
			}
		};
		
		checking(new Expectations(){{
			one(source).nextRequest(); will(returnValue(desc));
			one(source).handleError(error); will(returnValue(EHttpErrorAction.Retry));
			one(source).handleResponse(null);
			one(source).nextRequest(); will(returnValue(null));
		}});
		
		seq.start(0);
		latch.await(50, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertEquals(1, httpService.requestCount);
		assertEquals(1, httpService.retryCount);
		assertFalse(seq.hadError());
		assertEquals(1, httpService.finsihedCount);
	}
	
	public void testStartWithRequestHasErrorRuntimeErrorThrown() throws Exception {
		final HttpRequest req = mock(HttpRequest.class);
		final HttpRequestDescriptor desc = new HttpRequestDescriptor(req);
		desc.setDelay(0);
		
		final EHttpCommsError error = EHttpCommsError.Connect;
		httpService.responder = new IServiceResponder() {
			public void doCallback(IHttpServiceCallback callback) {
				callback.error(error);
			}
		};
		
		checking(new Expectations(){{
			one(source).nextRequest(); will(returnValue(desc));
			one(source).handleError(error); will(throwException(new RuntimeException()));
		}});
		
		seq.start(0);
		latch.await(50, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertEquals(1, httpService.requestCount);
		assertTrue(seq.hadError());
		assertEquals(1, httpService.finsihedCount);
	}
	
	//------------------------------------------------
	
	protected void setUp() throws Exception {
		super.setUp();
		httpService = new MockService();
		service = Executors.newSingleThreadScheduledExecutor();
		latch = new CountDownLatch(1);
		source = mock(IHttpRunHandler.class);
		seq = new HttpSessionSequencer(service, latch, source, httpService);
	}
	
	protected void tearDown() throws Exception {
		service.shutdown();
		super.tearDown();
	}
	
	interface IServiceResponder {
		public void doCallback(IHttpServiceCallback callback);
	}
	
	class MockService implements IHttpService {
		int requestCount = 0;
		int retryCount = 0;
		int finsihedCount = 0;
		IServiceResponder responder;
		
		public void request(HttpRequest req, IHttpServiceCallback callback) {
			requestCount++;
			responder.doCallback(callback);
		}
		
		public void finished(){
			finsihedCount++;
		}

		public void addTransactionEventsListener(IHttpTransactionEventsListener listener) {
		}

		public void retry(HttpRequest req, IHttpServiceCallback callback) {
			retryCount++;
			responder.doCallback(callback);
		}
	}
	
	class ErrorService extends MockService {
		public void request(HttpRequest req, IHttpServiceCallback callback) {
			throw new RuntimeException();
		}
	}
	
	

}

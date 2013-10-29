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


import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.sh.muckle.runtime.HttpTransactionEventTimingAdapter;
import org.sh.muckle.runtime.IHttpTimingListener;


public class HttpTransactionEventTimingAdapterTest extends MockObjectTestCase {

	HttpTransactionEventTimingAdapter adapter;
	IHttpTimingListener tl;
	
	public void testConnectStart(){
		checking(new Expectations(){{
			one(tl).connectStart(with(any(Long.class)));
		}});
		adapter.connectStart();
	}
	
	public void testConnected(){
		checking(new Expectations(){{
			one(tl).connected(with(any(Long.class)));
		}});
		adapter.connected();
	}
	
	public void testSendStart(){
		checking(new Expectations(){{
			one(tl).sendStart(with(any(Long.class)));
		}});
		adapter.sendStart(null);
	}
	
	public void testRetry(){
		checking(new Expectations(){{
			one(tl).sendStart(with(any(Long.class)));
		}});
		adapter.retry(null);
	}
	
	public void testResponseReceived(){
		final HttpResponse resp = mock(HttpResponse.class);
		checking(new Expectations(){{
			one(resp).getStatus(); will(returnValue(HttpResponseStatus.OK));
			one(resp).getHeader("Content-Length"); will(returnValue(null));
			one(tl).responseReceived(with(any(Long.class)), with(any(Long.class)));
		}});
		adapter.responseReceived(resp);
	}
	
	public void testNoListeners(){
		adapter = new HttpTransactionEventTimingAdapter();
		adapter.connectStart();
		adapter.connected();
		adapter.sendStart(null);
		adapter.retry(null);
		adapter.responseReceived(null);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		tl = mock(IHttpTimingListener.class);
		adapter = new HttpTransactionEventTimingAdapter(tl);
	}

}

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
package test.org.sh.muckle.runtime.js;

import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.runtime.js.ITraceFunctionStorage;
import org.sh.muckle.runtime.js.TraceObject;

public class TraceObjectTest extends MockObjectTestCase {

	TraceObject to;
	ITraceFunctionStorage storage;
	
	public void testGetName(){
		assertEquals("Trace", to.getClassName());
	}
	
	public void testGetSetOnConnectStart(){
		final Callable f = mock(Callable.class);
		checking(new Expectations(){{
			one(storage).setConnectStart(f);
			one(storage).getConnectStart(); will(returnValue(f));
		}});
		to.put("onConnectStart", null, f);
		assertTrue(f == to.get("onConnectStart", null));
	}
	
	public void testGetSetOnConnected(){
		final Callable f = mock(Callable.class);
		checking(new Expectations(){{
			one(storage).setConnected(f);
			one(storage).getConnected(); will(returnValue(f));
		}});
		to.put("onConnected", null, f);
		assertTrue(f == to.get("onConnected", null));
	}
	
	public void testGetSetOnSend(){
		final Callable f = mock(Callable.class);
		checking(new Expectations(){{
			one(storage).setSend(f);
			one(storage).getSend(); will(returnValue(f));
		}});
		to.put("onSend", null, f);
		assertTrue(f == to.get("onSend", null));
	}
	
	public void testGetSetOnRetry(){
		final Callable f = mock(Callable.class);
		checking(new Expectations(){{
			one(storage).setRetry(f);
			one(storage).getRetry(); will(returnValue(f));
		}});
		to.put("onRetry", null, f);
		assertTrue(f == to.get("onRetry", null));
	}
	
	public void testGetSetOnReceive(){
		final Callable f = mock(Callable.class);
		checking(new Expectations(){{
			one(storage).setReceive(f);
			one(storage).getReceive(); will(returnValue(f));
		}});
		to.put("onReceive", null, f);
		assertTrue(f == to.get("onReceive", null));
	}
	
	public void testGetSetOnError(){
		final Callable f = mock(Callable.class);
		checking(new Expectations(){{
			one(storage).setError(f);
			one(storage).getError(); will(returnValue(f));
		}});
		to.put("onError", null, f);
		assertTrue(f == to.get("onError", null));
	}
	
	public void testGetResult(){
		final Callable f = mock(Callable.class);
		checking(new Expectations(){{
			one(storage).setResult(f);
			one(storage).getResult(); will(returnValue(f));
		}});
		to.put("getResult", null, f);
		assertTrue(f == to.get("getResult", null));
	}
	
	public void testGetSetNotFount(){
		to.put("NOT_PROPERTY", null, null);
		assertEquals(Scriptable.NOT_FOUND, to.get("NOT_PROPERTY", null));
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		storage = mock(ITraceFunctionStorage.class);
		to = new TraceObject(storage);
	}

}

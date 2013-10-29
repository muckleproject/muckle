package test.org.sh.muckle.runtime.js;

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


import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.runtime.HttpSessionOK;
import org.sh.muckle.runtime.IHttpService;
import org.sh.muckle.runtime.IHttpServiceCallback;
import org.sh.muckle.runtime.IHttpSessionSequenceStatus;
import org.sh.muckle.runtime.IHttpTransactionEventsListener;
import org.sh.muckle.runtime.js.DataEventBuilder;
import org.sh.muckle.runtime.js.IJSHttpTimerListenerFactory;
import org.sh.muckle.runtime.js.TimeResolver;


public class DataEventBuilderTest extends ScriptTestCase {

	DataEventBuilder builder;
	
	public void testGetClassName(){
		assertEquals("DataEventBuilder", builder.getClassName());
	}
	
	public void testGetNonExistantValue(){
		assertNull(runScript("d.NOT_THERE;"));
	}

	public void testGetValuesNotRun(){
		assertNull(runScript("d.values"));
	}

	public void testTimeResolver(){
		assertTrue(runScript("d.timeResolver") instanceof TimeResolver);
	}
	
	public void testRun(){
		addToScope(new RunEmulator(), "run");
		Scriptable v = (Scriptable) runScript("run(d); d.values");
		assertEquals(1, v.getIds().length);
	}
	
	//--------------------------------------------------
	
	protected void setUp() throws Exception {
		super.setUp();
		builder = new DataEventBuilder();
		addToScope(builder, "d");
	}

	class TestService implements IHttpService {
		IHttpTransactionEventsListener l;
		
		public void finished() {
		}

		public void request(HttpRequest req, IHttpServiceCallback callback) {
		}

		public void addTransactionEventsListener(IHttpTransactionEventsListener listener) {
			l = listener;
		}

		public void retry(HttpRequest req, IHttpServiceCallback callback) {
		}
	}

	class RunEmulator implements Callable {
		
		Mockery mocker = new Mockery();

		public Object call(Context ctx, Scriptable scope, Scriptable thisObj,	Object[] args) {
			IJSHttpTimerListenerFactory f = (IJSHttpTimerListenerFactory)args[0];
			TestService service = new TestService();
			f.addListeneners(service);
			
			final HttpRequest req = mocker.mock(HttpRequest.class);
			final HttpResponse resp = mocker.mock(HttpResponse.class);
			mocker.checking(new Expectations(){{
				one(resp).getHeader("Content-Length"); will(returnValue("0"));
			}});
			service.l.sendStart(req);
			service.l.responseReceived(resp);
			f.completed(ctx, scope, new IHttpSessionSequenceStatus[] {HttpSessionOK.STATIC});
			return null;
		}
		
	}

}

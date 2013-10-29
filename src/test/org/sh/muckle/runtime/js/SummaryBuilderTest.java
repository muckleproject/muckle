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
import org.sh.muckle.runtime.HttpTransactionEventTimingAdapter;
import org.sh.muckle.runtime.IHttpService;
import org.sh.muckle.runtime.IHttpServiceCallback;
import org.sh.muckle.runtime.IHttpSessionSequenceStatus;
import org.sh.muckle.runtime.IHttpTransactionEventsListener;
import org.sh.muckle.runtime.js.IJSHttpTimerListenerFactory;
import org.sh.muckle.runtime.js.SummaryBuilder;

public class SummaryBuilderTest extends ScriptTestCase {

	final static String SUMMARY = "summary";
	
	SummaryBuilder sb;
	Scriptable constructed;
	
	public void testGetClassName(){
		assertEquals("SummaryBuilder", sb.getClassName());
	}
	
	public void testImplementsTimerListenerFactory(){
		assertTrue(sb instanceof IJSHttpTimerListenerFactory);
		TestService service = new TestService();
		((IJSHttpTimerListenerFactory)sb).addListeneners(service);
		assertTrue(service.l instanceof HttpTransactionEventTimingAdapter);
	}
	
	public void testGetSummaryNoResults(){
		assertNull(runScript("s.summary;"));
	}
	
	public void testGetNonExistantValue(){
		assertNull(runScript("s.NOT_THERE;"));
	}
	
	public void testGetSummaryResults(){
		addToScope(new RunEmulator(), "run");
		Scriptable s = (Scriptable)runScript(" run(s); s.summary;");
		assertEquals(4, s.getIds().length);
		assertEquals(1, ((Integer)s.get("sessionCount", s)).intValue());
		
		Scriptable d = (Scriptable)s.get("duration", s);
		
		double min = ((Double)d.get("min", d)).doubleValue();
		//assertEquals(99.0, ((Double)d.get("min", d)).doubleValue());
		assertEquals(min, ((Double)d.get("max", d)).doubleValue());
		assertEquals(min, ((Double)d.get("average", d)).doubleValue());
		
		Scriptable st = (Scriptable)s.get("step", s);
		assertTrue(((Double)st.get("min", st)).doubleValue()> 15);
		assertTrue(((Double)st.get("max", st)).doubleValue() > 15);
		assertTrue(((Double)st.get("average", st)).doubleValue() > 15);
		
		Scriptable c = (Scriptable)s.get("connect", s);
		assertTrue(((Double)c.get("min", c)).doubleValue()> 15);
		assertTrue(((Double)c.get("max", c)).doubleValue() > 15);
		assertTrue(((Double)c.get("average", c)).doubleValue() > 15);
	}
	
	public void testGetSummaryResultsAfterSecondRun(){
		addToScope(new RunEmulator(), "run");
		Scriptable s = (Scriptable)runScript(" run(s); run(s); s.summary;");
		assertEquals(4, s.getIds().length);
		assertEquals(1, ((Integer)s.get("sessionCount", s)).intValue());
		
		Scriptable d = (Scriptable)s.get("duration", s);
		assertTrue(((Double)d.get("min", d)).doubleValue()> 50);
		assertTrue(((Double)d.get("max", d)).doubleValue() > 50);
		assertTrue(((Double)d.get("average", d)).doubleValue() > 50);
		
		Scriptable st = (Scriptable)s.get("step", s);
		assertTrue(((Double)st.get("min", st)).doubleValue()> 15);
		assertTrue(((Double)st.get("max", st)).doubleValue() > 15);
		assertTrue(((Double)st.get("average", st)).doubleValue() > 15);
	}
	
	//---------------------------------------------------
	
	protected void setUp() throws Exception {
		super.setUp();
		sb = new SummaryBuilder();
		addToScope(sb, "s");
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
		Mockery mocker;
		HttpRequest req;
		HttpResponse resp;
		
		RunEmulator(){
			mocker = new Mockery();
			req = mocker.mock(HttpRequest.class);
			resp = mocker.mock(HttpResponse.class);
		}
		
		public Object call(Context ctx, Scriptable scope, Scriptable thisObject, Object[] args) {
			IJSHttpTimerListenerFactory f = (IJSHttpTimerListenerFactory)args[0];
			TestService service = new TestService();
			f.addListeneners(service);
			service.l.connectStart();
			sleep(20);
			service.l.connected();
			
			mocker.checking(new Expectations(){{
				allowing(resp).getHeader("Content-Length"); will(returnValue("0"));
			}});
			service.l.sendStart(req);
			sleep(20);
			service.l.responseReceived(resp);
			service.l.sendStart(req);
			sleep(20);
			service.l.responseReceived(resp);
			f.completed(ctx, scope, new IHttpSessionSequenceStatus[] {HttpSessionOK.STATIC});
			return null;
		}
		
		void sleep(long millis){
			try {Thread.sleep(millis);} catch (InterruptedException e) {}
		}
		
	}

}

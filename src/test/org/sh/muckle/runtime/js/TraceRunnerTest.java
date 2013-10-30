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

import java.io.File;
import java.io.FileWriter;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.ILogger;
import org.sh.muckle.runtime.IHttpTransactionEventsListener;
import org.sh.muckle.runtime.js.RuntimeLogger;
import org.sh.muckle.runtime.js.ScriptCache;
import org.sh.muckle.runtime.js.TraceRunner;

import test.org.sh.TempFileHelper;


public class TraceRunnerTest extends MockObjectTestCase {

	TempFileHelper helper;
	File testRoot;
	ScriptCache cache;
	ILogger logger;
	
	CalledFunction flag;
	
	public void testNothingDefined() throws Exception {
		IHttpTransactionEventsListener tracer = buildFor("");
		tracer.connectStart();
		tracer.connected();
		tracer.sendStart(null);
		tracer.retry(null);
		tracer.responseReceived(null);
	}
	
	public void testTracePresent() throws Exception {
		buildFor("trace");
	}
	
	public void testConnectStart() throws Exception {
		IHttpTransactionEventsListener tracer = buildFor("trace.onConnectStart = function(){called()}");
		tracer.connectStart();
		assertTrue(flag.called);
	}
	
	public void testConnected() throws Exception {
		IHttpTransactionEventsListener tracer = buildFor("trace.onConnected = function(){called()}");
		tracer.connected();
		assertTrue(flag.called);
	}
	
	public void testSend() throws Exception {
		IHttpTransactionEventsListener tracer = buildFor("trace.onSend = function(req){called(req.uri)}");
		final HttpRequest req = mock(HttpRequest.class);
		checking(new Expectations(){{
			one(req).getUri(); will(returnValue("abc"));
		}});
		tracer.sendStart(req);
		assertTrue(flag.called);
		assertEquals("abc", flag.param);
	}
	
	public void testRetry() throws Exception {
		IHttpTransactionEventsListener tracer = buildFor("trace.onRetry = function(req){called(req.uri)}");
		final HttpRequest req = mock(HttpRequest.class);
		checking(new Expectations(){{
			one(req).getUri(); will(returnValue("abc"));
		}});
		tracer.retry(req);
		assertTrue(flag.called);
		assertEquals("abc", flag.param);
	}
	
	public void testReceive() throws Exception {
		IHttpTransactionEventsListener tracer = buildFor("trace.onReceive = function(resp){called(resp.status)}");
		final HttpResponse resp = mock(HttpResponse.class);
		checking(new Expectations(){{
			one(resp).getStatus(); will(returnValue(HttpResponseStatus.OK));
		}});
		tracer.responseReceived(resp);
		assertTrue(flag.called);
		assertEquals("200", flag.param);
	}
	
	
	//-------------------------------------
	
	IHttpTransactionEventsListener buildFor(String script) throws Exception{
		File f = helper.createFile(testRoot, "test", ".js");
		FileWriter fw = new FileWriter(f);
		
		try {
			fw.write(script);
		}
		finally{
			fw.close();
		}
		
		return new TestTraceRunner(f, cache, new RuntimeLogger(f.getAbsolutePath(), logger));
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		helper = new TempFileHelper();
		testRoot = helper.createDir(this);
		cache = new ScriptCache();
		logger = mock(ILogger.class);
		flag = new CalledFunction();
	}

	protected void tearDown() throws Exception {
		helper.cleanUp();
		super.tearDown();
	}
	
	class CalledFunction implements Callable {

		boolean called = false;
		String param;
		
		public Object call(Context arg0, Scriptable arg1, Scriptable arg2,	Object[] args) {
			param = args.length > 0 ? Context.toString(args[0]) : null;
			called = true;
			return null;
		}
		
	}
	
	class TestTraceRunner extends TraceRunner {

		public TestTraceRunner(File script, ScriptCache cache, RuntimeLogger logger) throws Exception {
			super(script, cache, logger);
		}
		
		protected void addRuntimeObjects(Scriptable scope, File scriptDir, ScriptCache cache) throws Exception {
			super.addRuntimeObjects(scope, scriptDir, cache);
			scope.put("called", scope, flag);
		}
	}
}

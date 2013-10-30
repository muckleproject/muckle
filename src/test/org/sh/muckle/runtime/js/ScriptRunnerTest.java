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


import java.io.File;
import java.io.FileWriter;







import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.sh.muckle.ILogger;
import org.sh.muckle.runtime.EHttpCommsError;
import org.sh.muckle.runtime.EHttpErrorAction;
import org.sh.muckle.runtime.HttpRequestDescriptor;
import org.sh.muckle.runtime.IHttpRunHandler;
import org.sh.muckle.runtime.js.IParamsJsonSource;
import org.sh.muckle.runtime.js.RuntimeLogger;
import org.sh.muckle.runtime.js.ScriptCache;
import org.sh.muckle.runtime.js.ScriptRunner;

import test.org.sh.TempFileHelper;

public class ScriptRunnerTest extends MockObjectTestCase {

	TempFileHelper helper;
	File testRoot;
	ScriptCache cache;
	
	HttpResponse resp;
	
	ILogger logger;
	
	final static String IMMEDIATE_NULL = "session.onNextRequest = nextRequest; function nextRequest(){return null;}\n";
	final static String NO_RETURN = "session.onNextRequest = nextRequest; function nextRequest(){}\n";
	final static String EMPTY_OBJECT = "session.onNextRequest = nextRequest; function nextRequest(){return new HttpRequest();}\n";
	final static String STATUS_CHECKER = "session.onHandleResponse = handleResponse; function handleResponse(resp){resp.status;}\n";
	
	public void testNextRequestNotDefined() throws Exception {
		checking(new Expectations(){{
			one(logger).warning(with(any(String.class)));
		}});
		IHttpRunHandler runner = buildFor("");
		assertNull(runner.nextRequest());
	}
	
	public void testNextRequestIsPrimitive() throws Exception {
		checking(new Expectations(){{
			one(logger).warning(with(any(String.class)));
		}});
		IHttpRunHandler runner = buildFor("session.onNextRequest = 1;");
		assertNull(runner.nextRequest());
	}
	
	public void testNextReturnsNothing() throws Exception {
		IHttpRunHandler runner = buildFor(NO_RETURN);
		assertNull(runner.nextRequest());
	}
	
	public void testNextReturnsNull() throws Exception {
		IHttpRunHandler runner = buildFor(IMMEDIATE_NULL);
		assertNull(runner.nextRequest());
	}
	
	public void testNextReturnsReq() throws Exception {
		IHttpRunHandler runner = buildFor(EMPTY_OBJECT);
		HttpRequestDescriptor desc = runner.nextRequest();
		assertEquals(HttpMethod.GET, desc.getRequest().getMethod());
	}

	public void XXtestNextReturnsReqPerformance() throws Exception {
		IHttpRunHandler runner = buildFor("var req = new HttpRequest(); session.onNextRequest = function(){req.uri ='/abc'; return req;}");
		long start = System.currentTimeMillis();
		for(int i=0; i<3000000; i++){
			runner.nextRequest();
		}
		System.out.println(System.currentTimeMillis() - start);
	}

	public void testHandleResponse() throws Exception{
		checking(new Expectations(){{
			one(resp).getStatus(); will(returnValue(HttpResponseStatus.OK));
		}});
		
		IHttpRunHandler runner = buildFor(IMMEDIATE_NULL + STATUS_CHECKER);
		runner.handleResponse(resp);
	}
	
	public void XXtestHandleResponsePerformance() throws Exception {
		DefaultHttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		IHttpRunHandler runner = buildFor("session.onNextRequest = function(){}\n " + STATUS_CHECKER);
		
		long start = System.currentTimeMillis();
		for(int i=0; i<3000000; i++){
			runner.handleResponse(resp);
		}
		System.out.println("Resp " + (System.currentTimeMillis() - start));
	}
	
	public void testHandleErrorNotDefined() throws Exception{
		checking(new Expectations(){{
			one(logger).warning(with(any(String.class)));
			one(logger).warning(with(any(String.class)));
		}});
		IHttpRunHandler runner = buildFor("");
		assertEquals(EHttpErrorAction.Abort, runner.handleError(EHttpCommsError.Connect));
	}
	
	public void testHandleErrorDefinedButNoReturn() throws Exception{
		checking(new Expectations(){{
			one(logger).warning(with(any(String.class)));
			one(logger).warning(with(any(String.class)));
		}});
		IHttpRunHandler runner = buildFor("function handleError(error){}");
		assertEquals(EHttpErrorAction.Abort, runner.handleError(EHttpCommsError.Connect));
	}
	
	public void testHandleErrorDefined() throws Exception{
		checking(new Expectations(){{
			one(logger).warning(with(any(String.class)));
		}});
		IHttpRunHandler runner = buildFor("session.onHandleError = handleError; function handleError(error){return HttpErrorAction.RETRY;}");
		assertEquals(EHttpErrorAction.Retry, runner.handleError(EHttpCommsError.Connect));
	}
	
	public void testHandleErrorParamSet() throws Exception{
		checking(new Expectations(){{
			one(logger).warning(with(any(String.class)));
			one(logger).warning(with(any(String.class)));
		}});
		IHttpRunHandler runner = buildFor("function handleError(error){if(error.isConnect){return HttpErrorAction.ABORT;}}");
		assertEquals(EHttpErrorAction.Abort, runner.handleError(EHttpCommsError.Connect));
	}
	
	public void XXtestHandleErrorPerformance() throws Exception {
		IHttpRunHandler runner = buildFor("session.onNextRequest = function(){};  session.onHandleError = function handleError(error){return HttpErrorAction.RETRY;}");
		
		long start = System.currentTimeMillis();
		for(int i=0; i<3000000; i++){
			runner.handleError(EHttpCommsError.Connect);
		}
		System.out.println("error " + (System.currentTimeMillis() - start));
	}
	
	public void testSetGetNextRequest() throws Exception{
		ScriptRunner runner = (ScriptRunner)buildFor("session.onNextRequest = function(){}");
		assertNotNull(runner.getNextRequestFunction());
		
		final Callable c = mock(Callable.class);
		runner.setNextRequestFunction(c);
		assertTrue(c == runner.getNextRequestFunction());
	}
	
	public void testSetGetHandleError() throws Exception{
		ScriptRunner runner = (ScriptRunner)buildFor("session.onNextRequest = function(){}");
		assertNull(runner.getHandleErrorFunction());
		
		final Callable c = mock(Callable.class);
		runner.setHandleErrorFunction(c);
		assertTrue(c == runner.getHandleErrorFunction());
	}
	
	public void testSetGetHandleResponse() throws Exception{
		ScriptRunner runner = (ScriptRunner)buildFor("session.onNextRequest = function(){}");
		assertNull(runner.getHandleResponseFunction());
		
		final Callable c = mock(Callable.class);
		runner.setHandleResponseFunction(c);
		assertTrue(c == runner.getHandleResponseFunction());
	}
	
	public void testDelayCalculatorNoParam() throws Exception{
		ScriptRunner runner = (ScriptRunner)buildFor("session.onNextRequest = function(){}");
		final Callable calc = runner.getDelayCalculator();
		runner.handleResponse(null);
		
		Long result = (Long)ContextFactory.getGlobal().call(new ContextAction() {
			public Object run(Context ctx) {
				Object[] args = new Object[0];
				return calc.call(ctx, null, null, args);
			}
		});
		
		assertEquals(0, result.intValue());
	}
	
	public void testDelayCalculatorNotInitialised() throws Exception{
		ScriptRunner runner = (ScriptRunner)buildFor("session.onNextRequest = function(){}");
		final Callable calc = runner.getDelayCalculator();
		
		Long result = (Long)ContextFactory.getGlobal().call(new ContextAction() {
			public Object run(Context ctx) {
				Object[] args = new Object[] {new Double(2000)};
				return calc.call(ctx, null, null, args);
			}
		});
		
		assertEquals(0, result.intValue());
	}
	
	public void testDelayCalculatorInitialised() throws Exception{
		ScriptRunner runner = (ScriptRunner)buildFor("session.onNextRequest = function(){}");
		final Callable calc = runner.getDelayCalculator();
		runner.handleResponse(null);
		runner.handleResponse(null);
		Thread.sleep(100);
		
		Long result = (Long)ContextFactory.getGlobal().call(new ContextAction() {
			public Object run(Context ctx) {
				Object[] args = new Object[] {new Double(2000)};
				return calc.call(ctx, null, null, args);
			}
		});
		
		assertTrue(result.longValue() < 1950);
		assertTrue(result.longValue() > 1800);
	}
	
	public void testRequireFunctionPresent() throws Exception {
		File f = helper.createFile(testRoot, "required", ".js");
		FileWriter fw = new FileWriter(f);
		
		try {
			fw.write("var r = 1;");
		}
		finally{
			fw.close();
		}

		buildFor("require('./required.js'); session.onNextRequest = function(){}");
	}
	
	public void testParseJSONFunctionPresent() throws Exception {
		buildFor("JSON.parse('{}'); session.onNextRequest = function(){}");
	}
	
	public void testEscapeFunctionPresent() throws Exception {
		buildFor("escape(); session.onNextRequest = function(){}");
	}
	
	public void testUnescapeFunctionPresent() throws Exception {
		buildFor("unescape; session.onNextRequest = function(){}");
	}
	
	public void testReadFileFunctionPresent() throws Exception {
		buildFor("readFile; session.onNextRequest = function(){}");
	}
	
	public void testWriteFileFunctionPresent() throws Exception {
		buildFor("writeFile; session.onNextRequest = function(){}");
	}
	
	public void testSessionParametersNoParamsSource() throws Exception {
		IHttpRunHandler runner = buildFor("var req = new HttpRequest(); session.onNextRequest = function nextRequest(){req.uri = session.parameters; return req;}");
		HttpRequestDescriptor desc = runner.nextRequest();
		assertEquals("null", desc.getRequest().getUri());
	}
	
	public void testSessionParametersParamsSourceBadJSON() throws Exception {
		final IParamsJsonSource source = mock(IParamsJsonSource.class);
		checking(new Expectations(){{
			one(source).getParamsJsonString(); will(returnValue("{"));
		}});
		
		try {
			buildFor("session.onNextRequest = function(){}", source);
			fail();
		}
		catch(Exception e){}
		
	}
	
	public void testSessionParametersParamsSource() throws Exception {
		final IParamsJsonSource source = mock(IParamsJsonSource.class);
		checking(new Expectations(){{
			one(source).getParamsJsonString(); will(returnValue("{\"x\":1}"));
			one(source).getCommonJsonString(); will(returnValue(null));
		}});
		
		IHttpRunHandler runner = buildFor("var req = new HttpRequest(); session.onNextRequest = function nextRequest(){req.uri = JSON.stringify(session.parameters); return req;}", source);
		HttpRequestDescriptor desc = runner.nextRequest();
		assertEquals("{\"x\":1}", desc.getRequest().getUri());
	}
	
	public void testSessionParametersCommonSource() throws Exception {
		final IParamsJsonSource source = mock(IParamsJsonSource.class);
		checking(new Expectations(){{
			one(source).getParamsJsonString(); will(returnValue(null));
			one(source).getCommonJsonString(); will(returnValue("{\"x\":1}"));
		}});
		
		IHttpRunHandler runner = buildFor("var req = new HttpRequest(); session.onNextRequest = function nextRequest(){req.uri = JSON.stringify(session.common); return req;}", source);
		HttpRequestDescriptor desc = runner.nextRequest();
		assertEquals("{\"x\":1}", desc.getRequest().getUri());
	}
	
	//-----------------------------------------------------
	
	IHttpRunHandler buildFor(String script) throws Exception {
		return buildFor(script, null);
	}
	
	IHttpRunHandler buildFor(String script, IParamsJsonSource source) throws Exception {
		File f = helper.createFile(testRoot, "test", ".js");
		FileWriter fw = new FileWriter(f);
		
		try {
			fw.write(script);
		}
		finally{
			fw.close();
		}
		
		return new ScriptRunner(f, cache, new RuntimeLogger(f.getAbsolutePath(), logger), source);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		helper = new TempFileHelper();
		testRoot = helper.createDir(this.getClass().getSimpleName());
		cache = new ScriptCache();
		resp = mock(HttpResponse.class);
		logger = mock(ILogger.class);
	}

	protected void tearDown() throws Exception {
		helper.cleanUp();
		super.tearDown();
	}

}

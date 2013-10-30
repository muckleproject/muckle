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

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.ILogger;
import org.sh.muckle.runtime.IHttpConnectionInfo;
import org.sh.muckle.runtime.js.ScriptCache;
import org.sh.muckle.runtime.js.SessionRunnerConstructor;

import test.org.sh.TempFileHelper;

public class SessionRunnerConstructorTest extends ScriptTestCase {

	SessionRunnerConstructor trc;
	TempFileHelper helper;
	File testRoot;
	
	public void testGetClassName(){
		assertEquals("SessionRunner", trc.getClassName());
	}
	
	public void testConstructNotEnoughParams(){
		try {
			runScript("var r = new TestRunner()");
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testConstructSingleParam(){
		Scriptable constructed = (Scriptable) runScript("new TestRunner('hostname')");
		assertEquals("SessionRunner", constructed.getClassName());
	}
	
	public void testConnectionInfoSingleParam(){
		IHttpConnectionInfo info = (IHttpConnectionInfo) runScript("new TestRunner('hostname')");
		assertEquals("hostname", info.getHostString());
		assertEquals("hostname:80", info.getRemoteAddress().toString());
		assertFalse(info.isSecure());
		assertFalse(info.isProxied());
	}
	
	public void testConnectionInfoTwoParam(){
		IHttpConnectionInfo info = (IHttpConnectionInfo) runScript("new TestRunner('hostname', 777.666)");
		assertEquals("hostname:777", info.getHostString());
		assertEquals("hostname:777", info.getRemoteAddress().toString());
		assertFalse(info.isSecure());
	}
	
	public void testConnectionInfoThreeParam(){
		IHttpConnectionInfo info = (IHttpConnectionInfo) runScript("new TestRunner('hostname', 777.666, true)");
		assertEquals("hostname:777", info.getHostString());
		assertEquals("hostname:777", info.getRemoteAddress().toString());
		assertTrue(info.isSecure());
	}
	
	public void testInvalidProperyName(){
		assertEquals(Context.getUndefinedValue(), runScript("var r = new TestRunner('host'); r.NOT_THERE"));
	}
	
	public void testRunIsMethod(){
		assertTrue(runScript("var r = new TestRunner('host'); r.run") instanceof Callable);
	}
	
	public void testRunNotEnoughParameters(){
		try {
			runScript("var r = new TestRunner('host'); r.run('','');");
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testRunBadScriptFile(){
		try {
			runScript("var r = new TestRunner('host'); r.run(1,1,'NOT_THERE.js');");
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testRunDoesNothing() throws Exception {
		createDoNothingScript();
		Integer status = (Integer)runScript("var r = new TestRunner('host'); r.run(10,100,'doNothing.js');");
		assertEquals(10, status.intValue());
	}
	
	public void testParamsValeuNull() throws Exception {
		createDoNothingScript();
		Integer status = (Integer)runScript("var r = new TestRunner('host'); r.run(10,100,'doNothing.js', null);");
		assertEquals(10, status.intValue());
	}
	
	public void testPassedParam() throws Exception {
		createPassedParamScript();
		Integer status = (Integer)runScript("var r = new TestRunner('host'); r.run(1,100,'param.js', [[0,1,2]]);");
		assertEquals(1, status.intValue());
	}
	
	public void testPassedParamNotEnoughValues() throws Exception {
		createPassedParamScript();
		try {
			runScript("var r = new TestRunner('host'); r.run(2,100,'param.js', [[0,1,2]]);");
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testParamsValeuNullCommmonPresent() throws Exception {
		createDoNothingScript();
		Integer status = (Integer)runScript("var r = new TestRunner('host'); r.run(10,100,'doNothing.js', null, {});");
		assertEquals(10, status.intValue());
	}
	
	public void testErrorsEmpty() throws Exception {
		createDoNothingScript();
		Scriptable errors = (Scriptable)runScript("var r = new TestRunner('host'); r.run(10,100,'doNothing.js'); r.errors");
		assertEquals(0, errors.getIds().length);
	}
	
	public void testErrorsNotEmpty() throws Exception {
		createConnectScript();
		Scriptable errors = (Scriptable)runScript("var r = new TestRunner('host'); r.run(10,100,'connect.js'); r.errors");
		assertEquals(1, errors.getIds().length);
	}
	
	public void testTraceScript() throws Exception {
		createConnectScript();
		createTraceScript();
		Scriptable errors = (Scriptable)runScript("var r = new TestRunner('host'); var tr = r.addTracer('tracer.js'); r.run(10,100,'connect.js'); r.errors");
		assertEquals(1, errors.getIds().length);
	}
	
	public void testRunWithSummary() throws Exception {
		createDoNothingScript();
		Scriptable summary = (Scriptable)runScript("var r = new TestRunner('host'); var sp = r.getSummaryProvider(); r.run(10,100,'doNothing.js'); sp.summary;");
		assertEquals(4, summary.getIds().length);
	}
	
	public void testRunWithDataCollection() throws Exception {
		createDoNothingScript();
		Scriptable values = (Scriptable)runScript("var r = new TestRunner('host'); var dp = r.getDataEventProvider(); r.run(10,100,'doNothing.js'); dp.values;");
		assertEquals(10, values.getIds().length);
	}
	
	public void testRunAbsoluteScriptPath() throws Exception{
		File script = createDoNothingScript();
		String path = script.getAbsolutePath().replace("\\", "/");
		Integer status = (Integer)runScript("var r = new TestRunner('host'); r.run(10,100,'" + path + "');");
		assertEquals(10, status.intValue());
	}
	
	public void testGetSummaryProviderIsMethod(){
		assertTrue(runScript("var r = new TestRunner('host'); r.getSummaryProvider") instanceof Callable);
	}
	
	public void testGetSummaryProviderMultiCallReturnsSameObject(){
		assertTrue((Boolean)runScript("var r = new TestRunner('host'); var s1 = r.getSummaryProvider(); r.getSummaryProvider() === s1"));
	}
	
	public void testGetDataEventProviderIsMethod(){
		assertTrue(runScript("var r = new TestRunner('host'); r.getDataEventProvider") instanceof Callable);
	}
	
	public void testGetDataEventProviderMultiCallReturnsSameObject(){
		assertTrue((Boolean)runScript("var r = new TestRunner('host'); var d1 = r.getDataEventProvider(); r.getDataEventProvider() === d1") );
	}
	
	public void testSetStatusListenerIsMethod(){
		assertTrue(runScript("var r = new TestRunner('host'); r.setStatusListener") instanceof Callable);
	}
	
	public void testSetStatusListener() throws Exception {
		assertTrue((Boolean)runScript("var r = new TestRunner('host'); r.setStatusListener(4, function(status){}); "));
	}
	
	public void testSetConnectionTimeout() throws Exception {
		assertEquals(40000, ((Integer)runScript("var r = new TestRunner('host'); r.setConnectionTimeout(40000); ")).intValue());
	}
	
	public void testSetConnectionTimeoutNoParameter() throws Exception {
		assertEquals(2000, ((Integer)runScript("var r = new TestRunner('host'); r.setConnectionTimeout(); ")).intValue());
	}
	
	public void testRunWithStatusListener() throws Exception {
		createDoNothingScript();
		Integer status = (Integer)runScript("var r = new TestRunner('host'); r.setStatusListener(4, function(status){}); r.run(10,100,'doNothing.js');");
		assertEquals(10, status.intValue());
	}
	
	public void testSetStatusNotEnoughParams() throws Exception {
		try {
			runScript("var r = new TestRunner('host'); r.setStatusListener(4);");
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testSetStatusSecondParamNotCallable() throws Exception {
		try {
			runScript("var r = new TestRunner('host'); r.setStatusListener(4, 1);");
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testSetProxyNoPort() throws Exception {
		IHttpConnectionInfo info = (IHttpConnectionInfo)runScript("var r = new TestRunner('host'); r.setProxy('proxy'); r");
		assertTrue(info.isProxied());
		assertEquals("proxy:80", info.getProxyAddress().toString());
	}
	
	public void testSetProxy() throws Exception {
		IHttpConnectionInfo info = (IHttpConnectionInfo)runScript("var r = new TestRunner('host'); r.setProxy('proxy', 8888); r");
		assertTrue(info.isProxied());
		assertEquals("proxy:8888", info.getProxyAddress().toString());
	}
	
	//-------------------------------------------------
	
	protected void setUp() throws Exception {
		super.setUp();
		helper = new TempFileHelper();
		testRoot = helper.createDir(this.getClass().getSimpleName());
		trc = new SessionRunnerConstructor(testRoot, new TestLogger(), new ScriptCache());
		addToScope(trc, "TestRunner");
	}
	
	protected void tearDown() throws Exception {
		helper.cleanUp();
		super.tearDown();
	}
	
	File createPassedParamScript() throws Exception{
		File s = helper.createFile(testRoot, "param", ".js");
		FileWriter fw = new FileWriter(s);
		fw.write("var p = session.parameters[1]\n");
		fw.write("session.onNextRequest = function (){return null;}\n");
		fw.close();
		return s;
	}
	
	File createDoNothingScript() throws Exception{
		File s = helper.createFile(testRoot, "doNothing", ".js");
		FileWriter fw = new FileWriter(s);
		fw.write("session.onNextRequest = function (){return null;}\n");
		fw.close();
		return s;
	}
	
	File createConnectScript() throws Exception{
		File s = helper.createFile(testRoot, "connect", ".js");
		FileWriter fw = new FileWriter(s);
		fw.write("session.onNextRequest = function (){return new HttpRequest();}\n");
		fw.write("session.onHandleError = function (error){return HttpErrorAction.ABORT;}");
		fw.close();
		return s;
	}
	
	File createTraceScript() throws Exception{
		File s = helper.createFile(testRoot, "tracer", ".js");
		FileWriter fw = new FileWriter(s);
		fw.close();
		return s;
	}

	//---------------------------------------------
	

	
	class TestLogger implements ILogger {
		public void warning(String s) {
		}

		public void error(String s) {
		}
		
	}

}

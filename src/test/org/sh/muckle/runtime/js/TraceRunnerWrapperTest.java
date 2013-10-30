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

import org.jmock.integration.junit3.MockObjectTestCase;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.ILogger;
import org.sh.muckle.runtime.js.ScriptCache;
import org.sh.muckle.runtime.js.TraceRunner;
import org.sh.muckle.runtime.js.TraceRunnerWrapper;

import test.org.sh.TempFileHelper;

public class TraceRunnerWrapperTest extends MockObjectTestCase {

	TraceRunnerWrapper wrapper;
	TempFileHelper helper;
	File testRoot;
	ScriptCache cache;
	
	public void testGetName(){
		assertEquals("Tracer", wrapper.getClassName());
	}
	
	public void testGetNotFound(){
		assertEquals(Scriptable.NOT_FOUND, wrapper.get("NOT_FOUND", null));
	}
	
	public void testGetTraceNotSet(){
		assertNull(wrapper.get("trace", null));
	}
	
	public void testGetTraceRunner() throws Exception {
		TraceRunner tr = wrapper.buildTraceRunner(cache, mock(ILogger.class));
		assertNotNull(tr);
	}
	
	public void testGetTraceNoTraceBuilt(){
		ContextFactory.getGlobal().call(new ContextAction() {
			public Object run(Context ctx) {
				wrapper.updateTraceProperty(ctx, ctx.initStandardObjects());
				return null;
			}
		});
		assertNull(wrapper.get("trace", null));
	}
	
	public void testGetTrace() throws Exception{
		wrapper.buildTraceRunner(cache, mock(ILogger.class));
		ContextFactory.getGlobal().call(new ContextAction() {
			public Object run(Context ctx) {
				wrapper.updateTraceProperty(ctx, ctx.initStandardObjects());
				return null;
			}
		});
		assertTrue(wrapper.get("trace", null) instanceof NativeObject);
	}
	
	public void testGetTraceGetResultNotDefined() throws Exception{
		File traceFile = helper.createFile(testRoot, "trace", ".js");
		FileWriter fw = new FileWriter(traceFile);
		try {
			fw.write("");
		}
		finally {
			fw.close();
		}
		wrapper = new TraceRunnerWrapper(traceFile);
		wrapper.buildTraceRunner(cache, mock(ILogger.class));
		ContextFactory.getGlobal().call(new ContextAction() {
			public Object run(Context ctx) {
				wrapper.updateTraceProperty(ctx, ctx.initStandardObjects());
				return null;
			}
		});
		assertNull(wrapper.get("trace", null));
	}
	

	//--------------------------------------------
	
	
	protected void setUp() throws Exception {
		super.setUp();
		helper = new TempFileHelper();
		testRoot = helper.createDir(this);
		cache = new ScriptCache();
		File traceFile = helper.createFile(testRoot, "trace", ".js");
		FileWriter fw = new FileWriter(traceFile);
		try {
			fw.write("trace.getResult = function(){return {x:1};}");
		}
		finally {
			fw.close();
		}
		wrapper = new TraceRunnerWrapper(traceFile);
	}
	
	protected void tearDown() throws Exception {
		helper.cleanUp();
		super.tearDown();
	}

}

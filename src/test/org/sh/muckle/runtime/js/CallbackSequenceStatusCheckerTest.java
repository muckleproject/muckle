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


import java.util.ArrayList;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.runtime.HttpSessionSequencer;
import org.sh.muckle.runtime.js.CallbackSequenceStatusChecker;

public class CallbackSequenceStatusCheckerTest extends ScriptTestCase {
	
	public void testSummaryContents(){
		String json = (String)runScript("var json; function f(status){json = JSON.stringify(status);} run(f); json;");
		assertTrue(json.contains("started"));
		assertTrue(json.contains("finished"));
		assertTrue(json.contains("error"));
		assertTrue(json.contains("min"));
		assertTrue(json.contains("max"));
	}

	public void testNoReturnValue(){
		assertTrue((Boolean)runScript("function f(status){} run(f)"));
	}

	public void testReturnStringValue(){
		assertTrue((Boolean)runScript("function f(status){return 'false'} run(f)"));
	}

	public void testReturnFalse(){
		assertFalse((Boolean)runScript("function f(status){return false;} run(f)"));
	}

	public void testReturnTrue(){
		assertTrue((Boolean)runScript("function f(status){return true;} run(f)"));
	}

	protected void setUp() throws Exception {
		super.setUp();
		addToScope(new Runner(), "run");
	}
	
	class Runner implements Callable {
		public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
			CallbackSequenceStatusChecker chk = new CallbackSequenceStatusChecker(1000, ctx, scope, (Callable)args[0]);
			assertEquals(1000, chk.getTimeout());
			assertEquals(1000, chk.getTimeout());
			assertEquals("SequenceStatusChecker", chk.getClassName());
			return chk.continueWith(new ArrayList<HttpSessionSequencer>());
		}
		
	}

}

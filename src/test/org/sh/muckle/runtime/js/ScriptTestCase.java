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


import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

import junit.framework.TestCase;

abstract public class ScriptTestCase extends TestCase {

	protected Scriptable scope;
	
	protected void setUp() throws Exception {
		super.setUp();
		ContextFactory.getGlobal().call(new Init());
	}
	
	protected void addToScope(Object object, String name){
		scope.put(name, scope, object);
	}
	
	protected Object runScript(String script){
		return ContextFactory.getGlobal().call(new ScriptRunner(script));
	}
	
	class Init implements ContextAction {
		public Object run(Context cx) {
			scope = cx.initStandardObjects();
			return null;
		}
	}
	
	class ScriptRunner implements ContextAction {
		String script;
		
		ScriptRunner(String script){
			this.script = script;
		}
		
		public Object run(Context cx) {
			if(script != null){
				cx.setOptimizationLevel(-1);
				return cx.evaluateString(scope, script, "", 0, null);
			}
			else {
				fail("Null script.");
				return null;
			}
		}
	}

}

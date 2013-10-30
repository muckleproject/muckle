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
package org.sh.muckle.runtime.js;

import java.io.File;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.json.JsonParser;
import org.mozilla.javascript.json.JsonParser.ParseException;
import org.sh.muckle.ILogger;
import org.sh.muckle.jssupport.AbstractReadOnlyScriptable;

public class TraceRunnerWrapper extends AbstractReadOnlyScriptable {

	Object trace;
	File script;
	TraceRunner runner;
	
	public TraceRunnerWrapper(File script) {
		this.script = script;
	}

	public Object get(String name, Scriptable scope) {
		Object value = NOT_FOUND;
		if("trace".equals(name)){
			value = trace;
		}
		return value;
	}

	public String getClassName() {
		return "Tracer";
	}

	public TraceRunner buildTraceRunner(ScriptCache cache, ILogger logger) throws Exception {
		runner = new TraceRunner(script, cache, logger);
		return runner;
	}

	public void updateTraceProperty(Context ctx, Scriptable scope) {
		trace = null;
		if(runner != null){
			Object result = runner.executeGetResult();
			if(result != null){
				try {
					trace = new JsonParser(ctx, scope).parseValue(Stringifier.stringify(ctx, scope, result));
				} 
				catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
			runner = null;
		}
	}

}

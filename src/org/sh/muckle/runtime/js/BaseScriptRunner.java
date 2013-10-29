package org.sh.muckle.runtime.js;

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


import java.io.File;
import java.io.IOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.json.JsonParser;
import org.mozilla.javascript.json.JsonParser.ParseException;
import org.sh.muckle.jsobjectloaderservice.ClientScriptLoaderService;

public class BaseScriptRunner  {
	
	Scriptable scope;
	RuntimeLogger logger;
	
	public BaseScriptRunner(File script, ScriptCache cache, RuntimeLogger logger) throws Exception {
		this.logger = logger;
		scope = buildScope(cache);
		addRuntimeObjects(scope, script.getParentFile(), cache);
	}

	protected Scriptable buildScope(ScriptCache cache){
		return (Scriptable) ContextFactory.getGlobal().call(new ContextAction() {
			public Object run(Context ctx) {
				ctx.setOptimizationLevel(9);
				return ctx.initStandardObjects();
			}
		});
	}
	
	protected void initScript(File script, ScriptCache cache) throws IOException {
		final Script s = cache.getScript(script);
		ContextFactory.getGlobal().call(new ContextAction() {
			public Object run(Context ctx) {
				return s.exec(ctx, scope);
			}
		});
	}
	
	protected void addRuntimeObjects(Scriptable scope, File scriptDir, ScriptCache cache) throws Exception {
		Require req = new Require(scriptDir, cache);
		scope.put(Require.NAME, scope, req);
		scope.put(ReadFile.NAME, scope, new ReadFile(req));
		scope.put(WriteFile.NAME, scope, WriteFile.STATIC);
		ClientScriptLoaderService.STATIC.loadScriptObjects(scope, scriptDir);
	}
	
	protected Object buildParams(final Scriptable scope, final String json) throws IOException {
		Object params = null;
		
		if(json != null){
			try {
				params = ContextFactory.getGlobal().call(new ContextAction() {
					public Object run(Context ctx) {
						try {
							return new JsonParser(ctx, scope).parseValue(json);
						} 
						catch (ParseException e) {
							throw new RuntimeException(e);
						}
					}
				});
			}
			catch(RuntimeException e){
				throw new IOException(e.getMessage());
			}
		}

		return params;
	}

}

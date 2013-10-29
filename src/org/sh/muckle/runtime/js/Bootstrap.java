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
import java.io.FileNotFoundException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.ILogger;
import org.sh.muckle.jsobjectloaderservice.ControlScriptLoaderService;
import org.sh.muckle.runtime.SysLogger;

public class Bootstrap {

	ILogger logger;

	public Bootstrap(ILogger logger) {
		this.logger = logger;
	}

	public int run(final String[] args) {
		int result = 0;
		if(args.length > 0){
			try {
				final File f = findFile(args[0]);
				final ScriptCache cache = new ScriptCache();
				final Script controlScript = cache.getScript(f);
				
				ContextFactory.getGlobal().call(new ContextAction() {
					
					public Object run(Context ctx) {
						ctx.setOptimizationLevel(9);
						
						Scriptable scope = ctx.initStandardObjects();
						
						// load external objects
						try {
							new ControlScriptLoaderService().loadScriptObjects(scope, f.getParentFile());
						} 
						catch (Exception e) {
							throw new RuntimeException(e.getMessage());
						}
						
						scope.put(TimeResolver.NAME, scope, TimeResolver.STATIC);
						scope.put(DataEvent.NAME, scope, DataEventConstructor.STATIC);
						Require req = new Require(f.getParentFile(), cache);
						scope.put(Require.NAME, scope, req);
						scope.put(ReadFile.NAME, scope, new ReadFile(req));
						scope.put(WriteFile.NAME, scope, WriteFile.STATIC);
						scope.put(SessionRunnerConstructor.NAME, scope, new SessionRunnerConstructor(f.getParentFile(), logger, cache));
						scope.put("args", scope, buildArgs(args, ctx, scope));
						
						putAdditionalObjectsInScope(scope);
						
						return controlScript.exec(ctx, scope);
					}
				});
				

			}
			catch(Throwable e){
				result = -1;
				logger.warning(e.getMessage());
			}
		}
		else {
			logger.error("Mandatory control script name not specified.");
			logger.warning("usage: muckle CONTROL_SCRIPT args ..");
		}
		
		return result;
	}
	
	Scriptable buildArgs(String[] args, Context ctx, Scriptable scope){
		Object[] elements = new Object[args.length];
		for(int i=0; i<args.length; i++){
			elements[i] = args[i];
		}
		return ctx.newArray(scope, elements);
	}
	
	//--------------------------------------------------
	
	protected void putAdditionalObjectsInScope(Scriptable scope){
	}
	
	//--------------------------------------------------

	public static void main(String[] args){
		new Bootstrap(new SysLogger()).run(args);
	}
	
	//--------------------------------------------------
	
	File findFile(String name) throws FileNotFoundException {
		File f = new File(name);
		if(!f.exists()){
			throw new FileNotFoundException("Cannot find file \"" + name + "\"");
		}
		return f;
	}
	
	//---------------------------------------------------
	
	

}

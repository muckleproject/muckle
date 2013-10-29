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
import java.util.HashSet;
import java.util.Stack;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrappedException;

public class Require implements Callable, IFileResolver  {

	final static String NAME = "require";
	
	HashSet<String> loadedPaths = new HashSet<String>();
	Stack<File> pathStack = new Stack<File>();
	
	ScriptCache cache;
	
	public Require(File rootPath, ScriptCache cache){
		this.cache = cache;
		pathStack.add(rootPath);
	}
	
	public Object call(Context context, Scriptable scope, Scriptable thisObj, Object[] params) {
		Object result = null;
		if(params.length > 0){
			for(int i=0; i<params.length; i++){
				File f = resolveName(Context.toString(params[i]));
				String path = f.getAbsolutePath();
				if(!loadedPaths.contains(path)){
					pathStack.push(f.getParentFile());
					
					result = ContextFactory.getGlobal().call(new ScriptLoader(scope, f));
					loadedPaths.add(path);
					
					pathStack.pop();
				}
			}
		}
		return result;
	}

	public String getClassName() {
		return "require";
	}
	
	public File resolveName(String name) {
		// look in current directory first
		File f = new File(pathStack.peek(), name);
		if(!f.isFile()){
			// try to see if absolute
			f = new File(name);
			if(!f.isFile()){
				// return a non existant path
				f = new File(pathStack.peek(), name);
			}
		}
		return f;
	}

	
	//---------------------------------------
	
	class ScriptLoader implements ContextAction {
		File scriptFile;
		Scriptable scope;
		
		ScriptLoader(Scriptable scope, File scriptFile){
			this.scriptFile = scriptFile;
			this.scope = scope;
		}
		
		public Object run(Context ctx) {
			try {
				ctx.setOptimizationLevel(9);
				return cache.getScript(scriptFile).exec(ctx, scope);
			}
			catch(Exception e){
				throw new WrappedException(e);
			}
		}
	}

}

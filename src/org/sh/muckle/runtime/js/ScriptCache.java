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
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;

public class ScriptCache extends ConcurrentHashMap<String, Script> {
	
	Exception compileError;

	public Script getScript(File file) throws IOException {
		String path = file.getCanonicalPath();
		Script script = get(path);
		
		if (script == null) {
			compileError = null;
			script = (Script) ContextFactory.getGlobal().call(new Compiler(file));
			if (compileError != null) {
				IOException e = new IOException(compileError.getMessage());
				compileError = null;
				throw e;
			}
			put(path, script);
		}
		
		return script;
	}	
	
	class Compiler implements ContextAction {

		File scriptFile;
		
		public Compiler(File file)  {
			scriptFile = file;
		}

		public Object run(Context ctx) {
			Object compiled = null;
			ctx.setOptimizationLevel(9);
			try {
				compiled = ctx.compileReader(new FileReader(scriptFile), scriptFile.getAbsolutePath(), 1, null);
			} 
			catch (Exception e) {
				compileError = e;
			}
			return compiled;
		}
		
	}

}

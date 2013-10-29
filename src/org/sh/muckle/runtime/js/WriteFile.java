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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class WriteFile implements Callable {

	public final static String NAME = "writeFile";
	
	public final static WriteFile STATIC = new WriteFile();
	
	private WriteFile(){}
	
	public Object call(Context context, Scriptable scope, Scriptable thisObj, Object[] params) {
		long written = 0;
		if(params.length > 0){
			File f = new File(Context.toString(params[0]));
			f = new File(f.getAbsoluteFile().getParent(), f.getName());
			try {
				f.createNewFile();
				if(params.length > 1){
					String encoding = params.length > 2 ? Context.toString(params[2]) : "utf-8";
					FileOutputStream fos = new FileOutputStream(f);
					try {
						OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
						try {
							String contents = Context.toString(params[1]);
							osw.write(contents);
							written = contents.length();
						}
						finally{
							osw.close();
						}
					}
					finally{
						fos.close();
						written = f.length();
					}
				}
			} 
			catch (Exception e) {
				throw new RuntimeException(e.getMessage() + " -\"" + f.getAbsolutePath() + "\"");
			}
			
		}
		return written;
	}

}

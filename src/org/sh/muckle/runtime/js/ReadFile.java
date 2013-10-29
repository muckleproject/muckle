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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrappedException;

public class ReadFile implements Callable {

	public final static String NAME = "readFile";
	
	IFileResolver fileResolver;
	
	public ReadFile(IFileResolver res) {
		this.fileResolver = res;
	}

	public Object call(Context context, Scriptable scope, Scriptable thisObj, Object[] params) {
		String contents = "";
		
		if(params.length > 0){
			File f = fileResolver.resolveName(Context.toString(params[0]));
			if(f.exists()){
				long len = f.length();
				if(len > 0){
					String encoding = params.length > 1 ? Context.toString(params[1]) : "utf-8";
					try {
						StringBuilder sb = new StringBuilder((int)len);
						InputStreamReader isr = new InputStreamReader(new FileInputStream(f), encoding);
						try {
							char[] chars = new char[20000];
							int charsRead;
							
							while((charsRead = isr.read(chars)) > 0){
								sb.append(chars, 0, charsRead);
							}
							
							contents = sb.toString();
						}
						finally{
							isr.close();
						}
					}
					catch(Exception e){
						throw new WrappedException(e);
					}
				}
			}
			else {
				throw new WrappedException(new FileNotFoundException(f.getAbsolutePath()));
			}
		}
		
		return contents;
	}

}

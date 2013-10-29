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


import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

public class Stringifier {
	
	public static String[] stringifyArray(Context ctx, Scriptable scope, Object arg){
		String[] strings = null;
		
		if(arg != null){
			if(arg instanceof NativeArray){
				Scriptable source = (Scriptable)arg;
				
				int len = (int)Context.toNumber(source.get("length", source));
				strings = new String[len];
				
				if(len > 0){
					Callable method = getStringify(scope);
					Object[] args = new Object[1];
					
					for(int i=0; i<strings.length; i++){
						args[0] = source.get(i, source);
						if(Scriptable.NOT_FOUND == args[0] || null == args[0]){
							throw new RuntimeException("Undefined value in source array.");
						}
						else {
							strings[i] = (String)method.call(ctx, scope, null, args);
						}
					}
				}
			}
			else {
				throw new RuntimeException("Stringifier. Expected Array but got \"" + arg.getClass().getSimpleName() + "\".");
			}
		}
		
		return strings;
	}
	
	public static String stringify(Context ctx, Scriptable scope, Object arg){
		String stringified = null;
		
		if(arg != null){
			if(arg instanceof Scriptable){
				Callable method = getStringify(scope);
				stringified = (String)method.call(ctx, scope, null, new Object[] {arg});
			}
			else {
				throw new RuntimeException("Stringifier. Expected Object but got \"" + arg.getClass().getSimpleName() + "\".");
			}
		}
		
		return stringified;
	}
	
	static Callable getStringify(Scriptable scope){
		Scriptable JSON = (Scriptable)scope.get("JSON", scope);
		return (Callable)JSON.get("stringify", JSON);
	}

}

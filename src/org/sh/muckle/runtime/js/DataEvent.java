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
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jssupport.AbstractReadOnlyScriptable;

public class DataEvent extends AbstractReadOnlyScriptable {
	
	public final static String NAME = "DataEvent";
	
	final static String[] IDS = {"start", "end", "contentLength"};
	final static Elapsed ELAPSED_METHOD = new Elapsed();

	long start;
	long end;
	long contentLength;
	
	public DataEvent(long start, long end, long contentLength) {
		this.start = start;
		this.end = end;
		this.contentLength = contentLength;
	}

	public Object[] getIds() {
		return IDS;
	}
	
	public Object get(String name, Scriptable startPoint) {
		Object value = null;
		
		if(IDS[0].equals(name)){
			value = start;
		}
		else if(IDS[1].equals(name)){
			value = end;
		}
		else if(IDS[2].equals(name)){
			value = contentLength;
		}
		else if("elapsed".equals(name)){
			value = ELAPSED_METHOD;
		}
		
		return value;
	}

	public String getClassName() {
		return NAME;
	}
	
	//---------------------------------------------------------
	
	static class Elapsed implements Callable {
		public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
			DataEvent se = (DataEvent)thisObj;
			return se.end - se.start;
		}
	}

}

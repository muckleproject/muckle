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
import org.sh.muckle.jssupport.AbstractObjectConstructor;
import org.sh.muckle.jssupport.AbstractReadOnlyScriptable;

public class TimeResolver extends AbstractReadOnlyScriptable {
	
	public final static String NAME = "TimeResolver";
	public final static Scriptable STATIC = new TimeResolverConstructor();
	
	public final static String[] IDS = {"startTimestamp", "nanoTimestamp"};

	
	long startTime;
	long nanoTime;
	
	GetMillisFunction getMillis;

	public TimeResolver(long startTime, long nanoTime) {
		this.startTime = startTime;
		this.nanoTime = nanoTime;
	}

	public Object[] getIds() {
		return IDS;
	}

	public Object get(String name, Scriptable start) {
		Object value = null;
		
		if("getMillisFor".equals(name)){
			if(getMillis == null){
				getMillis = new GetMillisFunction();
			}
			value = getMillis;
		}
		else if(IDS[0].equals(name)){
			value = startTime;
		}
		else if(IDS[1].equals(name)){
			value = nanoTime;
		}
		
		return value;
	}

	public String getClassName() {
		return NAME;
	}
	
	//-------------------------------------------------------------------
	
	class GetMillisFunction implements Callable {
		public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
			double time = args.length > 0 ? Context.toNumber(args[0]) : nanoTime;
			return (long)(startTime + (time - nanoTime)/1000000);
		}
	}
	
	static class TimeResolverConstructor extends AbstractObjectConstructor {
		public Scriptable construct(Context ctx, Scriptable scope, Object[] args) {
			if(args.length > 1) {
				return new TimeResolver((long)Context.toNumber(args[0]), (long)Context.toNumber(args[1]));
			}
			else {
				throw new RuntimeException(getArgsMessage());
			}
		}

		public String getClassName() {
			return NAME;
		}
		
	}
}

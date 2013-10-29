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


import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jssupport.AbstractObjectConstructor;

public class DataEventConstructor extends AbstractObjectConstructor {
	
	public final static DataEventConstructor STATIC = new DataEventConstructor();

	public Scriptable construct(Context ctx, Scriptable scope, Object[] args) {
		if(args.length > 1){
			long start = (long) Context.toNumber(args[0]);
			long end = (long) Context.toNumber(args[1]);
			long len = args.length > 2 ? (long) Context.toNumber(args[2]) : 0;
			return new DataEvent(start, end, len);
		}
		else {
			throw new RuntimeException(getArgsMessage());
		}
	}

	public String getClassName() {
		return DataEvent.NAME;
	}

}

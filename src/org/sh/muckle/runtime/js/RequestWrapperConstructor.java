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


import org.jboss.netty.handler.codec.http.HttpMethod;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jssupport.AbstractObjectConstructor;

public class RequestWrapperConstructor extends AbstractObjectConstructor {

	public final static String NAME = "HttpRequest";

	public Scriptable construct(Context ctx, Scriptable scope, Object[] args) {
		HttpMethod method = HttpMethod.GET;
		String uri = "/";
		if(args.length > 0){
			uri = Context.toString(args[0]);
			if(args.length > 1){
				method = HttpMethod.valueOf(Context.toString(args[1]).toUpperCase());
			}
		}
		return new RequestWrapper(method, uri);
	}

	public String getClassName() {
		return NAME;
	}

}

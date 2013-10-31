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
package org.sh.muckle.runtime.js;

import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpMessage;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;


class HeadersMethod implements Callable {
	
	HttpMessage message;
	
	HeadersMethod(HttpMessage message){
		this.message = message;
	}

	public Object call(Context ctx, Scriptable scope, Scriptable thisObject, Object[] args) {
		List<Map.Entry<String, String>> headersList = message.getHeaders();
		Object[] headers = new Object[headersList.size()];
		for(int i=0; i<headersList.size(); i++){
			Scriptable nv = ctx.newObject(scope);
			Map.Entry<String, String> entry = headersList.get(i);
			nv.put(entry.getKey(), nv, entry.getValue());
			headers[i] = nv;
		}
		return ctx.newArray(scope, headers);
	}
	
}


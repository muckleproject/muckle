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



import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jssupport.AbstractReadOnlyScriptable;

public class ResponseWrapper extends AbstractReadOnlyScriptable {
	
	HttpResponse resp;
	
	public ResponseWrapper(HttpResponse response) {
		setResponse(response);
	}

	public void setResponse(HttpResponse resp) {
		this.resp = resp;
	}

	public Object get(String name, Scriptable scope) {
		Object value = Context.getUndefinedValue();

		if("status".equals(name)){
			value = resp.getStatus().getCode();
		}
		else if("getContent".equals(name)){
			value = new ContentMethod(resp);
		}
		else if("getHeader".equals(name)){
			value = new HeaderMethod();
		}
		else if("getHeaders".equals(name)){
			value = new HeadersMethod(resp);
		}
		else if("getContentBytes".equals(name)){
			value = new ContentBytesMethod();
		}

		return value;
	}

	public String getClassName() {
		return "ResponseWrapper";
	}
	
	//---------------------------------------------------------------
	
	class ContentBytesMethod implements Callable {

		public Object call(Context ctx, Scriptable scope, Scriptable thisObject, Object[] args) {
			ChannelBuffer cb = resp.getContent();
			int len = cb != null ? cb.readableBytes() : 0;
			Object[] content = new Object[len];
			for(int i=0; i<content.length; i++){
				content[i] = new Integer(cb.readByte());
			}
			return ctx.newArray(scope, content);
		}
		
	}
	
	class HeaderMethod implements Callable {

		public Object call(Context ctx, Scriptable scope, Scriptable thisObject, Object[] args) {
			String value = null;
			if(args.length > 0){
				value = resp.getHeader(Context.toString(args[0]));
			}
			return value;
		}
		
	}
}

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

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrappedException;


class ContentMethod implements Callable {
	
	HttpMessage message;

	ContentMethod(HttpMessage message){
		this.message = message;
	}
	
	public Object call(Context ctx, Scriptable scope, Scriptable thisObject, Object[] args) {
		String charset = "UTF-8";
		if(args.length > 0){
			charset = Context.toString(args[0]);
		}
		
		try {
			ChannelBuffer cb = message.getContent();
			return cb != null ? cb.toString(Charset.forName(charset)) : "";
		}
		catch(UnsupportedCharsetException e){
			throw new WrappedException(e);
		}
	}
	
}


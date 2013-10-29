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


import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.Version;
import org.sh.muckle.jssupport.AbstractReadOnlyScriptable;
import org.sh.muckle.runtime.HttpRequestDescriptor;

public class RequestWrapper extends AbstractReadOnlyScriptable {
	
	final static String DEFAULT_USER_AGENT = Version.GetVersion();

	final static String URI = "uri";
	final static String DELAY = "delay";
	final static String SET_HEADER = "setHeader";
	final static String SET_CONTENT = "setContent";
	final static String METHOD = "method";
	
	HttpRequest request;
	Integer delay;
	
	public RequestWrapper(HttpMethod method, String uri){
		request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, uri);
		request.setHeader(HttpHeaders.Names.USER_AGENT, DEFAULT_USER_AGENT);
	}

	public void put(String name, Scriptable scope, Object property) {
		if(URI.equals(name)){
			request.setUri(Context.toString(property));
		}
		else if(DELAY.equals(name)){
			delay = (int)Context.toNumber(property);
		}
		else if(METHOD.equals(name)){
			request.setMethod(HttpMethod.valueOf(Context.toString(property).toUpperCase()));
		}
	}

	public Object get(String name, Scriptable scope) {
		Object value = NOT_FOUND;
		
		if(SET_HEADER.equals(name)){
			value = new HeaderSetter();
		}
		else if(SET_CONTENT.equals(name)){
			value = new ContentSetter();
		}
		else if(URI.equals(name)){
			value = request.getUri();
		}
		else if(METHOD.equals(name)){
			value = request.getMethod().getName();
		}
		else if(DELAY.equals(name)){
			value = delay;
		}
		
		return value;
	}

	public String getClassName() {
		return RequestWrapperConstructor.NAME;
	}
	
	public HttpRequestDescriptor getRequestDescriptor(){
		HttpRequestDescriptor desc = new HttpRequestDescriptor(request);
		if(delay != null){
			desc.setDelay(delay);
		}
		return desc;
	}

	//------------------------------------------------------
	
	class HeaderSetter implements Callable {
		public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
			if(args.length > 1){
				request.setHeader(Context.toString(args[0]), Context.toString(args[1]));
			}
			return null;
		}
	}
	
	class ContentSetter implements Callable {
		public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
			if(args.length > 1){
				Charset encoding = Charset.forName(args.length > 2 ? Context.toString(args[2]) : "UTF-8");
				request.setContent(ChannelBuffers.copiedBuffer(Context.toString(args[0]), encoding));
				request.setHeader(HttpHeaders.Names.CONTENT_TYPE, Context.toString(args[1] + "; charset=" + encoding.displayName()));
				request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, request.getContent().readableBytes());
			}
			return null;
		}
	}

}

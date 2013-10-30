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


import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jssupport.AbstractReadOnlyScriptable;
import org.sh.muckle.runtime.EHttpCommsError;
import org.sh.muckle.runtime.ICommsErrorVisitor;

public class HttpErrorWrapper extends AbstractReadOnlyScriptable implements ICommsErrorVisitor{

	EHttpCommsError error;
	Boolean isConnect;
	Boolean isSend;
	Boolean isReceive;
	
	public Object get(String name, Scriptable scope) {
		Object property = null;
		if("isConnect".equals(name)){
			property = isConnect;
		}
		else if("isSend".equals(name)){
			property = isSend;
		}
		else if("isReceive".equals(name)){
			property = isReceive;
		}
		return property;
	}

	public String getClassName() {
		return "HttpError";
	}

	public HttpErrorWrapper setError(EHttpCommsError error) {
		this.error = error;
		error.accept(this);
		return this;
	}
	
	//--------------------------

	public Object visitConnect() {
		isConnect = true;
		isSend = false;
		isReceive = false;
		return null;
	}

	public Object visitSend() {
		isConnect = false;
		isSend = true;
		isReceive = false;
		return null;
	}

	public Object visitReceive() {
		isConnect = false;
		isSend = false;
		isReceive = true;
		return null;
	}

}

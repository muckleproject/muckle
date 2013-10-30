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

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jssupport.AbstractReadOnlyScriptable;

public class TraceObject extends AbstractReadOnlyScriptable {

	final static String ON_CONNECT_START = "onConnectStart";
	final static String ON_CONNECTED = "onConnected";
	final static String ON_SEND = "onSend";
	final static String ON_RETRY = "onRetry";
	final static String ON_RECEIVE = "onReceive";
	final static String ON_ERROR = "onError";
	
	ITraceFunctionStorage storage;
	
	public TraceObject(ITraceFunctionStorage storage) {
		this.storage = storage;
	}

	public void put(String name, Scriptable scope, Object value) {
		if(ON_SEND.equals(name)){
			storage.setSend((Callable) value);
		}
		else if(ON_RECEIVE.equals(name)){
			storage.setReceive((Callable) value);
		}
		else if(ON_RETRY.equals(name)){
			storage.setRetry((Callable) value);
		}
		else if(ON_CONNECTED.equals(name)){
			storage.setConnected((Callable) value);
		}
		else if(ON_CONNECT_START.equals(name)){
			storage.setConnectStart((Callable) value);
		}
		else if(ON_ERROR.equals(name)){
			storage.setError((Callable) value);
		}
	}

	public Object get(String name, Scriptable scope) {
		Object value =  NOT_FOUND;
		if(ON_SEND.equals(name)){
			value = storage.getSend();
		}
		else if(ON_RECEIVE.equals(name)){
			value = storage.getReceive();
		}
		else if(ON_RETRY.equals(name)){
			value = storage.getRetry();
		}
		else if(ON_CONNECTED.equals(name)){
			value = storage.getConnected();
		}
		else if(ON_CONNECT_START.equals(name)){
			value = storage.getConnectStart();
		}
		else if(ON_ERROR.equals(name)){
			value = storage.getError();
		}
		return value;
	}

	public String getClassName() {
		return "Trace";
	}

}

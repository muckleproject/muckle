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


import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jssupport.AbstractReadOnlyScriptable;

public class SessionObject extends AbstractReadOnlyScriptable {

	final static String ON_NEXT_REQUEST = "onNextRequest";
	final static String ON_HANDLE_RESPONSE = "onHandleResponse";
	final static String ON_HANDLE_ERROR = "onHandleError";
	final static String DELAY_CALC = "calcDelay";
	final static String PARAMS = "parameters";
	final static String COMMON = "common";

	IHandlerFunctionsStorage functionsStorage;
	Object params;
	Object common;
	
	public SessionObject(IHandlerFunctionsStorage functionsStorage, Object params, Object common) {
		this.functionsStorage = functionsStorage;
		this.params = params;
		this.common = common;
	}

	public Object get(String name, Scriptable scope) {
		Object value = NOT_FOUND;
		if(ON_NEXT_REQUEST.equals(name)){
			value = functionsStorage.getNextRequestFunction();
		}
		else if(ON_HANDLE_RESPONSE.equals(name)){
			value = functionsStorage.getHandleResponseFunction();
		}
		else if(ON_HANDLE_ERROR.equals(name)){
			value = functionsStorage.getHandleErrorFunction();
		}
		else if(PARAMS.equals(name)){
			value = params;
		}
		else if(COMMON.equals(name)){
			value = common;
		}
		else if(DELAY_CALC.equals(name)){
			value = functionsStorage.getDelayCalculator();
		}
		return value;
	}

	public void put(String name, Scriptable scope, Object value) {
		if(value instanceof Function){
			if(ON_NEXT_REQUEST.equals(name)){
				functionsStorage.setNextRequestFunction((Function) value);
			}
			else if(ON_HANDLE_RESPONSE.equals(name)){
				functionsStorage.setHandleResponseFunction((Function) value);
			}
			else if(ON_HANDLE_ERROR.equals(name)){
				functionsStorage.setHandleErrorFunction((Function) value);
			}
		}
	}

	public String getClassName() {
		return "Session";
	}

}

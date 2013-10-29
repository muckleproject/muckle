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


import java.util.concurrent.atomic.AtomicBoolean;

import org.sh.muckle.ILogger;

public class RuntimeLogger {

	ILogger logger;
	String scriptPath;
	
	AtomicBoolean nextRequestMissingLogged;
	AtomicBoolean handleResponseMissingLogged;
	AtomicBoolean errorHandlerMissingLogged;
	
	public RuntimeLogger(String scriptPath, ILogger logger) {
		this.logger = logger;
		this.scriptPath = scriptPath;

		nextRequestMissingLogged = new AtomicBoolean();
		handleResponseMissingLogged = new AtomicBoolean();
		errorHandlerMissingLogged = new AtomicBoolean();
	}

	public void errorHandlerMissing() {
		if(!errorHandlerMissingLogged.getAndSet(true)){
			warnFunction(SessionObject.ON_HANDLE_ERROR);
		}
	}

	public void nextRequestMissing() {
		if(!nextRequestMissingLogged.getAndSet(true)){
			warnFunction(SessionObject.ON_NEXT_REQUEST);
		}
	}

	public void handleResponseMissing() {
		if(!handleResponseMissingLogged.getAndSet(true)){
			warnFunction(SessionObject.ON_HANDLE_RESPONSE);
		}
	}
	
	public void warning(String s){
		logger.warning(scriptPath + " - " + s);
	}
	
	void warnFunction(String name){
		warning("session object property \"" + name + "\" not set!");
	}

}

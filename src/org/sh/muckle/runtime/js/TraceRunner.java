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

import java.io.File;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.ILogger;
import org.sh.muckle.runtime.EHttpCommsError;
import org.sh.muckle.runtime.IHttpTransactionEventsListener;

public class TraceRunner extends BaseScriptRunner implements IHttpTransactionEventsListener, ITraceFunctionStorage {
	
	Callable onConnectStart;
	Callable onConnected;
	Callable onSend;
	Callable onRetry;
	Callable onReceieve;
	Callable onError;
	Callable getResult;

	public TraceRunner(File script, ScriptCache cache, ILogger logger) throws Exception {
		super(script, cache, logger);
		
		addTraceRuntimeObjects(scope, script.getParentFile(), cache);
		initScript(script, cache);
	}

	//--------- IHttpTransactionEventsListener methods -----------
	
	public void connectStart() {
		runFunction(getConnectStart(), new Object[] {});
	}

	public void connected() {
		runFunction(getConnected(), new Object[] {});
	}

	public void sendStart(HttpRequest request) {
		runFunction(getSend(), new Object[] {new RequestWrapper(request)});
	}

	public void retry(HttpRequest request) {
		runFunction(getRetry(), new Object[] {new RequestWrapper(request)});
	}

	public void responseReceived(HttpResponse response) {
		runFunction(getReceive(), new Object[] {new ResponseWrapper(response)});
	}
	
	public void error(EHttpCommsError error) {
		runFunction(getError(), new Object[] {new HttpErrorWrapper().setError(error)});
	}
	
	//----- end IHttpTransactionEventsListener methods -----------

	//------------ ITraceFunctionStorage --------------------
	
	public void setConnectStart(Callable function) {
		onConnectStart = function;
	}

	public Callable getConnectStart() {
		return onConnectStart;
	}

	public void setConnected(Callable function) {
		onConnected = function;
	}

	public Callable getConnected() {
		return onConnected;
	}

	public void setSend(Callable function) {
		onSend = function;
	}

	public Callable getSend() {
		return onSend;
	}

	public void setRetry(Callable function) {
		onRetry = function;
	}

	public Callable getRetry() {
		return onRetry;
	}

	public void setReceive(Callable function) {
		onReceieve = function;
	}

	public Callable getReceive() {
		return onReceieve;
	}
	
	public void setError(Callable function) {
		onError = function;
	}

	public Callable getError() {
		return onError;
	}
	
	public void setResult(Callable function) {
		getResult = function;
	}

	public Callable getResult() {
		return getResult;
	}
	
	//--------- end ITraceFunctionStorage --------------------
	
	public Object executeGetResult() {
		return runFunction(getResult(), new Object[] {});
	}

	//---------------------------------------------------------
	
	void addTraceRuntimeObjects(Scriptable scope, File parentFile, ScriptCache cache) {
		scope.put("trace", scope, new TraceObject(this));
	}
	
	Object runFunction(final Callable f, final Object[] args){
		if(f != null){
			return ContextFactory.getGlobal().call(new ContextAction() {
				public Object run(Context ctx) {
					return f.call(ctx, scope, null, args);
				}
			});
		}
		else {return null;}
	}


}

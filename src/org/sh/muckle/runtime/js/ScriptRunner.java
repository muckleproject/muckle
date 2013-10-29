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


import java.io.File;
import java.io.IOException;

import org.jboss.netty.handler.codec.http.HttpResponse;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.runtime.EHttpCommsError;
import org.sh.muckle.runtime.EHttpErrorAction;
import org.sh.muckle.runtime.HttpRequestDescriptor;
import org.sh.muckle.runtime.IHttpRunHandler;

public class ScriptRunner extends BaseScriptRunner implements IHttpRunHandler, IHandlerFunctionsStorage {
	
	Callable nextRequest;
	Callable handleResponse;
	Callable handleError;
	DelayCalculator delayCalculator;
	
	ResponseWrapper responseWrapper;
	HttpErrorWrapper errorWrapper;
	
	public ScriptRunner(File script, ScriptCache cache, RuntimeLogger logger, IParamsJsonSource source) throws Exception {
		super(script, cache, logger);
		responseWrapper = new ResponseWrapper();
		errorWrapper = new HttpErrorWrapper();
		delayCalculator = new DelayCalculator();
		
		addRuntimeObjects(scope, script.getParentFile(), cache, source);
		initScript(script, cache);
		
		if(nextRequest == null){
			logger.nextRequestMissing();
			nextRequest = new NoNextRequest();
		}
	}

	//--------------- IHttpRunHandler methods ------------------------
	
	public HttpRequestDescriptor nextRequest() throws IOException {
		HttpRequestDescriptor desc = null;
		
		if(nextRequest != null){
			Object v = ContextFactory.getGlobal().call(new ContextAction() {
				public Object run(Context ctx) {
					return nextRequest.call(ctx, scope, null, new Object[] {});
				}
			});
			
			if(v != null && v instanceof RequestWrapper){
				desc = ((RequestWrapper)v).getRequestDescriptor();
			}
		}
		
		return desc;
	}
	

	public void handleResponse(HttpResponse resp) {
		delayCalculator.setStart();
		
		if(handleResponse != null){
			responseWrapper.setResponse(resp);
			ContextFactory.getGlobal().call(new ContextAction() {
				public Object run(Context ctx) {
					handleResponse.call(ctx, scope, null, new Object[] {responseWrapper});
					return null;
				}
			});
		}
	}

	public EHttpErrorAction handleError(final EHttpCommsError error) {
		delayCalculator.setStart();
		EHttpErrorAction action =  EHttpErrorAction.Abort;
		
		if(handleError != null){
			Object o = ContextFactory.getGlobal().call(new ContextAction() {
				public Object run(Context ctx) {
					errorWrapper.setError(error);
					return handleError.call(ctx, scope, null, new Object[]{errorWrapper});
				}
			});
			if(o != null && o instanceof HttpErrorActionWrapper){
				action = ((HttpErrorActionWrapper)o).getAction();
			}
		}
		else {
			logger.warning("Returned \"Abort\" as handleError function not defined!");
		}
		
		return action;
	}

	//----------- end IHttpRunHandler methods ------------------------
	
	//---------------------- IHandlerFunctionsStorage ----------------

	public Callable getDelayCalculator(){
		return delayCalculator;
	}

	public void setNextRequestFunction(Callable f) {
		this.nextRequest = f;
	}

	public Callable getNextRequestFunction() {
		return nextRequest;
	}

	public void setHandleResponseFunction(Callable f) {
		this.handleResponse = f;
	}

	public Callable getHandleResponseFunction() {
		return handleResponse;
	}

	public void setHandleErrorFunction(Callable f) {
		this.handleError = f;
	}

	public Callable getHandleErrorFunction() {
		return handleError;
	}
	
	//------------------ end IHandlerFunctionsStorage ----------
	
	void addRuntimeObjects(Scriptable scope, File scriptDir, ScriptCache cache, IParamsJsonSource source) throws Exception {
		scope.put(RequestWrapperConstructor.NAME, scope, new RequestWrapperConstructor());
		scope.put("HttpErrorAction", scope, HttpErrorAction.STATIC);
		Object params = source != null ? buildParams(scope, source.getParamsJsonString()) : null;
		Object common = source != null ? buildParams(scope, source.getCommonJsonString()) : null;
		scope.put("session", scope, new SessionObject(this, params, common));
	}


	//-----------------------------------------------

	 class NoNextRequest implements Callable {
		public Object call(Context ctx, Scriptable scope, Scriptable thisObj,	Object[] args) {
			return null;
		}
	}

	 class DelayCalculator implements Callable {
		long start = -1;
		
		void setStart(){
			if(start == -1){
				start = System.currentTimeMillis();
			}
		}
		
		public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
			long millis = 0;
			if(start > -1 && args.length > 0){
				long desired = (long)Context.toNumber(args[0]);
				long elapsed = System.currentTimeMillis() - start;
				millis = desired - elapsed;
			}
			return millis;
		}
	}

}

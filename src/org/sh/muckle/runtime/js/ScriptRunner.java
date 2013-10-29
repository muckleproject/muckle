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
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.json.JsonParser;
import org.mozilla.javascript.json.JsonParser.ParseException;
import org.sh.muckle.jsobjectloaderservice.ClientScriptLoaderService;
import org.sh.muckle.runtime.EHttpCommsError;
import org.sh.muckle.runtime.EHttpErrorAction;
import org.sh.muckle.runtime.HttpRequestDescriptor;
import org.sh.muckle.runtime.IHttpRunHandler;

public class ScriptRunner implements IHttpRunHandler, IHandlerFunctionsStorage {
	
	Scriptable scope;
	
	Callable nextRequest;
	Callable handleResponse;
	Callable handleError;
	DelayCalculator delayCalculator;
	
	ResponseWrapper responseWrapper;
	HttpErrorWrapper errorWrapper;
	
	RuntimeLogger logger;
	
	public ScriptRunner(File script, ScriptCache cache, RuntimeLogger logger, IParamsJsonSource source) throws Exception {
		this.logger = logger;
		responseWrapper = new ResponseWrapper();
		errorWrapper = new HttpErrorWrapper();
		delayCalculator = new DelayCalculator();
		
		scope = buildScope(cache);
		addRuntimeObjects(scope, script.getParentFile(), cache, source);
		initScript(script, cache);
		
		if(nextRequest == null){
			logger.nextRequestMissing();
			nextRequest = new NoNextRequest();
		}
	}

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
	
	public Callable getDelayCalculator(){
		return delayCalculator;
	}

	//---------------------- IHandlerFunctionsStorage ----------

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

	
	Scriptable buildScope(ScriptCache cache){
		return (Scriptable) ContextFactory.getGlobal().call(new ContextAction() {
			public Object run(Context ctx) {
				ctx.setOptimizationLevel(9);
				return ctx.initStandardObjects();
			}
		});
	}
	
	void initScript(File script, ScriptCache cache) throws IOException {
		final Script s = cache.getScript(script);
		ContextFactory.getGlobal().call(new ContextAction() {
			public Object run(Context ctx) {
				return s.exec(ctx, scope);
			}
		});
	}
	
	void addRuntimeObjects(Scriptable scope, File scriptDir, ScriptCache cache, IParamsJsonSource source) throws Exception {
		Require req = new Require(scriptDir, cache);
		scope.put(Require.NAME, scope, req);
		scope.put(ReadFile.NAME, scope, new ReadFile(req));
		scope.put(WriteFile.NAME, scope, WriteFile.STATIC);
		scope.put(RequestWrapperConstructor.NAME, scope, new RequestWrapperConstructor());
		scope.put("HttpErrorAction", scope, HttpErrorAction.STATIC);
		ClientScriptLoaderService.STATIC.loadScriptObjects(scope, scriptDir);
		Object params = source != null ? buildParams(scope, source.getParamsJsonString()) : null;
		Object common = source != null ? buildParams(scope, source.getCommonJsonString()) : null;
		scope.put("session", scope, new SessionObject(this, params, common));
	}
	
	 Object buildParams(final Scriptable scope, final String json) throws IOException {
		Object params = null;
		
		if(json != null){
			try {
				params = ContextFactory.getGlobal().call(new ContextAction() {
					public Object run(Context ctx) {
						try {
							return new JsonParser(ctx, scope).parseValue(json);
						} 
						catch (ParseException e) {
							throw new RuntimeException(e);
						}
					}
				});
			}
			catch(RuntimeException e){
				throw new IOException(e.getMessage());
			}
		}

		return params;
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

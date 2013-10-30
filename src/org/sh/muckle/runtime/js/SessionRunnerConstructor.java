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
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.ILogger;
import org.sh.muckle.jssupport.AbstractObjectConstructor;
import org.sh.muckle.jssupport.AbstractReadOnlyScriptable;
import org.sh.muckle.runtime.AbstractCompletedFinder;
import org.sh.muckle.runtime.ErrorItem;
import org.sh.muckle.runtime.ErrorSummariser;
import org.sh.muckle.runtime.IHttpConnectionInfo;
import org.sh.muckle.runtime.IHttpRunHandler;
import org.sh.muckle.runtime.IHttpRunHandlerFactory;
import org.sh.muckle.runtime.IHttpSequenceStatusChecker;
import org.sh.muckle.runtime.IHttpService;
import org.sh.muckle.runtime.IHttpSessionSequenceStatus;
import org.sh.muckle.runtime.IHttpTimerListenerFactory;
import org.sh.muckle.runtime.SessionsRunner;
import org.sh.muckle.runtime.WaitForeverStatusChecker;

public class SessionRunnerConstructor extends AbstractObjectConstructor {
	
	public final static String NAME = "SessionRunner";
	
	File scriptRoot;
	ILogger errorLogger;
	ScriptCache scriptCache;
	
	public SessionRunnerConstructor(File scriptRoot, ILogger errorLogger, ScriptCache scriptCache) {
		this.scriptRoot = scriptRoot;
		this.errorLogger = errorLogger;
		this.scriptCache = scriptCache;
	}

	public Scriptable construct(Context ctx, Scriptable scope, Object[] args) {
		if(args.length > 0){
			String host = Context.toString(args[0]);
			int port = args.length > 1 ? (int)Context.toNumber(args[1]) : 80;
			boolean secure = args.length > 2 ? Context.toBoolean(args[2]) : false;

			return new TestRunner(host, port, secure, ctx.newArray(scope, 0));
		}
		else {
			throw new RuntimeException(NAME + ": Not enough parameters supplied to constructor.");
		}
	}

	public String getClassName() {
		return NAME;
	}

	
	//---------------------------------------------------
	
	class TestRunner extends AbstractReadOnlyScriptable implements IHttpConnectionInfo, IHttpTimerListenerFactory {

		final static int DEFAULT_CONNECTION_TIMEOUT = 2000;
		
		InetSocketAddress remoteAddress;
		InetSocketAddress proxyAddress;
		String remoteAddressString;
		boolean secure;
		
		ArrayList<IJSHttpTimerListenerFactory> listenerFactories = new ArrayList<>();
		
		Scriptable errors;
		SummaryBuilder summaryProvider;
		DataEventBuilder dataEventProvider;
		
		ArrayList<TraceRunner> tracers = new ArrayList<>();
		int tracerIndex;
		
		Callable statusListener;
		long timeout;
		int connectTimeoutMillis = DEFAULT_CONNECTION_TIMEOUT;
		
		public TestRunner(String host, int port, boolean secure, Scriptable errors) {
			remoteAddress = new InetSocketAddress(host, port);
			this.secure = secure;
			remoteAddressString = port == 80 ? host : host + ":" + port;
			this.errors = errors;
		}

		public Object get(String name, Scriptable start) {
			Object value = NOT_FOUND;
			
			if("run".equals(name)){
				value = new RunMethod();
			}
			else if("getSummaryProvider".equals(name)){
				value = new GetSummaryProviderMethod();
			}
			else if("getDataEventProvider".equals(name)){
				value = new GetDataEventProviderMethod();
			}
			else if("setStatusListener".equals(name)){
				value = new SetStatusListenerMethod();
			}
			else if("setProxy".equals(name)){
				value = new SetProxyMethod();
			}
			else if("setConnectionTimeout".equals(name)){
				value = new SetConnectionTimeoutMethod();
			}
			else if("errors".equals(name)){
				value = errors;
			}
			else if("addTracer".equals(name)){
				value = new AddTracerMethod();
			}
			
			return value;
		}

		public String getClassName() {
			return NAME;
		}

		//--------- IHttpConnectionInfo methods -------------
		
		public InetSocketAddress getRemoteAddress() {
			return remoteAddress;
		}

		public String getHostString() {
			return remoteAddressString;
		}

		public boolean isSecure() {
			return secure;
		}
		
		public boolean isProxied(){
			return proxyAddress != null;
		}
		
		public InetSocketAddress getProxyAddress(){
			return proxyAddress;
		}
		
		//----- end IHttpConnectionInfo methods -------------

		public void addListeneners(IHttpService service) {
			// add any trace listeners
			if(tracerIndex < tracers.size()){
				service.addTransactionEventsListener(tracers.get(tracerIndex++));
			}
			// add timing listeners
			for(int i=0; i<listenerFactories.size(); i++){
				listenerFactories.get(i).addListeneners(service);
			}
		}
		
		//----------------------------------------------------
		
		File resolveScriptToFile(String scriptName){
			File f = new File(scriptName);
			return f.isFile() ? f : new File(scriptRoot, scriptName);
		}
		
		//----------------------------------------------------
		
		class RunMethod implements Callable {

			public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
				if(args.length > 2){
					int count = (int)Context.toNumber(args[0]);
					double startupRate = Context.toNumber(args[1]);
					final File scriptPath = resolveScriptToFile(Context.toString(args[2]));
					
					final IParamsJsonSource jsonSource = getJsonSource(ctx, scope, args, count);
					final IHttpSequenceStatusChecker checker = getChecker(ctx, scope);
					final IHttpRunHandlerFactory factory = new IHttpRunHandlerFactory(){
						public IHttpRunHandler buildHandler() throws Exception  {
							return new ScriptRunner(scriptPath, scriptCache, errorLogger, jsonSource);
						}};
					
					SessionsRunner runner = new SessionsRunner(connectTimeoutMillis);
					
					try {
						tracerIndex = 0;
						IHttpSessionSequenceStatus[] status = runner.run(count, startupRate, TestRunner.this,
								factory, TestRunner.this, checker);
						
						for(int i=0; i<listenerFactories.size(); i++){
							listenerFactories.get(i).completed(ctx, scope, status);
						}
						
						List<ErrorItem> errors = new ErrorSummariser().summarise(status);
						setErrors(ctx, scope, errors);
						
						return new SuccessCounter().count(status);
					} 
					catch (Exception e) {
						throw new RuntimeException(e.getMessage());
					}
					finally {
						runner.shutdown();
					}
				}
				else {
					throw new RuntimeException(NAME + ": Not enough parameters supplied to run method.");
				}
			}
			
			IParamsJsonSource getJsonSource(Context ctx, Scriptable scope, Object[] args, int requiredCount) {
				if(args.length > 3 ){
					String[] params = Stringifier.stringifyArray(ctx, scope, args[3]);
					if(params != null && params.length < requiredCount){
						throw new RuntimeException("Not enough parameters available. Expected " + requiredCount + " but got " + params.length + ".");
					}
					String common = null;
					if(args.length > 4 ){
						common = Stringifier.stringify(ctx, scope, args[4]);
					}
					return new ArrayJSONSource(params, common);
				}
				else {
					return NullJSONSource.STATIC;
				}
			}
			
			void setErrors(Context ctx, Scriptable scope, List<ErrorItem> errorList) {
				Object[] errorObjects = new Object[errorList.size()];
				
				for(int i=0; i<errorList.size(); i++){
					Scriptable obj = ctx.newObject(scope);
					obj.put("message", obj, errorList.get(i).getMessage());
					obj.put("count", obj, errorList.get(i).getCount());
					errorObjects[i] = obj;
				}
				
				errors = ctx.newArray(scope, errorObjects);
			}
			
			IHttpSequenceStatusChecker getChecker(final Context ctx, final Scriptable scope){
				if(statusListener == null){
					return new WaitForeverStatusChecker();
				}
				else {
					return new CallbackSequenceStatusChecker(timeout, ctx, scope, statusListener);
				}
			}
		}

		//----------------------------------------------------

		class GetSummaryProviderMethod implements Callable {

			public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
				if(summaryProvider == null){
					summaryProvider = new SummaryBuilder();
					listenerFactories.add(summaryProvider);
				}
				return summaryProvider;
			}
		}

		//----------------------------------------------------

		class GetDataEventProviderMethod implements Callable {

			public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
				if(dataEventProvider == null){
					dataEventProvider = new DataEventBuilder();
					listenerFactories.add(dataEventProvider);
				}
				return dataEventProvider;
			}
		}
		
		//----------------------------------------------------

		class SetProxyMethod implements Callable {
			public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
				String host = Context.toString(args[0]);
				int port = args.length > 1 ? (int)Context.toNumber(args[1]) : 80;
				proxyAddress = new InetSocketAddress(host, port);
				return null;
			}
		}
		
		//----------------------------------------------------

		class SetConnectionTimeoutMethod implements Callable {
			public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
				connectTimeoutMillis = args.length > 0 ? (int)Context.toNumber(args[0]) : DEFAULT_CONNECTION_TIMEOUT;
				return connectTimeoutMillis;
			}
		}
		
		//----------------------------------------------------

		class SetStatusListenerMethod implements Callable {
			public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
				boolean set = false;
				if(args.length > 1){
					timeout = (long)Context.toNumber(args[0]) * 1000;
					if(args[1] instanceof Callable){
						statusListener = (Callable)args[1];
						set = true;
					}
					else {
						throw new RuntimeException(NAME + ": Second parameter supplied to setStatusListener method is not callable. ");
					}
				}
				else {
					throw new RuntimeException(NAME + ": Not enough parameters supplied to setStatusListener method.");
				}
				return set;
			}
		}
		

		//----------------------------------------------------


		class AddTracerMethod implements Callable {
			public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
				File scriptPath = resolveScriptToFile(Context.toString(args[0]));
				new RuntimeLogger(scriptPath.getName(), errorLogger);
				try {
					tracers.add(new TraceRunner(scriptPath, scriptCache, errorLogger));
				} 
				catch (Exception e) {
					throw new RuntimeException(e);
				}
				return null;
			}
		}
		
		//----------------------------------------------------
		class SuccessCounter extends AbstractCompletedFinder<Object> {

			public int count(IHttpSessionSequenceStatus[] results){
				ArrayList<Object> success = getCompleted(results);
				return success.size();
			}

			protected Object getElementAt(int index) {
				return null;
			}
		}
	}
}

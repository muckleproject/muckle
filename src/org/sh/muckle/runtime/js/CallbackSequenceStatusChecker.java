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


import java.util.List;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jssupport.AbstractReadOnlyScriptable;
import org.sh.muckle.runtime.HttpSessionSequencer;
import org.sh.muckle.runtime.HttpSessionsStatus;
import org.sh.muckle.runtime.IHttpSequenceStatusChecker;

public class CallbackSequenceStatusChecker extends AbstractReadOnlyScriptable implements IHttpSequenceStatusChecker {

	final static String[] IDS = {"started", "finished", "errors", "elapsed", "min", "max"};
	
	Context ctx;
	Scriptable scope;
	Callable callable;
	long timeout;
	
	HttpSessionsStatus status;
	long start = -1;
	long elapsed;

	public CallbackSequenceStatusChecker(long timeout, Context ctx, Scriptable scope,	Callable callable) {
		this.timeout = timeout;
		this.ctx = ctx;
		this.scope = scope;
		this.callable = callable;
	}

	public Object[] getIds(){
		return IDS;
	}
	
	public Object get(String name, Scriptable start) {
		Object value = null;
		if(IDS[0].equals(name)){
			value = status.getStartedCount();
		}
		else if(IDS[1].equals(name)){
			value = status.getFinishedCount();
		}
		else if(IDS[2].equals(name)){
			value = status.getErrorCount();
		}
		else if(IDS[3].equals(name)){
			value = elapsed;
		}
		else if(IDS[4].equals(name)){
			value = status.getMinStep();
		}
		else if(IDS[5].equals(name)){
			value = status.getMaxStep();
		}
		return value;
	}

	public String getClassName() {
		return "SequenceStatusChecker";
	}

	//------------ IHttpSequenceStatusChecker methods -------------
	
	public long getTimeout() {
		if(start < 0){
			start = System.currentTimeMillis();
		}
		return timeout;
	}

	public boolean continueWith(List<HttpSessionSequencer> sessions) {
		elapsed = System.currentTimeMillis() - start;
		status = HttpSessionsStatus.buildFrom(sessions);
		Object result = callable.call(ctx, scope, null, new Object[]{this});
		return (result instanceof Boolean) ? ((Boolean)result).booleanValue() : true;
	}

	//-------- end IHttpSequenceStatusChecker methods -------------
}

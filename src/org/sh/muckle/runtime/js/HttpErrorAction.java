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
import org.sh.muckle.runtime.EHttpErrorAction;

public class HttpErrorAction extends AbstractReadOnlyScriptable {

	final static Scriptable STATIC = new HttpErrorAction();

	final static Scriptable CONTINUE = new HttpErrorActionWrapper(EHttpErrorAction.Continue);
	final static Scriptable RETRY = new HttpErrorActionWrapper(EHttpErrorAction.Retry);
	final static Scriptable ABORT = new HttpErrorActionWrapper(EHttpErrorAction.Abort);
	
	public Object get(String name, Scriptable scope) {
		Object property = null;
		if("RETRY".equals(name)){
			property = RETRY;
		}
		else if("CONTINUE".equals(name)){
			property = CONTINUE;
		}
		else if("ABORT".equals(name)){
			property = ABORT;
		}
		return property;
	}

	public String getClassName() {
		return "HttpErrorAction";
	}

}

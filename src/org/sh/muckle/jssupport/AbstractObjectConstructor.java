package org.sh.muckle.jssupport;

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


import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

abstract public class AbstractObjectConstructor extends AbstractReadOnlyScriptable implements Function {

	public Object get(String arg0, Scriptable arg1) {
		return null;
	}

	public Object call(Context arg0, Scriptable arg1, Scriptable arg2, Object[] arg3) {
		return null;
	}
	
	protected String getArgsMessage() {
		throw new RuntimeException(getClassName() + ": Not enough parameters supplied to constructor.");
	}

}

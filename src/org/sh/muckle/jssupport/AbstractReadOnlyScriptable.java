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


import org.mozilla.javascript.Scriptable;

public abstract class AbstractReadOnlyScriptable implements Scriptable {

	final static Object[] EMPTY = {};
	
	public void delete(String arg0) {
	}

	public void delete(int arg0) {
	}

	public Object get(int arg0, Scriptable arg1) {
		return null;
	}

	public Object getDefaultValue(Class<?> hint) {
		return String.class == hint ? getClassName() : null;
	}

	public Object[] getIds() {
		return EMPTY;
	}

	public Scriptable getParentScope() {
		return null;
	}

	public Scriptable getPrototype() {
		return null;
	}

	public boolean has(String arg0, Scriptable arg1) {
		return false;
	}

	public boolean has(int arg0, Scriptable arg1) {
		return false;
	}

	public boolean hasInstance(Scriptable arg0) {
		return false;
	}

	public void put(String arg0, Scriptable arg1, Object arg2) {
	}

	public void put(int arg0, Scriptable arg1, Object arg2) {
	}

	public void setParentScope(Scriptable arg0) {
	}

	public void setPrototype(Scriptable arg0) {
	}

}

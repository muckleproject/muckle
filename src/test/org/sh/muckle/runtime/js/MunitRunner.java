package test.org.sh.muckle.runtime.js;

/*
*	Copyright 2013 The Muckle Project
*
*	Licensed under the Apache License, Version 2.0 (the "License");
*	you may not use this file except in compliance with the License.
*	You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*	Unless required by applicable law or agreed to in writing, software
*	distributed under the License is distributed on an "AS IS" BASIS,
*	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*	See the License for the specific language governing permissions and
*	limitations under the License.
*/


import org.mozilla.javascript.Scriptable;
import org.sh.muckle.ILogger;
import org.sh.muckle.jsprint.Print;
import org.sh.muckle.jsprint.Println;
import org.sh.muckle.runtime.SysLogger;
import org.sh.muckle.runtime.js.Bootstrap;

public class MunitRunner extends Bootstrap {

	public MunitRunner(ILogger logger) {
		super(logger);
	}
	
	protected void putAdditionalObjectsInScope(Scriptable scope){
		scope.put(Println.NAME, scope, Println.STATIC);
		scope.put(Print.NAME, scope, Print.STATIC);
	}

	public static void main(String[] args){
		new MunitRunner(new SysLogger()).run(args);
	}
}

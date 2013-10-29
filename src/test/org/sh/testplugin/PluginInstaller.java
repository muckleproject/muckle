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

package test.org.sh.testplugin;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jsobjectloaderservice.IClientScriptObjectService;
import org.sh.muckle.jsobjectloaderservice.IControlScriptObjectService;
import org.sh.muckle.jsobjectloaderservice.IInitialisationSupport;


public class PluginInstaller implements IClientScriptObjectService, IControlScriptObjectService {

	public void installObjects(IInitialisationSupport support) throws Exception {
		support.addToScope("testplugin", new TestFunction());
		support.getFileRoot();
	}
	
	class TestFunction implements Callable {

		public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
			return null;
		}
		
	}

}

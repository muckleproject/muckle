package org.sh.muckle.jsobjectloaderservice;

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
import java.util.Iterator;
import java.util.ServiceLoader;

import org.mozilla.javascript.Scriptable;

abstract public class AbstractScriptObjectLoaderService<T extends IInstallableObjectService>  {
	
	final ServiceLoader<T> loader = ServiceLoader.load(getClassToLoad());

	public void loadScriptObjects(final Scriptable scope, final File root) throws Exception {
		final IInitialisationSupport support = new IInitialisationSupport() {
			public File getFileRoot() {
				return root;
			}
			
			public void addToScope(String name, Object obj) {
				scope.put(name, scope, obj);
			}
		};
		
		Iterator<T> installables = loader.iterator();
		while(installables.hasNext()){
			installables.next().installObjects(support);
		}
	}
	
	abstract Class<T> getClassToLoad();
}

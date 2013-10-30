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
package org.sh.muckle.runtime.js;

import org.mozilla.javascript.Callable;

public interface ITraceFunctionStorage {

	public void setConnectStart(Callable function);
	public Callable getConnectStart();

	public void setConnected(Callable function);
	public Callable getConnected();

	public void setSend(Callable function);
	public Callable getSend();

	public void setRetry(Callable function);
	public Callable getRetry();

	public void setReceive(Callable function);
	public Callable getReceive();

	public void setError(Callable function);
	public Callable getError();

	public void setResult(Callable function);
	public Callable getResult();
	
}

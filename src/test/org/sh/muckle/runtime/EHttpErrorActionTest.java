package test.org.sh.muckle.runtime;

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


import org.sh.muckle.runtime.EHttpErrorAction;
import org.sh.muckle.runtime.IHttpErrorActionVisitor;

import junit.framework.TestCase;

public class EHttpErrorActionTest extends TestCase implements IHttpErrorActionVisitor {

	public void testForEmmaCodeCoverageBug(){
		EHttpErrorAction.values();
		EHttpErrorAction.valueOf("Continue");
	}
	
	public void testContinue(){
		assertEquals(1, EHttpErrorAction.Continue.accept(this));
	}
	
	public void testAbort(){
		assertEquals(2, EHttpErrorAction.Abort.accept(this));
	}
	
	public void testRetry(){
		assertEquals(3, EHttpErrorAction.Retry.accept(this));
	}
	

	public Object visitContinue() {
		return 1;
	}

	public Object visitAbort() {
		return 2;
	}

	public Object visitRetry() {
		return 3;
	}

}

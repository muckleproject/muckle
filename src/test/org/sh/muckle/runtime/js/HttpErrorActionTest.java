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


import org.sh.muckle.runtime.EHttpErrorAction;
import org.sh.muckle.runtime.js.HttpErrorAction;
import org.sh.muckle.runtime.js.HttpErrorActionWrapper;

import junit.framework.TestCase;

public class HttpErrorActionTest extends TestCase {

	HttpErrorAction action;
	
	public void testClassName(){
		assertEquals("HttpErrorAction", action.getClassName());
	}
	
	public void testGetRetry(){
		assertTrue(EHttpErrorAction.Retry == ((HttpErrorActionWrapper) action.get("RETRY", null)).getAction());
	}
	
	public void testGetAbort(){
		assertTrue(EHttpErrorAction.Abort == ((HttpErrorActionWrapper) action.get("ABORT", null)).getAction());
	}
	
	public void testGetContinue(){
		assertTrue(EHttpErrorAction.Continue == ((HttpErrorActionWrapper) action.get("CONTINUE", null)).getAction());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		action = new HttpErrorAction();
	}

}

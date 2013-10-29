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
import org.sh.muckle.runtime.js.HttpErrorActionWrapper;

import junit.framework.TestCase;

public class HttpErrorActionWrapperTest extends TestCase {

	HttpErrorActionWrapper wrapper;
	
	public void testClassName(){
		assertEquals("Abort", wrapper.getClassName());
	}
	
	public void testGet(){
		assertNull(wrapper.get("", null));
	}
	
	public void testGetAction(){
		assertTrue(EHttpErrorAction.Abort == wrapper.getAction());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		wrapper = new HttpErrorActionWrapper(EHttpErrorAction.Abort);
	}

}

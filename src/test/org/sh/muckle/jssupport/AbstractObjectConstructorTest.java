package test.org.sh.muckle.jssupport;

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


import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jssupport.AbstractObjectConstructor;

import junit.framework.TestCase;

public class AbstractObjectConstructorTest extends TestCase {

	Function constructor;
	
	public void testGet() {
		assertNull(constructor.get("", null));
	}

	public void testCall() {
		assertNull(constructor.call(null, null, null, null));
	}

	protected void setUp() throws Exception {
		super.setUp();
		constructor = new Constructor();
	}

	
	class Constructor extends AbstractObjectConstructor {

		public Scriptable construct(Context arg0, Scriptable arg1, Object[] arg2) {
			return null;
		}

		public String getClassName() {
			return null;
		}
		
	}
}

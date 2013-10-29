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


import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jssupport.AbstractReadOnlyScriptable;

import junit.framework.TestCase;

public class AbstractReadOnlyScriptableTest extends TestCase {

	Scriptable ts;
	
	public void testDeleteString() {
		ts.delete("s");
	}

	public void testDeleteInt() {
		ts.delete(0);
	}

	public void testGetIndex() {
		assertNull(ts.get(0, null));
	}

	public void testGetDefaultValueString() {
		assertEquals("classname", ts.getDefaultValue(String.class));
	}

	public void testGetDefaultValueNotStringClass() {
		assertNull(ts.getDefaultValue(Number.class));
	}

	public void testGetIds() {
		assertEquals(0, ts.getIds().length);
	}

	public void testGetParentScope() {
		assertNull(ts.getParentScope());
	}

	public void testGetPrototype() {
		assertNull(ts.getPrototype());
	}

	public void testHasString() {
		assertFalse(ts.has("", null));
	}

	public void testHasInt() {
		assertFalse(ts.has(0, null));
	}

	public void testHasInstance() {
		assertFalse(ts.hasInstance(null));
	}

	public void testPutString() {
		ts.put("", null, null);
	}

	public void testPutInt() {
		ts.put(1,  null,  null);
	}

	public void testSetParentScope() {
		ts.setParentScope(null);
	}

	public void testSetPrototype() {
		ts.setPrototype(null);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		ts = new TestScriptable();
	}

	class TestScriptable extends AbstractReadOnlyScriptable {

		public Object get(String arg0, Scriptable arg1) {
			return null;
		}

		public String getClassName() {
			return "classname";
		}
		
	}
}

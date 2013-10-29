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


import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.runtime.js.Stringifier;

public class StringifierTest extends ScriptTestCase {
	
	public void testNull(){
		assertNull(runScript("strArray(null)"));
	}

	public void testNotArray(){
		try {
			runScript("strArray('abc')");
			fail();
		}
		catch(RuntimeException e){}
	}

	public void testNewArray(){
		String[] array = (String[])runScript("strArray(new Array())");
		assertEquals(0, array.length);
	}

	public void testUnpopulatedArray(){
		try {
			runScript("strArray(new Array(10))");
			fail();
		}
		catch(RuntimeException e){}
	}

	public void testArrayOfNulls(){
		try {
			runScript("strArray([null, null])");
			fail();
		}
		catch(RuntimeException e){}
	}

	public void testEmptyArray(){
		String[] array = (String[])runScript("strArray([])");
		assertEquals(0, array.length);
	}
	
	public void testSingleArrayContents(){
		String[] array = (String[])runScript("strArray([[]])");
		assertEquals(1, array.length);
		assertEquals("[]", array[0]);
	}
	
	public void testSingleObjectContents(){
		String[] array = (String[])runScript("strArray([{}])");
		assertEquals(1, array.length);
		assertEquals("{}", array[0]);
	}
	
	public void testMultipleArrayContents(){
		String[] array = (String[])runScript("strArray([[1], [2], [3]])");
		assertEquals(3, array.length);
		assertEquals("[1]", array[0]);
		assertEquals("[2]", array[1]);
		assertEquals("[3]", array[2]);
	}
	
	public void testEmptyObject(){
		String s = (String)runScript("str({})");
		assertEquals("{}", s);
	}
	
	public void testObject(){
		String s = (String)runScript("str({p:1})");
		assertEquals("{\"p\":1}", s);
	}
	
	public void testNullObject(){
		assertNull((String)runScript("str(null)"));
	}
	
	public void testNotObject(){
		try {
			runScript("str('abc')");
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testConstruct(){
		new Stringifier();
	}
	
	//-------------------------------------------
	
	protected void setUp() throws Exception {
		super.setUp();
		addToScope(new ArrayWrapper(), "strArray");
		addToScope(new ObjectWrapper(), "str");
	}
	
	class ArrayWrapper implements Callable {
		public Object call(Context ctx, Scriptable scope, Scriptable thisObj,	Object[] args) {
			return Stringifier.stringifyArray(ctx, scope, args[0]);
		}
		
	}
	
	class ObjectWrapper implements Callable {
		public Object call(Context ctx, Scriptable scope, Scriptable thisObj,	Object[] args) {
			return Stringifier.stringify(ctx, scope, args[0]);
		}
		
	}

}

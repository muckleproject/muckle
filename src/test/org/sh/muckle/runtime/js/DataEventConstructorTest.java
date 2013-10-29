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


import org.sh.muckle.runtime.js.DataEventConstructor;


public class DataEventConstructorTest extends ScriptTestCase {

	DataEventConstructor constructor;
	
	public void testgetClassName() {
		assertEquals("DataEvent", constructor.getClassName());
	}
	
	public void testConstructNoParams(){
		try {
			runScript("new StartEnd()");
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testStartIsStringNotNumber(){
		assertEquals(0, ((Long)runScript("var se = new StartEnd('not a number', '33'); se.start")).longValue());
	}
	
	public void testStartIsString(){
		assertEquals(22, ((Long)runScript("var se = new StartEnd('22', '33'); se.start")).longValue());
	}
	
	public void testEndIsString(){
		assertEquals(33, ((Long)runScript("var se = new StartEnd('22', '33'); se.end")).longValue());
	}
	
	public void testStartIsNumber(){
		assertEquals(22, ((Long)runScript("var se = new StartEnd(22.2, 33.3); se.start")).longValue());
	}
	
	public void testEndIsNumber(){
		assertEquals(33, ((Long)runScript("var se = new StartEnd(22.2, 33.3); se.end")).longValue());
	}
	
	public void testLengthNotDefined(){
		assertEquals(0, ((Long)runScript("var se = new StartEnd(22.2, 33.3); se.contentLength")).longValue());
	}
	
	public void testLengthDefined(){
		assertEquals(100, ((Long)runScript("var se = new StartEnd(22.2, 33.3, 100); se.contentLength")).longValue());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		constructor = DataEventConstructor.STATIC;
		addToScope(constructor, "StartEnd");
	}

}

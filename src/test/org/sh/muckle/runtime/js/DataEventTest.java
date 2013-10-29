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


import org.sh.muckle.runtime.js.DataEvent;

public class DataEventTest extends ScriptTestCase {
	
	DataEvent se;
	
	public void testGetClassName(){
		assertEquals("DataEvent", se.getClassName());
	}
	
	public void testGetStart(){
		assertEquals(22, ((Long)runScript("se.start")).longValue());
	}

	public void testGetEnd(){
		assertEquals(33, ((Long)runScript("se.end")).longValue());
	}

	public void testGetContentLength(){
		assertEquals(10, ((Long)runScript("se.contentLength")).longValue());
	}

	public void testElapsed(){
		assertEquals(11, ((Long)runScript("se.elapsed();")).longValue());
	}
	
	public void testStringify(){
		String json = (String) runScript("JSON.stringify(se)");
		assertTrue(json.contains("\"start\":22"));
		assertTrue(json.contains("\"end\":33"));
	}

	protected void setUp() throws Exception {
		super.setUp();
		se = new DataEvent(22, 33, 10);
		addToScope(se, "se");
	}

}

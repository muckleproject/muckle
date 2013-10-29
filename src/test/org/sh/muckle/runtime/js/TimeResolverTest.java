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


import org.sh.muckle.runtime.js.TimeResolver;

public class TimeResolverTest extends ScriptTestCase {

	TimeResolver tr;
	long startTime;
	long nanoTime;
	
	public void testGetClassName(){
		assertEquals("TimeResolver", tr.getClassName());
	}
	
	public void testGetMillisForNoParameterSupplied(){
		Long m = (Long)runScript("tr.getMillisFor()");
		assertEquals(startTime, m.longValue());
	}
	
	public void testGetMillisFor(){
		Long m = (Long)runScript("tr.getMillisFor(-96000000)");
		assertEquals(startTime + 3, m.longValue());
	}
	
	public void testStringify(){
		String s = (String)runScript("JSON.stringify(tr)");
		assertTrue(s.contains("\"startTimestamp\":" + startTime));
		assertTrue(s.contains("\"nanoTimestamp\":" + nanoTime));
	}
	
	public void testGetMillisMultipleCalls(){
		Long m = (Long)runScript("tr.getMillisFor(-99000000); tr.getMillisFor(-96000000)");
		assertEquals(startTime + 3, m.longValue());
	}
	
	public void testGetNonExistantProperty(){
		assertNull(runScript("tr.z"));
	}
	
	public void testConstructorNoParams(){
		try {
			runScript("new TimeResolver()");
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testConstructorWithParams(){
		Long m = (Long)runScript("new TimeResolver(2, 0).getMillisFor(1000000)");
		assertEquals(3, m.longValue());
	}
	
	//-----------------------------------------------------------
	
	protected void setUp() throws Exception {
		super.setUp();
		startTime = System.currentTimeMillis();
		nanoTime = -99000000;
		tr = new TimeResolver(startTime, nanoTime);
		
		addToScope(tr, "tr");
		addToScope(TimeResolver.STATIC, TimeResolver.NAME);
	}

}

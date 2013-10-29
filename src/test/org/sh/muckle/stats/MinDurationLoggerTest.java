package test.org.sh.muckle.stats;

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


import org.sh.muckle.stats.MinDurationLogger;
import org.sh.muckle.stats.StartEndMarker;

import junit.framework.TestCase;

public class MinDurationLoggerTest extends TestCase {

	MinDurationLogger logger;
	
	public void testHasValue(){
		assertFalse(logger.hasValue());
	}
	
	public void testNoValuesThrowsException(){
		try {
			logger.getValue();
			fail();
		}
		catch(IllegalStateException e){}
	}
	
	public void testOneValue(){
		StartEndMarker m = new StartEndMarker();
		m.setEndTime(System.currentTimeMillis());
		logger.compareWith(m);
		assertTrue(m == logger.getValue());
	}
	
	public void testMultiValuesSmallerFirst(){
		StartEndMarker smaller = new StartEndMarker();
		smaller.setEndTime(System.currentTimeMillis());
		logger.compareWith(smaller);

		StartEndMarker larger = new StartEndMarker();
		larger.setEndTime(System.currentTimeMillis()+10);
		logger.compareWith(larger);
		
		assertTrue(smaller == logger.getValue());
	}
	
	public void testMultiValueslargerFirst(){
		StartEndMarker larger = new StartEndMarker();
		larger.setEndTime(System.currentTimeMillis()+10);
		logger.compareWith(larger);
		
		StartEndMarker smaller = new StartEndMarker();
		smaller.setEndTime(System.currentTimeMillis());
		logger.compareWith(smaller);

		assertTrue(smaller == logger.getValue());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		logger = new MinDurationLogger();
	}

}

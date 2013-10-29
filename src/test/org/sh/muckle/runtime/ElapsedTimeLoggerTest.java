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


import org.sh.muckle.runtime.ElapsedTimeLogger;
import org.sh.muckle.stats.StartEndMarker;

import junit.framework.TestCase;

public class ElapsedTimeLoggerTest extends TestCase {

	ElapsedTimeLogger logger;
	
	public void testGetValueDefault(){
		StartEndMarker m = logger.getValue();
		assertEquals(0, m.getElapsedTime());
	}
	
	public void testHasValue(){
		assertFalse(logger.hasValue());
	}
	
	public void testGetValueEndWithNoStart(){
		logger.responseReceived(1,0);
		assertFalse(logger.hasValue());
	}
	
	public void testGetValueSingleEvent(){
		logger.connectStart(0);
		logger.responseReceived(1,0);
		assertTrue(logger.hasValue());
		assertEquals(1, logger.getValue().getElapsedTime());
	}
	
	public void testGetValueMultipleEvent(){
		logger.connectStart(0);
		logger.responseReceived(1,0);
		logger.connectStart(10);
		logger.responseReceived(14,0);
		assertEquals(14, logger.getValue().getElapsedTime());
	}
	
	public void testUnusedMethods(){
		logger.sendStart(0);
		logger.connected(0);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		logger = new ElapsedTimeLogger();
	}

}

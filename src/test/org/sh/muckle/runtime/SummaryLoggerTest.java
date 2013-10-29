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


import org.sh.muckle.runtime.SummaryLogger;

import junit.framework.TestCase;

public class SummaryLoggerTest extends TestCase {

	SummaryLogger logger;
	
	public void testGetElapsedNoEventsLogged(){
		try {
			logger.getElapsed();
			fail();
		}
		catch(IllegalStateException e){}
	}
	
	public void testGetMinStepNoEventsLogged(){
		try {
			logger.getMinStep();
			fail();
		}
		catch(IllegalStateException e){}
	}
	
	public void testGetMaxStepNoEventsLogged(){
		try {
			logger.getMaxStep();
			fail();
		}
		catch(IllegalStateException e){}
	}
	
	public void testGetAverageStepNoEventsLogged(){
		try {
			logger.getAverageStep();
			fail();
		}
		catch(IllegalStateException e){}
	}
	
	public void testGetMinNoEventsLogged(){
		try {
			logger.getMinConnect();
			fail();
		}
		catch(IllegalStateException e){}
	}
	
	public void testGetMaxNoEventsLogged(){
		try {
			logger.getMaxConnect();
			fail();
		}
		catch(IllegalStateException e){}
	}
	
	public void testGetAverageNoEventsLogged(){
		try {
			logger.getAverageConnect();
			fail();
		}
		catch(IllegalStateException e){}
	}
	
	public void testHasValuesNoEventsLogger(){
		assertFalse(logger.hasValues());
	}
	
	public void testHasValuesOnlyConnect(){
		logger.connectStart(0);
		logger.connected(2);
		assertFalse(logger.hasValues());
	}
	
	public void testHasValuesOnlyStep(){
		logger.sendStart(2);
		logger.responseReceived(3,2);
		assertFalse(logger.hasValues());
	}
	
	public void testGetValuesOneEvent(){
		logger.connectStart(0);
		logger.connected(2);
		logger.sendStart(2);
		logger.responseReceived(3,1);
		assertTrue(logger.hasValues());
		
		assertEquals(1, logger.getMinStep().getElapsedTime());
		assertEquals(1, logger.getMaxStep().getElapsedTime());
		assertEquals(1.0, logger.getAverageStep());
		
		assertEquals(2, logger.getMinConnect().getElapsedTime());
		assertEquals(2, logger.getMaxConnect().getElapsedTime());
		assertEquals(2.0, logger.getAverageConnect());

		assertEquals(3, logger.getElapsed().getElapsedTime());
	}
	
	public void testGetValuesMultipleEvent(){
		logger.connectStart(0);
		logger.connected(1);
		logger.sendStart(0);
		logger.responseReceived(1,1);
		logger.sendStart(2);
		logger.responseReceived(5,0);
		assertTrue(logger.hasValues());
		assertEquals(1, logger.getMinStep().getElapsedTime());
		assertEquals(3, logger.getMaxStep().getElapsedTime());
		assertEquals(5, logger.getElapsed().getElapsedTime());
		assertEquals(2.0, logger.getAverageStep());
	}
	
	protected void setUp() throws Exception {
		logger = new SummaryLogger();
		super.setUp();
	}

}

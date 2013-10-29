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


import org.sh.muckle.runtime.StepLogger;
import org.sh.muckle.runtime.TCPConnectionEvent;
import org.sh.muckle.runtime.TCPDataSelector;
import org.sh.muckle.runtime.TCPSendReceieveEvent;

import junit.framework.TestCase;

public class TCPDataSelectorTest extends TestCase {

	TCPDataSelector sel;
	StepLogger logger;
	
	public void testEmpty(){
		assertEquals(0, sel.selectFrom(logger).size());
	}
	
	public void testOnlyConnection(){
		logger.add(new TCPConnectionEvent(0));
		logger.add(new TCPConnectionEvent(0));
		assertEquals(0, sel.selectFrom(logger).size());
	}
	
	public void testOnlyData(){
		logger.add(new TCPSendReceieveEvent(0,0,0));
		logger.add(new TCPSendReceieveEvent(0,0,0));
		assertEquals(2, sel.selectFrom(logger).size());
		
		logger.clear();
		logger.add(new TCPSendReceieveEvent(0,0,0));
		assertEquals(1, sel.selectFrom(logger).size());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		logger = new StepLogger();
		sel = new TCPDataSelector();
	}

}

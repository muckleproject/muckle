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
import org.sh.muckle.runtime.TCPSendReceieveEvent;

import junit.framework.TestCase;

public class StepLoggerTest extends TestCase {

	StepLogger sl;
	
	public void testNoEnd(){
		sl.sendStart(0);
		assertEquals(0, sl.size());
	}
	
	public void testSingleConnectEntry(){
		sl.connectStart(0);
		sl.connected(99);
		assertEquals(1, sl.size());
		assertEquals(99, ((TCPConnectionEvent)sl.get(0)).getElapsedTime());
	}
	
	public void testSingleSendRecieveEntry(){
		sl.sendStart(0);
		sl.responseReceived(99, 1);
		assertEquals(1, sl.size());
		assertEquals(99, ((TCPSendReceieveEvent)sl.get(0)).getElapsedTime());
		assertEquals(1, ((TCPSendReceieveEvent)sl.get(0)).getContentLength());
	}
	
	public void testSingleConnectSendRecieveEntry(){
		sl.connectStart(0);
		sl.connected(99);
		sl.sendStart(0);
		sl.responseReceived(98, 2);
		assertEquals(2, sl.size());
		assertEquals(99, ((TCPConnectionEvent)sl.get(0)).getElapsedTime());
		assertEquals(98, ((TCPSendReceieveEvent)sl.get(1)).getElapsedTime());
		assertEquals(2, ((TCPSendReceieveEvent)sl.get(1)).getContentLength());
	}
	
	public void testMultipleSendRecieveEntry(){
		sl.sendStart(0);
		sl.responseReceived(99, 1);
		sl.sendStart(0);
		sl.responseReceived(98, 2);
		sl.sendStart(0);
		sl.responseReceived(97, 3);
		assertEquals(3, sl.size());
		assertEquals(99, ((TCPSendReceieveEvent)sl.get(0)).getElapsedTime());
		assertEquals(98, ((TCPSendReceieveEvent)sl.get(1)).getElapsedTime());
		assertEquals(97, ((TCPSendReceieveEvent)sl.get(2)).getElapsedTime());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		sl = new StepLogger();
	}

}

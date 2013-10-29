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


import org.sh.muckle.runtime.IHttpTransactionEventVisitor;
import org.sh.muckle.runtime.TCPConnectionEvent;
import org.sh.muckle.runtime.TCPSendReceieveEvent;

import junit.framework.TestCase;

public class TCPConnectionEventTest extends TestCase implements IHttpTransactionEventVisitor {

	TCPConnectionEvent ev;
	
	public void testVisit(){
		assertNotNull(ev.accept(this));
	}
	
	public void testStartEndTime(){
		assertEquals(22,  ev.getStartTime());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		ev = new TCPConnectionEvent(22);
	}

	public Object visit(TCPConnectionEvent ev) {
		return this;
	}

	public Object visit(TCPSendReceieveEvent ev) {
		return null;
	}

}

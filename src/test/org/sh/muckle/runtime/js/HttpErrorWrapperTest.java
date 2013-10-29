package test.org.sh.muckle.runtime.js;

/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at http://mozilla.org/MPL/2.0/. 
*/


import org.sh.muckle.runtime.EHttpCommsError;
import org.sh.muckle.runtime.js.HttpErrorWrapper;

import junit.framework.TestCase;

public class HttpErrorWrapperTest extends TestCase {

	HttpErrorWrapper wrapper;
	
	public void testConnectionError(){
		wrapper.setError(EHttpCommsError.Connect);
		assertTrue((Boolean)wrapper.get("isConnect", null));
		assertFalse((Boolean)wrapper.get("isSend", null));
		assertFalse((Boolean)wrapper.get("isReceive", null));
	}
	
	public void testSendError(){
		wrapper.setError(EHttpCommsError.Send);
		assertFalse((Boolean)wrapper.get("isConnect", null));
		assertTrue((Boolean)wrapper.get("isSend", null));
		assertFalse((Boolean)wrapper.get("isReceive", null));
	}
	
	public void testReceiveError(){
		wrapper.setError(EHttpCommsError.Receive);
		assertFalse((Boolean)wrapper.get("isConnect", null));
		assertFalse((Boolean)wrapper.get("isSend", null));
		assertTrue((Boolean)wrapper.get("isReceive", null));
	}
	
	public void testgetClassName(){
		assertEquals("HttpError", wrapper.getClassName());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		wrapper = new HttpErrorWrapper();
	}

}

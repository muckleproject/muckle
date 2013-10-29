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


import org.sh.muckle.runtime.EHttpCommsError;
import org.sh.muckle.runtime.ICommsErrorVisitor;

import junit.framework.TestCase;

public class EHttpCommsErrorTest extends TestCase implements ICommsErrorVisitor {

	public void testForEmmaCodeCoverageBug(){
		EHttpCommsError.values();
		EHttpCommsError.valueOf("Connect");
	}
	
	public void testConnect(){
		assertEquals(1, EHttpCommsError.Connect.accept(this));
	}
	
	public void testSend(){
		assertEquals(2, EHttpCommsError.Send.accept(this));
	}
	
	public void testRecieve(){
		assertEquals(3, EHttpCommsError.Receive.accept(this));
	}
	
	public Object visitConnect() {
		return 1;
	}

	public Object visitSend() {
		return 2;
	}

	public Object visitReceive() {
		return 3;
	}
	

}

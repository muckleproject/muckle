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


import org.sh.muckle.runtime.ErrorItem;

import junit.framework.TestCase;

public class ErrorItemTest extends TestCase {

	final static String S1 = "s 1";
	
	ErrorItem item;
	
	public void testGetConstructedValues(){
		assertEquals(S1, item.getMessage());
		assertEquals(1, item.getCount());
	}
	
	public void testInCount(){
		item.inc();
		assertEquals(S1, item.getMessage());
		assertEquals(2, item.getCount());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		item = new ErrorItem(S1);
		
	}

}

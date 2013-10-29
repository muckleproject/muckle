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


import org.sh.muckle.runtime.AbstractCompletedFinder;
import org.sh.muckle.runtime.HttpSessionHadError;
import org.sh.muckle.runtime.HttpSessionOK;
import org.sh.muckle.runtime.IHttpSessionSequenceStatus;

import junit.framework.TestCase;

public class AbstractCompletedBuilderTest extends TestCase {

	TestLister tl;
	
	public void testEmptyResults(){
		assertEquals(0, tl.getCompleted(new IHttpSessionSequenceStatus[] {}).size());
	}
	
	public void testPassResults(){
		assertEquals(1, tl.getCompleted(new IHttpSessionSequenceStatus[] {HttpSessionOK.STATIC}).size());
	}
	
	public void testFailResults(){
		assertEquals(0, tl.getCompleted(new IHttpSessionSequenceStatus[] {new HttpSessionHadError(null)}).size());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		tl = new TestLister();
	}
	
	class TestLister extends AbstractCompletedFinder<Object> {
		protected Object getElementAt(int index) {
			return null;
		}
	}

}

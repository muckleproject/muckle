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


import java.util.List;

import org.sh.muckle.runtime.ErrorItem;
import org.sh.muckle.runtime.ErrorSummariser;
import org.sh.muckle.runtime.HttpSessionHadError;
import org.sh.muckle.runtime.HttpSessionOK;
import org.sh.muckle.runtime.IHttpSessionSequenceStatus;

import junit.framework.TestCase;

public class ErrorSummariserTest extends TestCase {
	
	final static String S1 = "s 1";
	final static String S1_AGAIN = "s 1";
	final static String S2 = "s 2";
	

	ErrorSummariser es;
	
	public void testEmpty(){
		assertEquals(0, es.summarise(new IHttpSessionSequenceStatus[] {}).size());
	}
	
	public void testAllOk(){
		assertEquals(0, es.summarise(new IHttpSessionSequenceStatus[] {HttpSessionOK.STATIC, HttpSessionOK.STATIC}).size());
	}
	
	public void testSingleFailure(){
		IHttpSessionSequenceStatus[] results =  { new HttpSessionHadError(new RuntimeException(S1))};
		List<ErrorItem> errors = es.summarise(results);
		assertEquals(1, errors.size());
		assertEquals(S1, errors.get(0).getMessage());
		assertEquals(1, errors.get(0).getCount());
	}
	
	public void testMultipleFailuresSameMessage(){
		IHttpSessionSequenceStatus[] results =  { 
				new HttpSessionHadError(new RuntimeException(S1))
				,new HttpSessionHadError(new RuntimeException(S1_AGAIN))
				};
		List<ErrorItem> errors = es.summarise(results);
		assertEquals(1, errors.size());
		assertEquals(S1, errors.get(0).getMessage());
		assertEquals(2, errors.get(0).getCount());
	}
	
	public void testMultipleFailuresDifferentMessage(){
		IHttpSessionSequenceStatus[] results =  { 
				new HttpSessionHadError(new RuntimeException(S1))
				,new HttpSessionHadError(new RuntimeException(S2))
				};
		List<ErrorItem> errors = es.summarise(results);
		assertEquals(2, errors.size());
		assertEquals(S1, errors.get(0).getMessage());
		assertEquals(1, errors.get(0).getCount());
		assertEquals(S2, errors.get(1).getMessage());
		assertEquals(1, errors.get(1).getCount());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		es = new ErrorSummariser();
	}

}

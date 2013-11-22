package test.org.sh.muckle.runtime.js;

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


import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.mozilla.javascript.Function;
import org.sh.muckle.runtime.js.IHandlerFunctionsStorage;
import org.sh.muckle.runtime.js.SessionObject;


public class SessionObjectTest extends MockObjectTestCase {

	final static String ON_NEXT_REQUEST = "onNextRequest";
	final static String ON_HANDLE_RESPONSE = "onHandleResponse";
	final static String ON_HANDLE_ERROR = "onHandleError";
	
	IHandlerFunctionsStorage functions;
	SessionObject so;
	Object params;
	Object common;
	
	public void testGetName(){
		assertEquals("Session", so.getClassName());
	}
	
	public void testGetNextRequest(){
		checking(new Expectations(){{
			one(functions).getNextRequestFunction(); will(returnValue(null));
		}});
		assertNull(so.get(ON_NEXT_REQUEST, null));
	}
	
	public void testSetNextRequest(){
		final Function f = mock(Function.class);
		checking(new Expectations(){{
			one(functions).setNextRequestFunction(f);
		}});
		so.put(ON_NEXT_REQUEST, null, f);
	}
	
	public void testGetHandleResponse(){
		checking(new Expectations(){{
			one(functions).getHandleResponseFunction(); will(returnValue(null));
		}});
		assertNull(so.get(ON_HANDLE_RESPONSE, null));
	}
	
	public void testSetHandleResponse(){
		final Function f = mock(Function.class);
		checking(new Expectations(){{
			one(functions).setHandleResponseFunction(f);
		}});
		so.put(ON_HANDLE_RESPONSE, null, f);
	}
	
	public void testGetHandleError(){
		checking(new Expectations(){{
			one(functions).getHandleErrorFunction(); will(returnValue(null));
		}});
		assertNull(so.get(ON_HANDLE_ERROR, null));
	}
	
	public void testSetHandleError(){
		final Function f = mock(Function.class);
		checking(new Expectations(){{
			one(functions).setHandleErrorFunction(f);
		}});
		so.put(ON_HANDLE_ERROR, null, f);
	}
	
	public void testGetParameters(){
		assertTrue(params == so.get("parameters", null));
	}
	
	public void testGetCommon(){
		assertTrue(common == so.get("common", null));
	}
	
	public void testGetDelayCalc(){
		checking(new Expectations(){{
			one(functions).getDelayCalculator(); will(returnValue(null));
		}});
		assertNull(so.get("calcDelay", null));
	}
	
	public void testGetAutoRetriesSetter(){
		checking(new Expectations(){{
			one(functions).getAutoRetriesSetter(); will(returnValue(null));
		}});
		assertNull(so.get("setAutoRetries", null));
	}
	

	protected void setUp() throws Exception {
		super.setUp();
		functions = mock(IHandlerFunctionsStorage.class);
		params = new Object();
		common = new Object();
		so = new SessionObject(functions, params, common);
	}

}

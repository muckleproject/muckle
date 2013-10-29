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
import org.sh.muckle.ILogger;
import org.sh.muckle.runtime.js.RuntimeLogger;


public class RuntimeLoggerTest extends MockObjectTestCase {

	RuntimeLogger rl;
	ILogger logger;
	
	public void testLogNextRequestMissing(){
		checking(new Expectations(){{
			one(logger).warning(with(any(String.class)));
		}});
		
		rl.nextRequestMissing();
		rl.nextRequestMissing();
	}
	
	
	public void testLogHandleResponseMissing(){
		checking(new Expectations(){{
			one(logger).warning(with(any(String.class)));
		}});
		
		rl.handleResponseMissing();
		rl.handleResponseMissing();
	}
	
	
	public void testLogHandleErrorMissing(){
		checking(new Expectations(){{
			one(logger).warning(with(any(String.class)));
		}});
		
		rl.errorHandlerMissing();
		rl.errorHandlerMissing();
	}
	
	public void testWaring(){
		checking(new Expectations(){{
			one(logger).warning(with(any(String.class)));
		}});
		
		rl.warning("");
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		logger = mock(ILogger.class);
		rl = new RuntimeLogger("path", logger);
	}

}

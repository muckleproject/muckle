package test.org.sh.muckle.jsobjectloaderservice;

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
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jsobjectloaderservice.ClientScriptLoaderService;


public class ClientScriptLoaderServiceTest extends MockObjectTestCase {

	ClientScriptLoaderService service;
	
	public void testLoad() throws Exception {
		final Scriptable s = mock(Scriptable.class);
		checking(new Expectations(){{
			one(s).put(with(any(String.class)), with(any(Scriptable.class)), with(any(Callable.class)));
		}});
		service.loadScriptObjects(s, null);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		service =  ClientScriptLoaderService.STATIC;
	}

}

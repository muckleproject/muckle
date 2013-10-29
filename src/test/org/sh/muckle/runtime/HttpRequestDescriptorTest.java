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


import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.sh.muckle.runtime.HttpRequestDescriptor;


public class HttpRequestDescriptorTest extends MockObjectTestCase {

	HttpRequestDescriptor desc;
	HttpRequest req;
	
	public void testGetRequest(){
		assertTrue(req == desc.getRequest());
	}
	
	public void testGetDelayDefault(){
		assertEquals(0, desc.getDelay());
	}
	
	public void testGetDelay(){
		assertFalse(desc.hasDelay());
		
		desc.setDelay(201);
		assertTrue(desc.hasDelay());
		assertEquals(201, desc.getDelay());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		req = mock(HttpRequest.class);
		desc = new HttpRequestDescriptor(req);
	}

}

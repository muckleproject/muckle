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


import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.sh.muckle.runtime.CookieManager;


public class CookieManagerTest extends MockObjectTestCase {

	CookieManager cm;
	HttpRequest req;
	HttpResponse resp;
	List<String> setCookieHeaders;
	
	
	public void testSetCookiesEmpty(){
		//this is validated by virtue of the fact that it does nothing to the request. ie there are no expectations of the mocked req.
		cm.setRequestCookies(req);
	}

	public void testGetCookiesNoneInResponse(){
		assertEquals(0, cm.getCookiesFromResponse(resp, req));
	}
	
	public void testSingleNameValueOnly(){
		setCookieHeaders.add("name=TEST");
		assertEquals(1, cm.getCookiesFromResponse(resp, req));
		
		checking(new Expectations(){{
			one(req).setHeader(HttpHeaders.Names.COOKIE, "name=TEST");
		}});
		cm.setRequestCookies(req);
	}
	
	public void testMulipleNameValueOnly(){
		setCookieHeaders.add("name=TEST");
		setCookieHeaders.add("name1=TEST1");
		assertEquals(2, cm.getCookiesFromResponse(resp, req));
		
		checking(new Expectations(){{
			one(req).setHeader(HttpHeaders.Names.COOKIE, "name=TEST; name1=TEST1");
		}});
		cm.setRequestCookies(req);
	}
	
	public void testMulipleSameNameDifferentPath(){
		setCookieHeaders.add("name=TEST");
		setCookieHeaders.add("name=TEST; path=/fred");
		assertEquals(2, cm.getCookiesFromResponse(resp, req));
		
		checking(new Expectations(){{
			one(req).setHeader(HttpHeaders.Names.COOKIE, "name=TEST");
		}});
		cm.setRequestCookies(req);
	}
	
	//----------------------------------------------------
	
	protected void setUp() throws Exception {
		super.setUp();
		
		setCookieHeaders = new ArrayList<>();
		req = mock(HttpRequest.class);
		resp = mock(HttpResponse.class);
		cm = new CookieManager();
		
		checking(new Expectations(){{
			allowing(resp).getHeaders(HttpHeaders.Names.SET_COOKIE); will(returnValue(setCookieHeaders));
		}});
	}

}

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


import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.sh.muckle.Version;
import org.sh.muckle.runtime.js.RequestWrapper;
import org.sh.muckle.runtime.js.RequestWrapperConstructor;


public class RequestWrapperConstructorTest extends ScriptTestCase {

	public void testConstructNoParams(){
		Object req = runScript("new HttpRequest()");
		RequestWrapper rw = (RequestWrapper)req;
		assertEquals("/", rw.getRequestDescriptor().getRequest().getUri());
		assertEquals(HttpMethod.GET, rw.getRequestDescriptor().getRequest().getMethod());
	}

	public void testDefaultUserAgent(){
		Object req = runScript("new HttpRequest()");
		RequestWrapper rw = (RequestWrapper)req;
		assertEquals(Version.GetVersion(), rw.getRequestDescriptor().getRequest().getHeader("User-Agent"));
	}
	
	public void testDefaultVersion(){
		Object req = runScript("new HttpRequest()");
		RequestWrapper rw = (RequestWrapper)req;
		assertEquals(HttpVersion.HTTP_1_1, rw.getRequestDescriptor().getRequest().getProtocolVersion());
	}
	
	public void testConstructWithURI(){
		Object req = runScript("new HttpRequest('/abc/xyz')");
		RequestWrapper rw = (RequestWrapper)req;
		assertEquals("/abc/xyz", rw.getRequestDescriptor().getRequest().getUri());
		assertEquals(HttpMethod.GET, rw.getRequestDescriptor().getRequest().getMethod());
	}
	
	
	public void testConstructWithURIAndMethod(){
		Object req = runScript("new HttpRequest('/abc/xyz', 'PoSt')");
		RequestWrapper rw = (RequestWrapper)req;
		assertEquals("/abc/xyz", rw.getRequestDescriptor().getRequest().getUri());
		assertEquals(HttpMethod.POST, rw.getRequestDescriptor().getRequest().getMethod());
	}
	
	public void testConstructWithURIAndMethodNotStrings(){
		Object req = runScript("new HttpRequest(1, true)");
		RequestWrapper rw = (RequestWrapper)req;
		assertEquals("1", rw.getRequestDescriptor().getRequest().getUri());
		assertEquals("TRUE", rw.getRequestDescriptor().getRequest().getMethod().getName());
	}
	
	public void testGetName(){
		assertEquals("HttpRequest", new RequestWrapperConstructor().getClassName());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		addToScope(new RequestWrapperConstructor(), "HttpRequest");
	}

}

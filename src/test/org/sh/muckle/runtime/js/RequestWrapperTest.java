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


import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.runtime.HttpRequestDescriptor;
import org.sh.muckle.runtime.js.RequestWrapper;


public class RequestWrapperTest extends ScriptTestCase {

	RequestWrapper wrapper;
	
	public void testGetName(){
		assertEquals("HttpRequest", wrapper.getClassName());
	}
	
	public void testGetDefaultValue(){
		assertEquals("HttpRequest", wrapper.getDefaultValue(String.class));
	}
	
	public void testGetDescriptorConstructorValues(){
		HttpRequestDescriptor desc = wrapper.getRequestDescriptor();
		HttpRequest req = desc.getRequest();
		assertEquals(HttpVersion.HTTP_1_1, req.getProtocolVersion());
		assertEquals(HttpMethod.GET, req.getMethod());
		assertEquals("/", req.getUri());
		assertFalse(desc.hasDelay());
	}
	
	public void testSetGetDelayString(){
		wrapper.put("delay", null, "123");
		assertTrue(wrapper.getRequestDescriptor().hasDelay());
		assertEquals(123, wrapper.getRequestDescriptor().getDelay());
		assertEquals(123, wrapper.get("delay", null));
	}
	
	public void testSetGetDelayIntegerNumber(){
		wrapper.put("delay", null, new Integer(123));
		assertTrue(wrapper.getRequestDescriptor().hasDelay());
		assertEquals(123, wrapper.getRequestDescriptor().getDelay());
	}
	
	public void testSetGetDelayNumber(){
		wrapper.put("delay", null, new Double(123));
		assertTrue(wrapper.getRequestDescriptor().hasDelay());
		assertEquals(123, wrapper.getRequestDescriptor().getDelay());
	}
	
	public void testGetSetURI(){
		assertEquals("/" , wrapper.get("uri", null));
		wrapper.put("uri", null, "/abc/def");
		assertEquals("/abc/def" , wrapper.get("uri", null));
	}
	
	public void testGetSetMethod(){
		assertEquals("GET" , wrapper.get("method", null));
		wrapper.put("method", null, "DELETE");
		assertEquals("DELETE" , wrapper.get("method", null));
	}
	
	public void testSetHeaderNotEnoughParameters(){
		runScript("req.setHeader('ABC')");
		assertEquals(null, wrapper.getRequestDescriptor().getRequest().getHeader("ABC"));
	}
	
	public void testSetHeader(){
		runScript("req.setHeader('ABC', 'abc')");
		assertEquals("abc", wrapper.getRequestDescriptor().getRequest().getHeader("ABC"));
	}
	
	public void testSetContentNoParams(){
		runScript("req.setContent()");
		assertEquals(0, wrapper.getRequestDescriptor().getRequest().getContent().readableBytes());
	}
	
	public void testSetContentDefaultEncoding(){
		runScript("req.setContent('ABC', 'type')");
		assertEquals(3, wrapper.getRequestDescriptor().getRequest().getContent().readableBytes());
		assertEquals("type; charset=UTF-8", wrapper.getRequestDescriptor().getRequest().getHeader(HttpHeaders.Names.CONTENT_TYPE));
		assertEquals("3", wrapper.getRequestDescriptor().getRequest().getHeader(HttpHeaders.Names.CONTENT_LENGTH));
	}
	
	public void testSetContentEncodingSupplied(){
		runScript("req.setContent('ABC', 'type', 'utf-16')");
		assertEquals(8, wrapper.getRequestDescriptor().getRequest().getContent().readableBytes());
		assertEquals("type; charset=UTF-16", wrapper.getRequestDescriptor().getRequest().getHeader(HttpHeaders.Names.CONTENT_TYPE));
		assertEquals("8", wrapper.getRequestDescriptor().getRequest().getHeader(HttpHeaders.Names.CONTENT_LENGTH));
	}
	
	public void testSetContentBadEncoding(){
		try {
			runScript("req.setContent('ABC', 'type', 'BAD_ENCODING')");
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testGetNotPresent(){
		assertEquals(Scriptable.NOT_FOUND, wrapper.get("NOT_THERE", null));
	}
	
	public void testPutNotPresent(){
		wrapper.put("NOT_THERE", null, null);
	}
	
	public void testConstructWithRequest(){
		wrapper = new RequestWrapper(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.DELETE, "/"));
		assertTrue(HttpMethod.DELETE == wrapper.getRequestDescriptor().getRequest().getMethod());
	}
	

	protected void setUp() throws Exception {
		super.setUp();
		wrapper = new RequestWrapper(HttpMethod.GET, "/");
		addToScope(wrapper, "req");
	}

}

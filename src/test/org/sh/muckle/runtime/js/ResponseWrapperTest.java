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


import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrappedException;
import org.sh.muckle.runtime.js.ResponseWrapper;


public class ResponseWrapperTest extends ScriptTestCase {

	HttpResponse resp;
	ResponseWrapper wrapper;
	Mockery mocker;
	
	public void testGetClassName(){
		assertEquals("ResponseWrapper", wrapper.getClassName());
	}
	
	public void testGetUnknown(){
		assertTrue(Context.getUndefinedValue() == runScript("resp['ssss']"));
	}
	
	public void testGetStatus(){
		mocker.checking(new Expectations(){{
			one(resp).getStatus(); will(returnValue(HttpResponseStatus.NOT_FOUND));
		}});
		
		assertEquals(404, runScript("resp.status"));
	}
	
	public void testGetContentBadCharset(){
		mocker.checking(new Expectations(){{
			one(resp).getContent(); will(returnValue(ChannelBuffers.EMPTY_BUFFER));
		}});
		
		try {
			runScript("resp.getContent('BAD_NAME')");
			fail();
		}
		catch(WrappedException e){}
	}
	
	public void testGetContentNullBuffer(){
		mocker.checking(new Expectations(){{
			one(resp).getContent(); will(returnValue(null));
		}});
		
		assertEquals(0, ((String)runScript("resp.getContent()")).length());
	}
	
	public void testGetContentEmptyNoCharset(){
		mocker.checking(new Expectations(){{
			one(resp).getContent(); will(returnValue(ChannelBuffers.EMPTY_BUFFER));
		}});
		
		assertEquals(0, ((String)runScript("resp.getContent()")).length());
	}
	
	public void testGetContentDefaultIsUTF8() throws Exception {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
		osw.write("\u20ac");
		osw.close();
		ChannelBuffers.wrappedBuffer(baos.toByteArray());
		
		mocker.checking(new Expectations(){{
			one(resp).getContent(); will(returnValue(ChannelBuffers.wrappedBuffer(baos.toByteArray())));
		}});
		
		String c = (String)runScript("resp.getContent()");
		
		assertTrue("\u20ac".equals(c));
	}
	
	public void testGetContentUTF8() throws Exception {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
		osw.write("\u20ac");
		osw.close();
		ChannelBuffers.wrappedBuffer(baos.toByteArray());
		
		mocker.checking(new Expectations(){{
			one(resp).getContent(); will(returnValue(ChannelBuffers.wrappedBuffer(baos.toByteArray())));
		}});
		
		String c = (String)runScript("resp.getContent('utf-8')");
		
		assertTrue("\u20ac".equals(c));
	}
	
	public void testGetContentNotDefault() throws Exception {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-16");
		osw.write("\u20ac");
		osw.close();
		ChannelBuffers.wrappedBuffer(baos.toByteArray());
		
		mocker.checking(new Expectations(){{
			one(resp).getContent(); will(returnValue(ChannelBuffers.wrappedBuffer(baos.toByteArray())));
		}});
		
		String c = (String)runScript("resp.getContent('utf-16')");
		
		assertTrue("\u20ac".equals(c));
	}
	
	public void testGetContentBytesEmpty(){
		mocker.checking(new Expectations(){{
			one(resp).getContent(); will(returnValue(ChannelBuffers.EMPTY_BUFFER));
		}});
		
		Scriptable s = (Scriptable)runScript("resp.getContentBytes()");
		assertEquals(0.0, s.get("length", null));
	}
	
	public void testGetContentBytesNullBuffer() throws Exception {
		mocker.checking(new Expectations(){{
			one(resp).getContent(); will(returnValue(null));
		}});
		
		Scriptable s = (Scriptable)runScript("resp.getContentBytes()");
		assertEquals(0.0, s.get("length", null));
	}
	
	public void testGetContentBytes() throws Exception {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
		osw.write("\u20ac ");
		osw.close();
		ChannelBuffers.wrappedBuffer(baos.toByteArray());
		
		mocker.checking(new Expectations(){{
			one(resp).getContent(); will(returnValue(ChannelBuffers.wrappedBuffer(baos.toByteArray())));
		}});
		
		Scriptable s = (Scriptable)runScript("resp.getContentBytes()");
		assertEquals(4.0, s.get("length", null));
		assertEquals(-30, s.get(0, null));
		assertEquals(-126, s.get(1, null));
		assertEquals(-84, s.get(2, null));
		assertEquals(32, s.get(3, null));
	}
	
	public void testGetContentBytesIsArray() throws Exception {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
		osw.write("\u20ac ");
		osw.close();
		ChannelBuffers.wrappedBuffer(baos.toByteArray());
		
		mocker.checking(new Expectations(){{
			one(resp).getContent(); will(returnValue(ChannelBuffers.wrappedBuffer(baos.toByteArray())));
		}});
		
		assertEquals(32, runScript("resp.getContentBytes()[3]"));
	}
	
	public void testGetHeadersEmpty() {
		mocker.checking(new Expectations(){{
			one(resp).getHeaders(); will(returnValue(new ArrayList<Map.Entry<String, String>>()));
		}});
		Scriptable s = (Scriptable)runScript("resp.getHeaders()");
		assertEquals(0.0, s.get("length", null));
	}
	
	public void testGetHeaders() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name1", "v1");
		map.put("name2", "v2");
		final List<Map.Entry<String, String>> headersList = new ArrayList<Map.Entry<String, String>>();
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while(it.hasNext()){
			headersList.add(it.next());
		}
		
		mocker.checking(new Expectations(){{
			allowing(resp).getHeaders(); will(returnValue(headersList));
		}});
		
		Scriptable s = (Scriptable)runScript("resp.getHeaders()");
		assertEquals(2.0, s.get("length", null));
		
		String json = (String)runScript("JSON.stringify(resp.getHeaders())");
		assertTrue(json.startsWith("[{"));
		assertTrue(json.endsWith("}]"));
		assertTrue(json.contains("{\"name1\":\"v1\"}"));
		assertTrue(json.contains("{\"name2\":\"v2\"}"));
	}
	
	public void testGetHeaderNotFound(){
		final String NAME = "name";
		mocker.checking(new Expectations(){{
			one(resp).getHeader(NAME); will(returnValue(null));
		}});
		
		assertNull(runScript("resp.getHeader('name')"));
	}
	
	public void testGetHeaderNull(){
		mocker.checking(new Expectations(){{
			one(resp).getHeader("null"); will(returnValue(null));
		}});
		
		assertNull(runScript("resp.getHeader(null)"));
	}
	
	public void testGetHeader(){
		final String NAME = "name";
		mocker.checking(new Expectations(){{
			one(resp).getHeader(NAME); will(returnValue("value"));
		}});
		
		assertEquals("value", runScript("resp.getHeader('name')"));
	}
	
	
	protected void setUp() throws Exception {
		super.setUp();
		mocker = new Mockery();
		resp = mocker.mock(HttpResponse.class);
		wrapper = new ResponseWrapper();
		wrapper.setResponse(resp);
		addToScope(wrapper, "resp");
	}

	protected void tearDown() throws Exception {
		mocker.assertIsSatisfied();
	}

}

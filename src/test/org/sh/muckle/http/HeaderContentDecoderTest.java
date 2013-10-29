package test.org.sh.muckle.http;

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


import java.util.Map;

import org.sh.muckle.http.HeaderContentDecoder;

import junit.framework.TestCase;

public class HeaderContentDecoderTest extends TestCase {

	public void testIsEmptyNull(){
		HeaderContentDecoder dec = new HeaderContentDecoder(null);
		assertTrue(dec.isEmpty());
	}

	public void testIsEmptyEmptyString(){
		HeaderContentDecoder dec = new HeaderContentDecoder("");
		assertTrue(dec.isEmpty());
	}

	public void testHasParamsFalse(){
		HeaderContentDecoder dec = new HeaderContentDecoder("no-cache");
		assertFalse(dec.isEmpty());
		assertFalse(dec.hasParameters());
	}

	public void testHasParamsTrue(){
		HeaderContentDecoder dec = new HeaderContentDecoder(" UserID=JohnDoe; Max-Age=3600; Version=1");
		assertFalse(dec.isEmpty());
		assertTrue(dec.hasParameters());
	}
	
	public void testGetMapEmpty(){
		HeaderContentDecoder dec = new HeaderContentDecoder(null);
		assertEquals(0, dec.getParametersMap().size());
	}
	
	public void testGetMapAllAreKeyValue(){
		HeaderContentDecoder dec = new HeaderContentDecoder(" UserID=JohnDoe; Max-Age=3600; Version=1");
		Map<String, String> map = dec.getParametersMap();
		assertEquals(3, map.size());
		assertNotNull(map.get("UserID"));
		assertNotNull(map.get("Max-Age"));
		assertNotNull(map.get("Version"));
	}
	
	public void testGetMapNotAllAreKeyValue(){
		HeaderContentDecoder dec = new HeaderContentDecoder(" text/plain; Max-Age=3600; Version=1");
		Map<String, String> map = dec.getParametersMap();
		assertEquals(2, map.size());
		assertNotNull(map.get("Max-Age"));
		assertNotNull(map.get("Version"));
	}
	
	public void testGetIndexOf(){
		HeaderContentDecoder dec = new HeaderContentDecoder(" text/plain; Max-Age=3600; Version=1");
		assertEquals(0, dec.getParameterIndexOf("text/"));
	}
	
	public void testGetIndexOfNotPresent(){
		HeaderContentDecoder dec = new HeaderContentDecoder(" text/plain; Max-Age=3600; Version=1");
		assertEquals(-1, dec.getParameterIndexOf("NOT_THERE"));
	}

}

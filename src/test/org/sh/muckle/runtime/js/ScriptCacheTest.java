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


import java.io.File;
import java.io.IOException;

import org.mozilla.javascript.Script;
import org.sh.muckle.runtime.js.ScriptCache;

import test.org.sh.TempFileHelper;
import junit.framework.TestCase;


public class ScriptCacheTest extends TestCase {

	ScriptCache cache;
	TempFileHelper helper;
	File testRoot;
	
	public void testNonExistantFile() throws IOException{
		try {
			cache.getScript(new File("NOT_PRESENT_"));
			fail();
		}
		catch(IOException e){}
	}
	
	public void testScriptSyntaxError() throws IOException{
		File f = helper.copyResource("/test/testfiles/syntax_error.js", testRoot, "bad", ".js");
		try {
			cache.getScript(f);
			fail();
		}
		catch(IOException e){
			assertTrue(e.getMessage().toLowerCase().contains("missing"));
		}
	}
	
	public void testMultigetScript() throws IOException {
		File f = helper.copyResource("/test/testfiles/lib1.js", testRoot, "lib1", ".js");
		Script first = cache.getScript(f);
		assertTrue(first == cache.getScript(f));
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		cache = new ScriptCache();
		helper = new TempFileHelper();
		testRoot = helper.createDir(this.getClass().getSimpleName());
	}
	
	protected void tearDown() throws Exception {
		helper.cleanUp();
		super.tearDown();
	}

}

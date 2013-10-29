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
import java.io.FileWriter;
import java.io.IOException;

import org.mozilla.javascript.WrappedException;
import org.sh.muckle.runtime.js.Require;
import org.sh.muckle.runtime.js.ScriptCache;

import test.org.sh.TempFileHelper;

public class RequireTest extends ScriptTestCase {

	Require require;
	TempFileHelper fileHelper;
	File testRoot;
	ScriptCache cache;
	
	public void testPathDoesNotExist(){
		try {
			runScript("require('');");
			fail();
		}
		catch(WrappedException e){
			assertTrue(e.getMessage().toLowerCase().contains("access"));
		}
	}
	
	public void testNoPath(){
		assertNull(runScript("require();"));
	}
	
	public void testSyntaxError() throws IOException{
		try {
			File lib = fileHelper.copyResource("/test/testfiles/syntax_error.js", testRoot, "syntax_error", ".js");
			runScript("require('" + lib.getCanonicalPath().replace("\\","/") + "');\n name;");
			fail();
		}
		catch(WrappedException e){			
			assertTrue(e.getMessage().toLowerCase().contains("missing :"));
		}
	}
	
	public void testLoadSingleLibAbsolutePath() throws IOException{
		File lib = fileHelper.copyResource("/test/testfiles/lib1.js", testRoot, "lib1", ".js");
		Object result = runScript("require('" + lib.getCanonicalPath().replace("\\","/") + "');\n name;");
		assertEquals("lib1", result.toString());
	}
	
	public void testLoadMoreThanOnce() throws IOException{
		File lib = fileHelper.copyResource("/test/testfiles/xinc.js", testRoot, "xinc", ".js");
		String name = lib.getCanonicalPath().replace("\\","/");
		Object result = runScript("var x = 0; require('" + name + "', '" + name + "');\n x;");
		assertEquals("1.0", result.toString());
	}
	
	public void testMuliple() throws IOException{
		File lib = fileHelper.copyResource("/test/testfiles/lib1.js", testRoot, "lib1", ".js");
		String libName = lib.getCanonicalPath().replace("\\","/");
		File inc = fileHelper.copyResource("/test/testfiles/xinc.js", testRoot, "xinc", ".js");
		String incName = inc.getCanonicalPath().replace("\\","/");

		Object result = runScript("var x=22;require('" +  libName + "','" +  incName + "');\n x + name;");
		assertEquals("23lib1", result.toString());
	}
	
	public void testNested() throws IOException{
		File top = fileHelper.createFile(testRoot, "top", ".js");
		String topName = top.getCanonicalPath().replace("\\","/");
		File nested1 = fileHelper.createFile(testRoot, "nested1", ".js");
		String nested1Name = nested1.getCanonicalPath().replace("\\","/");
		File nested2 = fileHelper.createFile(testRoot, "nested2", ".js");
		String nested2Name = nested2.getCanonicalPath().replace("\\","/");
		
		write("require('" + nested1Name +"')", top);
		write("require('" + nested2Name +"')", nested1);
		write("var x='nested2'; x", nested2);
		
		Object result = runScript("require('" +  topName + "');\n x;");
		assertEquals("nested2", result.toString());
	}
	
	public void testLoadSingleLibRelativePath() throws IOException{
		fileHelper.copyResource("/test/testfiles/lib1.js", testRoot, "lib1", ".js");
		Object result = runScript("require('lib1.js');\n name;");
		assertEquals("lib1", result.toString());
	}
	
	public void testLoadSingleLibRelativePathWithDots() throws IOException{
		fileHelper.copyResource("/test/testfiles/lib1.js", testRoot, "lib1", ".js");
		Object result = runScript("require('./lib1.js');\n name;");
		assertEquals("lib1", result.toString());
	}
	
	public void testNestedRelativeInSameDirectory() throws IOException{
		File top = fileHelper.createFile(testRoot, "top", ".js");
		File nested1 = fileHelper.createFile(testRoot, "nested1", ".js");
		File nested2 = fileHelper.createFile(testRoot, "nested2", ".js");
		
		write("require('nested1.js')", top);
		write("require('nested2.js')", nested1);
		write("var x='nested2';", nested2);
		
		Object result = runScript("require('top.js');\n x;");
		assertEquals("nested2", result.toString());
	}
	
	public void testNestedRelativeInDifferentDirectory() throws IOException{
		File top = fileHelper.createFile(testRoot, "top", ".js");
		File level1 = fileHelper.createDir(testRoot, "level1");
		File nested1 = fileHelper.createFile(level1, "nested1", ".js");
		File nested2 = fileHelper.createFile(level1, "nested2", ".js");
		
		write("require('level1/nested1.js')", top);
		write("require('nested2.js')", nested1);
		write("var x='nested2';", nested2);
		
		Object result = runScript("require('top.js');\n x;");
		assertEquals("nested2", result.toString());
	}
	
	public void testNestedRelativeAllInDifferentDirectory() throws IOException{
		File top = fileHelper.createFile(testRoot, "top", ".js");
		File level1 = fileHelper.createDir(testRoot, "level1");
		File nested1 = fileHelper.createFile(level1, "nested1", ".js");
		File level2 = fileHelper.createDir(level1, "level2");
		File nested2 = fileHelper.createFile(level2, "nested2", ".js");
		
		write("require('level1/nested1.js');", top);
		write("require('level2/nested2.js')", nested1);
		write("var x='nested2';", nested2);
		
		Object result = runScript("require('top.js');\n x;");
		assertEquals("nested2", result.toString());
	}
	
	public void testGetClassName(){
		assertEquals("require", require.getClassName());
	}
	
	//--------------------------------------------
	
	protected void setUp() throws Exception {
		super.setUp();
		
		fileHelper = new TempFileHelper();
		testRoot = fileHelper.createDir(this.getClass().getSimpleName());

		cache = new ScriptCache();
		require = new Require(testRoot, cache);
		addToScope(require, "require");
	}
	
	protected void tearDown() throws Exception {
		fileHelper.cleanUp();
		super.tearDown();
	}
	
	void write(String contents, File dest) throws IOException{
		FileWriter fw = new FileWriter(dest);
		fw.write(contents);
		fw.close();
	}

}

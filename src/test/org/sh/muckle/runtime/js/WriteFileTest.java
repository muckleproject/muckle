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
import java.io.FileReader;
import java.io.IOException;

import org.sh.muckle.runtime.js.WriteFile;

import test.org.sh.TempFileHelper;

public class WriteFileTest extends ScriptTestCase {
	
	TempFileHelper helper;
	File testRoot;
	
	public void testNoParams(){
		assertEquals(0, (long)runScript("writeFile()"));
	}
	
	public void testRelativeFileNamedSubdirectoryDowsNotExist() {
		try {
			runScript("writeFile('NOT_THERE/test.txt')");
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testRelativeFileNameNoContent() throws IOException {
		File f = helper.createFile(testRoot, "test", ".txt");
		f.delete();
		assertFalse(f.exists());
		assertEquals(0, (long)runScript("writeFile('test.txt')"));
		assertTrue(f.exists());
	}
	
	public void testRelativeFileNameWithContent() throws IOException {
		File f = helper.createFile(testRoot, "test", ".txt");
		f.delete();
		assertFalse(f.exists());
		assertEquals(4, (long)runScript("writeFile('test.txt', '1234')"));
		assertTrue(f.exists());
		assertEquals("1234", readFile(f));
	}
	
	public void testAbsoluteFileNameWithContent() throws IOException {
		File f = helper.createFile(testRoot, "test", ".txt");
		f.delete();
		assertFalse(f.exists());
		assertEquals(4, (long)runScript("writeFile('" + f.getAbsolutePath().replace('\\', '/') + "', '1234')"));
		assertTrue(f.exists());
		assertEquals("1234", readFile(f));
	}

	public void testRelativeFileNameWithContentAndEncoding() throws IOException {
		File f = helper.createFile(testRoot, "test", ".txt");
		f.delete();
		assertFalse(f.exists());
		assertEquals(10, (long)runScript("writeFile('test.txt', '1234', 'utf-16')"));
		assertTrue(f.exists());
	}

	public void testUnknownEncoding() throws IOException {
		File f = helper.createFile(testRoot, "test", ".txt");
		f.delete();
		assertFalse(f.exists());
		try {
			runScript("writeFile('test.txt', '1234', 'NOT_AN_ENCODING')");
			fail();
		}
		catch(RuntimeException e){}
	}

	//---------------------------------------------
	
	protected void setUp() throws Exception {
		super.setUp();
		helper = new TempFileHelper();
		testRoot = helper.createDir(this);
		System.setProperty("user.dir", testRoot.getAbsolutePath());
		addToScope(WriteFile.STATIC, WriteFile.NAME);
	}

	protected void tearDown() throws Exception {
		helper.cleanUp();
		super.tearDown();
	}
	
	String readFile(File f) throws IOException {
		StringBuilder sb = new StringBuilder();
		FileReader fr = new FileReader(f);
		char[] chars = new char[1000];
		int charsRead;
		while((charsRead = fr.read(chars)) > 0){
			sb.append(chars, 0, charsRead);
		}
		fr.close();
		return sb.toString();
	}

}

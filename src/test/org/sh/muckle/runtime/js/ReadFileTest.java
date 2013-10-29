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
import java.io.FileOutputStream;
import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.sh.muckle.runtime.js.IFileResolver;
import org.sh.muckle.runtime.js.ReadFile;

import test.org.sh.TempFileHelper;

public class ReadFileTest extends ScriptTestCase {
	
	Mockery mocker;
	IFileResolver res;
	TempFileHelper helper;
	File testRoot;
	
	public void testReadNoFilePath(){
		assertEquals("", runScript("readFile()"));
	}
	
	public void testReadNonExistantFile(){
		try {
			final File f = new File("NOT_HERE");
			mocker.checking(new Expectations(){{
				one(res).resolveName("NOT_HERE"); will(returnValue(f));
			}});
			runScript("readFile('NOT_HERE')");
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testReadEmptyFileDefaultEncoding() throws IOException{
		final File f = helper.createFile(testRoot, "test", ".txt");
		mocker.checking(new Expectations(){{
			one(res).resolveName(f.getName()); will(returnValue(f));
		}});
		assertEquals("", runScript("readFile('" + f.getName() + "')"));
	}
	
	public void testReadFileDefaultEncoding() throws IOException{
		final File f = helper.createFile(testRoot, "test", ".txt");
		String contents = "some contents";
		writeFile(f, contents.getBytes("utf-8"));
		mocker.checking(new Expectations(){{
			one(res).resolveName(f.getName()); will(returnValue(f));
		}});
		assertEquals(contents, runScript("readFile('" + f.getName() + "')"));
	}
	
	public void testReadFileBadEncoding() throws IOException{
		try {
			final File f = helper.createFile(testRoot, "test", ".txt");
			String contents = "some contents";
			writeFile(f, contents.getBytes("utf-8"));
			mocker.checking(new Expectations(){{
				one(res).resolveName(f.getName()); will(returnValue(f));
			}});
			runScript("readFile('" + f.getName() + "','BAD_ENCODING')");
			fail();
		}
		catch(RuntimeException e){}
	}
	
	public void testReadFileWithEncoding() throws IOException{
		final File f = helper.createFile(testRoot, "test", ".txt");
		String contents = "some contents";
		writeFile(f, contents.getBytes("utf-8"));
		mocker.checking(new Expectations(){{
			one(res).resolveName(f.getName()); will(returnValue(f));
		}});
		assertEquals(contents, runScript("readFile('" + f.getName() + "', 'utf-8')"));
	}
	
	
	
	//-------------------------------------------

	void writeFile(File f, byte[] contents) throws IOException {
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(contents);
		fos.close();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		helper = new TempFileHelper();
		testRoot = helper.createDir(this);
		mocker = new Mockery();
		res = mocker.mock(IFileResolver.class);
		addToScope(new ReadFile(res), ReadFile.NAME);
	}
	
	protected void tearDown() throws Exception {
		helper.cleanUp();
		super.tearDown();
	}

}

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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import org.sh.muckle.runtime.js.Bootstrap;

import test.org.sh.TempFileHelper;
import junit.framework.TestCase;

public class BootstrapTest extends TestCase {

	TempFileHelper helper;
	File testRoot;
	
	PrintStream oldOut, oldErr, out;
	ByteArrayOutputStream baos;
	
	public void testNoArgs(){
		Bootstrap.main(new String[] {});
		out.flush();
		String msg = baos.toString();
		assertTrue(msg.contains("usage:"));
	}
	
	public void testScriptDoesNotExists(){
		Bootstrap.main(new String[] {"test.js"});
		out.flush();
		String msg = baos.toString().toLowerCase();
		assertTrue(msg.contains("cannot find"));
	}
	
	public void XXXtestPrintlnPresent() throws IOException {
		File f = createScript("println('Hello World');");
		Bootstrap.main(new String[] {f.getAbsolutePath()});
		out.flush();
		String msg = baos.toString();
		assertTrue(msg.contains("Hello World"));
	}
	
	public void XXXtestPrintPresent() throws IOException {
		File f = createScript("print('Hello World');");
		Bootstrap.main(new String[] {f.getAbsolutePath()});
		out.flush();
		String msg = baos.toString();
		assertTrue(msg.contains("Hello World"));
	}
	
	public void testDataEventPresent() throws IOException {
		File f = createScript("new DataEvent(1,2,3);");
		Bootstrap.main(new String[] {f.getAbsolutePath()});
		out.flush();
		String msg = baos.toString();
		assertEquals(0, msg.length());
	}
	
	public void testTimeResolverPresent() throws IOException {
		File f = createScript("new TimeResolver(1,2);");
		Bootstrap.main(new String[] {f.getAbsolutePath()});
		out.flush();
		String msg = baos.toString();
		assertEquals(0, msg.length());
	}
	
	public void testRequirePresent() throws IOException {
		File f = createScript("require();");
		Bootstrap.main(new String[] {f.getAbsolutePath()});
		out.flush();
		String msg = baos.toString();
		assertEquals(0, msg.length());
	}
	
	public void testReadFilePresent() throws IOException {
		File f = createScript("readFile();");
		Bootstrap.main(new String[] {f.getAbsolutePath()});
		out.flush();
		String msg = baos.toString();
		assertEquals(0, msg.length());
	}
	
	public void testWriteFilePresent() throws IOException {
		File f = createScript("writeFile();");
		Bootstrap.main(new String[] {f.getAbsolutePath()});
		out.flush();
		String msg = baos.toString();
		assertEquals(0, msg.length());
	}
	
	public void testSessionRunnerPresent() throws IOException {
		File f = createScript("new SessionRunner('a');");
		Bootstrap.main(new String[] {f.getAbsolutePath()});
		out.flush();
		String msg = baos.toString();
		assertEquals(0, msg.length());
	}
	
	public void testCommandLineArgs() throws IOException {
		File f = createScript("args.join();");
		Bootstrap.main(new String[] {f.getAbsolutePath(), "second"});
		out.flush();
		String msg = baos.toString();
		assertEquals(0, msg.length());
	}
	
	//--------------------------------------------------
	
	File createScript(String source) throws IOException {
		File f = helper.createFile(testRoot, "test", ".js");
		FileWriter fw = new FileWriter(f);
		fw.write(source);
		fw.close();
		return f;
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		helper = new TempFileHelper();
		testRoot = helper.createDir(this.getClass().getSimpleName());
		oldOut = System.out;
		oldErr = System.err;
		baos = new ByteArrayOutputStream();
		out = new PrintStream(baos);
		System.setOut(out);
		System.setErr(out);
	}

	protected void tearDown() throws Exception {
		System.setOut(oldOut);
		System.setErr(oldErr);
		helper.cleanUp();
		super.setUp();
	}

}

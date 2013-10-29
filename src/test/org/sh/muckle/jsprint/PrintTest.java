package test.org.sh.muckle.jsprint;

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
import java.io.PrintStream;

import org.sh.muckle.jsprint.Print;

import test.org.sh.muckle.runtime.js.ScriptTestCase;

public class PrintTest extends ScriptTestCase {
	
	PrintStream oldOut;
	PrintStream out;
	ByteArrayOutputStream baos;
	
	public void testNoParam(){
		runScript("print()");
		assertEquals("", getOutput());
	}
	
	public void testNumericParam(){
		runScript("print(123)");
		assertEquals("123" , getOutput());
	}
	
	public void testStringParam(){
		runScript("print('123')");
		assertEquals("123" , getOutput());
	}

	public void testNullParam(){
		runScript("print(null)");
		assertEquals("null" , getOutput());
	}

	public void testBooleanParam(){
		runScript("var x = true; print(x)");
		assertEquals("true" , getOutput());
	}

	protected void setUp() throws Exception {
		super.setUp();
		addToScope(Print.STATIC, "print");
		captureOut();
	}
	
	void captureOut() {
		oldOut = System.out;
		baos = new ByteArrayOutputStream();
		out = new PrintStream(baos, true);
		System.setOut(out);
	}
	
	String getOutput() {
		out.flush();
		return baos.toString();
	}


	protected void tearDown() throws Exception {
		freeOut();
		super.tearDown();
	}

	private void freeOut() {
		System.setOut(oldOut);
	}

}

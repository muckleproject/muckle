package test.org.sh.muckle.runtime;

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

import org.sh.muckle.runtime.SysLogger;

import junit.framework.TestCase;

public class SysLoggerTest extends TestCase {

	SysLogger sl;
	PrintStream oldOut, oldErr, out;
	ByteArrayOutputStream baos;
	
	public void testWarning(){
		sl.warning("danger will robinson");
		out.flush();
		assertTrue(baos.toString().startsWith("danger will robinson"));
	}
	
	public void testError(){
		sl.error("too late mr smith");
		out.flush();
		assertTrue(baos.toString().startsWith("too late mr smith"));
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		sl = new SysLogger();
		oldOut = System.out;
		oldErr = System.err;
		baos = new ByteArrayOutputStream();
		out = new PrintStream(baos);
		System.setOut(out);
		System.setErr(out);
	}

}

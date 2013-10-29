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


import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.sh.muckle.jsobjectloaderservice.IClientScriptObjectService;
import org.sh.muckle.jsobjectloaderservice.IControlScriptObjectService;
import org.sh.muckle.jsobjectloaderservice.IInitialisationSupport;
import org.sh.muckle.jsprint.Print;
import org.sh.muckle.jsprint.PrintInstaller;
import org.sh.muckle.jsprint.Println;

public class PrintInstallerTest extends MockObjectTestCase {

	PrintInstaller inst;
	
	public void testIsClientInstaller(){
		assertTrue(inst instanceof IClientScriptObjectService);
	}
	
	public void testIsControlInstaller(){
		assertTrue(inst instanceof IControlScriptObjectService);
	}
	
	public void testInstallObjects() throws Exception {
		final IInitialisationSupport supp = mock(IInitialisationSupport.class);
		checking(new Expectations(){{
			one(supp).addToScope(Print.NAME, Print.STATIC);
			one(supp).addToScope(Println.NAME, Println.STATIC);
		}});
		inst.installObjects(supp);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		inst = new PrintInstaller();
	}

}

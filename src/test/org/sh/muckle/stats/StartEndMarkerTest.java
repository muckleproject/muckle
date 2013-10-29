package test.org.sh.muckle.stats;

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


import org.sh.muckle.stats.StartEndMarker;

import junit.framework.TestCase;

public class StartEndMarkerTest extends TestCase {

	StartEndMarker ev;
	
	public void testGetStartTime() throws Exception {
		long now = System.currentTimeMillis();
		assertTrue(Math.abs(now - ev.getStartTime()) < 2);
	}
	
	public void testSetStartTime() throws Exception {
		ev.setStartTime(22);
		assertEquals(22, ev.getStartTime());
	}
	
	public void testSetGetEndTime(){
		assertEquals(0, ev.getEndTime());
		ev.setEndTime(22);
		assertEquals(22, ev.getEndTime());
	}
	
	public void testGetElapsedTime(){
		ev.setStartTime(22);
		ev.setEndTime(25);
		assertEquals(3, ev.getElapsedTime());
	}
	
	public void testConstructorWithStartTime(){
		ev = new StartEndMarker(22);
		assertEquals(22, ev.getStartTime());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		ev = new StartEndMarker();
	}

}

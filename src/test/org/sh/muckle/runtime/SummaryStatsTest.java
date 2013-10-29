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


import java.util.ArrayList;
import java.util.List;

import org.sh.muckle.runtime.SummaryLogger;
import org.sh.muckle.runtime.SummaryStats;

import junit.framework.TestCase;

public class SummaryStatsTest extends TestCase {

	List<SummaryLogger> loggers;
	
	public void testNoLogs(){
		SummaryStats stats = SummaryStats.buildFor(loggers);
		assertEquals(0, stats.getMinDuration());
		assertEquals(0, stats.getMaxDuration());
		assertEquals(0.0, stats.getAverageDuration());
		assertEquals(0, stats.getMinStep());
		assertEquals(0, stats.getMaxStep());
		assertEquals(0.0, stats.getAverageStep());
	}
	
	public void testLogs(){
		SummaryLogger log1 = new SummaryLogger();
		log1.connectStart(0);
		log1.connected(0);
		log1.sendStart(0);
		log1.responseReceived(1,0);
		log1.sendStart(1);
		log1.responseReceived(3,0);
		
		SummaryLogger log2 = new SummaryLogger();
		log2.connectStart(0);
		log2.connected(0);
		log2.sendStart(0);
		log2.responseReceived(2,0);
		log2.sendStart(4);
		log2.responseReceived(5,0);
		
		loggers.add(log1);
		loggers.add(log2);
		
		SummaryStats stats = SummaryStats.buildFor(loggers);
		assertEquals(3, stats.getMinDuration());
		assertEquals(5, stats.getMaxDuration());
		assertEquals(4.0, stats.getAverageDuration(), 0.1);
		assertEquals(1, stats.getMinStep());
		assertEquals(2, stats.getMaxStep());
		assertEquals(1.5, stats.getAverageStep(), 0.1);
		assertEquals(0, stats.getMinConnect());
		assertEquals(0, stats.getMaxConnect());
		assertEquals(0.0, stats.getAverageConnect(), 0.1);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		loggers = new ArrayList<>();
	}

}

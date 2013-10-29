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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.sh.muckle.runtime.HttpSessionSequencer;
import org.sh.muckle.runtime.HttpSessionsStatus;
import org.sh.muckle.runtime.IHttpRunHandler;
import org.sh.muckle.runtime.IHttpService;


public class HttpSessionsStatusTest extends MockObjectTestCase {

	ArrayList<HttpSessionSequencer> sessions;
	ScheduledExecutorService scheduler;
	IHttpRunHandler runHandler;
	IHttpService httpService;
	
	HttpSessionSequencer seq;
	
	
	public void testEmptySessionList(){
		HttpSessionsStatus status = HttpSessionsStatus.buildFrom(sessions);
		assertEquals(0, status.getStartedCount());
		assertEquals(0, status.getFinishedCount());
		assertEquals(0, status.getErrorCount());
		assertEquals(0, status.getMinStep());
		assertEquals(0, status.getMaxStep());
	}
	
	public void testSessionNotStarted(){
		sessions.add(seq);
		HttpSessionsStatus status = HttpSessionsStatus.buildFrom(sessions);
		assertEquals(0, status.getStartedCount());
		assertEquals(0, status.getFinishedCount());
		assertEquals(0, status.getErrorCount());
	}
	
	public void testSessionStarted() throws Exception {
		sessions.add(seq);
		checking(new Expectations(){{
			one(runHandler).nextRequest(); will(returnValue(null));
			one(httpService).finished();
		}});
		seq.start(0);
		Thread.sleep(50);
		
		HttpSessionsStatus status = HttpSessionsStatus.buildFrom(sessions);
		assertEquals(1, status.getStartedCount());
		assertEquals(1, status.getFinishedCount());
		assertEquals(0, status.getErrorCount());
	}
	
	public void testAbortedSession(){
		sessions.add(seq);
		checking(new Expectations(){{
			one(httpService).finished();
		}});
		seq.abort();
		
		HttpSessionsStatus status = HttpSessionsStatus.buildFrom(sessions);
		assertEquals(0, status.getStartedCount());
		assertEquals(1, status.getFinishedCount());
		assertEquals(1, status.getErrorCount());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		scheduler = Executors.newSingleThreadScheduledExecutor();
		runHandler = mock(IHttpRunHandler.class);
		httpService = mock(IHttpService.class);
		sessions = new ArrayList<>();
		seq = new HttpSessionSequencer(scheduler, new CountDownLatch(1), runHandler, httpService);
	}

}

package org.sh.muckle.runtime;

/*
Copyright 2013 The Muckle Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


import java.util.List;

public class HttpSessionsStatus {
	
	int startedCount;
	int finishedCount;
	int errorCount;
	
	int minStep;
	int maxStep;

	public HttpSessionsStatus(int started, int finished, int error, int minStep, int maxStep) {
		this.startedCount = started;
		this.finishedCount = finished;
		this.errorCount = error;
		this.minStep = minStep;
		this.maxStep = maxStep;
	}

	public static HttpSessionsStatus buildFrom(	List<HttpSessionSequencer> sessions) {
		int started = 0;
		int finished = 0;
		int error = 0;

		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		
		for(int i=0; i<sessions.size(); i++){
			HttpSessionSequencer seq = sessions.get(i);
			
			if(seq.isStarted()){
				started++;
			}
			
			if(seq.isFinished()){
				finished++;
			}
			
			if(seq.hadError()){
				error++;
			}
			else {
				min = Math.min(min, seq.getStepCount());
				max = Math.max(max, seq.getStepCount());
			}
		}
		
		if(min == Integer.MAX_VALUE){
			min = 0;
		}
		
		if(max == Integer.MIN_VALUE){
			max = 0;
		}

		return new HttpSessionsStatus(started, finished, error, min, max);
	}

	public int getStartedCount() {
		return startedCount;
	}

	public int getFinishedCount() {
		return finishedCount;
	}

	public int getErrorCount() {
		return errorCount;
	}
	
	public int getMinStep() {
		return minStep;
	}

	public int getMaxStep() {
		return maxStep;
	}


}

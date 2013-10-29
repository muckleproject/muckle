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


import org.sh.muckle.stats.MaxDurationLogger;
import org.sh.muckle.stats.MinDurationLogger;
import org.sh.muckle.stats.StartEndMarker;

public class SummaryLogger implements IHttpTimingListener {
	
	MinDurationLogger minStep = new MinDurationLogger();
	MaxDurationLogger maxStep = new MaxDurationLogger();
	
	MinDurationLogger minConnect = new MinDurationLogger();
	MaxDurationLogger maxConnect = new MaxDurationLogger();
	
	ElapsedTimeLogger elapsed = new ElapsedTimeLogger();
	
	long requestStart;
	double stepAccumulated;
	int stepCount;
	
	long connectStart;
	double connectAccumulated;
	int connectCount;
	
	public StartEndMarker getElapsed() {
		if(elapsed.hasValue()){
			return elapsed.getValue();
		}
		else {
			throw new IllegalStateException();
		}
	}

	public StartEndMarker getMinStep() {
		return minStep.getValue();
	}

	public StartEndMarker getMaxStep() {
		return maxStep.getValue();
	}
	
	public double getAverageStep() {
		if(stepCount != 0){
			return stepAccumulated/stepCount;
		}
		else {
			throw new IllegalStateException("No events logged.");
		}
	}

	public StartEndMarker getMinConnect() {
		return minConnect.getValue();
	}

	public StartEndMarker getMaxConnect() {
		return maxConnect.getValue();
	}
	
	public double getAverageConnect() {
		if(connectCount != 0){
			return connectAccumulated/connectCount;
		}
		else {
			throw new IllegalStateException("No events logged.");
		}
	}

	public boolean hasValues() {
		return stepCount > 0 && connectCount > 0;
	}

	//------------ IHttpTimingListener methods ---------- 

	public void sendStart(long time) {
		requestStart = time;
	}

	public void responseReceived(long time, long contentLength) {
		TCPSendReceieveEvent m = new TCPSendReceieveEvent(requestStart, time, contentLength);
		
		minStep.compareWith(m);
		maxStep.compareWith(m);
		
		stepCount++;
		stepAccumulated += m.getElapsedTime();
		
		elapsed.responseReceived(time, contentLength);
	}

	public void connectStart(long time) {
		elapsed.connectStart(time);
		connectStart = time;
	}

	public void connected(long time) {
		TCPConnectionEvent m = new TCPConnectionEvent(connectStart);
		m.setEndTime(time);
		
		minConnect.compareWith(m);
		maxConnect.compareWith(m);
		
		connectCount++;
		connectAccumulated += m.getElapsedTime();
	}

	//-------- end IHttpTimingListener methods ---------- 
}

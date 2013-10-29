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


import org.sh.muckle.stats.StartEndMarker;

public class ElapsedTimeLogger implements IHttpTimingListener {

	StartEndMarker m;
	
	public StartEndMarker getValue() {
		return m != null ? m : new StartEndMarker(0);
	}
	
	public boolean hasValue() {
		return m != null;
	}

	//--------- IHttpTimingListener methods ---------
	
	public void sendStart(long time) {
	}

	public void responseReceived(long time, long contentLength) {
		if(m != null){
			m.setEndTime(time);
		}
	}

	public void connectStart(long time) {
		if(m == null){
			m = new StartEndMarker(time);
		}
	}

	public void connected(long time) {
	}

	//----- end IHttpTimingListener methods ---------
	
}

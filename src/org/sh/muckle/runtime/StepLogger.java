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


import java.util.ArrayList;

public class StepLogger extends ArrayList<IHttpTransactionEvent> implements IHttpTimingListener {

	long sendStart;
	long connectStart;
	
	//--------- IHttpTimingListener methods -------------
	
	public void sendStart(long time) {
		this.sendStart = time;
	}

	public void responseReceived(long time, long contentLength) {
		TCPSendReceieveEvent m = new TCPSendReceieveEvent(sendStart, time, contentLength);
		add(m);
	}

	public void connectStart(long time) {
		this.connectStart = time;
	}

	public void connected(long time) {
		TCPConnectionEvent m = new TCPConnectionEvent(connectStart);
		m.setEndTime(time);
		add(m);
	}

}

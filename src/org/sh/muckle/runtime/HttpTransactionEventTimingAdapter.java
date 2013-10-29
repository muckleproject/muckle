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

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class HttpTransactionEventTimingAdapter implements IHttpTransactionEventsListener {

	ArrayList<IHttpTimingListener> timingListeners;
	
	public HttpTransactionEventTimingAdapter() {
		timingListeners = new ArrayList<>();
	}
	
	public HttpTransactionEventTimingAdapter(IHttpTimingListener timingListener) {
		this();
		addTimingListener(timingListener);
	}
	
	public void addTimingListener(IHttpTimingListener timingListener){
		timingListeners.add(timingListener);
	}

	public void connectStart() {
		int len = timingListeners.size();
		if(len > 0){
			long now = System.nanoTime();
			for(int i=0; i<len; i++){
				timingListeners.get(i).connectStart(now);
			}
		}
	}

	public void connected() {
		int len = timingListeners.size();
		if(len > 0){
			long now = System.nanoTime();
			for(int i=0; i<len; i++){
				timingListeners.get(i).connected(now);
			}
		}
	}

	public void sendStart(HttpRequest request) {
		int len = timingListeners.size();
		if(len > 0){
			long now = System.nanoTime();
			for(int i=0; i<len; i++){
				timingListeners.get(i).sendStart(now);
			}
		}
	}

	public void retry(HttpRequest request) {
		sendStart(request);
	}

	public void responseReceived(HttpResponse response) {
		int len = timingListeners.size();
		if(len > 0){
			long now = System.nanoTime();
			long contentLength = HttpHeaders.getContentLength(response);
			for(int i=0; i<len; i++){
				timingListeners.get(i).responseReceived(now, contentLength);
			}
		}
	}

}

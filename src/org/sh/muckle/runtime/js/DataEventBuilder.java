package org.sh.muckle.runtime.js;

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
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jssupport.AbstractReadOnlyScriptable;
import org.sh.muckle.runtime.HttpSessionOK;
import org.sh.muckle.runtime.HttpTransactionEventTimingAdapter;
import org.sh.muckle.runtime.IHttpService;
import org.sh.muckle.runtime.IHttpSessionSequenceStatus;
import org.sh.muckle.runtime.StepLogger;
import org.sh.muckle.runtime.TCPDataSelector;
import org.sh.muckle.runtime.TCPSendReceieveEvent;

public class DataEventBuilder extends AbstractReadOnlyScriptable implements IJSHttpTimerListenerFactory {
	
	ArrayList<StepLogger> loggers  = new ArrayList<>();
	
	Scriptable values;
	Scriptable timeResolver;
	
	public DataEventBuilder(){
		//long start = System.currentTimeMillis();
		//while(System.currentTimeMillis() == start);
		// put up with small timer inaccuracy as currentTimeMillis may tick at 10s of milliseconds
		timeResolver = new TimeResolver(System.currentTimeMillis(), System.nanoTime());
	}

	public void addListeneners(IHttpService service) {
		StepLogger sl = new StepLogger();
		service.addTransactionEventsListener(new HttpTransactionEventTimingAdapter(sl));
		loggers.add(sl);
	}

	public void completed(Context ctx, Scriptable scope, IHttpSessionSequenceStatus[] results) {
		ArrayList<Object> completed = new ArrayList<>();
		TCPDataSelector selector = new TCPDataSelector();
		for(int i=0; i<results.length; i++){
			if(HttpSessionOK.STATIC == results[i]){
				List<TCPSendReceieveEvent> dataEvents = selector.selectFrom(loggers.get(i));
				ArrayList<Object> jsEvents = new ArrayList<>();
				for(int j=0; j<dataEvents.size(); j++){
					TCPSendReceieveEvent ev = dataEvents.get(j);
					jsEvents.add(new DataEvent(ev.getStartTime(), ev.getEndTime(), ev.getContentLength()));
				}
				Scriptable arr = ctx.newArray(scope, jsEvents.toArray());
				completed.add(arr);
			}
		}
		
		values = ctx.newArray(scope, completed.toArray());
		loggers.clear();
	}

	public Object get(String name, Scriptable start) {
		Object value = null;
		if("values".equals(name)){
			value = values;
		}
		else if("timeResolver".equals(name)){
			value = timeResolver;
		}
		return value;
	}

	public String getClassName() {
		return "DataEventBuilder";
	}
}

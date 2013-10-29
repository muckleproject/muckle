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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.jssupport.AbstractReadOnlyScriptable;
import org.sh.muckle.runtime.AbstractCompletedFinder;
import org.sh.muckle.runtime.HttpTransactionEventTimingAdapter;
import org.sh.muckle.runtime.IHttpService;
import org.sh.muckle.runtime.IHttpSessionSequenceStatus;
import org.sh.muckle.runtime.SummaryLogger;
import org.sh.muckle.runtime.SummaryStats;

public class SummaryBuilder extends AbstractReadOnlyScriptable implements IJSHttpTimerListenerFactory {

	ArrayList<SummaryLogger> loggers = new ArrayList<>();
	Scriptable summary;

	public Object get(String name, Scriptable start) {
		Object value = null;
		
		if("summary".equals(name)){
			value = summary;
		}
		
		return value;
	}

	public String getClassName() {
		return "SummaryBuilder";
	}

	//---------- IJSHttpTimerListenerFactory methods -----------
	
	public void addListeneners(IHttpService service) {
		SummaryLogger summaryLogger = new SummaryLogger();
		service.addTransactionEventsListener(new HttpTransactionEventTimingAdapter(summaryLogger));
		loggers.add(summaryLogger);
	}

	public void completed(Context ctx, Scriptable scope, IHttpSessionSequenceStatus[] results) {
		summary = ctx.newObject(scope);
		
		ArrayList<SummaryLogger> completed = new CompletedBuilder().getCompleted(results);
		SummaryStats stats = SummaryStats.buildFor(completed);

		summary.put("sessionCount", summary, new Integer(completed.size()));
		summary.put("duration", summary, buildMinMaxAv(ctx, scope, stats.getMinDuration(), stats.getMaxDuration(), stats.getAverageDuration()));
		summary.put("step", summary, buildMinMaxAv(ctx, scope, stats.getMinStep(), stats.getMaxStep(), stats.getAverageStep()));
		summary.put("connect", summary, buildMinMaxAv(ctx, scope, stats.getMinConnect(), stats.getMaxConnect(), stats.getAverageConnect()));
		
		loggers.clear();
	}
	
	Scriptable buildMinMaxAv(Context ctx, Scriptable scope, long min, long max, double av){
		Scriptable s = ctx.newObject(scope);
		s.put("min", s, convertToMillis2DecimalPlaces(min));
		s.put("max", s, convertToMillis2DecimalPlaces(max));
		s.put("average", s, convertToMillis2DecimalPlaces(av));
		return s;
	}
	
	double convertToMillis2DecimalPlaces(double nanos){
		return Math.round(nanos/10000d)/100d;
	}

	//------ end IJSHttpTimerListenerFactory methods -----------

	//----------------------------------------------------------
	
	class CompletedBuilder extends AbstractCompletedFinder<SummaryLogger> {

		protected SummaryLogger getElementAt(int index) {
			return loggers.get(index);
		}
		
	}


}

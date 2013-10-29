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

import org.sh.muckle.stats.MaxDurationLogger;
import org.sh.muckle.stats.MinDurationLogger;

public class SummaryStats {
	
	long minDuration;
	long maxDuration;
	double averageDuration;
	
	long minStep;
	long maxStep;
	double averageStep;
	
	long minConnect;
	long maxConnect;
	double averageConnect;

	public static SummaryStats buildFor(List<SummaryLogger> loggers) {
		SummaryStats stats =  new SummaryStats();
		
		if(loggers.size() > 0){
			MinDurationLogger minStep = new MinDurationLogger();
			MaxDurationLogger maxStep = new MaxDurationLogger();
			MinDurationLogger minConnect = new MinDurationLogger();
			MaxDurationLogger maxConnect = new MaxDurationLogger();
			MinDurationLogger minElapsed = new MinDurationLogger();
			MaxDurationLogger maxElapsed = new MaxDurationLogger();
			
			double averageStep = 0;
			double averageConnect = 0;
			double averageDuration = 0;
	
			for(int i=0; i<loggers.size(); i++){
				SummaryLogger sl = loggers.get(i);
				if(sl.hasValues()){
					minStep.compareWith(sl.getMinStep());
					maxStep.compareWith(sl.getMaxStep());
					
					minConnect.compareWith(sl.getMinConnect());
					maxConnect.compareWith(sl.getMaxConnect());
					
					minElapsed.compareWith(sl.getElapsed());
					maxElapsed.compareWith(sl.getElapsed());
					
					averageStep += sl.getAverageStep();
					averageConnect += sl.getAverageConnect();
					averageDuration += sl.getElapsed().getElapsedTime();
				}
			}
			
			stats.minDuration = minElapsed.hasValue() ? minElapsed.getValue().getElapsedTime() : 0;
			stats.maxDuration = maxElapsed.hasValue() ? maxElapsed.getValue().getElapsedTime() : 0;
			stats.averageDuration = (averageDuration/loggers.size()) ;
			
			stats.minStep = minStep.hasValue() ? minStep.getValue().getElapsedTime() : 0;
			stats.maxStep = maxStep.hasValue() ? maxStep.getValue().getElapsedTime() : 0;
			stats.averageStep = (averageStep/loggers.size()) ;
			
			stats.minConnect = minConnect.hasValue() ? minConnect.getValue().getElapsedTime() : 0;
			stats.maxConnect = maxConnect.hasValue() ? maxConnect.getValue().getElapsedTime() : 0;
			stats.averageConnect = (averageConnect/loggers.size()) ;
		}
		
		return stats;
	}

	public long getMinDuration() {
		return minDuration;
	}

	public long getMaxDuration() {
		return maxDuration;
	}

	public double getAverageDuration() {
		return averageDuration;
	}

	public long getMinStep() {
		return minStep;
	}

	public long getMaxStep() {
		return maxStep;
	}

	public double getAverageStep() {
		return averageStep;
	}

	public long getMinConnect() {
		return minConnect;
	}

	public long getMaxConnect() {
		return maxConnect;
	}

	public double getAverageConnect() {
		return averageConnect;
	}
}

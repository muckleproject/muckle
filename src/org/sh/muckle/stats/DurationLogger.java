package org.sh.muckle.stats;

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


abstract class DurationLogger {

	StartEndMarker value;
	long currentElapsed = Long.MAX_VALUE;
	
	DurationLogger(){
		currentElapsed = getDefaultElapsed();
	}
	

	public boolean hasValue() {
		return value != null;
	}

	public StartEndMarker getValue() {
		if(value == null) {
			throw new IllegalStateException();
		}
		else {
			return value;
		}
	}

	public void compareWith(StartEndMarker marker) {
		long elapsed = marker.getElapsedTime();
		if(isNewValue(elapsed, currentElapsed)){
			value = marker;
			currentElapsed = elapsed;
		}
	}
	
	abstract boolean isNewValue(long newValue, long oldValue);
	abstract long getDefaultElapsed();

}

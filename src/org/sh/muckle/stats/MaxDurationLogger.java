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


public class MaxDurationLogger extends DurationLogger{

	@Override
	boolean isNewValue(long newValue, long oldValue) {
		return newValue > oldValue;
	}

	@Override
	long getDefaultElapsed() {
		return Long.MIN_VALUE;
	}
}

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
import java.util.List;

public class TCPDataSelector implements IHttpTransactionEventVisitor {

	ArrayList<TCPSendReceieveEvent> selected = new ArrayList<>();
	
	public List<TCPSendReceieveEvent> selectFrom(StepLogger logger) {
		selected.clear();
		for(int i=0; i<logger.size(); i++){
			logger.get(i).accept(this);
		}
		return selected;
	}

	public Object visit(TCPSendReceieveEvent ev) {
		selected.add(ev);
		return null;
	}

	public Object visit(TCPConnectionEvent ev) {
		return null;
	}

}

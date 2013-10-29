package org.sh.muckle.http;

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

import java.util.HashMap;
import java.util.Map;

public class HeaderContentDecoder {
	
	boolean isEmpty;
	String[] params;

	public HeaderContentDecoder(String content) {
		isEmpty = content == null;
		if(!isEmpty){
			content = content.trim();
			isEmpty = content.length() == 0;
			if(!isEmpty){
				params = content.split(";");
				if(params.length > 1){
					for(int i=0; i<params.length; i++){
						params[i] = params[i].trim();
					}
				}
			}
		}
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public boolean hasParameters() {
		return params != null && params.length > 1;
	}
	
	public Map<String, String> getParametersMap(){
		HashMap<String, String> map = new HashMap<>();
		if(params != null){
			for(int i=0; i<params.length; i++){
				String s = params[i];
				if(s.contains("=")){
					String[] nv = s.split("=");
					map.put(nv[0].trim(), nv[1].trim());
				}
			}
		}
		return map;
	}

	public int getParameterIndexOf(String string) {
		int index = -1;
		if(params != null){
			for(int i=0; i<params.length; i++){
				if(params[i].contains(string)){
					index = i;
					break;
				}
			}
		}
		return index;
	}

}

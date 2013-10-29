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
import java.util.Iterator;
import java.util.List;

import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class CookieManager {
	
	ArrayList<Cookie> currentCookies = new ArrayList<>();

	/**
	 * At the moment this is simplistic and sends back all cookies received.
	 * @param req
	 */
	public void setRequestCookies(HttpRequest req) {
		boolean haveCookiesToSend = false;
		
		CookieEncoder enc = new CookieEncoder(false);
		for(int i=0; i<currentCookies.size(); i++){
			Cookie c = currentCookies.get(i);
			enc.addCookie(c.getName(), c.getValue());
			haveCookiesToSend = true;
		}
		
		if(haveCookiesToSend){
			req.setHeader(HttpHeaders.Names.COOKIE, enc.encode());
		}
	}

	public int getCookiesFromResponse(HttpResponse resp, HttpRequest req) {
		List<String> setCookies = resp.getHeaders(HttpHeaders.Names.SET_COOKIE);
		CookieDecoder dec = new CookieDecoder();
		for(int i=0; i<setCookies.size(); i++){
			Iterator<Cookie> it = dec.decode(setCookies.get(i)).iterator();
			while(it.hasNext()){
				Cookie c = it.next();
				currentCookies.add(c);
			}
		}
		return setCookies.size();
	}



}

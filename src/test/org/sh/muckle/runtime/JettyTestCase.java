package test.org.sh.muckle.runtime;

/*
*	Copyright 2013 The Muckle Project
*
*	Licensed under the Apache License, Version 2.0 (the "License");
*	you may not use this file except in compliance with the License.
*	You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*	Unless required by applicable law or agreed to in writing, software
*	distributed under the License is distributed on an "AS IS" BASIS,
*	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*	See the License for the specific language governing permissions and
*	limitations under the License.
*/


import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;

import junit.framework.TestCase;

abstract public class JettyTestCase extends TestCase {

	protected Server jetty;
	protected int jettyPort;
	
	protected void setUp() throws Exception {
		super.setUp();
		jetty = new Server();
		
		ServerConnector sc = new ServerConnector(jetty);
		jetty.setConnectors(new Connector[] {sc});
		
		HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(getHandlers());
        jetty.setHandler(handlers);
        
 		jetty.start();
		jettyPort = sc.getLocalPort();
	}
	
	abstract Handler[] getHandlers();

	protected void tearDown() throws Exception {
		jetty.stop();
		jetty.join();
		super.tearDown();
	}

}

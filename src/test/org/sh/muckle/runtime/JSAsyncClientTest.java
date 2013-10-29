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


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.sh.muckle.ILogger;
import org.sh.muckle.jsprint.Println;
import org.sh.muckle.runtime.js.DataEvent;
import org.sh.muckle.runtime.js.DataEventConstructor;
import org.sh.muckle.runtime.js.ScriptCache;
import org.sh.muckle.runtime.js.SessionRunnerConstructor;

import test.org.sh.TempFileHelper;

abstract  public class JSAsyncClientTest extends JettyTestCase {
	
	TempFileHelper helper;
	File root;
	
	public void testRepeatedGet() throws Exception {
		
		final File clientScriptPath = createScript();

		final StringBuilder controlScript = new StringBuilder();
		controlScript.append("var runner = new SessionRunner('localhost', " + jettyPort +");\n");
		//controlScript.append("runner.setConnectionTimeout(10000);\n");
		//controlScript.append("runner.setProxy('localhost', 8888);\n");
		//controlScript.append("var runner = new SessionRunner('localhost', 8080);\n");
		//controlScript.append("var runner = new SessionRunner('www.google.co.uk', 443, true);\n");
		controlScript.append("var sp = runner.getSummaryProvider();\n");
		controlScript.append("var dp = runner.getDataEventProvider();\n");
		controlScript.append("runner.setStatusListener(4, function(status){println(JSON.stringify(status));});\n");
		controlScript.append("runner.run(1000, 50, '" + clientScriptPath.getName() +"');\n");
		controlScript.append("println(JSON.stringify(runner.errors));\n");
		controlScript.append("println(JSON.stringify(sp.summary));\n");
		//controlScript.append("println(JSON.stringify(dp.values));\n");
		
		ContextFactory.getGlobal().call(new ContextAction() {
								
			public Object run(Context ctx) {
				Scriptable scope = ctx.initStandardObjects();
				scope.put(SessionRunnerConstructor.NAME, scope, new SessionRunnerConstructor(clientScriptPath.getParentFile(), new TestLogger(), new ScriptCache()));
				scope.put(Println.NAME, scope, Println.STATIC);
				scope.put(DataEvent.NAME, scope, DataEventConstructor.STATIC);
				
				return ctx.evaluateString(scope, controlScript.toString(), "test", 1, null);
			}
		});
		
	}
	

	
	
	//--------------------------------------------------
	
	class TestLogger implements ILogger {
		public void warning(String s) {
			System.out.println(s);
		}

		public void error(String s) {
			System.err.println(s);
		}
	}

	Handler[] getHandlers() {
		return new Handler[] { new TestHandler() };
	}
	
	
	class TestHandler extends AbstractHandler {
		AtomicInteger count = new AtomicInteger();
		String content;
		
		TestHandler(){
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<1000; i++){
				sb.append("0123456789");
			}
			content = sb.toString();
		}
		
		public void handle(String target, Request baseRequest, HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException, ServletException {
//try {Thread.sleep(20);}catch(Exception e){}
			httpResp.setContentType("text/html");
			httpResp.setStatus(HttpServletResponse.SC_OK);
			httpResp.setContentLength(content.length());
			httpResp.getWriter().write(content);
			baseRequest.setHandled(true);
//System.out.println(count.addAndGet(1) + " " + content.length());
		}
		
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		helper = new TempFileHelper();
		root = helper.createDir(this.getClass().getSimpleName());
	}
	
	protected void tearDown() throws Exception {
		helper.cleanUp();
		super.tearDown();
	}
	
	File createScript() throws Exception{
		File s = helper.createFile(root, "test", ".js");
		FileWriter fw = new FileWriter(s);
		fw.write("var req = new HttpRequest();\n");
		fw.write("var max = 40;\n");
		fw.write("var count = 0;\n");
		fw.write("var retries;\n");
		fw.write("session.onNextRequest = function (){retries = 0; req.delay = session.calcDelay(count * 1000); count++; return count <= max ? req : null;}\n");
		fw.write("session.onHandleResponse = function (resp){if(resp.getContent().length != 10000){ throw 'Bad length '+  resp.getContent().length;};}\n");
		fw.write("session.onHandleError = function (error){return retries-- > 0 ? HttpErrorAction.RETRY : HttpErrorAction.ABORT;}");
		fw.close();
		return s;
	}
	
	File XXcreateScript() throws Exception{
		File s = helper.createFile(root, "test", ".js");
		FileWriter fw = new FileWriter(s);
		fw.write("var req = new HttpRequest();\n");
		fw.write("req.delay = 1000;\n");
		fw.write("var count = 1000;\n");
		fw.write("var n='start';\n");
		fw.write("session.onNextRequest = function (){req.uri ='/st/State/' + n;  return count-- > 0 ? req : null;}\n");
		fw.write("session.onHandleResponse = function (resp){n = JSON.parse(resp.getContent()).n;}\n");
		fw.close();
		return s;
	}

}

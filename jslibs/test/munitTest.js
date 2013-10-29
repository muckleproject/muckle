
suite("A test of the assert functions", function(){
	
	test("assertEquals for boolean passes", function(){
		assertEquals(true, true);
	});
	
	test("assertEquals for strings ", function(){
		assertEquals("abc", "abc");
	});
	
	test("assertEquals for numbers ", function(){
		assertEquals(1.1, 1.1);
	});
	
	test("assertDefined  ", function(){
		assertDefined({});
	});
	
});


suite("A test of before and after", function(){
	var a = 0;
	
	test("before is called", function(){
		assertEquals(1, a);
	});
	
	
	after(function(){
		a++;
	});
	
	test("after and before are called", function(){
		assertEquals(3, a);
	});
	
	
	before(function(){
		a++;
	});

	
});

suite("An Munit objects ", function(){
	var mu;
	var f;
	
	before(function(){
		mu = new Munit();
		f = function(){};
	});
	
	test("addSuite method adds a suite", function(){
		assertEquals(0, mu.suites.length);
		mu.addSuite("test name", f);
		assertEquals(1, mu.suites.length);
	});
	
	test("addTest method adds a test", function(){
		assertEquals(0, mu.tests.length);
		mu.addTest("test name", f);
		assertEquals(1, mu.tests.length);
	});
	
	test("setBefore method sets a before method", function(){
		assertEquals(null, mu.before);
		mu.setBefore(f);
		assertEquals(f, mu.before);
	});
	
	test("setAfter method sets an after method", function(){
		assertEquals(null, mu.after);
		mu.setAfter(f);
		assertEquals(f, mu.after);
	});
	
	test("assertEquals method throws exception if not equal", function(){
		try {
			mu.assertEquals(true, false);
			fail("exception not thrown");
		}
		catch(err){
			assertEquals(true, err.expected);
			assertEquals(false, err.actual);
			assertEquals(undefined, err.msg);
		}
	});
	
	test("assertDefined method throws exception if not defined", function(){
		try {
			mu.assertDefined(undefined);
			fail("exception not thrown");
		}
		catch(err){
			assertEquals(true, err.undefinedError);
			assertEquals(undefined, err.msg);
		}
	});
	
	test("assertDefined method does nothing if defined", function(){
		try {
			mu.assertDefined({});
		}
		catch(err){
			fail("unexpected error");
		}
	});
	
	test("assertEquals method has optional message third parameter", function(){
		var msg = "some message";
		try {
			mu.assertEquals(true, false, msg);
			fail("exception not thrown");
		}
		catch(err){
			assertEquals(msg, err.msg, "message comparison");
		}
	});
	
	test("fail method throws exception ", function(){
		var msg = "abc";
		var errorThrown = false;
		try {
			mu.fail(msg);
		}
		catch(err){
			errorThrown = true;
			assertEquals(msg, err);
		}
		assertEquals(true, errorThrown, "exception not thrown")
	});
	
	test("run method does nothing if no suites are added ", function(){
		var rep = {};
		mu.run(rep);
	});
	
	test("run method runs suites and produces a report", function(){
		var rep = {
			setSuiteName: function(name){this.name = name},
			summarise: function(){this.summariseCalled = true},
			hadErrors:function() {return false;}
		};
		
		var desc = "test suite";
		var suiteCalled = false;
		mu.addSuite(desc, function(){suiteCalled = true});
		
		mu.run(rep);
		
		assertEquals(true, suiteCalled);
		assertEquals(desc, rep.name);
		assertEquals(true, rep.summariseCalled);
	});
	
	test("runTest method runs a supplied test function", function(){
		var rep = {
			setTestName: function(name){this.name = name}
		};
		
		var desc = "test";
		var testCalled = false;
		
		mu.reporter = rep;
		mu.runTest(desc, function(){testCalled = true});
		
		assertEquals(true, testCalled);
		assertEquals(desc, rep.name);
	});
	
	test("runTest method calls produces a failure message in report on assertion failure", function(){
		var rep = {
			setTestName: function(name){this.name = name},
			fail: function(err){this.err = err}
		};
		
		var desc = "test";
		mu.reporter = rep;
		mu.runTest(desc, function(){mu.assertEquals(true, false)});
		
		assertEquals(desc, rep.name);
		assertEquals(true, rep.err.expected);
	});
	
	test("runTest method calls produces a failure message in report on runtime errors", function(){
		var rep = {
			setTestName: function(name){this.name = name},
			fail: function(err){this.err = err}
		};
		
		var desc = "test";
		mu.reporter = rep;
		mu.runTest(desc, function(){Z++});
		
		assertEquals(desc, rep.name);
		assertDefined(rep.err);
	});
	
	test("runTest method calls before method if defined", function(){
		var rep = {
			setTestName: function(name){this.name = name},
		};
		mu.reporter = rep;
		
		var beforeCalled = false;
		mu.setBefore(function(){beforeCalled = true})
		
		mu.runTest("", function(){});
		assertEquals(true, beforeCalled);
	});
	
	test("runTest method calls after method if defined", function(){
		var rep = {
			setTestName: function(name){this.name = name},
		};
		mu.reporter = rep;
		
		var afterCalled = false;
		mu.setAfter(function(){afterCalled = true})
		
		mu.runTest("", function(){});
		assertEquals(true, afterCalled);
	});
	
	
});
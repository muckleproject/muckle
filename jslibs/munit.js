function PrintlnWriter(){
}

PrintlnWriter.prototype = {
	record: function(message){
		println(message);
	}
};

function SimpleReporter(recorder){
	this.recorder = recorder;
	this.resetCounters();
}

SimpleReporter.prototype = {

	setSuiteName: function(name){
		this.resetCounters();
		this.recorder.record(name);
	},
	
	setTestName: function(name){
		this.testCount++;
		this.recorder.record("  " + name);
	},
	
	fail: function(err){
		if(err.expected != null){
			this.printFailed(err, " expected <" + err.expected + "> actual <" + err.actual + ">");
		}
		else if(err.undefinedError){
			this.printFailed(err, " not defined");
		}
		else {
			this.error(err);
		}
	},
	
	printFailed: function(err, detail){
		this.failuresCount++;
		var msg = err.msg != null ? '"' + err.msg + '"': '';
		this.recorder.record("    Failed : " + msg + detail);
	},
	
	error: function(err){
		this.errorCount++;
		this.recorder.record("    Error: " + err);
	},
		
	summarise: function() {
		this.recorder.record("Tests run:" + this.testCount + ", Failures: " + this.failuresCount+ ", Errors: " + this.errorCount);
	},
	
	resetCounters: function(){
		this.testCount = 0;
		this.failuresCount = 0;
		this.errorCount = 0;
	},
	
	hadErrors: function() {
		return 	this.failuresCount > 0 || this.errorCount > 0;
	}
};

function Munit(){
	this.suites = [];
	this.tests = [];
	this.reporter = null;
	this.before = null;
	this.after = null;
}

Munit.prototype = {
		
	addSuite: function(desc, suiteFunction){
		this.suites.push({desc:desc, suiteFunction:suiteFunction});
	},
	
	setBefore: function(beforeFunction){
		this.before = beforeFunction;
	},
	
	setAfter: function(afterFunction){
		this.after = afterFunction;
	},
	
	run: function(reporter){
		this.reporter = reporter;
		for(var i=0; i<this.suites.length; i++){
			
			var suite = this.suites[i];
			reporter.setSuiteName(suite.desc);
			
			this.before = null;
			this.after = null;
			this.tests = [];
			
			try {
				// add any tests and before and after functions
				suite.suiteFunction();
				// now run the tests
				for(var t=0; t<this.tests.length; t++){
					this.runTest(this.tests[t].desc, this.tests[t].testFunction);
				}
			}
			catch(err){
				reporter.error(err);
			}
			finally{
				reporter.summarise();
			}
			
			if(reporter.hadErrors()){
				throw "Munit halted.";
			}
		}
		this.reporter = null;
		this.before = null;
		this.after = null;
		this.tests = [];
	},
	
	addTest: function(desc, testFunction){
		this.tests.push({desc:desc, testFunction:testFunction})
	},
	
	runTest: function(desc, testFunction){
		try {
			
			if(this.before !== null){
				this.before();
			}
			
			try {
				this.reporter.setTestName(desc);
				testFunction();
			}
			finally {
				if(this.after !== null){
					this.after();
				}
			}
		}
		catch(err){
			this.reporter.fail(err);
		}
	},
	
	assertEquals: function(expected, actual, msg){
		if(expected !== actual){
			throw {expected:expected, actual:actual, msg:msg};
		}
	},
	
	assertDefined: function(item, msg){
		if(item === undefined){
			throw {undefinedError:true, msg:msg};
		}
	},
	
	fail: function(msg){
		throw msg;
	}
};

function suite(desc, tests){
	munit.addSuite(desc, tests);
}

function test(desc, testFunction){
	munit.addTest(desc, testFunction);
}

function assertEquals(expected, actual, msg){
	munit.assertEquals(expected, actual, msg);
}

function assertDefined(item, msg){
	munit.assertDefined(item, msg);
}

function fail(msg){
	munit.fail(msg);
}

function before(setupFunction){
	munit.setBefore(setupFunction);
}

function after(afterFunction){
	munit.setAfter(afterFunction);
}

var munit = new Munit();


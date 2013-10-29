require("munit.js");

// The other args are unit test files. Calling require will load them ready for running.
for(var i=1; i<args.length; i++){
	require(args[i]);
}

munit.run(new SimpleReporter(new PrintlnWriter()));


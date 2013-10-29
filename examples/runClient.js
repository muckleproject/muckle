var runner = new SessionRunner('localhost', 8080);
var sp = runner.getSummaryProvider();
runner.setStatusListener(4, function(status){println(JSON.stringify(status));});

var count = args.length > 2 ? args[2] : 1000;
var startupRate = args.length > 3 ? args[3] : 50;
runner.run(count, startupRate, args[1]);

println(JSON.stringify(runner.errors));
println(JSON.stringify(sp.summary));

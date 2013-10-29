// Example Muckle control script that shows use of StatsHelper to analyse 
// the matrix of data collected during a run.

require('jslibs/statsHelper.js')

var runner = new SessionRunner('localhost', 8080);
var sp = runner.getSummaryProvider();

// By getting a data provider we will record all events during a run.
var dp = runner.getDataEventProvider();

runner.setStatusListener(4, function(status){println(JSON.stringify(status));});

var count = args.length > 2 ? args[2] : 1000;
var startupRate = args.length > 3 ? args[3] : 50;
runner.run(count, startupRate, args[1]);

println(JSON.stringify(runner.errors));
println(JSON.stringify(sp.summary));

var helper = new StatsHelper();
// find the time that we started this run
println(helper.getEarliestDate(dp));

// calculate the mean response time and std deviation for each step for over all the sessions
var stats = helper.calcMeanAndSigmaForMatrix(dp.values);
for(var i=0; i<stats.length; i++){
	println(JSON.stringify(stats[i]));
}


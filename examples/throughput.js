// Example Muckle client script that shows use of StatsHelper to 
// calculate throughput and response times over time.

require('jslibs/statsHelper.js')

var runner = new SessionRunner('localhost', 8080);
var sp = runner.getSummaryProvider();
var dp = runner.getDataEventProvider();

runner.setStatusListener(4, function(status){println(JSON.stringify(status));});

var count = args.length > 2 ? args[2] : 1000;
var startupRate = args.length > 3 ? args[3] : 50;
runner.run(count, startupRate, args[1]);

println(JSON.stringify(runner.errors));
println(JSON.stringify(sp.summary));

var helper = new StatsHelper();

// put the collected DataEvents into one second slots
var slots = helper.putMatrixInTimeslots(dp.values, 1000);

// for each slot calculate the throughput and average response times
var throughputs = [];
for(var i=0; i<slots.length; i++){
	var throughput = 0;
	var responseTimes = [];

	var slotEntries = slots[i];
	for(var si=0; si<slotEntries.length; si++){
		throughput += slotEntries[si].contentLength;
		responseTimes.push(slotEntries[si].end-slotEntries[si].start);
	}
	// push an anonymous object for each slot
	throughputs.push({ throughput: throughput, 
		               responseTime: helper.convertMeanSigmaToMillis(helper.calcMeanAndSigma(responseTimes))});
}

// display data 
for(var i=0; i<throughputs.length; i++){
	println(JSON.stringify(throughputs[i]));
}


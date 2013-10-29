require("../statsHelper.js");

suite("The StatsHelper calcMean function ", function(){
	
	var helper;
	
	before(function(){
		helper = new StatsHelper();
	});
	
	test("returns zero  when no parameter is supplied", function(){
		assertEquals(0, helper.calcMean());
	});
	
	test("returns zero  when empty array is supplied", function(){
		assertEquals(0, helper.calcMean([]));
	});
	
	test("returns zero when array has zeros", function(){
		assertEquals(0, helper.calcMean([0, 0, 0]));
	});
	
	test("returns the value of the data point when only one present ", function(){
		assertEquals(22, helper.calcMean([22]));
	});
	
	test("returns the mean of a set of datapoints", function(){
		assertEquals(4, helper.calcMean([2, 4, 6]));
	});
	
	test("returns the mean of a set of datapoints that are strings", function(){
		assertEquals(4, helper.calcMean(['2', '4', '6']));
	});
	
	
	
});

suite("The StatsHelper calcMeanAndSigma function ", function(){
	
	var helper;
	
	before(function(){
		helper = new StatsHelper();
	});
	
	test("returns zero for mean and sigma when no parameter is supplied", function(){
		var res = helper.calcMeanAndSigma();
		assertEquals(0, res.average);
		assertEquals(0, res.sigma);
	});
	
	test("returns zero for mean and sigma when empty array is supplied", function(){
		var res = helper.calcMeanAndSigma([]);
		assertEquals(0, res.average);
		assertEquals(0, res.sigma);
	});
	
	test("returns sigma of zero when when only one datapoint is supplied", function(){
		var res = helper.calcMeanAndSigma([22]);
		assertEquals(22, res.average, "average");
		assertEquals(0, res.sigma, "sigma");
	});
	
	test("returns mean and sigma of supplied datapoints", function(){
		var res = helper.calcMeanAndSigma([600, 470, 170, 430, 300]);
		assertEquals(394, res.average, "average");
		assertEquals(147, Math.floor(res.sigma), "sigma");
	});
	
	test("returns mean and sigma of supplied datapoints that are strings", function(){
		var res = helper.calcMeanAndSigma(['600', '470', '170', '430', '300']);
		assertEquals(394, res.average, "average");
		assertEquals(147, Math.floor(res.sigma), "sigma");
	});
	
});

suite("The StatsHelper calcMeanAndSigmaForDataEvents function ", function(){
	
	var helper;
	
	before(function(){
		helper = new StatsHelper();
	});
	
	test("returns zero for mean and sigma when no parameter is supplied", function(){
		var res = helper.calcMeanAndSigmaForDataEvents();
		assertEquals(0, res.average, "average");
		assertEquals(0, res.sigma, "sigma");
	});
	
	test("returns zero for mean and sigma when empty array is supplied", function(){
		var res = helper.calcMeanAndSigmaForDataEvents([]);
		assertEquals(0, res.average, "average");
		assertEquals(0, res.sigma, "sigma");
	});
	
	test("returns  mean and sigma of supplied data events", function(){
		var res = helper.calcMeanAndSigmaForDataEvents([new DataEvent(0,20), new DataEvent(0,40), new DataEvent(0,60)]);
		assertEquals(40, res.average, "average");
		assertEquals(16, Math.floor(res.sigma), "sigma");
	});

});

suite("The StatsHelper calcMeanAndSigmaForMatrix function ", function(){
	
	var helper;
	
	before(function(){
		helper = new StatsHelper();
	});
	
	test("returns empty array when no parameter is supplied", function(){
		var results = helper.calcMeanAndSigmaForMatrix();
		assertEquals(0, results.length);
	});
	
	test("returns empty array when empty array is supplied", function(){
		var results = helper.calcMeanAndSigmaForMatrix([]);
		assertEquals(0, results.length);
	});
	
	test("returns array when empty data event arrays are supplied", function(){
		var results = helper.calcMeanAndSigmaForMatrix([[], []]);
		assertEquals(0, results.length);
	});
	
	test("returns one element for two sessions of one step", function(){
		var results = helper.calcMeanAndSigmaForMatrix([[new DataEvent(0,220000000)], [new DataEvent(0,240000000)]]);
		assertEquals(1, results.length, "length");
		assertEquals(230, results[0].average, "average");
		assertEquals(10, Math.floor(results[0].sigma), "sigma");
	});
	
	test("returns two elements for one session of two steps", function(){
		var results = helper.calcMeanAndSigmaForMatrix([[new DataEvent(0,220000000), new DataEvent(0,240000000)]]);
		assertEquals(2, results.length, "length");
		assertEquals(220, results[0].average, "average");
		assertEquals(0, Math.floor(results[0].sigma), "sigma");
		assertEquals(240, results[1].average, "average");
		assertEquals(0, Math.floor(results[1].sigma), "sigma");
	});
	
});


suite("The StatsHelper getEarliestDate function ", function(){
	var helper;
	var dataProvider;
	var getMillisParamValue;
	
	before(function(){
		helper = new StatsHelper();
		getMillisParamValue = -1;
		dataProvider = {values:[], timeResolver: {getMillisFor: function(nanos){getMillisParamValue=nanos; return 0;}}};
	});
	
	test("returns null if no values supplied", function(){
		dataProvider.values.push([]);
		assertEquals(null, helper.getEarliestDate(dataProvider));
	});
	
	test("returns null if empty values supplied", function(){
		assertEquals(null, helper.getEarliestDate(dataProvider));
	});
	
	test("returns the earliest date", function(){
		dataProvider.values.push([new DataEvent(2,220000000)]);
		dataProvider.values.push([new DataEvent(1,240000000)]);
		var date = helper.getEarliestDate(dataProvider);
		assertEquals(1, getMillisParamValue, "getMillisParamValue");
		assertEquals(70, date.getYear());
	});

});

suite("The StatsHelper putMatrixInTimeslots function ", function(){
	var helper;
	var matrix;
	
	before(function(){
		helper = new StatsHelper();
		matrix = [];
	});
	
	test("returns an empty array if no values supplied", function(){
		assertEquals(0, helper.putMatrixInTimeslots().length);
	});
	
	test("returns an empty array if values matrix is empty", function(){
		assertEquals(0, helper.putMatrixInTimeslots(matrix).length);
	});
	
	test("returns the expected array when only one datapoint", function(){
		matrix.push([new DataEvent(210000000,220000000)]);
		var sloted = helper.putMatrixInTimeslots(matrix, 1000);
		assertEquals(1, sloted.length);
		assertEquals(matrix[0][0], sloted[0][0]);
	});
	
	test("defaults to 1 second slots when no slot time specified", function(){
		matrix.push([new DataEvent(210000000,220000000), new DataEvent(1220000000,1240000000)]);
		var sloted = helper.putMatrixInTimeslots(matrix);
		assertEquals(2, sloted.length);
		assertEquals(matrix[0][0], sloted[0][0]);
		assertEquals(matrix[0][1], sloted[1][0]);
	});
	
	test("returns the expected array when only one session has datapoints", function(){
		matrix.push([new DataEvent(210000000,220000000), new DataEvent(1220000000,1240000000)]);
		var sloted = helper.putMatrixInTimeslots(matrix, 1000);
		assertEquals(2, sloted.length);
		assertEquals(matrix[0][0], sloted[0][0]);
		assertEquals(matrix[0][1], sloted[1][0]);
	});
	
	test("returns the expected array for multiple sessions of one datapoint", function(){
		matrix.push([new DataEvent(1220000000,1240000000)]);
		matrix.push([new DataEvent(210000000,220000000)]);
		var sloted = helper.putMatrixInTimeslots(matrix, 1000);
		assertEquals(2, sloted.length);
		assertEquals(matrix[1][0], sloted[0][0]);
		assertEquals(matrix[0][0], sloted[1][0]);
	});


});


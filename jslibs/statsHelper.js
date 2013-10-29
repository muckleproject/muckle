function StatsHelper(){
	
}

StatsHelper.prototype = {
		
	calcMean: function (/*Array*/ dataPoints){
		var mean = 0;
		if(dataPoints !== undefined){
			if(dataPoints.length > 0){
				for(var i=0; i<dataPoints.length; i++){
					mean += (dataPoints[i]*1);
				}
				mean = mean/dataPoints.length;
			}
		}
		return mean;
	},

	calcMeanAndSigma: function (/*Array*/ dataPoints){
		var result = {};
		result.average = this.calcMean(dataPoints);
		
		var diffsSquared = [];
		if(dataPoints !== undefined){
			for(var i=0; i<dataPoints.length; i++){
				var diff = dataPoints[i]-result.average;
				diffsSquared.push(diff*diff);
			}
		}
		result.sigma = Math.sqrt(this.calcMean(diffsSquared));
		
		return result;
	},

	calcMeanAndSigmaForDataEvents: function (/*Array<DataEvent>*/ dataEvents){
		var dataPoints = [];
		
		if(dataEvents !== undefined){
			for(var i=0; i<dataEvents.length; i++){
				dataPoints.push(dataEvents[i].elapsed());
			}
		}
	
		return this.calcMeanAndSigma(dataPoints);
	},
	
	calcMeanAndSigmaForMatrix: function (/*Array<Array<DataEvent>>*/ matrix){
		var results = [];
		
		if(matrix !== undefined){
			if(matrix.length > 0){
				var columns = matrix[0].length;
				for(var col=0; col<columns; col++){
					var columnData = [];
					for(var row=0; row<matrix.length; row++){
						columnData.push(matrix[row][col]);
					}
					results.push(this.convertMeanSigmaToMillis(this.calcMeanAndSigmaForDataEvents(columnData)));
				}
			}
		}
		
		return results;
	},
	
	convertMeanSigmaToMillis: function(/*object*/ meanSigma){
		meanSigma.average = this.convertToMillis2DecimalPlaces(meanSigma.average);
		meanSigma.sigma = this.convertToMillis2DecimalPlaces(meanSigma.sigma);
		return meanSigma;
	},
	
	convertToMillis2DecimalPlaces: function(/*number*/ nanos){
		return Math.round(nanos/10000)/100;
	},
	
	getEarliestDate: function(dataProvider){
		var earliest = this.getEarliestStart(dataProvider.values);
		return earliest !== null ? new Date(dataProvider.timeResolver.getMillisFor(earliest.start)) : null;
	},

	getEarliestStart: function(matrix){
		return this.scan(matrix, function(first, second){ return first.start < second.start});
	},

	getLatestEnd: function(matrix){
		return this.scan(matrix, function(first, second){ return first.end > second.end});
	},

	scan: function(matrix, comparator){
		var result = null;
		
		if(matrix !== undefined){
			if(matrix.length > 0){
				var columns = matrix[0].length;
				for(var col=0; col<columns; col++){
					for(var row=0; row<matrix.length; row++){
						if(result !== null){
							if(comparator(matrix[row][col], result)){
								result = matrix[row][col];
							}
						}
						else {
							result = matrix[row][col];
						}
					}
				}
			}
		}
		
		return result;
	},
	
	putMatrixInTimeslots: function(matrix, millisecondTimeslotSpan) {
		var slots = [];
		
		var latest = this.getLatestEnd(matrix);
		if(latest != null){
			if(millisecondTimeslotSpan == null){
				millisecondTimeslotSpan = 1000;
			}
			var earliest = this.getEarliestStart(matrix);
			var nanoTimeslotSpan = millisecondTimeslotSpan*1000000;
			var slotCount = Math.ceil((latest.end - earliest.start)/nanoTimeslotSpan);
			while(slotCount-- > 0){
				slots.push([]);
			}
			
			// now we have the slots allocate the entries
			var columns = matrix[0].length;
			for(var col=0; col<columns; col++){
				for(var row=0; row<matrix.length; row++){
					var slotIndex = Math.floor((matrix[row][col].end - earliest.start)/nanoTimeslotSpan);
					slots[slotIndex].push(matrix[row][col]);
				}
			}
		}
		
		return slots;
	}


};



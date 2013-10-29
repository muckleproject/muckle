function ExampleObject(){
	this.lastResult = undefined;
}

ExampleObject.prototype = {

	add: function(a, b){
		this.lastResult = a + b;
		return this.getLastResult();
	},
	
	getLastResult: function(){
		return this.lastResult;
	}
};
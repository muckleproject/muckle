require("exampleObject.js");

suite("An exampleObject can", function(){
	
	var ex;
	
	before(function(){
		ex = new ExampleObject();
	})
	
	test("add two numbers", function(){
		assertEquals(4, ex.add(2, 2));
	});
	
	test("remember the last result", function(){
		ex.add(2, 2);
		assertEquals(4, ex.getLastResult());
	});
	
});
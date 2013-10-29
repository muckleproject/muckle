require('../encodeHelper.js');

suite("The uriEncodeObject function", function(){
	
	test("when called with no object returns empty string", function(){
		assertEquals('', uriEncodeObject());
	});
	
	test("when called with string instead of object returns empty string", function(){
		assertEquals('', uriEncodeObject('a string'));
	});
	
	test("when called with empty object returns empty string", function(){
		assertEquals('', uriEncodeObject({}));
	});
	
	test("when called with boolean primitive param encodes correctly", function(){
		assertEquals('x=true', uriEncodeObject({x:true}));
	});
	
	test("when called with numeric primitive param encodes correctly", function(){
		assertEquals('x=22', uriEncodeObject({x:22}));
	});
	
	test("when called with string primitive param encodes correctly", function(){
		assertEquals('x=22', uriEncodeObject({x:'22'}));
	});
	
	test("when called with string object param encodes correctly", function(){
		assertEquals('x=22', uriEncodeObject({x:new String('22')}));
	});
	
	test("when called with array param adds params with same name", function(){
		assertEquals('x=1&x=2&x=3', uriEncodeObject({x:[1,2,3]}));
	});

	test("when called with multi param object adds all parameters ", function(){
		assertEquals('x=22&y=some%20string', uriEncodeObject({x:22, y:'some string'}));
	});
	
});

suite("The uriDecodeString function", function(){
	
	test("when called with no string returns empty object", function(){
		assertEquals("{}", JSON.stringify(uriDecodeString()));
	});
	
	test("when called with single undefined parameter decodes an empty property", function(){
		assertEquals("{\"x\":\"\"}", JSON.stringify(uriDecodeString("x")));
	});
	
	test("when called with single parameter creates property", function(){
		assertEquals("{\"x\":\"1\"}", JSON.stringify(uriDecodeString("x=1")));
	});
	
	test("when called with multiple parameters creates multiple properties", function(){
		assertEquals("{\"x\":\"1\",\"y\":\"fred\"}", JSON.stringify(uriDecodeString("x=1&y=fred")));
	});
	
	test("when called with an encoded parameter decodes value", function(){
		assertEquals("{\"x\":\"some string\"}", JSON.stringify(uriDecodeString("x=some%20string")));
	});
	
	test("when called with parameters of same name creates an array property", function(){
		assertEquals("{\"x\":[\"1\",\"2\",\"3\"]}", JSON.stringify(uriDecodeString("x=1&x=2&x=3")));
	});
	
});



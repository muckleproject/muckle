require('../jslibs/encodeHelper.js');

// this POST example uses one of the servlet examples supplied with Tomcat
var req = new HttpRequest("/examples/servlets/servlet/RequestParamExample", 'POST');
req.delay = 1000;
var count = 20;

session.onNextRequest = function (){
	var params = {firstname:'me', lastname: new Date().getTime()};
	req.setContent(uriEncodeObject(params), 'application/x-www-form-urlencoded');
	return count-- > 0 ? req : null;
};

session.onHandleResponse = function (resp){
	// just read the contents.
	resp.getContent();
};


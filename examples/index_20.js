// Example Muckle client script

// create a request to be used for all steps. Uri and methods are defaults - "/" and "GET" respectively.
var req = new HttpRequest();

// delay 1000 ms before sending request
req.delay = 1000;

// number of iterations
var count = 20;

// number of retries
var retries = 0;

// set the handler that provides the HTTPRequest to be sent
session.onNextRequest = function (){
	// reset retries
	retries = 0; 
	//return the request or null if we have done enough
	return count-- > 0 ? req : null;
};

// set up the handler that processes the response
session.onHandleResponse = function (resp){
	// check the status aborting if not what was expected.
	if(resp.status != 200){
		throw "Unexpected status <" + resp.status + ">";
	}
};

// set up the handler for network errors
session.onHandleError = function (error){
	// if retries have been exhausted then abort otherwise try again
	return retries-- > 0 ? HttpErrorAction.RETRY : HttpErrorAction.ABORT;
};

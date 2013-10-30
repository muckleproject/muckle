var log = [];

trace.onConnectStart = function (){
	log.push({connectStartTime: new Date().getTime()});
};

trace.onConnected = function (){
	log.push({connectedTime: new Date().getTime()});
};

trace.onSend = function (req){
	log.push({request: {uri: req.uri}});
};

trace.onRetry = function (req){
	var current = log.pop();
	current.retry = {retryTime: new Date().getTime()};
	log.push(current);
};

trace.onReceive = function (resp){
	var current = log.pop();
	current.response = {status: resp.status};
	log.push(current);
};

trace.onError = function (message){
	var current = log.pop();
	current.response = {error: message};
	log.push(current);
};

trace.getResult = function(){
	return log;
}



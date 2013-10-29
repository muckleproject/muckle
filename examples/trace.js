var log = [];

trace.onRequest = function (req){
	log.push({request: {uri: req.uri}});
};

trace.onResponse = function (resp){
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


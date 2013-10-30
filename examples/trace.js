var log = [];

trace.onSend = function (req){
	log.push({request: {uri: req.uri}});
};

trace.onReceive = function (resp){
	var current = log.pop();
	current.response = {status: resp.status};
	log.push(current);
println(JSON.stringify(current));
};

trace.onError = function (message){
	var current = log.pop();
	current.response = {error: message};
	log.push(current);
};

trace.getResult = function(){
	return log;
}


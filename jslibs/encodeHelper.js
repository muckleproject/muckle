function uriEncodeObject(obj){
	var arr = [];
	for(var name in obj){
		var property = obj[name];
		if(Object.prototype.toString.apply(property) !== '[object Array]'){
			arr.push(escape(name) + "=" + escape(property));
		}
		else {
			for(var i=0; i<property.length; i++){
				arr.push(escape(name) + "=" + escape(property[i]));
			}
		}
	}
	return arr.join('&');
}

function uriDecodeString(paramsString){
	var decoded = new Object();
	if(paramsString != null){
		var regex = /([^&=]+)=?([^&]*)/g;
		var extracted;
		while( extracted = regex.exec(paramsString)){
			var name = unescape(extracted[1]);
			var value = unescape(extracted[2]);
			var property = decoded[name];
			if(property === undefined){
				decoded[name] = value;
			}
			else if(Object.prototype.toString.apply(property) === '[object Array]'){
				property.push(value);
			}
			else {
				var arr = [];
				arr.push(property);
				arr.push(value);
				decoded[name] = arr;
			}
		}
	}
	return decoded;
}
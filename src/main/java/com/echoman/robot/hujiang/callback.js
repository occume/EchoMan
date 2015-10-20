
var code, message, 
	data,
	userid, username, ssotoken;

function parseLoginResult(result){
	code = result.code;
	message = result.message;
	data = result.data;
	userid = data.userid;
	username = data.username;
	ssotoken = data.ssotoken;
}

var verifyCode, salt;

function ptui_checkVC(a, d, b, f, c){
	verifyCode = d;
	salt = b;
}

var loginResultCode, loginResultMsg, userName;
function ptuiCB(a, b, c, d, e, f){
	loginResultCode = a;
	loginResultMsg = e;
	userName = f;
}
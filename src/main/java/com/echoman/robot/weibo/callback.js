;
var retcode, servertime, pcid, 
	nonce, pubkey, rsakv, is_openlock,
	lm, smsurl, showpin, exectime;

var arrURL;

var sinaSSOController = {
	preloginCallBack: function(data){
		retcode = data.retcode;
		servertime = data.servertime;
		pcid = data.pcid;
		nonce = data.nonce;
		pubkey = data.pubkey;
		rsakv = data.rsakv;
	},
	setCrossDomainUrlList: function(data){
		retcode = data.retcode;
		arrURL = data.arrURL;
	}
};

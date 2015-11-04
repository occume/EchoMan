;
var retcode, servertime, pcid, 
	nonce, pubkey, rsakv, is_openlock,
	lm, smsurl, showpin, exectime;

var arrURL;
var uniqueid;

var sinaSSOController = {
	preloginCallBack: function(data){
		retcode = data.retcode;
		servertime = data.servertime;
		pcid = data.pcid;
		nonce = data.nonce;
		pubkey = data.pubkey;
		rsakv = data.rsakv;
	},
	feedBackUrlCallBack: function(data){
		if(data.result){}
			uniqueid = data.userinfo.uniqueid;
	},
	setCrossDomainUrlList: function(data){
		retcode = data.retcode;
		arrURL = data.arrURL;
	}
};

var parent = {
	sinaSSOController: sinaSSOController
};

/**
 *  
 */
var domid, relationMyfollowHtml;
var FM = {
	view: function(data){
		domid = data.domid;
		relationMyfollowHtml = data.html;
	}
};
/**
 * search ui
 */
var searchHtml;
//STK && STK.pageletM && STK.pageletM.view
var STK = {
	pageletM:{
		view: function(data){
			searchHtml = data.html;
		}
	}	
};

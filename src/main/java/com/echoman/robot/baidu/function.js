;
var guideRandom = (function() {
    return "xxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g,
    function(c) {
        var r = Math.random() * 16 | 0,
        v = c == "x" ? r: (r & 3 | 8);
        return v.toString(16);
    }).toUpperCase();
})();

var token;
var getToken = function(rsp) {
    token = rsp.data.token;
};

var id, userId, name, userName, portrait;

function fillModel(){
	if(!(PageData && PageData.user)) return;
	var user = PageData.user;
	id = user.id;
	userId = user.user_id;
	name = user.name;
	userName = user.user_name;
	portrait = user.portrait;
}

;
var fid, tbs, forumName, 
	threadTitle, author, totalPage, replyNum;

function fillThreadInfo(){
	fid = PageData.forum.forum_id;
	tbs = PageData.tbs;
	forumName = PageData.forum.forum_name;
	threadTitle = PageData.thread.title;
	author =  PageData.thread.author;
	replyNum = new Number(PageData.thread.reply_num);
	totalPage = new Number(PageData.pager.total_page);
}

;
function _GetGroupPortal_Callback(result){
	var groups = result.data.group;
	_.each(groups, function(item, index){
		groupMap.put(new java.lang.Long(item.groupid), item.groupname);
	});
};

var groupClass, createTime, fingerMemo, groupName;

function _GroupMember_Callback(result){
	createTime = new Number(result.data.create_time);
	groupClass = new Number(result.data['class']);
	fingerMemo = result.data.finger_memo;
	groupName = result.data.group_name;
	
	var items = result.data.item;
	_.each(items, function(item, index){
		members.add(new com.echoman.robot.qq.model.QqGroupMember(item.uin, item.nick, gid));
	});
}
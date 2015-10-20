;

function _GetGroupPortal_Callback(result){
	var groups = result.data.group;
	_.each(groups, function(item, index){
		groupMap.put(new java.lang.Long(item.groupid), item.groupname);
	});
}
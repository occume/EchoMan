;

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

fillModel();

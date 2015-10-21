# EchoMan

Usage:

	// 
	
	//login and sign

	Robot baiduRobot = Robots.newRobot(RobotType);
	baiduRobot
	.setAccount(account)
	.setPassword(password)
	.login()
	.sign();
	
	// reply thread
	baiduRobot.replyThread(threadid, content);
	
	// QQ group sign
	qqRobot.bachGroupSign();
	
	// do sth with the logined http connection
	LoginedHttpClient httpClient = baiduRobot.getHttpClient();
	
package com.echoman.robot.qq;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.model.RobotBean;
import com.echoman.robot.qq.model.QqGroup;
import com.echoman.robot.qq.model.GroupBox;
import com.echoman.robot.qq.model.QqGroupMember;
import com.echoman.storage.AsyncSuperDao;
import com.echoman.storage.Storable;
import com.echoman.util.CommonUtil;

public class QQBiz {

	private final static Logger LOG = LoggerFactory.getLogger(QQBiz.class);
	
	private QQRobot currRobot;
	private AsyncSuperDao dao = new AsyncSuperDao("jtyd_", 3);
	
	public QQBiz(){
		RobotBean bean = new RobotBean();
		bean.setAccount("530050582");
		bean.setPassword("guangguangshop-1");
		bean.setType("QQ");
		currRobot = new QQRobot(bean);
		currRobot.login();
	}
	
	private static QQBiz instance = new QQBiz();
	public static QQBiz getInstance(){ return instance;}
	
	public void fetchQQByGroup(){
		Map<Long, String> groupMap = currRobot.getGroupList();
		for(long gid: groupMap.keySet()){
			if(		gid == 87422469
				||	gid == 307599835
				||	gid == 25238781
				||	gid == 13222834
				||	gid == 348483138
				||	gid == 166272182
				||	gid == 259893200
			) continue;
			
			GroupBox box = currRobot.getGroupInfo(gid);
			QqGroup group = box.getGroup();
		
			dao.saveNow(group);
			List<QqGroupMember> mbs = box.getMembers();
			for(QqGroupMember member: mbs){
				dao.save(member);
			}
			CommonUtil.wait2(3000, 5000);
		}
		dao.save(Storable.IAMLATER);
//		System.out.println(groupMap);
	}
}

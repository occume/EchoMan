package com.echoman.robot;

import java.util.Map;

import com.echoman.model.RobotBean;
import com.echoman.robot.baidu.BaiduRobot;
import com.echoman.robot.hujiang.HujiangRobot;
import com.echoman.robot.qq.QQRobot;
import com.echoman.robot.weibo.WeiboRobot;
import com.echoman.robot.youku.YoukuRobot;
import com.google.common.collect.Maps;

import static com.echoman.robot.RobotType.*;

public class Factorys {
	
	public interface RobotFactory {
		public Robot newRobot(RobotBean bean);
	}
	
	private static Map<RobotType, RobotFactory> factories = Maps.newHashMap();
	
	static{
		factories.put(BAIDU, new RobotFactory() {
			@Override
			public Robot newRobot(RobotBean bean) {
				return new BaiduRobot(bean);
			}
		});
		factories.put(QQ, new RobotFactory() {
			@Override
			public Robot newRobot(RobotBean bean) {
				return new QQRobot(bean);
			}
		});
		factories.put(WEIBO, new RobotFactory() {
			@Override
			public Robot newRobot(RobotBean bean) {
				return new WeiboRobot(bean);
			}
		});
		factories.put(HUJIANG, new RobotFactory() {
			@Override
			public Robot newRobot(RobotBean bean) {
				return new HujiangRobot(bean);
			}
		});
		factories.put(YOUKU, new RobotFactory() {
			@Override
			public Robot newRobot(RobotBean bean) {
				return new YoukuRobot(bean);
			}
		});
	}
	
	public static RobotFactory getFactory(RobotType type){
		return factories.get(type);
	}
}

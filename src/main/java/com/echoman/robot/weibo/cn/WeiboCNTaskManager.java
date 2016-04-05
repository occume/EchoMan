package com.echoman.robot.weibo.cn;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weibo4j.Account;
import weibo4j.Timeline;
import weibo4j.Weibo;
import weibo4j.http.ImageItem;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;

import com.echoman.model.RobotBean;
import com.echoman.model.SendTasks;
import com.echoman.model.SendTasksLog;
import com.echoman.robot.weibo.model.WeiboUser;
import com.echoman.robot.weixin.model.WeixinArticle;
import com.echoman.util.CommonUtil;

public class WeiboCNTaskManager {

	private final static Logger LOG = LoggerFactory
			.getLogger(WeiboCNTaskManager.class);

	private WeiboCNDao dao;
	private WeiboCNBiz scheduler;

	public WeiboCNTaskManager(WeiboCNDao dao) {
		if(dao == null) dao = new WeiboCNDao("jtyd_", 1);
		this.dao = dao;
	}

	/**
	 * 私信，链接：公众号文章
	 * 
	 * @param user
	 * @param target
	 * @param content
	 */
	public void sendMessage4GZH(SendTasks sendTasks) {

		RobotBean bean = new RobotBean("WEIBO", sendTasks.getUserName(),
				sendTasks.getUserPassword());
		WeiboCNRobot robot = new WeiboCNRobot(bean);
		robot.login();

		WeixinArticle article = dao.getWeixinArticleById(sendTasks
				.getArticleId());
		LOG.info("Get article: {}", article);
		String content = "恭喜发财";
		if (article != null) {
			content = article.getArticleDesc();
			content += "\n";
			// content += article.getArticleUrl();
		}

		// List<WeiboUser> targets =
		// dao.getWeiboUserByGrabtag(sendTasks.getFansKeywords());
		List<WeiboUser> targets = dao.getWeiboUserByWithCount(300);

		// String tempUrl =
		// "http://www.wmjtyd.com/f/weixin/jtydWeixinArticle/redirectUrl?"
		String tempUrl = "http://www.wmjtyd.com/f/rd/wx?"
				+ "jtyd_userid={userId}&" + "jtyd_article_id={articleId}&"
				+ "jtyd_task_id={taskId}&" + "form_id={fromPlatformId}"
				+ "to_id={toPlatformId}";

		tempUrl = tempUrl
				.replaceAll("\\{articleId\\}", sendTasks.getArticleId())
				.replaceAll("\\{taskId\\}", sendTasks.getId())
				.replaceAll("\\{fromPlatformId\\}",
						sendTasks.getFromPlatformId() + "")
				.replaceAll("\\{toPlatformId\\}",
						sendTasks.getToPlatformId() + "");

		content += tempUrl;

		for (WeiboUser target : targets) {
			String id = target.getUserId();
			// String id = "1811578147";
			content = content.replaceAll("\\{userId\\}", id);
			LOG.info("Tempurl: {}", tempUrl);
			boolean ok = robot.chatUser(id, content);

			if (ok) {
				SendTasksLog log = new SendTasksLog(sendTasks.getId(),
						sendTasks.getArticleId(), target.getUserId(),
						target.getUserName());

				dao.save(log);
				LOG.info("Send success: {}", log);
			}
			/**
			 * not that good
			 */
			WeiboCNBiz.instance().checkAndChangeRobot();
			CommonUtil.wait2(3 * 1000, 10 * 1000);
		}
	}

	/**
	 * 微博营销 @发送图片
	 */
	private void Advertise(String ... userNames) {
		try {
			SendTasks sendTasks = dao.getSendTasksById(4);
			
			WeixinArticle article = dao.getWeixinArticleById(sendTasks.getArticleId());
			LOG.info("Get article: {}", article);
			String message = article.getArticleDesc();
			message += "\n";
			
			String tempUrl = "http://www.wmjtyd.com/f/rd/wx?"
					+ "jtyd_userid={userId}&" + "jtyd_article_id={articleId}&"
					+ "jtyd_task_id={taskId}&" + "form_id={fromPlatformId}"
					+ "to_id={toPlatformId}";

			tempUrl = tempUrl
					.replaceAll("\\{articleId\\}", sendTasks.getArticleId())
					.replaceAll("\\{taskId\\}", sendTasks.getId())
					.replaceAll("\\{fromPlatformId\\}",
							sendTasks.getFromPlatformId() + "")
					.replaceAll("\\{toPlatformId\\}",
							sendTasks.getToPlatformId() + "");

			message += tempUrl;
		
			message = "【 " + message + " 】 >>> ";
			
			Weibo weibo = new Weibo();
			weibo.setToken("2.004OMbyBSPZlZD2cb49a925dagPjxD");
			Timeline tm = new Timeline();
			
			byte[] content = Files.readAllBytes(Paths.get("D:/tmp/book.jpg"));
			System.out.println("content length:" + content.length);
			ImageItem pic = new ImageItem("pic", content);
			
			List<WeiboUser> targets = dao.getWeiboUserByIDRange(38434, 500);
			int atBatch = 10;
			int cnt = 0;
			
			StringBuffer receivers = new StringBuffer();
			StringBuffer receiverIDs = new StringBuffer();
			
			int sendCount = 1;
			System.out.println(">>> receivers: " + targets.size());
			
			for(WeiboUser user: targets){
				receivers.append(" @").append(user.getUserName());
				receiverIDs.append(user.getUserId()).append(",");
				cnt++;
				if(cnt >= atBatch){
					String message0 = message.replaceAll("\\{userId\\}", receiverIDs.toString().substring(0, 60));
					String advtest = message0 + receivers.toString();
					System.out.println(advtest);
					String s = URLEncoder.encode(advtest, "utf-8");
					Status status = tm.UploadStatus(s, pic);
					System.out.println((sendCount++) + " status :" + status);
					receivers = new StringBuffer();
					receiverIDs = new StringBuffer();
					cnt = 0;
//					break;
					CommonUtil.wait2(1000 * 60 * 3, 1000 * 60 * 5);
				}
			}
			
			System.out.println("Send task complete...");
//			Account account = new Account();
//			System.out.println("getAccountRateLimitStatus:"
//					+ account.getAccountRateLimitStatus());
			
			// }
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	String[] users = {"@金小麦5981", "@西北神牛", "@硅谷神牛", "@大秦神牛"};
	String[] contents = {
		"你好世界",
		"瞻彼淇奥，绿竹猗猗",
		"有匪君子",
		"如切如磋，如琢如磨",
		"瑟兮僴兮，赫兮咺兮",
		"有匪君子，终不可谖兮",
		"瞻彼淇奥，绿竹青青",
		"充耳琇莹，会弁如星",
		"瞻彼淇奥，绿竹如箦",
		"如金如锡，如圭如璧",
		"宽兮绰兮，猗重较兮",
		"善戏谑兮，不为虐兮",
		
		"考槃在涧",
		"硕人之宽",
		"独寐寤言",
		"永矢弗谖",
		"考槃在阿",
		"硕人之薖",
		"独寐寤歌",
		"永矢弗过",
		"考槃在陆",
		"硕人之轴",
		"独寐寤宿",
		"永矢弗告",
	};
	/**
	 * 不用频率，不同内容
	 * @param users
	 */
	public void test(){
		String access_token = "2.004OMbyBSPZlZD2cb49a925dagPjxD";
		Timeline tm = new Timeline(access_token);
		int userSize = users.length;
		int contentSize = contents.length;
		
		for(;;){
			String user = users[CommonUtil.random(0, userSize)];
			String content = contents[CommonUtil.random(0, contentSize)];
			String message = user + " " + content;
			System.out.println(">>>: " + message);
			send(tm, message);
			CommonUtil.wait2(80 * 1000, 120 * 1000);
		}
	}
	
	private void send(Timeline tm, String message){
		try {
			Status status = tm.updateStatus(message);
			System.out.println(status.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String...strings){
		new WeiboCNTaskManager(null).test();
	}
}

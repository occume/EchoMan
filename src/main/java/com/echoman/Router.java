package com.echoman;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.echoman.model.Response;
import com.echoman.robot.Robot;
import com.echoman.robot.Robots;
import com.echoman.robot.baidu.BaiduRobot;
import com.echoman.robot.baidu.model.PostInfo;

@Controller
public class Router {
	
	private static final String CONTENT_JSON = "application/json;charset=UTF-8";
	
	private static final Logger LOG = LoggerFactory.getLogger(Router.class);
	
	private final BaiduRobot baidu = new BaiduRobot();
	
	@RequestMapping(value="/")
	public String index(Map<String, Object> model){
		return "h4/index";
	}
	
//	@RequestMapping(value="/dispense", method = RequestMethod.POST, produces=CONTENT_JSON)
	@RequestMapping(value="/dispense", produces=CONTENT_JSON)
    public @ResponseBody
    Response dispense(HttpServletRequest request) {
		
		String vender = request.getParameter("vender");
		String userName = request.getParameter("name");
		
		System.out.println(vender + " " + userName);
		
//		Robot robot = robotManager.getRobot(vender, userName);
//		
//		if(robot == null){
//			robot = robotManager.newRobot(vender);
//			robotManager.enroll(vender, userName, robot);
//		}
		
//		Response resp = robot.execute(request.getParameterMap());

		return null;
	}
	
	@RequestMapping(value="/userInfos", produces=CONTENT_JSON)
    public @ResponseBody
    Response post() {
		
		Response resp = Response.getOk();
		List<PostInfo> posts = baidu.getPosts();
		
		resp.setPayload(posts);
		
		return resp;
	}

}

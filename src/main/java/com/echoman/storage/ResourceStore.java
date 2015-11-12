package com.echoman.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.echoman.util.CommonUtil;
import com.echoman.util.NamedThreadFactory;
import com.echoman.util.RegexUtil;

public class ResourceStore{
	
	private static class Holder{
		private static ResourceStore instance = new ResourceStore();
	}
	
	public static ResourceStore instance(){
		return Holder.instance;
	}

	private ExecutorService storeWorker;
	private String home = "D:/tmp/echoman/";
	
	private ResourceStore(){
		storeWorker = Executors.newCachedThreadPool(new NamedThreadFactory("ECHOMAN-RESOURCE-STORE-"));
	}
	
	public void add(ResurceBean bean){
		
		try {
			URL _url = new URL(bean.getUrl());
			String fileName = RegexUtil.getFileNameOfURL(bean.getUrl());
			String parentDir = home + bean.getSource() + "/"+ bean.getUserName();
			Path parentPath = Paths.get(parentDir);
			/**
			 * if parent direct does not exist
			 */
			if(!Files.exists(parentPath)) Files.createDirectory(parentPath);
			
			Path target = Paths.get(parentDir +"/" + fileName);
			if(!Files.exists(target)) Files.createFile(target);
			
			storeWorker.execute(new Loader(_url, target));
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void add(List<ResurceBean> urls){
		for(ResurceBean url: urls){
			add(url);
		}
	}
	
	private static class Loader implements Runnable{
		
		private InputStream in;
		private OutputStream out;
		private Path path;
		private ArrayList<ProgressListener> listeners;
		
		public Loader(URL url, Path target) throws IOException{
			in = url.openConnection().getInputStream();
			path = target;
			out = Files.newOutputStream(path);
			listeners = new ArrayList<>();
		}
		
		private void addListener(ProgressListener progressListener) {
			listeners.add(progressListener);
		}
		
		private void updateProgress(int m, int n) {
			for (ProgressListener listener: listeners)
			     listener.onProgress(m, n);
		}
		
		public void run(){
			int n = 0, total = 0;
			byte[] buf = new byte[1024 * 1024];
			try {
				while((n = in.read(buf)) != -1){
					total += n;
					out.write(buf, 0, n);
					out.flush();
					updateProgress(n, total);
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			CommonUtil.wait2(500, 2000);
		}
	}

	private static interface ProgressListener {
		void onProgress(int current, int total);
	}

	public static void main(String[] args) throws Exception {
		
		String url = "http://imgsrc.baidu.com/forum/w%3D580/sign=fad7d441a6ec08fa260013af69ec3d4d/b944ad345982b2b7dd76493a37adcbef77099b66.jpg";
		ResourceStore store = new ResourceStore();
//		store.add(url);
//		(/n)(?=(^/n)*)"
//		Pattern p = Pattern.compile("((/)(?=/):?.*\\..*)$", 1);
//		Pattern p = Pattern.compile("(/(?=(^/)*))");
		Pattern p = Pattern.compile("(?<=/)(([^/])+$)");
		Matcher m = p.matcher(url);
		
		int i = 0;
		if(m.find()){
			System.out.println("--- " + m.group(1));
		}
		
		String strTest = "hellowld";
        Pattern pattern = Pattern.compile("(?=ld)");
        Matcher matcher = pattern.matcher(strTest);
        if(matcher.find()) {
        	System.out.println(matcher);
        	System.out.println(matcher.group());
        }
        System.out.println(matcher.replaceAll("or"));
        
        String str = "12345678";
        Pattern pattern1 = Pattern.compile("(?<=\\d)(?=(\\d{3})+$)");
        Matcher matcher1 = pattern1.matcher(str);
        System.out.println(matcher1.replaceAll(","));
        
        String html = "<div id='post_content_78524296600' class='d_post_content j_d_post_content '>            <img class='BDE_Smiley' width='30' height='30' changedsize='false' "
        		+ "src='http://static.tieba.baidu.com/tb/editor/images/client/image_emoticon20.png'><img class='BDE_Smiley' width='30' height='30' changedsize='false' src='http://static.tieba.baidu.com/tb/editor/images/client/image_emoticon20.png'><img class='BDE_Smiley' width='30' height='30' changedsize='false' src='http://static.tieba.baidu.com/tb/editor/images/client/image_emoticon20.png'>晚上好！小伙伴来粗来！<br><img class='BDE_Image' src='http://imgsrc.baidu.com/forum/w%3D580/sign=125b38c5dff9d72a17641015e42b282a/6ec6a7efce1b9d16f5edc69bf5deb48f8c546425.jpg' changedsize='true' width='560' height='754' size='69160'><br><img class='BDE_Image' src='http://imgsrc.baidu.com/forum/w%3D580/sign=870cf4dcfbfaaf5184e381b7bc5594ed/e4039245d688d43fce83cd087b1ed21b0ef43b25.jpg' changedsize='true' width='560' height='757' size='70069'><br><img class='BDE_Image' src='http://imgsrc.baidu.com/forum/w%3D580/sign=6eb864f98144ebf86d716437e9fbd736/9d35e5dde71190ef731ecf25c81b9d16fcfa6066.jpg' changedsize='true' width='560' height='746' size='93888'></div>";
        Pattern pattern2 = Pattern.compile("src='([^']+)");
        Matcher matcher2 = pattern2.matcher(str);
        
        i = 0;
        while(matcher2.find()){
        	System.out.println(matcher2.group(i++));
        }
	}
}

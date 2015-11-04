package com.echoman.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class LoginedHttpClient {
	
	private final static Logger LOG = LoggerFactory.getLogger(LoginedHttpClient.class);
	
	private CloseableHttpClient httpClient;
	private BasicCookieStore cookieStore;
	
	private AtomicInteger requestCount = new AtomicInteger(0);
	private boolean logined;
	
	public boolean isLogined() {
		return logined;
	}

	public void setLogined(boolean logined) {
		this.logined = logined;
	}
	
	public int getRequestCount(){
		return requestCount.get();
	}

	private LoginedHttpClient(){
		
		cookieStore = new BasicCookieStore();
		CookieSpecProvider easySpecProvider = getCookieSpecProvider();
		Registry<CookieSpecProvider> registry = 
				RegistryBuilder
				.<CookieSpecProvider>create()
				.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
				.register("easy", easySpecProvider)
				.build();
		
		RequestConfig requestConfig = 
				RequestConfig.custom()
				.setCircularRedirectsAllowed(false)
				.setRelativeRedirectsAllowed(false)
				.setRedirectsEnabled(false)
				.setSocketTimeout(10000)
				.build();
		
		RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
		
		HttpHost proxy = new HttpHost("113.160.22.38", 8080);
		
		httpClient = HttpClients
				.custom()
				.setDefaultRequestConfig(requestConfig)
				.setRedirectStrategy(redirectStrategy)
				.setDefaultCookieSpecRegistry(registry)
				.setDefaultCookieStore(cookieStore)
//				.setProxy(proxy)
				.build();
	}
	
	private static CookieSpecProvider getCookieSpecProvider(){
		return new CookieSpecProvider() {
			public CookieSpec create(HttpContext context) {
				return createBrowserCompatSpec();
			}
		};
	}
	
	private static BrowserCompatSpec createBrowserCompatSpec(){
		 return new BrowserCompatSpec() {
				@Override
				public void validate(Cookie cookie, CookieOrigin origin)
						throws MalformedCookieException {
					/**
					 *  do sth against cookie
					 */
				}
			};
	}
	
	private RequestConfig getAllowRedirectReqConf(){
		RequestConfig requestConfig = 
				RequestConfig.custom()
				.setSocketTimeout(10000)
				.build();
		return requestConfig;
	}
	
	private static String getContent(HttpResponse httpResponse)throws IOException {
		
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		String ret = null;
		if (HttpStatus.SC_OK == statusCode || HttpStatus.SC_MOVED_TEMPORARILY == statusCode) {
			if (httpResponse.getEntity() != null) {
				ret = EntityUtils.toString(httpResponse.getEntity(),
						getEntityEncode(httpResponse.getEntity()));
			}
		}
		else{
			LOG.info("Status code is not ok: " + statusCode);
		}
		
		return ret;
	}
	
	private static String getEntityEncode(HttpEntity entity) {
		if (entity.getContentEncoding() != null
				&& Strings.isNullOrEmpty((entity.getContentEncoding().getValue()))) {
			return entity.getContentEncoding().getValue();
		}
		return Constant.Charset.UTF8;
	}
	
	public static LoginedHttpClient newHttp(){
		return new LoginedHttpClient();
	}
	
	public String get(String url){
		return get(url, null);
	}
	
	public String get(String url, Map<String, String> headers, boolean allowRedirect){
		
		String ret = null;
		try{
			
			ret = doGet(url, headers, allowRedirect);
			
		}catch(IOException e) {
			LOG.error("Remote request error: {}", e);
		}
		return ret;
		
	}
	
	public String get(String url, Map<String, String> headers){
		
		return get(url, headers, false);
		
	}
	
	private String doGet(String url, Map<String, String> headers, boolean allowRedirect) throws IOException{
		
		String content = "";
		CloseableHttpResponse response = null;
		HttpGet httpGet = new HttpGet(url);
		
		if(allowRedirect){
			httpGet.setConfig(getAllowRedirectReqConf());
		}
		
		/**
		 *  increment total request count
		 */
		requestCount.incrementAndGet();
		
		try {
			
			if(headers != null){
				for(Entry<String, String> h: headers.entrySet()){
					httpGet.setHeader(h.getKey(), h.getValue());
				}
			}
			
			response = httpClient.execute(httpGet);
			content = getContent(response);
			
			printCookie();
			
		} finally {
			if (response != null)
				response.close();
			httpGet.releaseConnection();
		}
		return content;
	}

	
	public String getJsessionId(){
		List<Cookie> cookies = cookieStore.getCookies();
		for(Cookie c: cookies){
			if(c.getName().equals("JSESSIONID")){
				return c.getValue();
			}
		}
		return "";
	}
	
	public String post(String url){
		
		String ret = null;
		try {
			
			ret = doPost(url, null, null);
			
		} catch (IOException e) {
			LOG.error("Remote request error: {}", e.getMessage());
		}
		return ret;
	}
	
	public String post(String url, Map<String, Object> params, Map<String, String> headers){
		
		String ret = null;
		try {
			
			ret = doPost(url, params, headers);
			
		} catch (IOException e) {
			LOG.error("Remote request error: {}", e.getMessage());
		}
		return ret;
	}
	
	private String doPost(String url, Map<String, Object> params, Map<String, String> headers)
			throws IOException {
		
		/**
		 * 
		 */
		requestCount.incrementAndGet();
		
		String content = "";
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (params != null) {
			Iterator<String> it = params.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = String.valueOf(params.get(key));
				nvps.add(new BasicNameValuePair(key, value));
			}
		}

		HttpPost httpPost = new HttpPost(url);
		
		if(headers != null){
			for(Entry<String, String> h: headers.entrySet()){
				httpPost.setHeader(h.getKey(), h.getValue());
			}
		}

		httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

		CloseableHttpResponse response = null;
		try {
			
			response = httpClient.execute(httpPost);
			content = getContent(response);
			
			printCookie();
			
		} finally {
			if (response != null)
				response.close();
		}
		return content;
	}
	
	private void printCookie(){
		List<Cookie> cookies = cookieStore.getCookies();
		if (cookies.isEmpty()) {

		} else {
			for (int i = 0; i < cookies.size(); i++) {
				LOG.debug(cookies.get(i).toString());
			}
		}
	}
	
	public String getCookie(String name){
		String ret = "";
		List<Cookie> cookies = cookieStore.getCookies();
		if (cookies.isEmpty()) {
			/**
			 *  no cookie
			 */
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				Cookie cookie = cookies.get(i);
				if(cookie.getName().equals(name)){
					ret = cookie.getValue();
				}
			}
		}
		return ret;
	}
}

package com.echoman.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.Consts;
import org.apache.http.Header;
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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
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
	private Header[] currHeaders;
	
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
		/**
		 * 
		 */
		RequestConfig requestConfig = 
				RequestConfig.custom()
				.setCircularRedirectsAllowed(false)
				.setRelativeRedirectsAllowed(false)
				.setRedirectsEnabled(false)
				.setSocketTimeout(30000)
				.build();
		
		RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
		
		HttpHost proxy = new HttpHost("203.195.162.96", 8080);
		
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
			currHeaders = response.getAllHeaders();
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
		return post(url, null, null);
	}

	public String post(String url, Map<String, Object> params, Map<String, String> headers){
		return post(url, params, headers, null, false);
	}
	
	public String post(String url, Map<String, Object> params, 
			Map<String, String> headers, boolean allowRedirect){
		return post(url, params, headers, null, allowRedirect);
	}
	
	public String post(String url, Map<String, Object> params, 
			Map<String, String> headers, String attach){
		return post(url, params, headers, attach, false);
	}
	/**
	 * post with attachment
	 * @param url
	 * @param params
	 * @param headers
	 * @param attach
	 * @param allowRedirect
	 * @return
	 */
	public String post(String url, Map<String, Object> params, 
			Map<String, String> headers, String attach, boolean allowRedirect){
		String ret = null;
		try {
			ret = doPost(url, params, headers, attach, allowRedirect);
		} catch (IOException e) {
			LOG.error("Remote request error: {}", e.getMessage());
		}
		return ret;
	}
	
	private String doPost(String url, Map<String, Object> params, 
			Map<String, String> headers, String attach, boolean allowRedirect)
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
		/**
		 * allow redirect
		 */
		if(allowRedirect){
			httpPost.setConfig(getAllowRedirectReqConf());
		}
		
		if(headers != null){
			for(Entry<String, String> h: headers.entrySet()){
				httpPost.setHeader(h.getKey(), h.getValue());
			}
		}
		
		HttpEntity entity = null;
		
		if(attach != null){
			MultipartEntityBuilder 
			entityBuilder = 
			MultipartEntityBuilder.create()
//			.setCharset(Consts.UTF_8)
			.addBinaryBody("attachment", new File(attach));

			for(NameValuePair nvp: nvps){
//				StringBody stringBody1 = new StringBody("Message 1", ContentType.MULTIPART_FORM_DATA);
//				entityBuilder.addPart(name, contentBody)
				entityBuilder.addTextBody(nvp.getName(), nvp.getValue(), 
						ContentType.create("multipart/form-data", Consts.UTF_8));
			}
			entity = entityBuilder.build();
		}
		else{
			entity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
		}

		httpPost.setEntity(entity);

		CloseableHttpResponse response = null;
		try {
			
			response = httpClient.execute(httpPost);
			content = getContent(response);
			currHeaders = response.getAllHeaders();
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
	
	public String getHeaderVal(String name){
		if(currHeaders == null) return "";
		for(Header hd: currHeaders){
			if(hd.getName().equalsIgnoreCase(name))
				return hd.getValue();
		}
		return "";
	}
	
	public String getLocation(){
		return getHeaderVal("Location");
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

package cn.people.one.core.util.http;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtils {

	private static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)";
	public static final String CHARSET_UTF8 = "UTF-8";
	public static final int TIMEOUT_ONE_MINUTE = 60000;
	/**
	 * GET请求
	 * @param url
	 * @param charset
	 * @param timeout
	 * @return
	 */
	public static String doGet(String url, String charset, int timeout) {
		// HttpClient
		CloseableHttpClient httpClient = HttpClients.custom().setUserAgent(USER_AGENT).build();

		HttpGet httpGet = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(timeout).setConnectTimeout(timeout)
				.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();// 设置请求
		httpGet.setConfig(requestConfig);
		httpGet.addHeader("Connection", "close");
		try {
			// get请求
			HttpResponse httpResponse = httpClient.execute(httpGet);

			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK) {
				// getEntity()
				HttpEntity httpEntity = httpResponse.getEntity();
				if(httpEntity != null) {
					return EntityUtils.toString(httpEntity, charset);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放资源
			httpGet.releaseConnection();
			closeQuietly(httpClient);
		}
		return null;
	}


	  public static String doPost(String url, String json, String charset, int timeout){
			// HttpClient
			CloseableHttpClient httpClient = HttpClients.custom().setUserAgent(USER_AGENT).build();
			// 设置请求
			HttpPost httpPost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();
			httpPost.setConfig(requestConfig);
			httpPost.addHeader("Connection", "close");
	    try {
			StringEntity s = new StringEntity(json,CHARSET_UTF8);
			s.setContentEncoding(CHARSET_UTF8);
			//发送json数据需要设置contentType
			s.setContentType(MediaType.APPLICATION_JSON_VALUE+";charset="+CHARSET_UTF8);
	      httpPost.setEntity(s);
	   // post请求
	   			HttpResponse httpResponse = httpClient.execute(httpPost);

	   			int statusCode = httpResponse.getStatusLine().getStatusCode();
	   			if(statusCode == HttpStatus.SC_OK) {
	   				// getEntity()
	   				HttpEntity httpEntity = httpResponse.getEntity();
	   				if(httpEntity != null) {
	   					return EntityUtils.toString(httpEntity, charset);
	   				}
	   			}
	    } catch (Exception e) {
	      throw new RuntimeException(e);
	    }finally {
			// 释放资源
			httpPost.releaseConnection();
			closeQuietly(httpClient);
		}
		return null;
	  }

	/**
	 * POST请求
	 * @param url
	 * @param params
	 * @param charset
	 * @param timeout
	 * @return
	 */
	public static String doPost(String url, Map<String, String> params, String charset, int timeout) {
		// HttpClient
		CloseableHttpClient httpClient = HttpClients.custom().setUserAgent(USER_AGENT).build();

		HttpPost httpPost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(timeout).setConnectTimeout(timeout)
				.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();// 设置请求
		httpPost.setConfig(requestConfig);

		httpPost.addHeader("Connection", "close");
		// 创建参数队列
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		for(String key : params.keySet()){
			postParams.add(new BasicNameValuePair(key, params.get(key)));
		}
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams, charset);
			httpPost.setEntity(entity);
			httpPost.addHeader("Authorization", getHeader(params));
			// post请求
			HttpResponse httpResponse = httpClient.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK) {
				// getEntity()
				HttpEntity httpEntity = httpResponse.getEntity();
				if(httpEntity != null) {
					return EntityUtils.toString(httpEntity, charset);
				}
			}
//			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
//	          {
////	           //   从头中取出转向的地址
//				String locationUrl=response.getLastHeader("Location").getValue();
//		        get(locationUrl);//跳转到重定向的url
//	            System.out.println("The page was redirected to:" + location);
//	           }
//		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放资源
			httpPost.releaseConnection();
			closeQuietly(httpClient);
		}
		return null;
	}

	private static String getHeader(Map<String, String> params) {
		 String auth = params.get("client_id") + ":" + params.get("client_secret");
	        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
	        String authHeader = "Basic " + new String(encodedAuth);
	        return authHeader;
	}


	/**
	 * 向指定URL发送GET方法的请求
	 *
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @param charsetName
	 *            编码
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param, String charsetName) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			if (param != null && param.trim().length() > 0) {
				urlNameString += "?" + param;
			}
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setConnectTimeout(90000); // 设置超时时间
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			// Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			// for (String key : map.keySet()) {
			// System.out.println(key + "--->" + map.get(key));
			// }
			// 定义 BufferedReader输入流来读取URL的响应
			if (null != charsetName) {
				in = new BufferedReader(new InputStreamReader(connection.getInputStream(), charsetName));
			} else {
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			}
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	private static void closeQuietly(CloseableHttpClient httpClient){
		if(httpClient != null) {
			try {
				httpClient.close();
			} catch (IOException e) {
				//do nothing
			}
		}
	}
}

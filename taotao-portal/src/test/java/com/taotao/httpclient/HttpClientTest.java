package com.taotao.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class HttpClientTest {

	@Test
	public void doGet() throws Exception {
		// 创建一个httpclient对象，这个对象就相当于是浏览器
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// 创建一个GET对象
		HttpGet get = new HttpGet("http://www.sogou.com");
		// 执行请求
		CloseableHttpResponse response = httpClient.execute(get);
		// 取响应的结果
		int statusCode = response.getStatusLine().getStatusCode();// 响应的状态码
		System.out.println(statusCode);
		HttpEntity entity = response.getEntity();// 响应的内容
		String string = EntityUtils.toString(entity, "utf-8");// 可以将响应的内容读到一个字符串中
		System.out.println(string);
		// 关闭httpclient
		response.close();
		httpClient.close();
	}

	@Test
	public void doPost() throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
	
		//创建一个post对象
		HttpPost post = new HttpPost("http://localhost:8082/httpclient/post.action");//请求的是8082的端口，需要先开启该tomcat
		//执行post请求
		CloseableHttpResponse response = httpClient.execute(post);
		String string = EntityUtils.toString(response.getEntity());
		System.out.println(string);
		response.close();
		httpClient.close();
		
	}

}

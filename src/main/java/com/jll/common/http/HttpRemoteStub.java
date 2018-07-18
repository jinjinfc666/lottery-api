package com.jll.common.http;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HttpRemoteStub {
	private static Logger logger = Logger.getLogger(HttpRemoteStub.class);
	
	private final static int SOCKET_TIMEOUT = 10000;
	
	private final static int CONNECT_TIMEOUT = 10000;
	
	/**
	 * synchronously sending the http GET method
	 * the execution result will be returned to the caller and the execution will block until the result is returned from the server.
	 * @param url
	 * @return
	 */
	public static Map<String, Object> synGet(URI uri,Map<String, String> headers,Map<String, String> queries){
		StringBuilder sb = new StringBuilder(uri.toString());
		if(queries != null && queries.size() > 0) {
			if(sb.indexOf("?") <= 0) {
				sb.append("?");
			}
        	Iterator<String> ite = queries.keySet().iterator();
        	while(ite.hasNext()) {
        		String queryName = ite.next();
        		String queryVal = queries.get(queryName);
        		sb.append(queryName).append("=").append(queryVal);
        	}
        }
		
		CloseableHttpClient  httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(sb.toString());
		RequestConfig requestConfig = RequestConfig.custom()  
                .setSocketTimeout(SOCKET_TIMEOUT)  
                .setConnectTimeout(CONNECT_TIMEOUT).build(); 
        httpGet.setConfig(requestConfig);
        
        if(headers != null && headers.size() > 0) {
        	Iterator<String> ite = headers.keySet().iterator();
        	while(ite.hasNext()) {
        		String headerName = ite.next().toString();
        		String headerVal = headers.get(headerName);
        		httpGet.addHeader(headerName, headerVal);
        	}
        }
        
        
		CloseableHttpResponse response = null;
		String responseBody = "";
		Map<String, Object> ret = new HashMap<>();
		
		try {
			response = httpclient.execute(httpGet);
			int status = response.getStatusLine().getStatusCode();  
            if (status == HttpStatus.SC_OK) {
            	HttpEntity entity = response.getEntity();
            	responseBody = EntityUtils.toString(entity);
            	ret.put("responseBody", responseBody);
            }
            ret.put("status", status);
            
            
		} catch (ClientProtocolException e) {
			
		} catch (IOException e) {
			
		}finally {
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
				}
			}
			
			try {
				httpclient.close();
			} catch (IOException e) {
			}
		}
		return ret;
	}
	
	/**
	 * asynchronously sending the http GET method
	 * the execution will not block and return to the caller immediately.
	 * the call back will execute once the server send response
	 * @param url
	 */
	public static void anyGet(URL url, Map<String, Object> headers){
		//after receiving the response from the server, then send message to message queue
		//the call back will execute after reading the message from the message queue
	}
	
	/**
	 * synchronously sending the http POST method
	 * the execution result will be returned to the caller and the execution will block until the result is returned from the server.
	 * @param url
	 * @param params
	 * @return
	 */
	public static Map<String, Object> synPost(URI uri,Map<String, String> headers, Map<String, Object> params){
		Map<String, Object> ret = new HashMap<>();
		CloseableHttpResponse response = null;
		String responseBody = "";
		CloseableHttpClient  httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(uri);
		if(params != null && params.size() > 0) {			
        	HttpEntity entity = produceEntity(headers, params);
        	
        	httpPost.setEntity(entity);
        }
		
		RequestConfig requestConfig = RequestConfig.custom()  
                .setSocketTimeout(SOCKET_TIMEOUT)  
                .setConnectTimeout(CONNECT_TIMEOUT).build(); 
		httpPost.setConfig(requestConfig);
        
        if(headers != null && headers.size() > 0) {
        	Iterator<String> ite = headers.keySet().iterator();
        	while(ite.hasNext()) {
        		String headerName = ite.next().toString();
        		String headerVal = headers.get(headerName);
        		httpPost.addHeader(headerName, headerVal);
        	}
        }
		
		try {
			response = httpclient.execute(httpPost);
			int status = response.getStatusLine().getStatusCode();  
            if (status == HttpStatus.SC_OK) {
            	HttpEntity entity = response.getEntity();
            	responseBody = EntityUtils.toString(entity);
            	ret.put("responseBody", responseBody);
            }
            ret.put("status", status);
            
            
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
				}
			}
			
			try {
				httpclient.close();
			} catch (IOException e) {
			}
		}
		
		return ret;
	}
	
	private static HttpEntity produceEntity(Map<String, String> headers, Map<String, Object> params) {
		HttpEntity entity = null;
		
		if(params != null && params.size() > 0) {
			String contentType = headers.get("Content-Type");
			if(contentType == null || contentType.length() == 0) {
				contentType = "application/x-www-form-urlencoded";
			}
			
			if(contentType.equals("application/json")) {
				entity = produceJSONEntity(params);
			}else if(contentType.equals("application/x-www-form-urlencoded")) {
				entity = produceFormEntity(params);
			}
		}
		
		return entity;
	}

	private static HttpEntity produceFormEntity(Map<String, Object> params) {
		List<NameValuePair> pairs = new ArrayList<>();
		HttpEntity entity = null;
		Iterator<String> ite = params.keySet().iterator();
		while(ite.hasNext()) {
			NameValuePair pair = null;
			String key = ite.next();
			Object val = params.get(key);
			if(val.getClass().getName().contains("Integer")
					|| val.getClass().getName().contains("Float")
					|| val.getClass().getName().contains("Long")
					|| val.getClass().getName().contains("Double")) {
				
				pair = new BasicNameValuePair(key, String.valueOf(val));
			}else {
				pair = new BasicNameValuePair(key, val.toString());
			}
			
			logger.debug(key + "=" + pair.getValue());
			pairs.add(pair);
		}		
		
		entity = new UrlEncodedFormEntity(pairs, Consts.UTF_8);
		
		return entity;
	}

	private static HttpEntity produceJSONEntity(Map<String, Object> params) {
		HttpEntity entity = null;
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		Iterator<String> ite = params.keySet().iterator();
		while(ite.hasNext()) {
			String key = ite.next();
			Object val = params.get(key);
			if(val.getClass().getName().contains("Integer")
					|| val.getClass().getName().contains("Float")
					|| val.getClass().getName().contains("Long")
					|| val.getClass().getName().contains("Double")) {
				
				node = node.putPOJO(key, val);
			}else {
				node = node.put(key, val.toString());
			}
		}
		
		try {
			entity = new StringEntity(mapper.writeValueAsString(node), ContentType.APPLICATION_JSON);
		} catch (UnsupportedCharsetException e) {
			
		} catch (JsonProcessingException e) {
			
		}
		return entity;
	}

	/**
	 * asynchronously sending the http POST method
	 * the execution will not block and return to the caller immediately.
	 * the call back will execute once the server send response
	 * @param url
	 */
	public static void anyPost(URL url, Map<String, Object> headers){
		//after receiving the response from the server, then send message to message queue
		//the call back will execute after reading the message from the message queue
	}
}

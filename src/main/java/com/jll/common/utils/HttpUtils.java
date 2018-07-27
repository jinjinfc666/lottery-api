package com.jll.common.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;



public class HttpUtils {
	public static String ENCODE_TYPE = "UTF-8";
	public static String sendGetRepuest(String url) {
		  StringBuffer returnRemark = new StringBuffer();
		  try {
			   // 发送报文
			   URL sendUrl = new URL(url.trim());
			   URLConnection connection = sendUrl.openConnection();
			   connection.setConnectTimeout(10000);
			   connection.setReadTimeout(10000);
			   connection.setDoOutput(true);
			   // 一旦发送成功，用以下方法就可以得到服务器的回应：
			   String sCurrentLine = "";
			   InputStream l_urlStream = connection.getInputStream();
			   InputStreamReader isr = new InputStreamReader(l_urlStream, "UTF-8");
			   BufferedReader l_reader = new BufferedReader(isr);
			   while ((sCurrentLine = l_reader.readLine()) != null) {
				   returnRemark.append(sCurrentLine.replaceAll("\t", ""));
			   }
		  } catch (Exception ex) {
			  ex.printStackTrace();
		  }
		  return returnRemark.toString();
	}	
	
	public static void main(String[] args) {
		System.out.println(sendGetRepuest("https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=6217002710000684874&cardBinCheck=true"));
	}
}
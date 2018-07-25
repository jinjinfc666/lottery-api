package com.ehome.stock.datasync.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.test.web.servlet.MockMvc;


import com.ehome.test.ControllerJunitBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.meterware.httpunit.DeleteMethodWebRequest;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.PutMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

import junit.framework.Assert;

public class BackstageSysControllerTest extends ControllerJunitBase{
	//protected MockMvc mockMvc;
		
	public BackstageSysControllerTest(String name) {
		super(name);
	}

	
	
	
//	@SuppressWarnings("unchecked")
//	public void testAddStockInfo() throws Exception{
//		//新创建一个“浏览器”对象
//		WebConversation wc = new WebConversation();
//		Map<String, Object> retItems = null;
//		
//		StockInfo stock = new StockInfo();
//		stock.setCode("002038");
//		stock.setField("1");
//		stock.setName("双鹭药业");
//		stock.setStockEx(0);
//		stock.setStatus(1);
//		
//		ObjectMapper mapper = new ObjectMapper();
//		ByteArrayInputStream bis = null;
//		try {
//			// WebRequest类，用于模仿客户的“请求”，通过它可以向服务器发送信息。 Get
//			WebRequest request = new GetMethodWebRequest("http://localhost:8080/api/stocks");
//			
//			//设置请求参数
//			request.setParameter(Constants.REQ_PARAM_STOCK_INFO_CODE, "002038");
//			
//			// WebResponse类，用于模仿浏览器获取服务器端的响应信息。
//			WebResponse response = wc.sendRequest(request);
//			
//			int  status = response.getResponseCode();
//			//比较HttpServletResponse.SC_OK==200    看status是否也是200
//			Assert.assertEquals(HttpServletResponse.SC_OK, status);
//			String result = response.getText();
//			//将结果变为Map集合
//			retItems = mapper.readValue(result, HashMap.class);
//			//Assert.assertNotNull()判断是否是空
//			Assert.assertNotNull(retItems);
//			
//			Assert.assertEquals(Constants.Status.SUCCESS.getValue(), retItems.get(Constants.RESPONSE_KEY_NAME_STATUS));
//			if(retItems.get(Constants.RESPONSE_KEY_NAME_DATA) != null){
//				String stockId = (String)((Map<String, Object>)retItems.get(Constants.RESPONSE_KEY_NAME_DATA)).get("id");
//				request = new DeleteMethodWebRequest("http://localhost:8080/api/stocks/"+stockId);
//				response = wc.sendRequest(request);
//				
//				status = response.getResponseCode();
//				
//				Assert.assertEquals(HttpServletResponse.SC_OK, status);
//				result = response.getText();
//				
//				retItems = mapper.readValue(result, HashMap.class);
//				
//				Assert.assertNotNull(retItems);
//				
//				Assert.assertEquals(Constants.Status.SUCCESS.getValue(), retItems.get(Constants.RESPONSE_KEY_NAME_STATUS));
//			}
//			
//			byte[] source = mapper.writeValueAsBytes(stock);
//			bis = new ByteArrayInputStream(source);
//			
//			request = new PostMethodWebRequest("http://localhost:8080/api/stocks", bis, "application/json;chatset=utf-8");
//			
//			response = wc.sendRequest(request);
//			
//			status = response.getResponseCode();
//			
//			Assert.assertEquals(HttpServletResponse.SC_OK, status);
//			result = response.getText();
//			
//			
//			
//			retItems = mapper.readValue(result, HashMap.class);
//			
//			Assert.assertNotNull(retItems);
//			
//			Assert.assertEquals(Constants.Status.SUCCESS.getValue(), retItems.get(Constants.RESPONSE_KEY_NAME_STATUS));
//			
//			//复位
//			request = new GetMethodWebRequest("http://localhost:8080/api/stocks");
//			request.setParameter(Constants.REQ_PARAM_STOCK_INFO_CODE, "002038");
//			response = wc.sendRequest(request);
//
//			status = response.getResponseCode();
//
//			Assert.assertEquals(HttpServletResponse.SC_OK, status);
//			result = response.getText();
//
//			retItems = mapper.readValue(result, HashMap.class);
//			Assert.assertNotNull(retItems);
//
//			Assert.assertEquals(Constants.Status.SUCCESS.getValue(), retItems.get(Constants.RESPONSE_KEY_NAME_STATUS));
//			if (retItems.get(Constants.RESPONSE_KEY_NAME_DATA) != null) {
//				String stockId = (String) ((Map<String, Object>) retItems.get(Constants.RESPONSE_KEY_NAME_DATA))
//						.get("id");
//				request = new DeleteMethodWebRequest("http://localhost:8080/api/stocks/" + stockId);
//				response = wc.sendRequest(request);
//
//				status = response.getResponseCode();
//
//				Assert.assertEquals(HttpServletResponse.SC_OK, status);
//				result = response.getText();
//
//				retItems = mapper.readValue(result, HashMap.class);
//
//				Assert.assertNotNull(retItems);
//
//				Assert.assertEquals(Constants.Status.SUCCESS.getValue(),
//						retItems.get(Constants.RESPONSE_KEY_NAME_STATUS));
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}finally{
//			if(bis != null){
//				bis.close();
//			}
//		}
//	}
	/**
	 *SysCode测试代码
	 * @author Silence
	 */
	public void addSysCode() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		try {
			WebRequest request = new PostMethodWebRequest("http://localhost:8080/settings/codeManagement");
			request.setParameter("type", "2");
			request.setParameter("typeCodeName", "qwesdadrt");
			request.setParameter("codeName", "qwesdadrsadadasdat");
			request.setParameter("codeVal", "qwesdadrsadadasdat");
			request.setParameter("remark", "流水走向1");
			WebConversation wc = new WebConversation();
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			System.out.println(retItems.get("data")+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+result);
			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	public void querySmallType() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		try {
			WebRequest request = new PostMethodWebRequest("http://localhost:8080/settings/smallType");
			request.setParameter("id", "1");
			WebConversation wc = new WebConversation();
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			System.out.println(retItems.get("data")+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+result);
			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void testquertBigType() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		try {
			WebRequest request = new PostMethodWebRequest("http://localhost:8080/settings/bigType");
			WebConversation wc = new WebConversation();
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			System.out.println(retItems.get("data")+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+result);
			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void updateSyscode() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		try {
			WebRequest request = new PostMethodWebRequest("http://localhost:8080/settings/updateBigType");
			request.setParameter("id", "11");
			request.setParameter("type", "2");
			request.setParameter("codeName", "qwert");
			request.setParameter("codeVal", "qwert");
			request.setParameter("seq", "11");
			request.setParameter("remark", "客户理赔");
			WebConversation wc = new WebConversation();
			WebResponse response = wc.sendRequest(request);
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			System.out.println(retItems.get("data")+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+result);
			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	public void updateSyscodeState() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		try {
			WebRequest request = new PostMethodWebRequest("http://localhost:8080/settings/updateState");
			request.setParameter("id", "11");
			request.setParameter("type", "1");
			request.setParameter("state", "0");
			WebConversation wc = new WebConversation();
			WebResponse response = wc.sendRequest(request);
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			System.out.println(retItems.get("data")+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+result);
			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
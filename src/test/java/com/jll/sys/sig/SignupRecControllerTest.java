package com.jll.sys.sig;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.springframework.http.MediaType;

import com.ehome.test.ControllerJunitBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jll.common.constants.Message;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class SignupRecControllerTest extends ControllerJunitBase {

	public SignupRecControllerTest(String name) {
		super(name);
	}


	/**
	 * 为了测试如何获取秒秒彩的上个期次的开奖结果 1，投注 2，查询开奖结果
	 * 
	 * @throws Exception
	 */
	public void testSgnupRecRecord_UI() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		 //ByteArrayInputStream bis = null;

		/*ObjectNode node = mapper.createObjectNode();
		node.putPOJO("payerName", "test001");
		node.putPOJO("payCardNumber", "222222222222");
		node.putPOJO("payType", "2");
		node.putPOJO("payChannel", "1");
		node.putPOJO("amount", "10");
		
		
		System.out.println(mapper.writeValueAsString(node));
		bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));*/
		WebRequest request = new GetMethodWebRequest("http://localhost:8080/signup/sgnupRecRecord");
		
		request.setParameter("startTime", "2018-11-01");
		request.setParameter("endTime", "2018-11-30");
		request.setParameter("pageIndex", "0");
		request.setParameter("pageSize", "31");
		request.setParameter("sourceFlag", "0");
		
		WebConversation wc = new WebConversation();

		token = queryToken(userName, pwd, clientId);
		request.setHeaderField("Authorization", "bearer " + token);

		WebResponse response = wc.sendRequest(request);

		int status = response.getResponseCode();

		Assert.assertEquals(HttpServletResponse.SC_OK, status);
		String result = response.getText();

		Map<String, Object> retItems = null;

		retItems = mapper.readValue(result, HashMap.class);

		Assert.assertNotNull(retItems);

		Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));

		//Thread.sleep(20000);
	}
	

	/**
	 * 为了测试如何获取秒秒彩的上个期次的开奖结果 1，投注 2，查询开奖结果
	 * 
	 * @throws Exception
	 */
	public void testSgnupRecRecord_admin_with_user() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		 //ByteArrayInputStream bis = null;

		/*ObjectNode node = mapper.createObjectNode();
		node.putPOJO("payerName", "test001");
		node.putPOJO("payCardNumber", "222222222222");
		node.putPOJO("payType", "2");
		node.putPOJO("payChannel", "1");
		node.putPOJO("amount", "10");
		
		
		System.out.println(mapper.writeValueAsString(node));
		bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));*/
		WebRequest request = new GetMethodWebRequest("http://localhost:8080/signup/sgnupRecRecord");
		
		request.setParameter("userName", "test001");
		request.setParameter("startTime", "2018-11-01");
		request.setParameter("endTime", "2018-11-30");
		request.setParameter("pageIndex", "0");
		request.setParameter("pageSize", "31");
		request.setParameter("sourceFlag", "1");
		
		WebConversation wc = new WebConversation();

		token = queryToken(userName, pwd, clientId);
		request.setHeaderField("Authorization", "bearer " + token);

		WebResponse response = wc.sendRequest(request);

		int status = response.getResponseCode();

		Assert.assertEquals(HttpServletResponse.SC_OK, status);
		String result = response.getText();

		Map<String, Object> retItems = null;

		retItems = mapper.readValue(result, HashMap.class);

		Assert.assertNotNull(retItems);

		Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));

		//Thread.sleep(20000);
	}
	
	
	/**
	 * 为了测试如何获取秒秒彩的上个期次的开奖结果 1，投注 2，查询开奖结果
	 * 
	 * @throws Exception
	 */
	public void testSgnupRecRecord_admin_without_user() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		 //ByteArrayInputStream bis = null;

		/*ObjectNode node = mapper.createObjectNode();
		node.putPOJO("payerName", "test001");
		node.putPOJO("payCardNumber", "222222222222");
		node.putPOJO("payType", "2");
		node.putPOJO("payChannel", "1");
		node.putPOJO("amount", "10");
		
		
		System.out.println(mapper.writeValueAsString(node));
		bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));*/
		WebRequest request = new GetMethodWebRequest("http://localhost:8080/signup/sgnupRecRecord");
		
		request.setParameter("startTime", "2018-11-01");
		request.setParameter("endTime", "2018-11-30");
		request.setParameter("pageIndex", "0");
		request.setParameter("pageSize", "31");
		request.setParameter("sourceFlag", "1");
		
		WebConversation wc = new WebConversation();

		token = queryToken(userName, pwd, clientId);
		request.setHeaderField("Authorization", "bearer " + token);

		WebResponse response = wc.sendRequest(request); 

		int status = response.getResponseCode();

		Assert.assertEquals(HttpServletResponse.SC_OK, status);
		String result = response.getText();

		Map<String, Object> retItems = null;

		retItems = mapper.readValue(result, HashMap.class);

		Assert.assertNotNull(retItems);

		Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));

		//Thread.sleep(20000);
	}
	
	
	public void logout(String token) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		try {
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/security/logout");
			WebConversation wc = new WebConversation();

			request.setHeaderField("Authorization", "bearer " + token);

			WebResponse response = wc.sendRequest(request);

			int status = response.getResponseCode();

			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();

			Map<String, Object> retItems = null;

			retItems = mapper.readValue(result, HashMap.class);

			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));

			//Thread.sleep(10000);
		} catch (Exception ex) {
			throw ex;
		}
	}
}
package com.jll.pay;

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
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class PaymentControlerTest extends ControllerJunitBase {

	public PaymentControlerTest(String name) {
		super(name);
	}


	/**
	 * 为了测试如何获取秒秒彩的上个期次的开奖结果 1，投注 2，查询开奖结果
	 * 
	 * @throws Exception
	 */
	public void ItestPayOrderToSystem() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;

		ObjectNode node = mapper.createObjectNode();
		node.putPOJO("payerName", "test001");
		node.putPOJO("payCardNumber", "222222222222");
		node.putPOJO("payType", "2");
		node.putPOJO("payChannel", "1");
		node.putPOJO("amount", "10");
		
		
		System.out.println(mapper.writeValueAsString(node));
		bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
		WebRequest request = new PostMethodWebRequest("http://localhost:8080/payment/pay-loading", 
				bis,
				MediaType.APPLICATION_JSON_VALUE);
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

		Thread.sleep(20000);
	}
	

	public void testPayOrderToSystem_online_bank() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;

		ObjectNode node = mapper.createObjectNode();
		node.putPOJO("payerName", "test001");
		node.putPOJO("payCardNumber", "222222222222");
		node.putPOJO("payType", "3");
		node.putPOJO("payChannel", "2");
		node.putPOJO("amount", "10");
		
		
		System.out.println(mapper.writeValueAsString(node));
		bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
		WebRequest request = new PostMethodWebRequest("http://localhost:8080/payment/pay-loading", 
				bis,
				MediaType.APPLICATION_JSON_VALUE);
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

		Thread.sleep(20000);
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

			Thread.sleep(10000);
		} catch (Exception ex) {
			throw ex;
		}
	}
}
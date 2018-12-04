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
import com.meterware.httpunit.HttpUnitOptions;
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
	public void testPayOrderToSystem() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;

		ObjectNode node = mapper.createObjectNode();
		//node.putPOJO("payerName", "test001");
		//node.putPOJO("payCardNumber", "222222222222");
		//node.putPOJO("payType", "2");
		node.putPOJO("payChannel", "73");
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

		//Thread.sleep(20000);
	}
	

	
	public void testPayOrderToSystem_online_bank() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;

		ObjectNode node = mapper.createObjectNode();
		//node.putPOJO("payerName", "test001");
		//node.putPOJO("payCardNumber", "222222222222");
		//node.putPOJO("payType", "3");
		node.putPOJO("payChannel", "74");
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

		//Thread.sleep(20000);
	}
	
	
	public void testPayOrderToSystem_caipay_online_bank_redirect_fail() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;

		ObjectNode node = mapper.createObjectNode();
		node.putPOJO("payChannel", "83");
		node.putPOJO("amount", "10");
		/*<input type="radio" name="accountType" value="0">借记卡
				<input type="radio" name="accountType" value="1">贷记卡*/
		/*node.putPOJO("accountType", "0");
		node.putPOJO("tranChannel", "102");*/
		/*<option>请选择充值银行</option>
		<!-- 								<option value="102">工商银行</option> -->
										<option value="103">农业银行</option>
										<option value="104">中国银行</option>
										<option value="105">建设银行</option>
		<!-- 								<option value="203">农业发展银行</option> -->
										<option value="301">交通银行</option>
		<!-- 								<option value="302">中信银行</option> -->
										<option value="303">光大银行</option>
										<option value="304">华夏银行</option>
										<option value="305">民生银行</option>
										<option value="306">广发银行</option>
										<option value="307">平安银行</option>
										<option value="308">招商银行</option>
		<!-- 								<option value="309">兴业银行</option> -->
		<!-- 								<option value="310">浦发银行</option> -->
		<!-- 								<option value="313">北京银行</option> -->
		<!-- 								<option value="315">恒丰银行</option> -->
		<!-- 								<option value="316">浙商银行</option> -->
		<!-- 								<option value="318">渤海银行</option> -->
										<option value="325">上海银行</option>
										<option value="403">邮储银行</option>
		<!-- 								<option value="440">徽商银行</option> -->
		<!-- 								<option value="441">广州市商业银行</option> -->
									</select>*/
									
		System.out.println(mapper.writeValueAsString(node));
		bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
		WebRequest request = new PostMethodWebRequest("http://localhost:8080/payment/pay-loading", 
				bis,
				MediaType.APPLICATION_JSON_VALUE);
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		
		token = queryToken(userName, pwd, clientId);
		request.setHeaderField("Authorization", "bearer " + token);

		WebResponse response = wc.getResponse(request);

		int status = response.getResponseCode();

		Assert.assertEquals(HttpServletResponse.SC_OK, status);
		String result = response.getText();

		Map<String, Object> retItems = null;

		retItems = mapper.readValue(result, HashMap.class);

		Assert.assertNotNull(retItems);

		Assert.assertEquals(Message.status.FAILED.getCode(), retItems.get(Message.KEY_STATUS));

		//Thread.sleep(20000);
	}
	
	
	public void testPayOrderToSystem_caipay_online_bank_redirect_success() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;

		ObjectNode node = mapper.createObjectNode();
		node.putPOJO("payChannel", "83");
		node.putPOJO("amount", "10");
		/*<input type="radio" name="accountType" value="0">借记卡
				<input type="radio" name="accountType" value="1">贷记卡*/
		node.putPOJO("accountType", "0");
		node.putPOJO("tranChannel", "102");
		/*<option>请选择充值银行</option>
		<!-- 								<option value="102">工商银行</option> -->
										<option value="103">农业银行</option>
										<option value="104">中国银行</option>
										<option value="105">建设银行</option>
		<!-- 								<option value="203">农业发展银行</option> -->
										<option value="301">交通银行</option>
		<!-- 								<option value="302">中信银行</option> -->
										<option value="303">光大银行</option>
										<option value="304">华夏银行</option>
										<option value="305">民生银行</option>
										<option value="306">广发银行</option>
										<option value="307">平安银行</option>
										<option value="308">招商银行</option>
		<!-- 								<option value="309">兴业银行</option> -->
		<!-- 								<option value="310">浦发银行</option> -->
		<!-- 								<option value="313">北京银行</option> -->
		<!-- 								<option value="315">恒丰银行</option> -->
		<!-- 								<option value="316">浙商银行</option> -->
		<!-- 								<option value="318">渤海银行</option> -->
										<option value="325">上海银行</option>
										<option value="403">邮储银行</option>
		<!-- 								<option value="440">徽商银行</option> -->
		<!-- 								<option value="441">广州市商业银行</option> -->
									</select>*/
									
		System.out.println(mapper.writeValueAsString(node));
		bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
		WebRequest request = new PostMethodWebRequest("http://localhost:8080/payment/pay-loading", 
				bis,
				MediaType.APPLICATION_JSON_VALUE);
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		
		token = queryToken(userName, pwd, clientId);
		request.setHeaderField("Authorization", "bearer " + token);

		WebResponse response = wc.getResponse(request);

		int status = response.getResponseCode();

		Assert.assertEquals(HttpServletResponse.SC_OK, status);
		/*String result = response.getText();

		Map<String, Object> retItems = null;

		retItems = mapper.readValue(result, HashMap.class);

		Assert.assertNotNull(retItems);

		Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));*/

		//Thread.sleep(20000);
	}
	
	
	public void testPayOrderToSystem_caipay_alipay_payment_url() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;

		ObjectNode node = mapper.createObjectNode();
		//node.putPOJO("payType", "10");
		node.putPOJO("payChannel", "82");
		node.putPOJO("amount", "10");
		
									
		System.out.println(mapper.writeValueAsString(node));
		bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
		WebRequest request = new PostMethodWebRequest("http://localhost:8080/payment/pay-loading", 
				bis,
				MediaType.APPLICATION_JSON_VALUE);
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		
		token = queryToken(userName, pwd, clientId);
		request.setHeaderField("Authorization", "bearer " + token);

		WebResponse response = wc.getResponse(request);

		int status = response.getResponseCode();

		Assert.assertEquals(HttpServletResponse.SC_OK, status);
		String result = response.getText();

		Map<String, Object> retItems = null;

		retItems = mapper.readValue(result, HashMap.class);

		Assert.assertNotNull(retItems);

		Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));

		//Thread.sleep(20000);
	}
	
	
	public void testPayOrderToSystem_caipay_wechatPay_payment_url() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;

		ObjectNode node = mapper.createObjectNode();
		//node.putPOJO("payType", "11");
		node.putPOJO("payChannel", "81");
		node.putPOJO("amount", "10");
		
									
		System.out.println(mapper.writeValueAsString(node));
		bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
		WebRequest request = new PostMethodWebRequest("http://localhost:8080/payment/pay-loading", 
				bis,
				MediaType.APPLICATION_JSON_VALUE);
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		
		token = queryToken(userName, pwd, clientId);
		request.setHeaderField("Authorization", "bearer " + token);

		WebResponse response = wc.getResponse(request);

		int status = response.getResponseCode();

		Assert.assertEquals(HttpServletResponse.SC_OK, status);
		String result = response.getText();

		Map<String, Object> retItems = null;

		retItems = mapper.readValue(result, HashMap.class);

		Assert.assertNotNull(retItems);

		Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));

		//Thread.sleep(20000);
	}
	
	
	public void testPayOrderToSystem_ui_test() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;

		ObjectNode node = mapper.createObjectNode();
		//node.putPOJO("payType", "1");
		node.putPOJO("payChannel", "81");
		node.putPOJO("amount", "10");
		
									
		System.out.println(mapper.writeValueAsString(node));
		bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
		WebRequest request = new PostMethodWebRequest("http://localhost:8080/payment/pay-loading", 
				bis,
				MediaType.APPLICATION_JSON_VALUE);
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		
		token = queryToken(userName, pwd, clientId);
		request.setHeaderField("Authorization", "bearer " + token);

		WebResponse response = wc.getResponse(request);

		int status = response.getResponseCode();

		Assert.assertEquals(HttpServletResponse.SC_OK, status);
		String result = response.getText();

		Map<String, Object> retItems = null;

		retItems = mapper.readValue(result, HashMap.class);

		Assert.assertNotNull(retItems);

		Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));

		//Thread.sleep(20000);
	}
	
	
	
	public void ItestWithdraw_unsufficient_balance() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;

		ObjectNode node = mapper.createObjectNode();
		node.putPOJO("bankId", "1");
		node.putPOJO("passoword", "test001");
		node.putPOJO("amount", "81478930.00");
		
									
		System.out.println(mapper.writeValueAsString(node));
		bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
		WebRequest request = new PostMethodWebRequest("http://localhost:8080/users/withdraw/apply", 
				bis,
				MediaType.APPLICATION_JSON_VALUE);
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		
		token = queryToken(userName, pwd, clientId);
		request.setHeaderField("Authorization", "bearer " + token);

		WebResponse response = wc.getResponse(request);

		int status = response.getResponseCode();

		Assert.assertEquals(HttpServletResponse.SC_OK, status);
		String result = response.getText();

		Map<String, Object> retItems = null;

		retItems = mapper.readValue(result, HashMap.class);

		Assert.assertNotNull(retItems);

		Assert.assertEquals(Message.status.FAILED.getCode(), retItems.get(Message.KEY_STATUS));

		Thread.sleep(20000);
	}
	
	
	public void ItestWithdraw_success() throws Exception {
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = null;
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;

		ObjectNode node = mapper.createObjectNode();
		node.putPOJO("bankId", "1");
		node.putPOJO("passoword", "test001");
		node.putPOJO("amount", "105.00");
		
									
		System.out.println(mapper.writeValueAsString(node));
		bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
		WebRequest request = new PostMethodWebRequest("http://localhost:8080/users/withdraw/apply", 
				bis,
				MediaType.APPLICATION_JSON_VALUE);
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		
		token = queryToken(userName, pwd, clientId);
		request.setHeaderField("Authorization", "bearer " + token);

		WebResponse response = wc.getResponse(request);

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
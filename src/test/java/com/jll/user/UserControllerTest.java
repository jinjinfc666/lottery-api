package com.jll.user;

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
import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.PutMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;


public class UserControllerTest extends ControllerJunitBase{
		
	public UserControllerTest(String name) {
		super(name);
	}	
	
	public void ItestRegUser() throws Exception{
		String userName = "agent001";
		String pwd = "agent001";
		String clientId = "lottery-client";
		String token = queryToken(userName, pwd, clientId);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("userName", "test001");
			node.putPOJO("loginPwd", "111111");
			node.putPOJO("platRebate", "5.00");
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PostMethodWebRequest("http://localhost:8080/users/players",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "Bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	public void ItestRegAgent() throws Exception{
		String userName = "admin";
		String pwd = "admin";
		String clientId = "lottery-admin";
		String token = queryToken(userName, pwd, clientId);
		
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("userName", "agent001");
			node.putPOJO("loginPwd", "111111");
			node.putPOJO("platRebate", "10.00");
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PostMethodWebRequest("http://localhost:8080/users/agents",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "Bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	public void ItestRegSysUser() throws Exception{
		String userName = "admin";
		String pwd = "admin";
		String clientId = "lottery-admin";
		String token = queryToken(userName, pwd, clientId);
		
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("userName", "sys_user_001");
			node.putPOJO("loginPwd", "sys_user_001");
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PostMethodWebRequest("http://localhost:8080/users/sys-users",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "Bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	public void ItestApplySMS() throws Exception{
		/*String userName = "admin";
		String pwd = "admin";
		String token = queryToken(userName, pwd);*/
		
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("userName", "sys_user_001");
			node.putPOJO("loginPwd", "sys_user_001");
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/users/test001/attrs/login-pwd/reset/sms");
			WebConversation wc = new WebConversation();
			
			/*request.setHeaderField("Authorization", "Bearer " + token);*/
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	public void ItestResetPwdSMS() throws Exception{
		/*String userName = "admin";
		String pwd = "admin";
		String token = queryToken(userName, pwd);*/
		
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("sms", "612416");
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PutMethodWebRequest("http://localhost:8080/users/test001/attrs/login-pwd/reset/sms",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			
			WebConversation wc = new WebConversation();
			
			/*request.setHeaderField("Authorization", "Bearer " + token);*/
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	
	public void ItestApplyEmail() throws Exception{
		/*String userName = "admin";
		String pwd = "admin";
		String token = queryToken(userName, pwd);*/
		
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("userName", "sys_user_001");
			node.putPOJO("loginPwd", "sys_user_001");
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/users/test001/attrs/login-pwd/pre-reset/email");
			WebConversation wc = new WebConversation();
			
			/*request.setHeaderField("Authorization", "Bearer " + token);*/
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	public void ItestResetPwdEmail() throws Exception{
		/*String userName = "admin";
		String pwd = "admin";
		String token = queryToken(userName, pwd);*/
		
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("sms", "612416");
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PutMethodWebRequest("http://localhost:8080/users/test001/attrs/login-pwd/reset/sms",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			
			WebConversation wc = new WebConversation();
			
			/*request.setHeaderField("Authorization", "Bearer " + token);*/
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	
	public void ItestQueryAllUsers() throws Exception{
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = queryToken(userName, pwd, clientId);
		
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("userName", "中国");
			node.putPOJO("startTime", "2018");
			node.putPOJO("endTime", "2018");
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/users/queryAllUserInfo?userName=中国&startTime=2018&endTime=2018");
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "Bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	
	public void ItestQDAgentNextAgent_non_agent() throws Exception{
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = queryToken(userName, pwd, clientId);
		
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			/*ObjectNode node = mapper.createObjectNode();
			node.putPOJO("userName", "中国");
			node.putPOJO("startTime", "2018");
			node.putPOJO("endTime", "2018");*/
			
			//bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/users/QDAgentNextAgent?id=2&startTime=2018-01-01&endTime=2018-12-01&pageIndex=1");
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "Bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_FORBIDDEN, status);
			/*String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));*/
		} catch (Exception e) {
			HttpException ex = (HttpException)e;
			Assert.assertEquals(403, ex.getResponseCode());
			/*e.printStackTrace();
			throw e;*/
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	
	public void ItestQDAgentNextAgent_agent() throws Exception{
		String userName = "agent001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = queryToken(userName, pwd, clientId);
		
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			/*ObjectNode node = mapper.createObjectNode();
			node.putPOJO("userName", "中国");
			node.putPOJO("startTime", "2018");
			node.putPOJO("endTime", "2018");*/
			
			//bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/users/QDAgentNextAgent?id=2&startTime=2018-01-01&endTime=2018-12-01&pageIndex=1");
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "Bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	
	public void ItestCreateUser_non_agent() throws Exception{
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = queryToken(userName, pwd, clientId);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("userName", "test001");
			node.putPOJO("loginPwd", "111111");
			node.putPOJO("platRebate", "5.00");
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PostMethodWebRequest("http://localhost:8080/users/players",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "Bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (HttpException e) {
			Assert.assertEquals(403, e.getResponseCode());
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	
	public void ItestCreateUser_agent() throws Exception{
		String userName = "agent001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = queryToken(userName, pwd, clientId);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("userName", "test001");
			node.putPOJO("loginPwd", "111111");
			node.putPOJO("platRebate", "5.00");
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PostMethodWebRequest("http://localhost:8080/users/players",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "Bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			//Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (HttpException e) {
			Assert.assertEquals(403, e.getResponseCode());
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	public void testQuerySiteMsgRec_player_noUserName() throws Exception{
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = queryToken(userName, pwd, clientId);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/users/site-msg-rec");
			/*request.setParameter("userName", "test001");
			request.setParameter("pageIndex", Integer.toString(0));
			request.setParameter("pageSize", Integer.toString(2));*/
			
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "Bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (HttpException e) {
			Assert.assertEquals(403, e.getResponseCode());
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	public void testQuerySiteMsgRec_player_withUserName() throws Exception{
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = queryToken(userName, pwd, clientId);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/users/site-msg-rec");
			request.setParameter("userName", "admin");
			request.setParameter("pageIndex", Integer.toString(0));
			request.setParameter("pageSize", Integer.toString(2));
			
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "Bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (HttpException e) {
			Assert.assertEquals(403, e.getResponseCode());
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	public void testQuerySiteMsgRec_agent() throws Exception{
		String userName = "agent001";
		String pwd = "test001";
		String clientId = "lottery-client";
		String token = queryToken(userName, pwd, clientId);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/users/site-msg-rec");
			//request.setParameter("userName", "admin");
			request.setParameter("pageIndex", Integer.toString(0));
			request.setParameter("pageSize", Integer.toString(2));
			
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "Bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (HttpException e) {
			Assert.assertEquals(403, e.getResponseCode());
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	public void testQuerySiteMsgRec_sysAdmin_admin() throws Exception{
		String userName = "admin";
		String pwd = "test001";
		String clientId = "lottery-admin";
		String token = queryToken(userName, pwd, clientId);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/users/site-msg-rec");
			//request.setParameter("userName", "admin");
			request.setParameter("pageIndex", Integer.toString(0));
			request.setParameter("pageSize", Integer.toString(2));
			
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "Bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
		} catch (HttpException e) {
			Assert.assertEquals(403, e.getResponseCode());
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	
}
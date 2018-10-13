package com.jll.game;

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
import com.meterware.httpunit.PutMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;


public class SysOperationControllerTest extends ControllerJunitBase{
		
	public SysOperationControllerTest(String name) {
		super(name);
	}	
	
	/**
	 * 正常情况下
	 * @throws Exception
	 */
	public void Itest_qcssc_manual_draw_result() throws Exception{
		String lottoType = "cqssc";
		String winningNum = "7,9,4,8,8";
		String issueNum = "181010-120";
		String userName = "admin";
		String pwd = "test001";
		
		String token = queryToken(userName, pwd);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("winningNum", winningNum);
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PutMethodWebRequest("http://localhost:8080/sys/oper/" + lottoType + "/issue/" + issueNum + "/manual-draw-result",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);
			
			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
			
			Thread.sleep(10000);
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	
	/**
	 * 针对不存在的期次 下手动开奖
	 * @throws Exception
	 */
	public void Itest_qcssc_manual_draw_result_error_issueNum() throws Exception{
		String lottoType = "cqssc";
		String winningNum = "7,9,4,8,8";
		String issueNum = "991010-119";
		String userName = "admin";
		String pwd = "test001";
		
		String token = queryToken(userName, pwd);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
						
			//ArrayNode array = mapper.createArrayNode();
			
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("winningNum", winningNum);
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PutMethodWebRequest("http://localhost:8080/sys/oper/" + lottoType + "/issue/" + issueNum + "/manual-draw-result",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);
			
			Assert.assertEquals(Message.status.FAILED.getCode(), 
					retItems.get(Message.KEY_STATUS));
			
			Assert.assertEquals(Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode(), 
					retItems.get(Message.KEY_ERROR_CODE));
			
			Thread.sleep(10000);
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	
	/**
	 * 不具有权限的用户调用接口 手动开奖
	 * @throws Exception
	 */
	public void Itest_qcssc_manual_draw_result_error_Permission() throws Exception{
		String lottoType = "cqssc";
		String winningNum = "7,9,4,8,8";
		String issueNum = "991010-119";
		String userName = "test001";
		String pwd = "test001";
		
		String token = queryToken(userName, pwd);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {			
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("winningNum", winningNum);
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PutMethodWebRequest("http://localhost:8080/sys/oper/" + lottoType + "/issue/" + issueNum + "/manual-draw-result",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);
			
			Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
			
			Thread.sleep(10000);
		}catch(Exception ex) {
			Assert.assertTrue(ex.getMessage().contains("403"));
		}
	}
	
	
	
	/**
	 * 测试期次状态不为3的情况下
	 * @throws Exception
	 */
	public void Itest_qcssc_manual_draw_result_error_status() throws Exception{
		String lottoType = "cqssc";
		String winningNum = "7,9,4,8,8";
		String issueNum = "181010-120";
		String userName = "admin";
		String pwd = "test001";
		
		String token = queryToken(userName, pwd);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {			
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("winningNum", winningNum);
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PutMethodWebRequest("http://localhost:8080/sys/oper/" + lottoType + "/issue/" + issueNum + "/manual-draw-result",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);
			
			Assert.assertEquals(Message.status.FAILED.getCode(), 
					retItems.get(Message.KEY_STATUS));
			
			Assert.assertEquals(Message.Error.ERROR_ISSUE_INVALID_STATUS.getCode(), 
					retItems.get(Message.KEY_ERROR_CODE));
			
			Thread.sleep(10000);
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	
	/**
	 * 测试私彩在非人工开奖的情况下
	 * @throws Exception
	 */
	public void Itest_ffc_manual_draw_result_non_manual_prizeModel() throws Exception{
		String lottoType = "ffc";
		String winningNum = "7,9,4,8,8";
		String issueNum = "181013-986";
		String userName = "admin";
		String pwd = "test001";
		
		String token = queryToken(userName, pwd);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {			
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("winningNum", winningNum);
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PutMethodWebRequest("http://localhost:8080/sys/oper/" + lottoType + "/issue/" + issueNum + "/manual-draw-result",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);
			
			Assert.assertEquals(Message.status.FAILED.getCode(), 
					retItems.get(Message.KEY_STATUS));
			
			Assert.assertEquals(Message.Error.ERROR_ISSUE_NOT_ALLOWED_MANUAL_DRAW_RESULT.getCode(), 
					retItems.get(Message.KEY_ERROR_CODE));
			
			Thread.sleep(10000);
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	/**
	 * 测试私彩在人工开奖的情况下
	 * @throws Exception
	 */
	public void Itest_ffc_manual_draw_result_manual_prizeModel() throws Exception{
		String lottoType = "ffc";
		String winningNum = "7,9,4,8,8";
		String issueNum = "181013-1005";
		String userName = "admin";
		String pwd = "test001";
		
		String token = queryToken(userName, pwd);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {			
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("winningNum", winningNum);
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PutMethodWebRequest("http://localhost:8080/sys/oper/" + lottoType + "/issue/" + issueNum + "/manual-draw-result",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);
			
			Assert.assertEquals(Message.status.SUCCESS.getCode(), 
					retItems.get(Message.KEY_STATUS));
			
			Thread.sleep(120000);
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	
	
	
	/**
	 * 测试私彩在非人工开奖的情况下
	 * @throws Exception
	 */
	public void Itest_5fc_manual_draw_result_non_manual_prizeModel() throws Exception{
		String lottoType = "5fc";
		String winningNum = "7,9,4,8,8";
		String issueNum = "181013-217";
		String userName = "admin";
		String pwd = "test001";
		
		String token = queryToken(userName, pwd);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {			
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("winningNum", winningNum);
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PutMethodWebRequest("http://localhost:8080/sys/oper/" + lottoType + "/issue/" + issueNum + "/manual-draw-result",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);
			
			Assert.assertEquals(Message.status.FAILED.getCode(), 
					retItems.get(Message.KEY_STATUS));
			
			Assert.assertEquals(Message.Error.ERROR_ISSUE_NOT_ALLOWED_MANUAL_DRAW_RESULT.getCode(), 
					retItems.get(Message.KEY_ERROR_CODE));
			
			Thread.sleep(10000);
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	/**
	 * 测试私彩在人工开奖的情况下
	 * @throws Exception
	 */
	public void test_5fc_manual_draw_result_manual_prizeModel() throws Exception{
		String lottoType = "5fc";
		String winningNum = "7,9,4,8,8";
		String issueNum = "181013-224";
		String userName = "admin";
		String pwd = "test001";
		
		String token = queryToken(userName, pwd);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {			
			ObjectNode node = mapper.createObjectNode();
			node.putPOJO("winningNum", winningNum);
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(node));
			WebRequest request = new PutMethodWebRequest("http://localhost:8080/sys/oper/" + lottoType + "/issue/" + issueNum + "/manual-draw-result",
					bis,
					MediaType.APPLICATION_JSON_VALUE);
			WebConversation wc = new WebConversation();
			
			request.setHeaderField("Authorization", "bearer " + token);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);
			
			Assert.assertEquals(Message.status.SUCCESS.getCode(), 
					retItems.get(Message.KEY_STATUS));
			
			Thread.sleep(10000);
		}catch(Exception ex) {
			throw ex;
		}
	}
}
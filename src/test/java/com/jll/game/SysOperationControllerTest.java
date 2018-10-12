package com.jll.game;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.springframework.http.MediaType;

import com.ehome.test.ControllerJunitBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jll.common.constants.Message;
import com.jll.entity.Issue;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;


public class SysOperationControllerTest extends ControllerJunitBase{
		
	public SysOperationControllerTest(String name) {
		super(name);
	}	
	
	public void test_qcssc_manual_draw_result() throws Exception{
		String lottoType = "cqssc";
		String winningNum = "7,9,4,8,8";
		String issueNum = "181010-120";
		String userName = "test001";
		String pwd = "test001";
		
		String token = queryToken(userName, pwd);
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
						
			ArrayNode array = mapper.createArrayNode();
			
			ObjectNode node = array.addObject();
			node.putPOJO("winningNum", winningNum);
			
			bis = new ByteArrayInputStream(mapper.writeValueAsBytes(array));
			WebRequest request = new PostMethodWebRequest("http://localhost:8080/sys/oper/" + lottoType + "/issue/" + issueNum + "/manual-draw-result",
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
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	
	
	
	
	
}
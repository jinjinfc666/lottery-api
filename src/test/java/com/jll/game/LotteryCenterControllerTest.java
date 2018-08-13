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


public class LotteryCenterControllerTest extends ControllerJunitBase{
		
	public LotteryCenterControllerTest(String name) {
		super(name);
	}	
	
	public void testBetting() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(3*60*1000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						String userName = "test001";
						String pwd = "test001";
						
						String token = queryToken(userName, pwd);
						ObjectMapper mapper = new ObjectMapper();
						ByteArrayInputStream bis = null;
						try {
							Issue currIssue = queryCurrIssue(token);
							int counter = 0;
							while(currIssue == null && counter <= 200) {
								counter++;
								
								currIssue = queryCurrIssue(token);
							}
							
							if(currIssue == null) {
								Assert.fail("Can't obtain the current issue!!!!");
							}
							
							ArrayNode array = mapper.createArrayNode();
							
							ObjectNode node = array.addObject();
							node.putPOJO("issueId", currIssue.getId());
							node.putPOJO("playType", 1);
							node.putPOJO("betNum", "1");
							node.putPOJO("betTotal", "1");
							node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("prizeRate", "1750");
							//node.putPOJO("state", "5.00");
							//node.putPOJO("delayPayoutFlag", "5.00");
							node.putPOJO("isZh", "0");
							//node.putPOJO("isZhBlock", "5.00");
							node.putPOJO("terminalType", "0");
							
							bis = new ByteArrayInputStream(mapper.writeValueAsBytes(array));
							WebRequest request = new PostMethodWebRequest("http://localhost:8080/lotteries/cqssc/bet/zh/1/wallet/15",
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
							//throw e;
						}finally {
							if(bis != null) {
								try {
									bis.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
					}
				});
				
				exe.start();
			}
			
			counter++;
			
			
		}
		
	}
	
	public Issue queryCurrIssue(String token) throws Exception{
		
		Issue currIssue = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/lotteries/cqssc/betting-issue");
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
			
			Map data = (Map)retItems.get("data");
			
			Map currIssueMap = (Map)data.get("currIssue");
			
			if(currIssueMap != null && currIssueMap.size() > 0) {
				//Long downCounter = (Long)((LinkedHashMap)currIssueMap).get(0);
				Integer downCounter = (Integer)currIssueMap.get("downCounter");
				Date endTime = new Date((Long)currIssueMap.get("endTime"));
				Integer id = (Integer)currIssueMap.get("id");
				String lotteryType = (String)currIssueMap.get("lotteryType");
				Date startTime = new Date((Long)currIssueMap.get("startTime"));
				Integer state = (Integer)currIssueMap.get("state");
				String issueNum = (String)currIssueMap.get("issueNum");
				currIssue = new Issue();
				
				currIssue.setDownCounter(new Long(downCounter.intValue()));
				currIssue.setEndTime(endTime);
				currIssue.setId(id);
				currIssue.setIssueNum(issueNum);
				currIssue.setLotteryType(lotteryType);
				currIssue.setStartTime(startTime);
				currIssue.setState(state);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return currIssue;
	}
	
	private String queryToken(String userName, String pwd) {
		String token = null;
		String tokenURL = "http://localhost:8080/oauth/token";
		ObjectMapper mapper = new ObjectMapper();
		try {
			WebRequest request = new PostMethodWebRequest(tokenURL);
			WebConversation wc = new WebConversation();
			
			request.setParameter("grant_type", "password");
			request.setParameter("client_id", "lottery-client");
			request.setParameter("client_secret", "secret_1");
			request.setParameter("username", userName);
			request.setParameter("password", pwd);
			
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertNotNull(retItems.get("access_token"));
			
			token = (String)retItems.get("access_token");
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		return token;
	}
	
}
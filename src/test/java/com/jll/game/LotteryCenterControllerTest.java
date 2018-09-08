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
	
	public void ItestBetting() throws Exception{
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
	
	
	public void ItestBetting_qcssc_qszx() throws Exception{
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
							node.putPOJO("betNum", "1,1,1");
							//node.putPOJO("betTotal", "1");
							//node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							//node.putPOJO("prizeRate", "1750");
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
	
	
	public void ItestBetting_qcssc_zszx() throws Exception{
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
							node.putPOJO("playType", 3);
							node.putPOJO("betNum", "1,1,1");
							//node.putPOJO("betTotal", "1");
							//node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							//node.putPOJO("prizeRate", "1750");
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
	
	
	public void ItestBetting_qcssc_hszx() throws Exception{
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
							node.putPOJO("playType", 5);
							node.putPOJO("betNum", "1,1,1");
							//node.putPOJO("betTotal", "1");
							//node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							//node.putPOJO("prizeRate", "1750");
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
	
	
	public void ItestBetting_qcssc_qszuxzs() throws Exception{
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
							node.putPOJO("playType", 7);
							node.putPOJO("betNum", "1234");
							//node.putPOJO("betTotal", "1");
							//node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							//node.putPOJO("prizeRate", "1750");
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
	
	public void ItestBetting_qcssc_qszuxzl() throws Exception{
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
							node.putPOJO("playType", 8);
							node.putPOJO("betNum", "1234");
							//node.putPOJO("betTotal", "1");
							//node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							//node.putPOJO("prizeRate", "1750");
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
	
	
	public void ItestBetting_qcssc_qszuxMix() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 9);
							node.putPOJO("betNum", "123;122");
							//node.putPOJO("betTotal", "1");
							//node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							//node.putPOJO("prizeRate", "1750");
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
	
	public void ItestBetting_qcssc_zszuxZs() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 10);
							node.putPOJO("betNum", "123");
							//node.putPOJO("betTotal", "1");
							//node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							//node.putPOJO("prizeRate", "1750");
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
	
	public void ItestBetting_qcssc_zszuxZl() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 11);
							node.putPOJO("betNum", "123");
							//node.putPOJO("betTotal", "1");
							//node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							//node.putPOJO("prizeRate", "1750");
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
	
	
	public void ItestBetting_qcssc_zszuxMix() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 12);
							node.putPOJO("betNum", "123");
							//node.putPOJO("betTotal", "1");
							//node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							//node.putPOJO("prizeRate", "1750");
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
	
	public void ItestBetting_qcssc_hszuxZs() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 13);
							node.putPOJO("betNum", "123");
							//node.putPOJO("betTotal", "1");
							//node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							//node.putPOJO("prizeRate", "1750");
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
	
	
	public void ItestBetting_qcssc_hszuxZl() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 14);
							node.putPOJO("betNum", "123");
							//node.putPOJO("betTotal", "1");
							//node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							//node.putPOJO("prizeRate", "1750");
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
	
	public void ItestBetting_qcssc_hszuxMix() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 15);
							node.putPOJO("betNum", "123");
							//node.putPOJO("betTotal", "1");
							//node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							//node.putPOJO("prizeRate", "1750");
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
	
	
	public void ItestBetting_qcssc_wxq2() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 16);
							node.putPOJO("betNum", "1,2");
							//node.putPOJO("betTotal", "1");
							//node.putPOJO("betAmount", "5.00");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							//node.putPOJO("prizeRate", "1750");
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
	
	
	
	public void ItestBetting_qcssc_wxq2Zx() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 18);
							node.putPOJO("betNum", "12345");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_qcssc_wxh2() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 31);
							node.putPOJO("betNum", "0,0");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_qcssc_wxh2Zx() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 33);
							node.putPOJO("betNum", "12345");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	public void ItestBetting_qcssc_bdw_qs() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 19);
							node.putPOJO("betNum", "12345");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_qcssc_bdw_zs() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 20);
							node.putPOJO("betNum", "12345");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_qcssc_bdw_hs() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 21);
							node.putPOJO("betNum", "12345");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_qcssc_dwd() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 22);
							node.putPOJO("betNum", "12345,,,,");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_guanDong11In5_qszx() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 34);
							node.putPOJO("betNum", "0102030405,06,07");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_guanDong11In5_qszux() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 36);
							node.putPOJO("betNum", "0102030405");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_guanDong11In5_wxq2() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 38);
							node.putPOJO("betNum", "01,02");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_guanDong11In5_wxq2Zx() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 40);
							node.putPOJO("betNum", "0102");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_guanDong11In5_bdw() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 42);
							node.putPOJO("betNum", "0102");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_guanDong11In5_dwd() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 43);
							node.putPOJO("betNum", "0102,01,02");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_guanDong11In5_qwDs() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 44);
							node.putPOJO("betNum", "543210");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	
	public void ItestBetting_guanDong11In5_qwZw() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 45);
							node.putPOJO("betNum", "345");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	
	public void ItestBetting_guanDong11In5_Rx1() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 46);
							node.putPOJO("betNum", "030405");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_guanDong11In5_Rx2() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 47);
							node.putPOJO("betNum", "0304");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_guanDong11In5_Rx3() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 48);
							node.putPOJO("betNum", "030405");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_guanDong11In5_Rx4() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 49);
							node.putPOJO("betNum", "03040506");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_guanDong11In5_Rx5() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 50);
							node.putPOJO("betNum", "0304050607");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_guanDong11In5_Rx6() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 51);
							node.putPOJO("betNum", "030405060708");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void ItestBetting_guanDong11In5_Rx7() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 52);
							node.putPOJO("betNum", "02030405060708");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
	
	
	public void testBetting_guanDong11In5_Rx8() throws Exception{
		int maxTimes = 10000;
		int counter = 0;
		
		while(counter < maxTimes) {
			Thread.sleep(1000);
			
			if(counter == 0) {
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						
						try {
							Thread.sleep(1*60*1000);
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
							node.putPOJO("playType", 53);
							node.putPOJO("betNum", "0203040506070809");
							node.putPOJO("times", "1");
							node.putPOJO("pattern", "1");
							node.putPOJO("isZh", "0");
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
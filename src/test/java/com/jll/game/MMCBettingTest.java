package com.jll.game;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.springframework.http.MediaType;

import com.ehome.test.ControllerJunitBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.entity.Issue;
import com.jll.entity.PlayType;
import com.jll.game.playtype.PlayTypeFacade;
import com.jll.game.playtypefacade.PlayTypeFactory;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.PutMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;


public class MMCBettingTest extends ControllerJunitBase{
		
	public MMCBettingTest(String name) {
		super(name);
	}
	
	public Map<String, Object> queryCurrIssue(String token, String lottoType) throws Exception{
		Map<String, Object> ret = new HashMap<String, Object>();
		List<PlayType> playTypes = new ArrayList<>();
		Issue currIssue = null;
		Issue lastIssue = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/lotteries/" + lottoType + "/betting-issue");
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
			
			Map lastIssueMap = (Map)data.get("lastIssue");
			
			List<Map<String, Object>> playTypesMap = (List<Map<String, Object>>)data.get("playType");
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
			
			if(lastIssueMap != null && lastIssueMap.size() > 0) {
				//Long downCounter = (Long)((LinkedHashMap)currIssueMap).get(0);
				Integer downCounter = (Integer)lastIssueMap.get("downCounter");
				Date endTime = new Date((Long)lastIssueMap.get("endTime"));
				Integer id = (Integer)lastIssueMap.get("id");
				String lotteryType = (String)lastIssueMap.get("lotteryType");
				Date startTime = new Date((Long)lastIssueMap.get("startTime"));
				Integer state = (Integer)lastIssueMap.get("state");
				String issueNum = (String)lastIssueMap.get("issueNum");
				lastIssue = new Issue();
				
				lastIssue.setDownCounter(new Long(downCounter== null?0:downCounter.intValue()));
				lastIssue.setEndTime(endTime);
				lastIssue.setId(id);
				lastIssue.setIssueNum(issueNum);
				lastIssue.setLotteryType(lotteryType);
				lastIssue.setStartTime(startTime);
				lastIssue.setState(state);
			}
			
			
			for(Map<String, Object> temp : playTypesMap) {
				//id=1, lotteryType=cqssc, classification=qszx|前三直选, ptName=fs, ptDesc=复式, state=1, mulSinFlag=1, isHidden=1, seq=1, createTime=1533895746000
				Integer id = (Integer)temp.get("id");
				String lotteryType = (String)temp.get("lotteryType");
				String classification = (String)temp.get("classification");
				String ptName = (String)temp.get("ptName");
				String ptDesc = (String)temp.get("ptDesc");
				Integer state = (Integer)temp.get("state");
				Integer mulSinFlag = (Integer)temp.get("mulSinFlag");
				Integer isHidden = (Integer)temp.get("isHidden");
				Integer seq = (Integer)temp.get("seq");
				PlayType playType = new PlayType();
				playType.setClassification(classification);
				playType.setId(id);
				playType.setIsHidden(isHidden);
				playType.setLotteryType(lotteryType);
				playType.setMulSinFlag(mulSinFlag);
				playType.setPtDesc(ptDesc);
				playType.setSeq(seq);
				playType.setState(state);
				playType.setPtName(ptName);
				playTypes.add(playType);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		ret.put("currIssue", currIssue);
		ret.put("playTypes", playTypes);
		ret.put("lastIssue", lastIssue);
		return ret;
	}
	
	
	/**
	 * 为了测试如何获取秒秒彩的上个期次的开奖结果
	 * 1，投注
	 * 2，查询开奖结果
	 * @throws Exception
	 */
	public void testQueryBetting_issue_mmc() throws Exception{
		int maxTimes = 1;
		int counter = 0;
		String lottoType = "mmc";
		long maxWaittingTime = 1000;
		
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		
		//String token ;
		//String winningNum = null;
		StringBuffer winningNumBuffer = new StringBuffer();
		Map<String, Integer> currIndx = new HashMap<>();
		Map<String, PlayTypeFacade> betNumbers = new HashMap<>();
		Map<String, String> tokens = new HashMap<>();
		Map<String, Boolean> isPerforming = new HashMap<>();
		isPerforming.put("isPerforming", false);
		
		try {
			Thread.sleep(60*1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		while(counter < maxTimes) {
			Thread.sleep(maxWaittingTime);
			String token = queryToken(userName, pwd, clientId);
			tokens.put("token", token);
			
			Map<String, Object> ret = queryCurrIssue(token, lottoType);
			int queryIssueCount = 0;
			
			while((ret == null 
					|| ret.size() == 0
					|| ret.get("playTypes") == null) 
					&& queryIssueCount <= 60000) {
				queryIssueCount++;
				
				ret = queryCurrIssue(token, lottoType);
				
				Thread.sleep(500);
			}
			
			if(ret == null || ret.size() == 0) {
				Assert.fail("Can't obtain the current issue!!!!");
			}
			
			Issue currIssue = (Issue)ret.get("lastIssue");
			List<PlayType> playTypes = (List<PlayType>)ret.get("playTypes");
			
			
			//final Integer currIssueId = currIssue.getId();
			final Integer currIssueId = 0;
			Thread exe = new Thread(new Runnable(){
				@Override
				public void run() {
					ObjectMapper mapper = new ObjectMapper();
					ByteArrayInputStream bis = null;
					String playTypeName = null;
					String betNum = null;
					try {
						ArrayNode array = mapper.createArrayNode();
						/*Integer indx = currIndx.get("currIndx");
						if(indx == null) {
							indx = 0;
							currIndx.put("currIndx", indx);
						}else {
							indx++;
							if(indx > playTypes.size() - 1) {
								indx = playTypes.size() -1;
							}
							currIndx.put("currIndx", indx);
						}*/
						
						Integer indx = 0;
						PlayType playType = playTypes.get(indx);
						
						if(playType.getPtName().equals("fs")) {
							playTypeName = playType.getClassification() + "/fs";
						}else if(playType.getPtName().equals("ds")){
							playTypeName = playType.getClassification() + "/ds";
						}else {
							playTypeName = playType.getClassification() + "/" + playType.getPtName();
						}
						
						PlayTypeFacade playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(playTypeName);
						
						if(playTypeFacade == null) {
							return ;
						}
						
						betNum = playTypeFacade.obtainSampleBetNumber();
						
						betNumbers.put(betNum, playTypeFacade);
												
						ObjectNode node = array.addObject();
						node.putPOJO("issueId", currIssueId);
						node.putPOJO("playType", playType.getId());
						node.putPOJO("betNum", betNum.toString());
						node.putPOJO("times", "1");
						node.putPOJO("pattern", "1");
						node.putPOJO("isZh", String.valueOf(Constants.ZhState.NON_ZH.getCode()));
						node.putPOJO("terminalType", "0");
						
						System.out.println(mapper.writeValueAsString(node));
						bis = new ByteArrayInputStream(mapper.writeValueAsBytes(array));
						WebRequest request = new PostMethodWebRequest("http://localhost:8080/lotteries/" +lottoType+ "/bet/zh/1/wallet/15",
								bis,
								MediaType.APPLICATION_JSON_VALUE);
						WebConversation wc = new WebConversation();
						
						String token = tokens.get("token");
						request.setHeaderField("Authorization", "bearer " + token);
						
						WebResponse response = wc.sendRequest(request);
						
						int  status = response.getResponseCode();
						
						Assert.assertEquals(HttpServletResponse.SC_OK, status);
						String result = response.getText();
						
						Map<String, Object> retItems = null;
						
						retItems = mapper.readValue(result, HashMap.class);
						
						Assert.assertNotNull(retItems);
						
						Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
						
						
						Thread.sleep(20000);
						
						
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
			
			counter++;
			
			
			
			while((ret == null 
					|| ret.size() == 0
					|| ret.get("lastIssue") == null) 
					&& queryIssueCount <= 60000) {
				queryIssueCount++;
				
				ret = queryCurrIssue(token, lottoType);
				
				Thread.sleep(500);
			}
			
			if(ret == null || ret.size() == 0) {
				Assert.fail("Can't obtain the current issue!!!!");
			}
			
			currIssue = (Issue)ret.get("lastIssue");
			if(currIssue != null) {
				System.out.println(String.format("issue Number   %s     winning number  %s", 
						currIssue.getIssueNum(),
						currIssue.getRetNum()));
			}
		}
	}
	
	
	public void ItestBetting_mmc() throws Exception{
		int maxTimes = 600000;
		int counter = 0;
		String lottoType = "mmc";
		long maxWaittingTime = 1000;
		
		String userName = "test001";
		String pwd = "test001";
		String clientId = "lottery-client";
		
		//String token ;
		//String winningNum = null;
		StringBuffer winningNumBuffer = new StringBuffer();
		Map<String, Integer> currIndx = new HashMap<>();
		Map<String, PlayTypeFacade> betNumbers = new HashMap<>();
		Map<String, String> tokens = new HashMap<>();
		Map<String, Boolean> isPerforming = new HashMap<>();
		isPerforming.put("isPerforming", false);
		
		try {
			Thread.sleep(60*1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		while(counter < maxTimes) {
			Thread.sleep(maxWaittingTime);
			String token = queryToken(userName, pwd, clientId);
			tokens.put("token", token);
			
			Map<String, Object> ret = queryCurrIssue(token, lottoType);
			int queryIssueCount = 0;
			
			while((ret == null 
					|| ret.size() == 0
					|| ret.get("currIssue") == null) 
					&& queryIssueCount <= 60000) {
				queryIssueCount++;
				
				ret = queryCurrIssue(token, lottoType);
				
				Thread.sleep(500);
			}
			
			if(ret == null || ret.size() == 0) {
				Assert.fail("Can't obtain the current issue!!!!");
			}
			
			Issue currIssue = (Issue)ret.get("currIssue");
			List<PlayType> playTypes = (List<PlayType>)ret.get("playTypes");
			if(currIssue.getDownCounter() <= 0 && !isPerforming.get("isPerforming")) {
				
				Thread exe = new Thread(new Runnable(){
					@Override
					public void run() {
						isPerforming.put("isPerforming", true);
						String userName = "admin";
						String pwd = "test001";
						String clientId = "lottery-admin";
						String adminToken = queryToken(userName, pwd, clientId);
						Random random = new Random();
						int playTypeIndx = random.nextInt(betNumbers.size());
						Iterator<String> keys = betNumbers.keySet().iterator();
						int indx = 0;
						PlayTypeFacade playTypeFacade = null;
						String betNum = null;
						while(keys.hasNext()) {
							betNum = keys.next();
							if(indx == playTypeIndx) {
								playTypeFacade = betNumbers.get(betNum);
								break;
							}
							
							indx++;
						}
						//winningNum = winningNumBuffer.toString();
						if(winningNumBuffer.length() == 0) {
							List<Map<String, String>> maps = playTypeFacade.parseBetNumber(betNum);
							if(maps != null && maps.size() > 0) {
								Map<String, String> row = maps.get(0);
								String winningNum = row.get(Constants.KEY_FACADE_BET_NUM_SAMPLE);
								for(int i = 0; i< winningNum.length(); i++) {
									String bit = winningNum.substring(i, i + 1);
									if(!",".equals(bit)) {
										winningNumBuffer.append(bit).append(",");
									}
								}
								winningNumBuffer.delete(winningNumBuffer.length() - 1, winningNumBuffer.length());
							}							
						}
						try {
							Thread.sleep(60000);
							manualDrawResult(lottoType,
									currIssue.getIssueNum(),
									winningNumBuffer.toString(),
									adminToken);
							
							winningNumBuffer.delete(0, winningNumBuffer.length());
							playTypes.clear();
							currIndx.remove("currIndx");
							
							Thread.sleep(120000);
							
							isPerforming.put("isPerforming", false);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				exe.start();
				
				
				continue;
			}
			
			final Integer currIssueId = currIssue.getId();
			Thread exe = new Thread(new Runnable(){
				@Override
				public void run() {
					ObjectMapper mapper = new ObjectMapper();
					ByteArrayInputStream bis = null;
					String playTypeName = null;
					String betNum = null;
					try {
						ArrayNode array = mapper.createArrayNode();
						Integer indx = currIndx.get("currIndx");
						if(indx == null) {
							indx = 0;
							currIndx.put("currIndx", indx);
						}else {
							indx++;
							if(indx > playTypes.size() - 1) {
								indx = playTypes.size() -1;
							}
							currIndx.put("currIndx", indx);
						}
						
						PlayType playType = playTypes.get(indx);
						
						if(playType.getPtName().equals("fs")) {
							playTypeName = playType.getClassification() + "/fs";
						}else if(playType.getPtName().equals("ds")){
							playTypeName = playType.getClassification() + "/ds";
						}else {
							playTypeName = playType.getClassification() + "/" + playType.getPtName();
						}
						
						PlayTypeFacade playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(playTypeName);
						
						if(playTypeFacade == null) {
							return ;
						}
						
						betNum = playTypeFacade.obtainSampleBetNumber();
						
						betNumbers.put(betNum, playTypeFacade);
												
						ObjectNode node = array.addObject();
						node.putPOJO("issueId", currIssueId);
						node.putPOJO("playType", playType.getId());
						node.putPOJO("betNum", betNum.toString());
						node.putPOJO("times", "1");
						node.putPOJO("pattern", "1");
						node.putPOJO("isZh", "0");
						node.putPOJO("terminalType", "0");
						
						System.out.println(mapper.writeValueAsString(node));
						bis = new ByteArrayInputStream(mapper.writeValueAsBytes(array));
						WebRequest request = new PostMethodWebRequest("http://localhost:8080/lotteries/" +lottoType+ "/bet/zh/1/wallet/15",
								bis,
								MediaType.APPLICATION_JSON_VALUE);
						WebConversation wc = new WebConversation();
						
						String token = tokens.get("token");
						request.setHeaderField("Authorization", "bearer " + token);
						
						WebResponse response = wc.sendRequest(request);
						
						int  status = response.getResponseCode();
						
						Assert.assertEquals(HttpServletResponse.SC_OK, status);
						String result = response.getText();
						
						Map<String, Object> retItems = null;
						
						retItems = mapper.readValue(result, HashMap.class);
						
						Assert.assertNotNull(retItems);
						
						Assert.assertEquals(Message.status.SUCCESS.getCode(), retItems.get(Message.KEY_STATUS));
						
						
						Thread.sleep(20000);
						
						
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
			
			counter++;
			
		}
	}
		
	
	public void manualDrawResult(String lottoType, 
			String issueNum, 
			String winningNum,
			String token) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayInputStream bis = null;
		try {
			ObjectNode node = mapper.createObjectNode();
			
			System.out.println(String.format("lottoType    %s ,   issueNum   %s    winningNum   %s", 
					lottoType,
					issueNum,
					winningNum));
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
		}finally {
			if(bis != null) {
				bis.close();
			}
		}
	}
	
	public void logout(String token) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		try {			
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/security/logout");
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
}
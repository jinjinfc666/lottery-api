package com.jll.game.playtype;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ehome.test.ServiceJunitBase;
import com.jll.common.constants.Constants;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;
import com.jll.game.playtypefacade.PlayTypeFactory;


@WebAppConfiguration 
public class EleIn5Q2zxDsPlayTypeFacadeImplTest extends ServiceJunitBase{
		
	public EleIn5Q2zxDsPlayTypeFacadeImplTest(String name) {
		super(name);
	}	
	
	@Resource
	PlayTypeFacade playTypeFacade;
	
	final String facadeName = "em|二码/qezx|前二直选/ds";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(facadeName);
	}

	@Override
	protected void tearDown() throws Exception {
		//super.tearDown();
	}
	
	public void testIsMatchWinningNum_winning(){
		String betNum = "01 02";
		Issue issue = new Issue();
		issue.setRetNum("01,02,03,9,6");
		
		OrderInfo order = new OrderInfo();
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertTrue(ret);
		
		
		betNum = "01 02;09 08";
		issue = new Issue();
		issue.setRetNum("01,02,03,9,6");
		
		order = new OrderInfo();
		order.setBetNum(betNum);
		
		ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertTrue(ret);
		
	}
	
	public void testPreProcessNumber(){
		Map<String, Object> params = new HashMap<>();
		//Date startTime = new Date();
		String betNum = "07 08";
		Integer times = 1;
		Float monUnit = 1.0F;
		Integer playType = 1;
		String lottoType = "cqssc";
				
		UserInfo user = new UserInfo();
		user.setId(14);
		user.setPlatRebate(new BigDecimal(5.0F));
		
		
		params.put("betNum", betNum);
		params.put("times", times);
		params.put("monUnit", monUnit);
		params.put("playType", playType);
		params.put("lottoType", lottoType);
		
		Map<String, Object> ret = playTypeFacade.preProcessNumber(params, user);
		Assert.assertNotNull(ret);
		
	}
	
	public void testParseBetNumber(){
		String betNum = "01 02";
		Date startDate = new Date();
		List<Map<String, String>> ret = playTypeFacade.parseBetNumber(betNum);
		
		Date endDate = new Date();
		System.out.println(String.format("create Arragnge %s , take over %s ms", 
				ret.size(),
				endDate.getTime() - startDate.getTime()));
		
		Assert.assertNotNull(ret);
		 
		Assert.assertTrue(ret.size() == 1331);
		
		betNum = "01 02;09 08";
		startDate = new Date();
		ret = playTypeFacade.parseBetNumber(betNum);
		
		endDate = new Date();
		System.out.println(String.format("create Arragnge %s , take over %s ms", 
				ret.size(),
				endDate.getTime() - startDate.getTime()));
		
		Assert.assertNotNull(ret);
		 
		Assert.assertTrue(ret.size() == 2662);		
	}
	
	
	public void testValidBetNum_invalid_betnum_(){
		String betNum = "a 01";
		OrderInfo order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
				
		betNum = "1";		
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		
		betNum = "01";		
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "01 02 03 04 05 06 07 08 09 10 11";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "01 02 03;01 02";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "01 01";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
	}
	
	public void testValidBetNum_valid_betnum_(){
		String betNum = "01 02";
		OrderInfo order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.validBetNum(order);
		Assert.assertTrue(ret);
		
		betNum = "01 02;03 04";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertTrue(ret);
		
	}
	
	public void testObtainSampleBetNumber(){
		int counter = 0;
		int maxCounter = 1000;
		String betNum = null;
		boolean isWinning = false;
		boolean isValid = false;
		while(counter < maxCounter) {
			betNum = playTypeFacade.obtainSampleBetNumber();
			
			System.out.println(String.format("current bet number   %s", 
					betNum));
			
			String winningNum = obtainWinningNum(betNum);
			OrderInfo order = new OrderInfo();
			order.setBetNum(betNum);
			
			Issue issue = new Issue();
			issue.setRetNum(winningNum);
			
			isValid = playTypeFacade.validBetNum(order);
			
			counter++;
			
			if(!isValid) {
				continue;
			}
			isWinning = playTypeFacade.isMatchWinningNum(issue, order);
			
			System.out.println(String.format("winingNum  %s   current bet number   %s   isVliad  %s    isWnning  %s", 
					winningNum,
					betNum,
					isValid,
					isWinning));
			
			Assert.assertTrue(isValid);
			
		}
	}
	
	private String obtainWinningNum(String betNum) {
		StringBuffer winningNumBuffer = new StringBuffer();
		List<Map<String, String>> maps = playTypeFacade.parseBetNumber(betNum);
		if(maps != null && maps.size() > 0) {
			Map<String, String> row = maps.get(0);
			String winningNum = row.get(Constants.KEY_FACADE_BET_NUM_SAMPLE);
			for(int i = 0; i< winningNum.length();) { 
				String bit = winningNum.substring(i, i + 1);
				if(!",".equals(bit)) {
					winningNumBuffer.append(bit).append(",");
				}
				
				i += 1;
			}
			winningNumBuffer.delete(winningNumBuffer.length() - 1, winningNumBuffer.length());
		}
		
		return winningNumBuffer.toString();
	}
}
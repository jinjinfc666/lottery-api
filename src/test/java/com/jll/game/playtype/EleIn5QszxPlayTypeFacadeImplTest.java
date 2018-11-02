package com.jll.game.playtype;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;

import com.ehome.test.ServiceJunitBase;
import com.jll.common.constants.Constants;
import com.jll.common.utils.DateUtil;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;
import com.jll.game.playtypefacade.PlayTypeFactory;

public class EleIn5QszxPlayTypeFacadeImplTest extends ServiceJunitBase{
		
	public EleIn5QszxPlayTypeFacadeImplTest(String name) {
		super(name);
	}	
	
	@Resource
	PlayTypeFacade playTypeFacade;
	
	final String facadeName = "sm|三码/qszx|前三直选/fs-ds";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(facadeName);
	}

	@Override
	protected void tearDown() throws Exception {
		//super.tearDown();
	}
	
	public void ItestParseBetNumber(){
		String betNum = "12";
		
		List<Map<String, String>> ret = playTypeFacade.parseBetNumber(betNum);
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 6);
	}
	
	
	public void ItestPreProcessNumber_05070408_0405080109_110604(){
		String betNum = "05070408,0405080109,110604";
		
		Map<String, Object> params = new HashMap<>();
		Integer times = 1;
		Float monUnit = 1.0F;
		Integer playType = 1;
		String lottoType = "gd11x5";
				
		UserInfo user = new UserInfo();
		user.setId(14);
		user.setPlatRebate(new BigDecimal(5.0F));
		
		Float betAmount = null;
		Integer betTotal = null;
		
		
		params.put("betNum", betNum);
		params.put("times", times);
		params.put("monUnit", monUnit);
		params.put("playType", playType);
		params.put("lottoType", lottoType);
		
		Map<String, Object> ret = playTypeFacade.preProcessNumber(params, user);
		
		Assert.assertNotNull(ret);
		
		betAmount = (Float)ret.get("betAmount");
		betTotal = (Integer)ret.get("betTotal");
		
		Assert.assertTrue(new BigDecimal(betAmount).compareTo(new BigDecimal(44.0F)) == 0);
		Assert.assertTrue(betTotal == 44);
	}
	
	public void ItestPreProcessNumber_01_0203_02(){
		String betNum = "01,0203,02";
		
		Map<String, Object> params = new HashMap<>();
		Integer times = 1;
		Float monUnit = 1.0F;
		Integer playType = 1;
		String lottoType = "gd11x5";
				
		UserInfo user = new UserInfo();
		user.setId(14);
		user.setPlatRebate(new BigDecimal(5.0F));
		
		Float betAmount = null;
		Integer betTotal = null;
		
		
		params.put("betNum", betNum);
		params.put("times", times);
		params.put("monUnit", monUnit);
		params.put("playType", playType);
		params.put("lottoType", lottoType);
		
		Map<String, Object> ret = playTypeFacade.preProcessNumber(params, user);
		
		Assert.assertNotNull(ret);
		
		betAmount = (Float)ret.get("betAmount");
		betTotal = (Integer)ret.get("betTotal");
		
		Assert.assertTrue(new BigDecimal(betAmount).compareTo(new BigDecimal(1.0F)) == 0);
		Assert.assertTrue(betTotal == 1);
	}
	
	public void ItestPreProcessNumber_01_0203_0203(){
		String betNum = "01,0203,0203";
		
		Map<String, Object> params = new HashMap<>();
		Integer times = 1;
		Float monUnit = 1.0F;
		Integer playType = 1;
		String lottoType = "gd11x5";
				
		UserInfo user = new UserInfo();
		user.setId(14);
		user.setPlatRebate(new BigDecimal(5.0F));
		
		Float betAmount = null;
		Integer betTotal = null;
		
		
		params.put("betNum", betNum);
		params.put("times", times);
		params.put("monUnit", monUnit);
		params.put("playType", playType);
		params.put("lottoType", lottoType);
		
		Map<String, Object> ret = playTypeFacade.preProcessNumber(params, user);
		
		Assert.assertNotNull(ret);
		
		betAmount = (Float)ret.get("betAmount");
		betTotal = (Integer)ret.get("betTotal");
		
		Assert.assertTrue(new BigDecimal(betAmount).compareTo(new BigDecimal(2.0F)) == 0);
		Assert.assertTrue(betTotal == 2);
	}
	
	
	public void ItestIsMatchWinningNum_winning(){
		String betNum = "01,02,04";
		Issue issue = new Issue();
		issue.setRetNum("01,02,04,09,06");
		
		OrderInfo order = new OrderInfo();
		//order.setIssueId(issueId);
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertTrue(ret);
		
	}
	
	
	public void ItestValidBetNum_invalid_betnum_(){
		
		String betNum = "00,01,02";
		OrderInfo order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "12,01,02";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "01,02,03,04";		
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = " ";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "01";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);		
		
		betNum = "1,1,1";		
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "1,1";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "1";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "01,01,02";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "0102,0102,02";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
	}
	
	public void ItestValidBetNum_valid_betnum_(){
		String betNum = "01,02,";
		for(int i = 3; i < 12; i++) {
			if(i < 10) {
				betNum += "0" + Integer.toString(i);				
			}else {
				betNum += Integer.toString(i);			
			}
			OrderInfo order = new OrderInfo();
			
			order.setBetNum(betNum);
			
			boolean ret = playTypeFacade.validBetNum(order);
			Assert.assertTrue(ret);
			
			betNum = "01,02,";
		}
		
	}
	

	public void ItestObtainSampleBetNumber(){
		int counter = 0;
		int maxCounter = 1000;
		String betNum = null;
		boolean isWinning = false;
		boolean isValid = false;
		while(counter < maxCounter) {
			betNum = playTypeFacade.obtainSampleBetNumber();
			OrderInfo order = new OrderInfo();
			order.setBetNum(betNum);
			
			isValid = playTypeFacade.validBetNum(order);
			
			/*System.out.println(String.format("current bet number   %s   isVliad  %s", 
					betNum,
					isValid));*/
			Assert.assertTrue(isValid);
			
			String winningNum = obtainWinningNum(betNum);
			
			Issue issue = new Issue();
			issue.setRetNum(winningNum);
			
			isWinning = playTypeFacade.isMatchWinningNum(issue, order);
			
			System.out.println(String.format("winingNum  %s   current bet number   %s   isVliad  %s    isWnning  %s", 
					winningNum,
					betNum,
					isValid,
					isWinning));
			
			counter++;
			
			System.out.println(String.format("current counter   %s", 
					counter));
		}
	}
	
	private String obtainWinningNum(String betNum) {
		StringBuffer winningNumBuffer = new StringBuffer();
		List<Map<String, String>> maps = playTypeFacade.parseBetNumber(betNum);
		if(maps != null && maps.size() > 0) {
			Map<String, String> row = maps.get(0);
			String winningNum = row.get(Constants.KEY_FACADE_BET_NUM_SAMPLE);
			for(int i = 0; i< winningNum.length();) { 
				String bit = winningNum.substring(i, i + 2);
				if(!",".equals(bit)) {
					winningNumBuffer.append(bit).append(",");
				}
				
				i += 2;
			}
			winningNumBuffer.delete(winningNumBuffer.length() - 1, winningNumBuffer.length());
		}
		
		return winningNumBuffer.toString();
	}
	
}
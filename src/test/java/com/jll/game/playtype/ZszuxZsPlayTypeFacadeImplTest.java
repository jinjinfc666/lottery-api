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
import com.jll.common.utils.MathUtil;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;
import com.jll.game.playtypefacade.PlayTypeFactory;

public class ZszuxZsPlayTypeFacadeImplTest extends ServiceJunitBase{
		
	public ZszuxZsPlayTypeFacadeImplTest(String name) {
		super(name);
	}	
	
	@Resource
	PlayTypeFacade playTypeFacade;
	
	final String facadeName = "zszux|中三组选/zsfs";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(facadeName);
	}

	@Override
	protected void tearDown() throws Exception {
		//super.tearDown();
	}
	
	public void testParseBetNumber(){
		String betNum = "01";
		Date startDate = new Date();
		List<Map<String, String>> ret = playTypeFacade.parseBetNumber(betNum);
		Date endDate = new Date();
		System.out.println(String.format("create Arragnge %s , take over %s ms", 
				ret.size(),
				endDate.getTime() - startDate.getTime()));
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 600);
		
		betNum = "012";
		startDate = new Date();
		ret = playTypeFacade.parseBetNumber(betNum);
		
		endDate = new Date();
		System.out.println(String.format("create Arragnge %s , take over %s ms", 
				ret.size(),
				endDate.getTime() - startDate.getTime()));
		
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 1800);
		
		betNum = "01;21";
		startDate = new Date();
		ret = playTypeFacade.parseBetNumber(betNum);
		
		endDate = new Date();
		System.out.println(String.format("create Arragnge %s , take over %s ms", 
				ret.size(),
				endDate.getTime() - startDate.getTime()));
		
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 1200);
		
		
		betNum = "0123456789";
		startDate = new Date();
		ret = playTypeFacade.parseBetNumber(betNum);
		endDate = new Date();
		System.out.println(String.format("create Arragnge %s , take over %s ms", 
				ret.size(),
				endDate.getTime() - startDate.getTime()));
		
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 27000);
	}
	
	public void testIsMatchWinningNum_winning(){
		String betNum = "12";
		Issue issue = new Issue();
		issue.setRetNum("6,1,2,2,9");
		
		OrderInfo order = new OrderInfo();
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertTrue(ret);
		
		betNum = "12";
		issue = new Issue();
		issue.setRetNum("6,1,1,2,9");
		
		order = new OrderInfo();
		order.setBetNum(betNum);
		
		ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertTrue(ret);
		
		
		betNum = "12;89";
		issue = new Issue();
		issue.setRetNum("6,1,2,1,9");
		
		order = new OrderInfo();
		order.setBetNum(betNum);
		
		ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertTrue(ret);
		
		betNum = "89;12";
		issue = new Issue();
		issue.setRetNum("6,1,2,1,9");
		
		order = new OrderInfo();
		order.setBetNum(betNum);
		
		ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertTrue(ret);
		
		
		betNum = "0123;89";
		issue = new Issue();
		issue.setRetNum("6,1,3,3,9");
		
		order = new OrderInfo();
		order.setBetNum(betNum);
		
		ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertTrue(ret);
	}
	
	
	public void testIsMatchWinningNum_lost(){
		String betNum = "02";
		Issue issue = new Issue();
		issue.setRetNum("6,1,3,3,9");
		
		OrderInfo order = new OrderInfo();
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertFalse(ret);
		
		
		betNum = "02;89";
		issue = new Issue();
		issue.setRetNum("6,1,3,3,9");
		
		order = new OrderInfo();
		order.setBetNum(betNum);
		
		ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertFalse(ret);
		
		
	}
	
	
	public void testPreProcessNumber(){
		Map<String, Object> params = new HashMap<>();
		//Date startTime = new Date();
		String betNum = "12";
		Integer times = 1;
		Float monUnit = 1.0F;
		Integer playType = 1;
		//String lottoType = "cqssc";
		Float betAmount = null;
		Integer betTotal = null;
		Float maxWinAmount = null;
		Float maxWinAmountCompare = null;
		BigDecimal singleBettingPrize = null;
		UserInfo user = new UserInfo();
		user.setId(14);
		user.setPlatRebate(new BigDecimal(5.0F));
		
		
		params.put("betNum", betNum);
		params.put("times", times);
		params.put("monUnit", monUnit);
		params.put("playType", playType);
		//params.put("lottoType", lottoType);
		
		Map<String, Object> ret = playTypeFacade.preProcessNumber(params, user);
		Assert.assertNotNull(ret);
		
		betAmount = (Float)ret.get("betAmount");
		betTotal = (Integer)ret.get("betTotal");
		maxWinAmount = (Float)ret.get("maxWinAmount");
		singleBettingPrize = (BigDecimal)ret.get("singleBettingPrize");
		Assert.assertTrue(new BigDecimal(betAmount).compareTo(new BigDecimal(2.0F)) == 0);
		Assert.assertTrue(betTotal == 2);
		
		maxWinAmountCompare = MathUtil.multiply(1, 
				times, 
				Float.class);
		maxWinAmountCompare = MathUtil.multiply(maxWinAmountCompare, 
				monUnit, 
				Float.class);
		maxWinAmountCompare = MathUtil.multiply(maxWinAmountCompare, 
				singleBettingPrize.floatValue(), 
				Float.class);
		
		Assert.assertTrue(new BigDecimal(maxWinAmount).compareTo(new BigDecimal(maxWinAmountCompare)) == 0);
		
		
		params = new HashMap<>();
		//Date startTime = new Date();
		betNum = "12;45";
		times = 1;
		monUnit = 1.0F;
		playType = 1;
		//lottoType = "cqssc";
		betAmount = null;
		betTotal = null;
		
		user = new UserInfo();
		user.setId(14);
		user.setPlatRebate(new BigDecimal(5.0F));
		
		
		params.put("betNum", betNum);
		params.put("times", times);
		params.put("monUnit", monUnit);
		params.put("playType", playType);
		//params.put("lottoType", lottoType);
		
		ret = playTypeFacade.preProcessNumber(params, user);
		Assert.assertNotNull(ret);
		
		betAmount = (Float)ret.get("betAmount");
		betTotal = (Integer)ret.get("betTotal");
		maxWinAmount = (Float)ret.get("maxWinAmount");
		singleBettingPrize = (BigDecimal)ret.get("singleBettingPrize");
		Assert.assertTrue(new BigDecimal(betAmount).compareTo(new BigDecimal(4.0F)) == 0);
		Assert.assertTrue(betTotal == 4);
		maxWinAmountCompare = MathUtil.multiply(2, 
				times, 
				Float.class);
		maxWinAmountCompare = MathUtil.multiply(maxWinAmountCompare, 
				monUnit, 
				Float.class);
		maxWinAmountCompare = MathUtil.multiply(maxWinAmountCompare, 
				singleBettingPrize.floatValue(), 
				Float.class);
		
		Assert.assertTrue(new BigDecimal(maxWinAmount).compareTo(new BigDecimal(maxWinAmountCompare)) == 0);
		
		
		
		params = new HashMap<>();
		//Date startTime = new Date();
		betNum = "0123456789";
		times = 1;
		monUnit = 1.0F;
		playType = 1;
		//lottoType = "cqssc";
		betAmount = null;
		betTotal = null;
		
		user = new UserInfo();
		user.setId(14);
		user.setPlatRebate(new BigDecimal(5.0F));
		
		
		params.put("betNum", betNum);
		params.put("times", times);
		params.put("monUnit", monUnit);
		params.put("playType", playType);
		//params.put("lottoType", lottoType);
		
		ret = playTypeFacade.preProcessNumber(params, user);
		Assert.assertNotNull(ret);
		
		betAmount = (Float)ret.get("betAmount");
		betTotal = (Integer)ret.get("betTotal");
		maxWinAmount = (Float)ret.get("maxWinAmount");
		singleBettingPrize = (BigDecimal)ret.get("singleBettingPrize");
		
		Assert.assertTrue(new BigDecimal(betAmount).compareTo(new BigDecimal(90.0F)) == 0);
		Assert.assertTrue(betTotal == 90);
		maxWinAmountCompare = MathUtil.multiply(1, 
				times, 
				Float.class);
		maxWinAmountCompare = MathUtil.multiply(maxWinAmountCompare, 
				monUnit, 
				Float.class);
		maxWinAmountCompare = MathUtil.multiply(maxWinAmountCompare, 
				singleBettingPrize.floatValue(), 
				Float.class);
		
		Assert.assertTrue(new BigDecimal(maxWinAmount).compareTo(new BigDecimal(maxWinAmountCompare)) == 0);
		
	}
	
	public void testValidBetNum_invalid_betnum_(){
		String betNum = "a1";
		OrderInfo order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
				
		betNum = "0";		
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
				
		betNum = "00123";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
	}
	
	public void testValidBetNum_valid_betnum_(){
		String betNum = "201";
		OrderInfo order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.validBetNum(order);
		Assert.assertTrue(ret);
		
		betNum = "12;34";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertTrue(ret);
		
		betNum = "0123456789;34";
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
			counter++;
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
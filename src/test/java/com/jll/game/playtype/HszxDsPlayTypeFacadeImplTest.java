package com.jll.game.playtype;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;

import com.ehome.test.ServiceJunitBase;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;
import com.jll.game.playtypefacade.PlayTypeFactory;

public class HszxDsPlayTypeFacadeImplTest extends ServiceJunitBase{
		
	public HszxDsPlayTypeFacadeImplTest(String name) {
		super(name);
	}	
	
	@Resource
	PlayTypeFacade playTypeFacade;
	
	final String facadeName = "hszx|后三直选/ds";
	
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
		String betNum = "096";
		Issue issue = new Issue();
		issue.setRetNum("0,0,0,9,6");
		
		OrderInfo order = new OrderInfo();
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertTrue(ret);
		
		
		betNum = "096;999";
		issue = new Issue();
		issue.setRetNum("0,0,0,9,6");
		
		order = new OrderInfo();
		order.setBetNum(betNum);
		
		ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertTrue(ret);
		
	}
	
	public void testPreProcessNumber(){
		Map<String, Object> params = new HashMap<>();
		//Date startTime = new Date();
		String betNum = "000";
		Integer times = 1;
		Float monUnit = 1.0F;
		Integer playType = 1;
		//String lottoType = "cqssc";
		Float betAmount = null;
		Integer betTotal = null;
		Float maxWinAmount = null;
		
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
		
		Assert.assertTrue(new BigDecimal(betAmount).compareTo(new BigDecimal(1.0F)) == 0);
		Assert.assertTrue(new BigDecimal(maxWinAmount).compareTo(new BigDecimal(933.55F)) == 0);
		Assert.assertTrue(betTotal == 1);
		
		
		params = new HashMap<>();
		//Date startTime = new Date();
		betNum = "000;001";
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
		
		Assert.assertTrue(new BigDecimal(betAmount).compareTo(new BigDecimal(2.0F)) == 0);
		Assert.assertTrue(new BigDecimal(maxWinAmount).compareTo(new BigDecimal(1867.1F)) == 0);
		Assert.assertTrue(betTotal == 2);
	}
	
	public void testParseBetNumber(){
		String betNum = "000";
		Date startDate = new Date();
		List<Map<String, String>> ret = playTypeFacade.parseBetNumber(betNum);
		
		Date endDate = new Date();
		System.out.println(String.format("create Arragnge %s , take over %s ms", 
				ret.size(),
				endDate.getTime() - startDate.getTime()));
		
		Assert.assertNotNull(ret);
		 
		Assert.assertTrue(ret.size() == 100);
		
		betNum = "000;999";
		startDate = new Date();
		ret = playTypeFacade.parseBetNumber(betNum);
		
		endDate = new Date();
		System.out.println(String.format("create Arragnge %s , take over %s ms", 
				ret.size(),
				endDate.getTime() - startDate.getTime()));
		
		Assert.assertNotNull(ret);
		 
		Assert.assertTrue(ret.size() == 200);		
	}
}
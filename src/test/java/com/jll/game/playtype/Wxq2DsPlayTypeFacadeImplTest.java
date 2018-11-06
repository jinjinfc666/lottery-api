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

public class Wxq2DsPlayTypeFacadeImplTest extends ServiceJunitBase{
		
	public Wxq2DsPlayTypeFacadeImplTest(String name) {
		super(name);
	}	
	
	@Resource
	PlayTypeFacade playTypeFacade;
	
	final String facadeName = "wxq2|五星前二/ds";
	
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
		String betNum = "00";
		Issue issue = new Issue();
		issue.setRetNum("0,0,0,9,6");
		
		OrderInfo order = new OrderInfo();
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertTrue(ret);
		
		
		betNum = "00;99";
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
		String betNum = "78";
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
		String betNum = "00";
		Date startDate = new Date();
		List<Map<String, String>> ret = playTypeFacade.parseBetNumber(betNum);
		
		Date endDate = new Date();
		System.out.println(String.format("create Arragnge %s , take over %s ms", 
				ret.size(),
				endDate.getTime() - startDate.getTime()));
		
		Assert.assertNotNull(ret);
		 
		Assert.assertTrue(ret.size() == 1000);
		
		betNum = "00;99";
		startDate = new Date();
		ret = playTypeFacade.parseBetNumber(betNum);
		
		endDate = new Date();
		System.out.println(String.format("create Arragnge %s , take over %s ms", 
				ret.size(),
				endDate.getTime() - startDate.getTime()));
		
		Assert.assertNotNull(ret);
		 
		Assert.assertTrue(ret.size() == 2000);		
	}
}
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
		String betNum = "0";
		Date startDate = new Date();
		List<Map<String, String>> ret = playTypeFacade.parseBetNumber(betNum);
		Date endDate = new Date();
		System.out.println(String.format("create Arragnge %s , take over %s ms", 
				ret.size(),
				endDate.getTime() - startDate.getTime()));
		
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 0);
		
		betNum = "01";
		startDate = new Date();
		ret = playTypeFacade.parseBetNumber(betNum);
		endDate = new Date();
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
	
	public void ItestIsMatchWinningNum_winning(){
		Date startTime = new Date();
		String betNum = "12,23,456";
		Issue issue = new Issue();
		issue.setIssueNum("");
		issue.setLotteryType(Constants.LottoType.CQSSC.getCode());
		issue.setRetNum("1,2,4,9,6");
		issue.setStartTime(startTime);
		issue.setState(Constants.IssueState.LOTTO_DARW.getCode());
		issue.setEndTime(DateUtil.addMinutes(startTime, 10));
		
		OrderInfo order = new OrderInfo();
		//order.setIssueId(issueId);
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertNotNull(ret);
		
	}
	
	public void ItestProcessNumber(){
		Map<String, Object> params = new HashMap<>();
		String betNum = "68";
		Integer times = 1;
		Float monUnit = 1.0F;
		String lottoType = "cqssc";
		Float betAmount = 0F;
		int betTotal = 0;
		
		params.put("betNum", betNum);
		params.put("times", times);
		params.put("monUnit", monUnit);
		params.put("lottoType", lottoType);
		UserInfo user = new UserInfo();
		user.setId(14);
		user.setUserName("test001");
		user.setUserType(Constants.UserType.PLAYER.getCode());
		user.setPlatRebate(new BigDecimal(5.0F));
		Map<String,Object> ret = playTypeFacade.preProcessNumber(params, user);
		Assert.assertNotNull(ret);
		
		betAmount = (Float)ret.get("betAmount");
		betTotal = (Integer)ret.get("betTotal");
		
		Assert.assertTrue(betAmount == 2);
		Assert.assertTrue(betTotal == 2);
	}
	
	public void ItestProcessNumber_towBetNumbers(){
		Map<String, Object> params = new HashMap<>();
		String betNum = "68;13";
		Integer times = 1;
		Float monUnit = 1.0F;
		String lottoType = "cqssc";
		Float betAmount = 0F;
		int betTotal = 0;
		
		params.put("betNum", betNum);
		params.put("times", times);
		params.put("monUnit", monUnit);
		params.put("lottoType", lottoType);
		UserInfo user = new UserInfo();
		user.setId(14);
		user.setUserName("test001");
		user.setUserType(Constants.UserType.PLAYER.getCode());
		user.setPlatRebate(new BigDecimal(5.0F));
		Map<String,Object> ret = playTypeFacade.preProcessNumber(params, user);
		Assert.assertNotNull(ret);
		
		betAmount = (Float)ret.get("betAmount");
		betTotal = (Integer)ret.get("betTotal");
		
		Assert.assertTrue(betAmount == 4);
		Assert.assertTrue(betTotal == 4);
	}
	
	public void ItestIsMatchWinningNum_sameBits(){
		Date startTime = new Date();
		String betNum = "60";
		Issue issue = new Issue();
		issue.setIssueNum("");
		issue.setLotteryType(Constants.LottoType.CQSSC.getCode());
		issue.setRetNum("9,0,0,0,7");
		issue.setStartTime(startTime);
		issue.setState(Constants.IssueState.LOTTO_DARW.getCode());
		issue.setEndTime(DateUtil.addMinutes(startTime, 10));
		
		OrderInfo order = new OrderInfo();
		//order.setIssueId(issueId);
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertFalse(ret);
	}
}
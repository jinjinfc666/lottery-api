package com.jll.game.playtype;

import java.math.BigDecimal;
import java.util.Date;
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

public class HszuxZsPlayTypeFacadeImplTest extends ServiceJunitBase{
		
	public HszuxZsPlayTypeFacadeImplTest(String name) {
		super(name);
	}	
	
	@Resource
	PlayTypeFacade playTypeFacade;
	
	final String facadeName = "hszux|后三组选/zsfs";
	
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
		String betNum = "123";
		
		List<Map<String, String>> ret = playTypeFacade.parseBetNumber(betNum);
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 18);
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
	
	public void testCalPrize_zszux_zs(){
		String winningNum = "0,0,1,1,9";
		String betNum = "19";
		String lottoType = "cqssc";
		BigDecimal prize = null;
		
		Issue issue = new Issue();
		issue.setId(5);
		issue.setIssueNum("181016-005");
		issue.setLotteryType(lottoType);
		issue.setRetNum(winningNum);
		
		
		OrderInfo order = new OrderInfo();
		order.setIssueId(issue.getId());
		order.setBetNum(betNum);
		//hszux|后三组选|zsfs--组三复式
		order.setPlayType(13);
		order.setTimes(1);
		order.setIsZh(0);
		order.setPattern(new BigDecimal(1));
		
		
		UserInfo user = new UserInfo();
		user.setId(14);
		user.setUserName("test001");
		user.setUserType(Constants.UserType.PLAYER.getCode());
		user.setPlatRebate(new BigDecimal(5.0F));
		prize = playTypeFacade.calPrize(issue, order, user);
		Assert.assertNotNull(prize);
				
		Assert.assertTrue(prize.compareTo(new BigDecimal(311.18F)) == 0);
	}
}
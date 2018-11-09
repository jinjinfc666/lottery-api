package com.jll.game.playtype;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;

import com.ehome.test.ServiceJunitBase;
import com.jll.common.constants.Constants;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;
import com.jll.game.playtypefacade.PlayTypeFactory;

public class EleIn5QwDsPlayTypeFacadeImplTest extends ServiceJunitBase{
		
	public EleIn5QwDsPlayTypeFacadeImplTest(String name) {
		super(name);
	}	
	
	@Resource
	PlayTypeFacade playTypeFacade;
	
	final String facadeName = "qwx|趣味型/qwdds|趣味定单双/fs";
	
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
		String betNum = "01";
		
		List<Map<String, String>> ret = playTypeFacade.parseBetNumber(betNum);
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 120);
		
		
		betNum = "02";
		
		ret = playTypeFacade.parseBetNumber(betNum);
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 3600);
		
		/*for(Map<String, String> temp : ret) {
			String tempNum = temp.get(Constants.KEY_FACADE_BET_NUM_SAMPLE);
			System.out.println(String.format("current value  %s", tempNum));
		}*/
		
		betNum = "03";
		
		ret = playTypeFacade.parseBetNumber(betNum);
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 18000);
		
		betNum = "04";
		
		ret = playTypeFacade.parseBetNumber(betNum);
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 24000);
		
		betNum = "05";
		
		ret = playTypeFacade.parseBetNumber(betNum);
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 9000);
		
		betNum = "06";
		
		ret = playTypeFacade.parseBetNumber(betNum);
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 720);
	}
	
	public void ItestIsMatchWinningNum_winning(){
		String betNum = "010204";
		Issue issue = new Issue();
		issue.setRetNum("01,02,04,09,06");
		
		OrderInfo order = new OrderInfo();
		//order.setIssueId(issueId);
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertNotNull(ret);
		
	}
	
	public void ItestIsMatchWinningNum_winning_0402(){
		String betNum = "0402";
		Issue issue = new Issue();
		issue.setRetNum("03,01,07,08,11");
		
		OrderInfo order = new OrderInfo();
		//order.setIssueId(issueId);
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertFalse(ret);
		
	}
	
	
	public void testIsMatchWinningNum_winning_05020103(){
		String betNum = "05020103";
		Issue issue = new Issue();
		//算5个奇数
		issue.setRetNum("09,07,11,01,01");
		
		OrderInfo order = new OrderInfo();
		//order.setIssueId(issueId);
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertFalse(ret);
		
	}
	
	public void testValidBetNum_invalid_betnum_(){
		String betNum = "07060504030201";
		OrderInfo order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = " ";		
		order = new OrderInfo();
		
		order.setBetNum(betNum); 
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		
		betNum = "060504030200";		
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		
		betNum = "6543210";		
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
				
		betNum = "06,05,04,03,02,01";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "060504030101";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "060504030107";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
	}
	
	public void testValidBetNum_valid_betnum_(){
		String betNum = "060504030201";
		OrderInfo order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.validBetNum(order);
		Assert.assertTrue(ret);
		
		betNum = "01";		
		order = new OrderInfo();
		
		order.setBetNum(betNum); 
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertTrue(ret);
		
		
		betNum = "0102";		
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertTrue(ret);
		
		
		betNum = "010203";		
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertTrue(ret);
				
		betNum = "01020304";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertTrue(ret);
		
		betNum = "0102030405";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertTrue(ret);
		
	}
	

	public void ItestObtainSampleBetNumber(){
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
	
	
	public void ItestPreProcessNumber(){
		Map<String, Object> params = new HashMap<>();
		String betNum = "0402";
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
		
		Assert.assertTrue(betAmount == 2);
		Assert.assertTrue(betTotal == 2);
		
	}
	
	public void ItestCalPrize_030504(){
		String winningNum = "06,02,01,03,04";
		String betNum = "030504";
		Map<String, Object> ret;
		BigDecimal prize = null;
		
		Issue issue = new Issue();
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
		ret = playTypeFacade.calPrize(issue, order, user);
		prize = new BigDecimal((Float)ret.get(Constants.KEY_WIN_AMOUNT));
		Assert.assertNotNull(prize);
				
		Assert.assertTrue(prize.compareTo(new BigDecimal(2.875F)) == 0);
	}
	
	public void ItestCalPrize_020406(){
		String winningNum = "08,10,06,04,07";
		String betNum = "020406";
		Map<String, Object> ret;
		BigDecimal prize = null;
		
		Issue issue = new Issue();
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
		ret = playTypeFacade.calPrize(issue, order, user);
		prize = new BigDecimal((Float)ret.get(Constants.KEY_WIN_AMOUNT));
		Assert.assertNotNull(prize);
				
		Assert.assertTrue(prize.compareTo(new BigDecimal(14.38440036773681640625F)) == 0);
	}
	
	
	public void ItestCalPrize_5(){
		String winningNum = "09,11,01,05,03";
		String betNum = "060204";
		Map<String, Object> ret;
		BigDecimal prize = null;
		
		Issue issue = new Issue();
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
		ret = playTypeFacade.calPrize(issue, order, user);
		prize = new BigDecimal((Float)ret.get(Constants.KEY_WIN_AMOUNT));
		Assert.assertNotNull(prize);
				
		Assert.assertTrue(prize.compareTo(new BigDecimal(71.81150054931640625F)) == 0);
	}
	
	public void ItestCalPrize_4(){
		String winningNum = "02,11,01,05,03";
		String betNum = "060205";
		Map<String, Object> ret;
		BigDecimal prize = null;
		
		Issue issue = new Issue();
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
		ret = playTypeFacade.calPrize(issue, order, user);
		prize = new BigDecimal((Float)ret.get(Constants.KEY_WIN_AMOUNT));
		Assert.assertNotNull(prize);
				
		Assert.assertTrue(prize.compareTo(new BigDecimal(5.75199985504150390625F)) == 0);
	}
	
	public void ItestCalPrize_3(){
		String winningNum = "02,10,01,05,03";
		String betNum = "060204";
		Map<String, Object> ret;
		BigDecimal prize = null;
		
		Issue issue = new Issue();
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
		ret = playTypeFacade.calPrize(issue, order, user);
		prize = new BigDecimal((Float)ret.get(Constants.KEY_WIN_AMOUNT));
		Assert.assertNotNull(prize);
				
		Assert.assertTrue(prize.compareTo(new BigDecimal(2.1565001010894775390625F)) == 0);
	}
	
	public void ItestCalPrize_2(){
		String winningNum = "02,10,04,05,03";
		String betNum = "060203";
		Map<String, Object> ret;
		BigDecimal prize = null;
		
		Issue issue = new Issue();
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
		ret = playTypeFacade.calPrize(issue, order, user);
		prize = new BigDecimal((Float)ret.get(Constants.KEY_WIN_AMOUNT));
		Assert.assertNotNull(prize);
				
		Assert.assertTrue(prize.compareTo(new BigDecimal(2.8750998973846435546875F)) == 0);
	}
	
	public void ItestCalPrize_1(){
		String winningNum = "06,02,08,03,04";
		String betNum = "030502";
		Map<String, Object> ret;
		BigDecimal prize = null;
		
		Issue issue = new Issue();
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
		ret = playTypeFacade.calPrize(issue, order, user);
		prize = new BigDecimal((Float)ret.get(Constants.KEY_WIN_AMOUNT));
		Assert.assertNotNull(prize);
				
		Assert.assertTrue(prize.compareTo(new BigDecimal(14.38440036773681640625F)) == 0);
	}
	
	public void ItestCalPrize_0(){
		String winningNum = "06,02,08,10,04";
		String betNum = "030501";
		Map<String, Object> ret;
		BigDecimal prize = null;
		
		Issue issue = new Issue();
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
		ret = playTypeFacade.calPrize(issue, order, user);
		prize = new BigDecimal((Float)ret.get(Constants.KEY_WIN_AMOUNT));
		Assert.assertNotNull(prize);
				
		Assert.assertTrue(prize.compareTo(new BigDecimal(424.340911865234375F)) == 0);
	}
}
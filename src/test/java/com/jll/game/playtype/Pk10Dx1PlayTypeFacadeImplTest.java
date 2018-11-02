package com.jll.game.playtype;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;

import com.ehome.test.ServiceJunitBase;
import com.jll.common.constants.Constants;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.game.playtypefacade.PlayTypeFactory;

public class Pk10Dx1PlayTypeFacadeImplTest extends ServiceJunitBase{
		
	public Pk10Dx1PlayTypeFacadeImplTest(String name) {
		super(name);
	}	
	
	@Resource
	PlayTypeFacade playTypeFacade;
	
	final String facadeName = "dx|大小/dym|第一名/fs-ds";
	
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
		String betNum = "00";
		
		List<Map<String, String>> ret = playTypeFacade.parseBetNumber(betNum);
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 1);
		
		betNum = "01";
		
		ret = playTypeFacade.parseBetNumber(betNum);
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 1);
		
		betNum = "0001";
		
		ret = playTypeFacade.parseBetNumber(betNum);
		Assert.assertNotNull(ret);
		
		Assert.assertTrue(ret.size() == 1);
		
	}
	
	public void ItestIsMatchWinningNum_winning(){
		String betNum = "0102030405";
		Issue issue = new Issue();
		issue.setRetNum("01,02,04,03,05");
		
		OrderInfo order = new OrderInfo();
		//order.setIssueId(issueId);
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.isMatchWinningNum(issue, order);
		Assert.assertTrue(ret);
		
	}
	
	
	public void testValidBetNum_invalid_betnum_(){
		String betNum = "-01";
		OrderInfo order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = " ";		
		order = new OrderInfo();
		
		order.setBetNum(betNum); 
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "02";		
		order = new OrderInfo();
		
		order.setBetNum(betNum); 
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "000000";
		order = new OrderInfo();
		
		order.setBetNum(betNum); 
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		betNum = "010101";
		order = new OrderInfo();
		
		order.setBetNum(betNum); 
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
		
		betNum = "011";		
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
						
		betNum = "00,01";
		order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertFalse(ret);
		
	}
	
	public void testValidBetNum_valid_betnum_(){
		String betNum = "00";
		OrderInfo order = new OrderInfo();
		
		order.setBetNum(betNum);
		
		boolean ret = playTypeFacade.validBetNum(order);
		Assert.assertTrue(ret);
		
		betNum = "01";		
		order = new OrderInfo();
		
		order.setBetNum(betNum); 
		
		ret = playTypeFacade.validBetNum(order);
		Assert.assertTrue(ret);
		
		betNum = "0001";		
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
	
	public void ItestObtainSampleBetNumber_050402(){
		int counter = 0;
		int maxCounter = 1000;
		String betNum = null;
		boolean isWinning = false;
		boolean isValid = false;
		//while(counter < maxCounter) {
			//betNum = playTypeFacade.obtainSampleBetNumber();
			betNum = "050402";
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
		//}
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
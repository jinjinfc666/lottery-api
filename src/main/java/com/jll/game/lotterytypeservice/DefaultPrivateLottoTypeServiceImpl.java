package com.jll.game.lotterytypeservice;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.OrderDelayState;
import com.jll.common.constants.Constants.OrderState;
import com.jll.common.constants.Constants.PrizeMode;
import com.jll.common.utils.MathUtil;
import com.jll.common.utils.Utils;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.game.BulletinBoard;
import com.jll.game.IssueService;
import com.jll.game.LotteryTypeService;
import com.jll.game.order.OrderService;
import com.jll.game.playtype.PlayTypeFacade;
import com.jll.game.playtype.PlayTypeService;
import com.jll.game.playtypefacade.PlayTypeFactory;
import com.jll.spring.extend.SpringContextUtil;
import com.jll.user.UserInfoService;
import com.jll.user.details.UserAccountDetailsService;
import com.jll.user.wallet.WalletService;


public abstract class DefaultPrivateLottoTypeServiceImpl extends DefaultLottoTypeServiceImpl
{
	private Logger logger = Logger.getLogger(DefaultPrivateLottoTypeServiceImpl.class);

	//private final String lotteryType = "cqssc";
	
	IssueService issueServ = (IssueService)SpringContextUtil.getBean("issueServiceImpl");
	
	CacheRedisService cacheServ = (CacheRedisService)SpringContextUtil.getBean("cacheRedisServiceImpl");
	
	WalletService walletServ = (WalletService)SpringContextUtil.getBean("walletServiceImpl");
	
	OrderService orderInfoServ = (OrderService)SpringContextUtil.getBean("orderServiceImpl");
	
	PlayTypeService playTypeServ = (PlayTypeService)SpringContextUtil.getBean("playTypeServiceImpl");
	
	UserAccountDetailsService accDetailsServ = (UserAccountDetailsService)SpringContextUtil.getBean("userAccountDetailsServiceImpl");
	
	UserInfoService userServ = (UserInfoService)SpringContextUtil.getBean("userInfoServiceImpl");
	
	@Override
	public void queryWinningNum(String message) {
		logger.debug(String.format("message  %s", message));
		String[] lottoTypeAndIssueNum = null;
		String lottoType = null;
		String issueNum = null;
		String codeTypeName = Constants.SysCodeTypes.LOTTERY_CONFIG_5FC.getCode();
		String codeNamePrizeMode = Constants.LotteryAttributes.PRIZE_MODE.getCode();
		String codeNameWinningRate = Constants.LotteryAttributes.WINING_RATE.getCode();
		String codeNameUplimitProfitLoss = Constants.LotteryAttributes.UP_LIMIT_PROFIT_LOSS.getCode();
		SysCode sysCodePrizeMode = cacheServ.getSysCode(codeTypeName, codeNamePrizeMode);
		SysCode sysCodeWinningRate = cacheServ.getSysCode(codeTypeName, codeNameWinningRate);
		SysCode sysCodeUplimitProfitLoss = cacheServ.getSysCode(codeTypeName, codeNameUplimitProfitLoss);
		
		PrizeMode prizeMode;
		lottoTypeAndIssueNum = ((String)message).split("\\|");
		lottoType = lottoTypeAndIssueNum[0];
		issueNum = lottoTypeAndIssueNum[1];
		
		if(sysCodePrizeMode == null
				|| StringUtils.isBlank(sysCodePrizeMode.getCodeVal())) {
			return;
		}
		
		prizeMode = PrizeMode.getByCode(sysCodePrizeMode.getCodeVal());
		switch(prizeMode) {
			case MANUAL :{
				if(lottoTypeAndIssueNum.length == 3) {
					manualDrawResult(lottoType, 
							issueNum, 
							lottoTypeAndIssueNum[2]);					
				}
				return ;
			}
			case NON_INTERVENTIONAL : {
				nonInterventional(lottoType, issueNum);
				return;
			}
			case INTERVENTIONAL:{
				interventional(lottoType, issueNum, sysCodeWinningRate);
				return ;
			}
			case DAEMO :{
				Float profitLoss = queryPlatProfitLoss(lottoType, issueNum);
				Float upLimitProfitLoss = Float.parseFloat(sysCodeUplimitProfitLoss.getCodeVal());
				if(profitLoss.floatValue() < upLimitProfitLoss.floatValue()) {
					//不干涉开奖
					nonInterventional(lottoType, issueNum);
				}else {
					//干涉开奖
					interventional(lottoType, issueNum, sysCodeWinningRate);
				}
				return;
			}
		}
	}

	/**
	 * 计算平台彩种盈亏
	 * @param issueNum
	 * @return
	 */
	private Float queryPlatProfitLoss(String lottoType, String issueNum) {
		Float ret = null;
		Issue issue = issueServ.getIssueByIssueNum(lottoType, issueNum);
		
		Map<String, Object> statInfo = cacheServ
				.getPlatStat(issue.getLotteryType());
		
		if(statInfo == null || statInfo.size() == 0) {
			return 0F;
		}
		
		ret = (Float)statInfo.get(Constants.KEY_LOTTO_TYPE_PROFIT_LOSS);
		return ret;
	}

	private void interventional(String lottoType, String issueNum, SysCode sysCodeWinningRate) {
		String winningNum;
		Issue issue;
		StringBuffer buffer = null;
		boolean isMatch = false;
		
		issue = issueServ.getIssueByIssueNum(lottoType, issueNum);
		
		Map<String, Object> statInfo = cacheServ
				.getStatGroupByBettingNum(issue.getLotteryType(), 
				issue.getId());
		Map<String, Object> rateDistribute = new HashMap<>();
		Double maxRate = null;		
		
		if(statInfo == null || statInfo.size() == 0) {
			nonInterventional(lottoType, issueNum);
			return ;
		}
		
		Iterator<String> keys = statInfo.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			if(Constants.KEY_ISSUE_TOTAL_BETTING_AMOUNT.equals(key)) {
				continue;
			}
			Float betAmount = (Float)statInfo.get(Constants.KEY_ISSUE_TOTAL_BETTING_AMOUNT);
			Float prizeAmount = (Float)statInfo.get(key);
			
			Double rate = MathUtil.divide(prizeAmount, betAmount, 5);
			if(rate < Double.parseDouble(sysCodeWinningRate.getCodeVal())){
				if(rateDistribute.get(String.valueOf(rate)) != null) {
					key = rateDistribute.get(String.valueOf(rate)) + ";";
					rateDistribute.put(String.valueOf(rate), key);						
				}else {
					rateDistribute.put(String.valueOf(rate), key);
				}
				
				if(maxRate == null
						|| maxRate.doubleValue() < rate.doubleValue()) {
					maxRate = rate;
				}
			}
			
		}
		
		if(maxRate == null || statInfo.size() < 10) {
			nonMatch(lottoType, issueNum, statInfo);
			return;
		}
		
		String maxBetAmount = (String)rateDistribute.get(String.valueOf(maxRate));
		if(maxBetAmount.contains(";")) {
			maxBetAmount = maxBetAmount.substring(0, 
					maxBetAmount.indexOf(";"));
		}
		
		while(true) {
			buffer = new StringBuffer();
			Random random = new Random();
			for(int i = 0; i < 5; i++) {
				int currIndex = random.nextInt(10);
				buffer.append(Integer.toString(currIndex)).append(",");
			}
			
			buffer.delete(buffer.length() - 1, buffer.length() + 1);
			
			isMatch = Pattern.matches(maxBetAmount, buffer.toString());
			
			if(isMatch) {
				winningNum = buffer.toString();
				issue = issueServ.getIssueByIssueNum(lottoType, issueNum);
				issue.setRetNum(winningNum);
				issue.setState(Constants.IssueState.LOTTO_DARW.getCode());
				issueServ.saveIssue(issue);
				
				changeBulletinBoard(lottoType, issueNum, issue);
				
				//inform the progress to payout
				cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, lottoType +"|"+ issueNum);
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

	private void nonMatch(String lottoType, String issueNum, Map<String, Object> statInfo) {
		StringBuffer buffer = null;
		boolean isMatch = false;
		Issue issue;
		String winningNum;
		
		while(true) {
			buffer = new StringBuffer();
			Random random = new Random();
			for(int i = 0; i < 5; i++) {
				int currIndex = random.nextInt(10);
				buffer.append(Integer.toString(currIndex)).append(",");
			}
			
			buffer.delete(buffer.length() - 1, buffer.length() + 1);
			
			Iterator<String> ite = statInfo.keySet().iterator();
			while(ite.hasNext()) {
				String key = ite.next();
				isMatch = Pattern.matches(key, buffer.toString());
				if(isMatch) {
					break;
				}
			}
			
			if(!isMatch) {
				winningNum = buffer.toString();
				issue = issueServ.getIssueByIssueNum(lottoType, issueNum);
				issue.setRetNum(winningNum);
				issue.setState(Constants.IssueState.LOTTO_DARW.getCode());
				issueServ.saveIssue(issue);
				
				changeBulletinBoard(lottoType, issueNum, issue);
				
				//inform the progress to payout
				cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, lottoType +"|"+ issueNum);
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void nonInterventional(String lottoType, String issueNum) {
		logger.debug(String.format("lottoType   %s,  issueNum  %s", lottoType, issueNum));
		String winningNum;
		Issue issue;
		
		winningNum = Utils.produce5Digits0to9Number();
		issue = issueServ.getIssueByIssueNum(lottoType, issueNum);
		issue.setRetNum(winningNum);
		issue.setState(Constants.IssueState.LOTTO_DARW.getCode());
		issueServ.saveIssue(issue);
		
		changeBulletinBoard(lottoType, issueNum, issue);
		
		//inform the progress to payout
		cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, lottoType +"|"+ issueNum);
	}

	private void manualDrawResult(String lottoType, String issueNum, String drawResult) {
		logger.debug(String.format("lottoType   %s,  issueNum  %s", lottoType, issueNum));
		String winningNum = null;
		Issue issue = null;
		
		winningNum = drawResult;
		if(StringUtils.isBlank(winningNum)) {
			return;
		}
		
		if(winningNum.split(",").length != 5) {
			return;
		}
		
		issue = issueServ.getIssueByIssueNum(lottoType, issueNum);
		issue.setRetNum(winningNum.replaceAll(" ", ","));
		issue.setState(Constants.IssueState.LOTTO_DARW.getCode());
		issueServ.saveIssue(issue);
		
		changeBulletinBoard(lottoType, issueNum, issue);
						
		//inform the progress to payout
		cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, lottoType +"|"+ issueNum);
	}
	
	
	public abstract String getCodeTypeName();
}

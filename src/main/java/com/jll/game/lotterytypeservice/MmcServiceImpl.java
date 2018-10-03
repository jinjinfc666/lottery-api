package com.jll.game.lotterytypeservice;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.PrizeMode;
import com.jll.common.utils.MathUtil;
import com.jll.common.utils.Utils;
import com.jll.entity.Issue;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.game.BulletinBoard;
import com.jll.game.IssueService;
import com.jll.game.order.OrderService;
import com.jll.game.playtype.PlayTypeFacade;
import com.jll.game.playtype.PlayTypeService;
import com.jll.game.playtypefacade.PlayTypeFactory;
import com.jll.spring.extend.SpringContextUtil;
import com.jll.user.UserInfoService;
import com.jll.user.details.UserAccountDetailsService;
import com.jll.user.wallet.WalletService;

/**
 * 秒秒彩
 * @author Administrator
 *
 */
public class MmcServiceImpl extends DefaultLottoTypeServiceImpl
{
	private Logger logger = Logger.getLogger(MmcServiceImpl.class);

	private final String lotteryType = Constants.LottoType.MMC.getCode();
	
	IssueService issueServ = (IssueService)SpringContextUtil.getBean("issueServiceImpl");
	
	CacheRedisService cacheServ = (CacheRedisService)SpringContextUtil.getBean("cacheRedisServiceImpl");
	
	WalletService walletServ = (WalletService)SpringContextUtil.getBean("walletServiceImpl");
	
	OrderService orderInfoServ = (OrderService)SpringContextUtil.getBean("orderServiceImpl");
	
	PlayTypeService playTypeServ = (PlayTypeService)SpringContextUtil.getBean("playTypeServiceImpl");
	
	UserAccountDetailsService accDetailsServ = (UserAccountDetailsService)SpringContextUtil.getBean("userAccountDetailsServiceImpl");
	
	UserInfoService userServ = (UserInfoService)SpringContextUtil.getBean("userInfoServiceImpl");
	
	String codeTypeName = Constants.SysCodeTypes.LOTTERY_CONFIG_MMC.getCode();
	
	@Override
	public synchronized List<Issue> makeAPlan() {
		//00:00-23:59  1秒钟一期
		Date currTime = new Date();
		List<Issue> issues = new ArrayList<>();
		//int maxAmount = 86399;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currTime);
		calendar.set(Calendar.MILLISECOND, 0);
		Integer issueCount = cacheServ.getMMCIssueCount(currTime);
		if(issueCount == null) {
			issueCount = new Integer(0);
		}
		Issue issue = new Issue();
		issue.setStartTime(calendar.getTime());
		calendar.add(Calendar.SECOND, 1);
		issue.setEndTime(calendar.getTime());
		issue.setIssueNum(generateLottoNumber(issueCount + 1));
		issue.setLotteryType(lotteryType);
		issue.setState(Constants.IssueState.BETTING.getCode());
		
		issues.add(issue);
		cacheServ.setMMCIssueCount(currTime, issueCount + 1);
		issueServ.savePlan(issues);
		
		//String cacheKey = Constants.KEY_PRE_PLAN + lotteryType;
		cacheServ.setPlan(lotteryType, issues);
		return issues;
	}

	@Override
	public String getLotteryType() {
		return lotteryType;
	}
	
	
	private String generateLottoNumber(int seq) {
		StringBuffer buffer = new StringBuffer();
		Date curr = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
		DecimalFormat numFormat = new DecimalFormat("000");

		buffer.append(format.format(curr)).append("-").append(numFormat.format(seq));
		
		String ret = buffer.toString();
		//logger.debug("lotto number :::::" + ret);
		return ret;
		
	}

	@Override
	public void queryWinningNum(String message) {
		logger.debug(String.format("Trying to handle %s", message));
		String[] lottoTypeAndIssueNum = null;
		String lottoType = null;
		String issueNum = null;		
		String codeNamePrizeMode = Constants.LotteryAttributes.PRIZE_MODE.getCode();
		String codeNameWinningRate = Constants.LotteryAttributes.WINING_RATE.getCode();
		String codeNameUplimitProfitLoss = Constants.LotteryAttributes.UP_LIMIT_PROFIT_LOSS.getCode();
		SysCode sysCodePrizeMode = cacheServ.getSysCode(codeTypeName, codeNamePrizeMode);
		SysCode sysCodeWinningRate = cacheServ.getSysCode(codeTypeName, codeNameWinningRate);
		SysCode sysCodeUplimitProfitLoss = cacheServ.getSysCode(codeTypeName, codeNameUplimitProfitLoss);
		
		lottoTypeAndIssueNum = ((String)message).split("\\|");
		lottoType = lottoTypeAndIssueNum[0];
		issueNum = lottoTypeAndIssueNum[1];
		
		PrizeMode prizeMode;
		
		if(sysCodePrizeMode == null
				|| StringUtils.isBlank(sysCodePrizeMode.getCodeVal())) {
			return;
		}
		
		prizeMode = PrizeMode.getByCode(sysCodePrizeMode.getCodeVal());
		switch(prizeMode) {
			case MANUAL :{
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
		issue = issueServ.getIssueByIssueNum(lottoType, issueNum);
		
		Map<String, Object> statInfo = cacheServ
				.getStatGroupByBettingNum(issue.getLotteryType(), 
				issue.getId());
		Map<String, Object> rateDistribute = new HashMap<>();
		Double maxRate = null;
		String playTypeName = null;
		PlayTypeFacade playTypeFacade = null;
		BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lottoType);
		
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
				
		String maxBetAmount = (String)rateDistribute.get(String.valueOf(maxRate));
		if(maxBetAmount.contains(";")) {
			maxBetAmount = maxBetAmount.substring(0, 
					maxBetAmount.indexOf(";"));
		}
		
		String playTypeId = maxBetAmount.split("|")[0];
		String betNum = maxBetAmount.split("|")[1];
		PlayType playType = playTypeServ.queryById(Integer.parseInt(playTypeId));
						
		if(playType.getPtName().equals("fs") || playType.getPtName().equals("ds")) {
			playTypeName = playType.getClassification() + "/fs-ds";
		}else {
			playTypeName = playType.getClassification() + "/" + playType.getPtName();
		}
		playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(playTypeName);
		
		winningNum = playTypeFacade.produceLostNumber(betNum);
		
		issue.setRetNum(winningNum);
		issue.setState(Constants.IssueState.LOTTO_DARW.getCode());
		issueServ.saveIssue(issue);
		
		if(bulletinBoard != null) {
			if(bulletinBoard.getLastIssue() != null) {
				Issue lastIssue = bulletinBoard.getLastIssue();
				if(lastIssue.getIssueNum().equals(issueNum)) {
					lastIssue.setRetNum(issue.getRetNum());
					cacheServ.setBulletinBoard(lottoType, bulletinBoard);												
				}
			}
		}
		
		//inform the progress to payout
		logger.debug(String.format("sending message to handle pay out %s", lottoType +"|"+ issueNum));
		cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, lottoType +"|"+ issueNum);
	}

	private void nonInterventional(String lottoType, String issueNum) {
		String winningNum;
		Issue issue;
		BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lottoType);
		
		winningNum = Utils.produce5Digits0to9Number();
		issue = issueServ.getIssueByIssueNum(lottoType, issueNum);
		issue.setRetNum(winningNum);
		issue.setState(Constants.IssueState.LOTTO_DARW.getCode());
		issueServ.saveIssue(issue);
		
		if(bulletinBoard != null) {
			if(bulletinBoard.getLastIssue() != null) {
				Issue lastIssue = bulletinBoard.getLastIssue();
				if(lastIssue.getIssueNum().equals(issueNum)) {
					lastIssue.setRetNum(issue.getRetNum());
					cacheServ.setBulletinBoard(lottoType, bulletinBoard);
				}
			}
		}
		
		//inform the progress to payout
		logger.debug(String.format("sending message to handle pay out %s", lottoType +"|"+ issueNum));
		cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, lottoType +"|"+ issueNum);
	}
}

package com.jll.game.lotterytypeservice;

import java.math.BigDecimal;
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

/**
 * 双分彩
 * @author Administrator
 *
 */
public class FfcServiceImpl extends DefaultLottoTypeServiceImpl
{
	private Logger logger = Logger.getLogger(FfcServiceImpl.class);

	private final String lotteryType = "ffc";
	
	IssueService issueServ = (IssueService)SpringContextUtil.getBean("issueServiceImpl");
	
	CacheRedisService cacheServ = (CacheRedisService)SpringContextUtil.getBean("cacheRedisServiceImpl");
	
	WalletService walletServ = (WalletService)SpringContextUtil.getBean("walletServiceImpl");
	
	OrderService orderInfoServ = (OrderService)SpringContextUtil.getBean("orderServiceImpl");
	
	PlayTypeService playTypeServ = (PlayTypeService)SpringContextUtil.getBean("playTypeServiceImpl");
	
	UserAccountDetailsService accDetailsServ = (UserAccountDetailsService)SpringContextUtil.getBean("userAccountDetailsServiceImpl");
	
	UserInfoService userServ = (UserInfoService)SpringContextUtil.getBean("userInfoServiceImpl");
	
	@Override
	public List<Issue> makeAPlan() {
		//00:00-23:59  1分钟一期
		List<Issue> issues = new ArrayList<>();
		int maxAmount = 1440;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		for(int i = 0; i < maxAmount; i++) {
			Issue issue = new Issue();
			issue.setStartTime(calendar.getTime());
			calendar.add(Calendar.MINUTE, 1);
			issue.setEndTime(calendar.getTime());
			issue.setIssueNum(generateLottoNumber(i + 1));
			issue.setLotteryType(lotteryType);
			issue.setState(Constants.IssueState.INIT.getCode());
			
			issues.add(issue);
		}
		
		issueServ.savePlan(issues);
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
		logger.debug("lotto number :::::" + ret);
		return ret;
		
	}

	@Override
	public void queryWinningNum(String message) {
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
			nonInterventional(lottoType, issueNum);
			return;
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
		
		winningNum = playTypeFacade.produceWinningNumber(betNum);
		
		issue.setRetNum(winningNum);
		issueServ.saveIssue(issue);
		
		//inform the progress to payout
		cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, issueNum);
	}

	private void nonInterventional(String lottoType, String issueNum) {
		String winningNum;
		Issue issue;
		BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lottoType);
		
		winningNum = Utils.produce5Digits0to9Number();
		issue = issueServ.getIssueByIssueNum(lottoType, issueNum);
		issue.setRetNum(winningNum);
		issueServ.saveIssue(issue);
		
		//inform the progress to payout
		cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, issueNum);
	}

	
	/*@Override
	public void payout(String issueNum) {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		UserInfo user = null;
		
		Issue issue = issueServ.getIssueByIssueNum(issueNum);
		List<OrderInfo> orders = null;
		boolean isMatch = false;
				
		if(issue == null) {
			return ;
		}
		
		if(issue.getState() != Constants.IssueState.END_ISSUE.getCode()) {
			return ;
		}
		
		if(StringUtils.isBlank(issue.getRetNum())) {
			return ;
		}
		
		orders = orderInfoServ.queryOrdersByIssue(issue.getId());
		
		if(orders == null || orders.size() == 0) {
			modifyIssueState(issue);
			return ;
		}
		
		for(OrderInfo order : orders) {
			user = userServ.getUserById(order.getUserId());
			//被取消的订单 或者延迟开奖的订单 跳过开奖
			if(order.getState() == Constants.OrderState.SYS_CANCEL.getCode()
					|| order.getState() == Constants.OrderState.USER_CANCEL.getCode()
					|| (order.getDelayPayoutFlag() != null 
							&& order.getDelayPayoutFlag() == OrderDelayState.DEPLAY.getCode())) {
				continue;
			}
			
			isMatch = isMatchWinningNum(issue, order);
			
			if(isMatch) {//赢
				//TODO 发奖金
				BigDecimal prize = calPrize(issue, order, user);
				//TODO 增加账户流水
				addUserAccountDetails(order, issue, prize);
				//TODO 修改用户余额
				modifyBal(order, user, prize);
				
				//TODO 修改订单状态
				modifyOrderState(order, Constants.OrderState.WINNING);
				
				setProfitLoss(issue, prize);
			}else {
				//TODO 修改订单状态
				modifyOrderState(order, Constants.OrderState.LOSTING);
			}
			
			//TODO
			rebate(issue, user, order);
			
		}
		
		modifyIssueState(issue);
	}

	private void setProfitLoss(Issue issue, BigDecimal prize) {
		Float profitLoss = queryPlatProfitLoss(issue.getIssueNum());
		if(profitLoss == null) {
			profitLoss = 0F;
		}
		
		profitLoss = MathUtil.subtract(profitLoss, 
				prize.floatValue(), 
				Float.class);
		Map<String, Object> items = new HashMap<>();
		items.put(Constants.KEY_LOTTO_TYPE_PROFIT_LOSS, profitLoss);
		cacheServ.setPlatStat(issue.getLotteryType(), items);
	}

	private void modifyIssueState(Issue issue) {
		issue.setState(Constants.IssueState.PAYOUT.getCode());
		issueServ.saveIssue(issue);
	}

	private void rebate(Issue issue, UserInfo user, OrderInfo order) {
		String superior = user.getSuperior();
		BigDecimal prize = null;
		
		if(StringUtils.isBlank(superior) || "0".equals(superior)) {
			return ;
		}
		
		String[] superiors = superior.split(",");
		
		superior = superiors[0];
		UserInfo superiorUser = userServ.getUserById(Integer.parseInt(superior));
		prize = calRebate(user, order);
		addUserAccountDetails(order, issue, prize);
		
		modifyBal(order, user, prize);
		
		setProfitLoss(issue, prize);
		
		rebate(issue, superiorUser, order);
		
	}

	private BigDecimal calRebate(UserInfo user, OrderInfo order) {
		BigDecimal prize = null;
		BigDecimal prizeRate = user.getRebate();
		BigDecimal betAmount = new BigDecimal(order.getBetAmount());
		
		prize = betAmount.multiply(prizeRate);
		return prize;
	}

	private void modifyOrderState(OrderInfo order, OrderState orderState) {
		order.setState(orderState.getCode());
		
		orderInfoServ.saveOrder(order);
	}

	private void modifyBal(OrderInfo order, UserInfo user, BigDecimal prize) {
		BigDecimal bal = null;
		UserAccount wallet = walletServ.queryById(order.getWalletId());
		bal = wallet.getBalance().add(prize);
		wallet.setBalance(bal);
		
		walletServ.updateWallet(wallet);
	}

	private void addUserAccountDetails(OrderInfo order, Issue issue, BigDecimal prize) {
		UserAccount wallet = walletServ.queryById(order.getWalletId());
		UserAccountDetails accDetails = new UserAccountDetails();
		BigDecimal preAmount = null;
		BigDecimal postAmount = null;
		
		preAmount = wallet.getBalance();
		postAmount = preAmount.add(prize);
		accDetails.setAmount(prize.floatValue());
		accDetails.setCreateTime(new Date());
		accDetails.setDataItemType(Constants.DataItemType.BALANCE.getCode());
		accDetails.setOperationType(Constants.AccOperationType.PAYOUT.getDesc());
		accDetails.setOrderId(order.getId());
		accDetails.setPostAmount(postAmount.floatValue());
		accDetails.setPreAmount(preAmount.floatValue());
		accDetails.setUserId(order.getUserId());
		accDetails.setWalletId(order.getWalletId());
		accDetailsServ.saveAccDetails(accDetails);
	}

	private BigDecimal calPrize(Issue issue, OrderInfo order, UserInfo user) {
		PlayType playType = null;
		String facadeName = null;
		Integer playTypeId = order.getPlayType();
		playType = playTypeServ.queryById(playTypeId);
		if(playType == null) {
			return null;
		}
		
		//playType = playTypes.get(0);
		facadeName = playType.getClassification() +"/" + playType.getPtName();
		PlayTypeFacade playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(facadeName);
		
		if(playTypeFacade == null) {
			return null;
		}
		
		return playTypeFacade.calPrize(issue, order, user);
	}

	private boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		PlayType playType = null;
		String facadeName = null;
		Integer playTypeId = order.getPlayType();
		playType = playTypeServ.queryById(playTypeId);
		if(playType == null) {
			return false;
		}
		
		
		facadeName = playType.getClassification() +"/" + playType.getPtName();
		PlayTypeFacade playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(facadeName);
		
		if(playTypeFacade == null) {
			return false;
		}
		
		return playTypeFacade.isMatchWinningNum(issue, order);
	}
	
	*/
	
}

package com.jll.game.lotterytypeservice;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.OrderDelayState;
import com.jll.common.constants.Constants.OrderState;
import com.jll.common.constants.Constants.UserType;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
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

public abstract class DefaultLottoTypeServiceImpl implements LotteryTypeService
{
	private Logger logger = Logger.getLogger(DefaultLottoTypeServiceImpl.class);

	//private final String lotteryType = "cqssc";
	
	IssueService issueServ = (IssueService)SpringContextUtil.getBean("issueServiceImpl");
	
	CacheRedisService cacheServ = (CacheRedisService)SpringContextUtil.getBean("cacheRedisServiceImpl");
	
	WalletService walletServ = (WalletService)SpringContextUtil.getBean("walletServiceImpl");
	
	OrderService orderInfoServ = (OrderService)SpringContextUtil.getBean("orderServiceImpl");
	
	PlayTypeService playTypeServ = (PlayTypeService)SpringContextUtil.getBean("playTypeServiceImpl");
	
	UserAccountDetailsService accDetailsServ = (UserAccountDetailsService)SpringContextUtil.getBean("userAccountDetailsServiceImpl");
	
	UserInfoService userServ = (UserInfoService)SpringContextUtil.getBean("userInfoServiceImpl");
	
	@Override
	public abstract List<Issue> makeAPlan();

	@Override
	public abstract String getLotteryType();
	
	@Override
	public abstract void queryWinningNum(String issueNum);

	@Override
	public void payout(String message) {
		logger.debug(String.format("Trying to handle %s", message));
		String[] lottoTypeAndIssueNum = null;
		String lottoType = null;
		String issueNum = null;
		
		//UserInfo user = null;
		
		lottoTypeAndIssueNum = ((String)message).split("\\|");
		lottoType = lottoTypeAndIssueNum[0];
		issueNum = lottoTypeAndIssueNum[1];
		
		Issue issue = issueServ.getIssueByIssueNum(lottoType, issueNum);
		//boolean isMatch = false;
				
		if(issue == null) {
			return ;
		}
		
		if(issue.getState().intValue() != Constants.IssueState.LOTTO_DARW.getCode()
				&& issue.getState().intValue() != Constants.IssueState.RE_PAYOUT.getCode()) {
			return ;
		}
		
		if(StringUtils.isBlank(issue.getRetNum())) {
			return ;
		}
		
		issueServ.payOutIssue(issue);
	}

	

	/*private void rebate(Issue issue, UserInfo user, OrderInfo order) {
		String superior = user.getSuperior();
		BigDecimal prize = null;
		
		if(StringUtils.isBlank(superior) || "0".equals(superior)) {
			return ;
		}
		
		String[] superiors = superior.split(",");
		
		superior = superiors[0];
		UserInfo superiorUser = userServ.getUserById(Integer.parseInt(superior));
		prize = calRebate(user, order);
		
		if(prize == null 
				|| prize.compareTo(new BigDecimal(0)) == 0) {
			return ;
		}
		
		addUserAccountDetails(order, superiorUser, issue, prize, 
				Constants.AccOperationType.REBATE);
		
		modifyBal(order, superiorUser, prize);
		
		rebate(issue, superiorUser, order);
	}

	private BigDecimal calRebate(UserInfo user, OrderInfo order) {
		BigDecimal rebate = null;
		BigDecimal rebateRate = user.getRebate();
		rebateRate = rebateRate.multiply(new BigDecimal(0.01F));
		BigDecimal betAmount = new BigDecimal(order.getBetAmount());
		
		rebate = betAmount.multiply(rebateRate);
		return rebate;
	}*/

	/*private void modifyOrderState(OrderInfo order, OrderState orderState) {
		order.setState(orderState.getCode());
		
		orderInfoServ.saveOrder(order);
	}*/

	/*private void modifyBal(OrderInfo order, UserInfo user, BigDecimal prize) {
		BigDecimal bal = null;
		Integer walletType = null;
		UserAccount wallet = walletServ.queryById(order.getWalletId());
		walletType = wallet.getAccType();
		wallet = walletServ.queryUserAccount(user.getId(), walletType);
		bal = new BigDecimal(wallet.getBalance()).add(prize);
		wallet.setBalance(bal.floatValue());
		
		walletServ.updateWallet(wallet);
	}

	private void addUserAccountDetails(OrderInfo order, UserInfo user, 
			Issue issue, BigDecimal prize, 
			Constants.AccOperationType opeType) {
		UserAccount wallet = walletServ.queryById(order.getWalletId());
		wallet = walletServ.queryUserAccount(user.getId(), wallet.getAccType());
		UserAccountDetails accDetails = new UserAccountDetails();
		BigDecimal preAmount = null;
		BigDecimal postAmount = null;
		preAmount = new BigDecimal(wallet.getBalance());
		logger.debug(String.format("prize %s,  preAmount   %s", 
				(prize == null?"":prize.floatValue()), 
				(preAmount == null?"":preAmount.floatValue())));
		postAmount = preAmount.add(prize);
		accDetails.setAmount(prize.floatValue());
		accDetails.setCreateTime(new Date());
		accDetails.setDataItemType(Constants.DataItemType.BALANCE.getCode());
		accDetails.setOperationType(opeType.getCode());
		accDetails.setOrderId(order.getId());
		accDetails.setPostAmount(postAmount.floatValue());
		accDetails.setPreAmount(preAmount.floatValue());
		accDetails.setUserId(user.getId());
		accDetails.setWalletId(wallet.getId());
		accDetailsServ.saveAccDetails(accDetails);
	}*/

	/*private BigDecimal calPrize(Issue issue, OrderInfo order, UserInfo user) {
		String playTypeName = null;
		PlayTypeFacade playTypeFacade = null;
		PlayType playType = null;
		Integer playTypeId = order.getPlayType();
		playType = playTypeServ.queryById(playTypeId);
		if(playType == null) {
			return null;
		}
		
		if(playType.getPtName().equals("fs") || playType.getPtName().equals("ds")) {
			playTypeName = playType.getClassification() + "/fs-ds";
		}else {
			playTypeName = playType.getClassification() + "/" + playType.getPtName();			
		}
		playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(playTypeName);
		
		if(playTypeFacade == null) {
			return null;
		}
		
		return playTypeFacade.calPrize(issue, order, user);
	}*/

	/*private boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		PlayType playType = null;
		String playTypeName = null;
		PlayTypeFacade playTypeFacade = null;
		
		Integer playTypeId = order.getPlayType();
		playType = playTypeServ.queryById(playTypeId);
		if(playType == null) {
			return false;
		}
				
		if(playType.getPtName().equals("fs") || playType.getPtName().equals("ds")) {
			playTypeName = playType.getClassification() + "/fs-ds";
		}else {
			playTypeName = playType.getClassification() + "/" + playType.getPtName();
		}
		playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(playTypeName);
		
		if(playTypeFacade == null) {
			return false;
		}
		
		return playTypeFacade.isMatchWinningNum(issue, order);
	}*/
	
	@Override
	public void payout(OrderInfo order, Issue issue, boolean isAuto) {
		
		issueServ.payoutOrder(order, issue, isAuto);
		/*if(issue == null){
			issue = issueServ.getIssueById(order.getIssueId());
		}
		 
		UserInfo user = userServ.getUserById(order.getUserId());
		
		//被取消的订单 或者延迟开奖的订单,或者已经开奖 跳过开奖
		if(order.getState() == Constants.OrderState.SYS_CANCEL.getCode()
				||	order.getState() == Constants.OrderState.WINNING.getCode()
				|| order.getState() == Constants.OrderState.LOSTING.getCode()
				|| order.getState() == Constants.OrderState.USER_CANCEL.getCode()
				|| (order.getDelayPayoutFlag() != null 
						&& order.getDelayPayoutFlag() == OrderDelayState.DEPLAY.getCode()
						&& isAuto)) {
			return;
		}
		
		boolean isMatch = isMatchWinningNum(issue, order);
		
		if(isMatch) {//赢
			//TODO 发奖金
			BigDecimal prize = calPrize(issue, order, user);
			
			//试玩用户跳过派奖
			if(UserType.DEMO_PLAYER.getCode() != user.getUserType()){
				//TODO 增加账户流水
				addUserAccountDetails(order, user, issue, prize, Constants.AccOperationType.PAYOUT);
				//TODO 修改用户余额
				modifyBal(order, user, prize);
			}
			
			//TODO 修改订单状态
			modifyOrderState(order, Constants.OrderState.WINNING);
		}else {
			//TODO 修改订单状态
			modifyOrderState(order, Constants.OrderState.LOSTING);
		}
		
		//试玩用户跳过返点
		if(UserType.DEMO_PLAYER.getCode() != user.getUserType().intValue()
				&& order.getState().intValue() != Constants.OrderState.RE_PAYOUT.getCode()){
			rebate(issue, user, order);
		}*/
		
	}
	
	
	protected void changeBulletinBoard(String lottoType, String issueNum, Issue issue) {
		BulletinBoard bulletinBoard;
		//防止多线程情况下，获取旧的信息
		bulletinBoard = cacheServ.getBulletinBoard(lottoType);
		if(bulletinBoard.getCurrIssue() == null) {
			try {
				Thread.sleep(30000);
				
				bulletinBoard = cacheServ.getBulletinBoard(lottoType);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(bulletinBoard != null) {
			if(bulletinBoard.getLastIssue() != null) {
				Issue lastIssue = bulletinBoard.getLastIssue();
				if(lastIssue.getIssueNum().equals(issueNum)) {
					lastIssue.setRetNum(issue.getRetNum());									
					bulletinBoard.setLastIssue(lastIssue);
					cacheServ.setBulletinBoard(lottoType, bulletinBoard);												
				}
			}
		}
	}
}

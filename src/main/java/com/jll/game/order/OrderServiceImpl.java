package com.jll.game.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.threadpool.ThreadPoolManager;
import com.jll.common.utils.MathUtil;
import com.jll.common.utils.Utils;
import com.jll.common.utils.sequence.GenSequenceService;
import com.jll.entity.GenSequence;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.game.IssueService;
import com.jll.game.LotteryTypeFactory;
import com.jll.game.LotteryTypeService;
import com.jll.game.playtype.PlayTypeFacade;
import com.jll.game.playtype.PlayTypeService;
import com.jll.game.playtypefacade.PlayTypeFactory;
import com.jll.user.UserInfoService;
import com.jll.user.details.UserAccountDetailsService;
import com.jll.user.wallet.WalletService;

@Configuration
@PropertySource("classpath:sys-setting.properties")
@Service
@Transactional
public class OrderServiceImpl implements OrderService
{
	private Logger logger = Logger.getLogger(OrderServiceImpl.class);

	@Resource
	OrderDao orderDao;

	@Resource
	UserInfoService userServ;
	
	@Resource
	WalletService walletServ;
	
	@Resource
	UserAccountDetailsService accDetailsServ;
	
	@Resource
	CacheRedisService cacheServ;
	
	@Resource
	GenSequenceService genSeqServ;
	
	@Resource
	PlayTypeService playTypeServ;	
	
	@Resource
	IssueService issueServ;
	
	@Override
	public String saveOrders(List<OrderInfo> orders, int walletId, int zhFlag, String lotteryType) {
		boolean isIssueValid = false;
		String isBalValid = "";
		boolean isWalletValid = false;
		boolean isBetting = false;
		Date currTime = new Date();
		String opeType = Constants.AccOperationType.BETTING.getCode();
		String sysCodeType = Constants.SysCodeTypes.FLOW_TYPES.getCode();
		Map<String, SysCode> accOpetioans = cacheServ.getSysCode(sysCodeType);
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		UserInfo user = userServ.getUserByUserName(userName);
		UserAccount wallet = walletServ.queryById(walletId);
		String seqVal = null;
		int bettingBlockTimes = 1000;
		int bettingBlockCounter = 0;
		
		if(Constants.LottoType.MMC.getCode().equals(lotteryType)) {
			processMMCIssue(orders);
		}
		
		isWalletValid = isWalletValid(walletId);
		if(!isWalletValid) {
			return String.valueOf(Message.Error.ERROR_USER__WALLET_INVALID.getCode());
		}
		
		
		if(!Constants.LottoType.MMC.getCode().equals(lotteryType)) {
			isIssueValid = verifyIssue(orders, lotteryType);
			if(!isIssueValid) {
				return String.valueOf(Message.Error.ERROR_GAME_EXPIRED_ISSUE.getCode());
			}
		}
		
		isBalValid = verifyBal(orders, wallet, lotteryType, user);
		if(!isBalValid.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
			return isBalValid;
		}
		
		if(accOpetioans == null || accOpetioans.size() == 0) {
			return String.valueOf(Message.Error.ERROR_COMMON_NO_ACCOUNT_OPERATION.getCode());
		}
		
		while(bettingBlockCounter < bettingBlockTimes) {
			bettingBlockCounter++;
			
			isBetting = cacheServ.isUserBetting(user, orders.get(0));
			logger.debug(String.format("user %s is betting:%s", user.getId(), isBetting));
			if(!isBetting) {
				break;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		cacheServ.setUserBettingFlag(user, orders.get(0));
		
		for(OrderInfo order : orders) {
			
			seqVal = Utils.gen16DigitsSeq(getSeq());
			order.setWalletId(walletId);
			order.setOrderNum(seqVal);
			order.setUserId(user.getId());
			order.setCreateTime(currTime);
			order.setState(Constants.OrderState.WAITTING_PAYOUT.getCode());
			orderDao.saveOrders(order);
			
			UserAccountDetails userDetails = new UserAccountDetails();
			userDetails.setUserId(user.getId());
			userDetails.setAmount(order.getBetAmount());
			userDetails.setCreateTime(currTime);
			
			SysCode bettingCode = accOpetioans.get(opeType);
			userDetails.setOperationType(bettingCode.getCodeName());
			userDetails.setOrderId(order.getId());
			float postAmount = MathUtil.subtract(wallet.getBalance().floatValue(), 
					order.getBetAmount(), 
					Float.class);
			
			userDetails.setPostAmount(postAmount);
			userDetails.setPreAmount(wallet.getBalance().floatValue());
			userDetails.setUserId(order.getUserId());
			userDetails.setWalletId(walletId);
			userDetails.setDataItemType(Constants.DataItemType.BALANCE.getCode());
			accDetailsServ.saveAccDetails(userDetails);
			
			wallet.setBalance(new BigDecimal(postAmount));
			
			
			//update the statistic in cache
			cacheServ.statGroupByBettingNum(lotteryType, order, user);
		}
		
		//update balance
		walletServ.updateWallet(wallet);
		
		cacheServ.releaseUserBettingFlag(user, orders.get(0));
		
		if(Constants.LottoType.MMC.getCode().equals(lotteryType)) {
			int issueId = orders.get(0).getIssueId();
			Issue issue = issueServ.getIssueById(issueId);
			issue.setState(Constants.IssueState.END_ISSUE.getCode());
			issueServ.saveIssue(issue);
			
			final String message = lotteryType +"|"+ issue.getIssueNum();
			ThreadPoolManager.getInstance().exeThread(new Runnable() {
				@Override
				public void run() {
					cacheServ.publishMessage(Constants.TOPIC_WINNING_NUMBER, 
							message);
				}
			});
		}
		
		/*try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return String.valueOf(Message.status.SUCCESS.getCode());
	}

	private boolean processMMCIssue(List<OrderInfo> orders) {
		LotteryTypeService lotteryTypeServ = null;
		
		List<Issue> issues = null;
		Issue issue = null;
		
		for(OrderInfo order : orders) {			
			if(lotteryTypeServ == null) {
				lotteryTypeServ = LotteryTypeFactory
						.getInstance()
						.createLotteryType(Constants.MMC_SERVICE_IMPL);
				issues = lotteryTypeServ.makeAPlan();
				if(issues == null || issues.size() == 0) {
					return false;
				}
			}
			
			issue = issues.get(0);
			
			order.setIssueId(issue.getId());
			
		}
		
		return true;
	}

	private boolean isWalletValid(int walletId) {
		UserAccount wallet = walletServ.queryById(walletId);
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		UserInfo user = userServ.getUserByUserName(userName);
		
		if(wallet != null 
				&& wallet.getUserId() == user.getId() 
				&& wallet.getState().intValue() == Constants.WalletState.NORMAL.getCode()) {
			return true;
		}
		
		return false;
	}

	private String verifyBal(List<OrderInfo> orders, UserAccount wallet, String lotteryType, UserInfo user) {
		BigDecimal bal = wallet.getBalance();
		BigDecimal totalAmount = new BigDecimal(0);
		PlayTypeFacade playTypeFacade = null;
		Integer playTypeId = null;
		PlayType playType = null;
		String playTypeName = null;
		Map<String, Object> params = null;
		Map<String, Object> betInfo = null;
		boolean isBetNumValid = false;
		
		for(OrderInfo order : orders) {
			
			if(playTypeFacade == null) {
				playTypeId = order.getPlayType();
				if(playTypeId == null) {
					return String.valueOf(Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
				}
				playType = playTypeServ.queryById(playTypeId);
				if(playType == null) {
					return String.valueOf(Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
				}
				
				if(playType.getPtName().equals("fs") || playType.getPtName().equals("ds")) {
					playTypeName = playType.getClassification() + "/fs-ds";
				}else {
					playTypeName = playType.getClassification() + "/" + playType.getPtName();			
				}
				playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(playTypeName);
			}
			
			isBetNumValid = playTypeFacade.validBetNum(order);
			if(!isBetNumValid) {
				return String.valueOf(Message.Error.ERROR_GAME_INVALID_BET_NUM.getCode());
			}
			
			params = new HashMap<>();
			params.put("betNum", order.getBetNum());
			params.put("times", order.getTimes());
			params.put("monUnit", order.getPattern().floatValue());
			params.put("lottoType", lotteryType);
			
			betInfo = playTypeFacade.preProcessNumber(params, user);
			order.setBetAmount((Float)betInfo.get("betAmount"));
			order.setBetTotal((Integer)betInfo.get("betTotal"));
			order.setPrizeRate((BigDecimal)betInfo.get("singleBettingPrize"));
			
			BigDecimal betAmount = new BigDecimal(order.getBetAmount());
			totalAmount = totalAmount.add(betAmount);
		}
		
		return bal.compareTo(totalAmount) == 1 ?String.valueOf(Message.status.SUCCESS.getCode()):String.valueOf(Message.Error.ERROR_GAME_BAL_INSUFFICIENT.getCode());
	}

	private boolean verifyIssue(List<OrderInfo> orders, String lotteryType) {
		boolean isIssueValid = false;
		Integer issueId = null;
		
		for(OrderInfo order : orders) {
			
			if(order.getIssueId() == null) {
				return false;
			}
			
			if(issueId == null
					|| issueId.intValue() == order.getIssueId()) {
				issueId = order.getIssueId();
			}else if(issueId.intValue() != order.getIssueId()){
				return false;
			}
						
		}
		
		isIssueValid = cacheServ.isIssueBetting(lotteryType, issueId);
				
		return isIssueValid;
	}
	
	private synchronized Long getSeq() {
		GenSequence seq = genSeqServ.querySeqVal();
		if(seq == null) {
			return null;
		}
		
		if(seq.getSeqVal() + 1 > 999999) {
			seq.setSeqVal(1L);
		}else {
			seq.setSeqVal(seq.getSeqVal() + 1);
		}
		
		genSeqServ.saveSeq(seq);
		
		return seq.getSeqVal();
	}

	@Override
	public List<OrderInfo> queryOrdersByIssue(Integer issueId) {
		return orderDao.queryOrdersByIssue(issueId);
	}

	@Override
	public void saveOrder(OrderInfo order) {
		/*List<OrderInfo> orders = new ArrayList<>();
		orders.add(order);*/
		orderDao.saveOrders(order);
	}

	@Override
	public double getUserBetTotalByDate(int walletId, int userId, Date start, Date end) {
		return orderDao.getUserBetTotalByDate(walletId, userId, start, end);
	}

	@Override
	public Map<String, Object> getOrderInfo(String orderNum) {
		Map<String, Object> ret = new HashMap<>();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data",orderDao.getOrderInfo(orderNum));
		return ret;
	}
}

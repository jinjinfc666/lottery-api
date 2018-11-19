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
import com.jll.common.constants.Constants.OrderDelayState;
import com.jll.common.constants.Message;
import com.jll.common.threadpool.QueueManager;
import com.jll.common.utils.MathUtil;
import com.jll.common.utils.Utils;
import com.jll.common.utils.sequence.GenSequenceService;
import com.jll.dao.PageBean;
import com.jll.entity.GenSequence;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.game.BulletinBoard;
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
		String isIssueValid = "";
		String isBalValid = "";
		boolean isWalletValid = false;
		Date currTime = null;
		String opeType = Constants.AccOperationType.BETTING.getCode();
		String sysCodeType = Constants.SysCodeTypes.FLOW_TYPES.getCode();
		Map<String, SysCode> accOpetioans = cacheServ.getSysCode(sysCodeType);
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		UserInfo user = userServ.getUserByUserName(userName);
		UserAccount wallet = null;
		String seqVal = null;

		wallet = walletServ.queryById(walletId);
		if (!Constants.LottoType.MMC.getCode().equals(lotteryType)) {
			isIssueValid = verifyIssue(orders, lotteryType);
			if (!isIssueValid.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
				return String.valueOf(isIssueValid);
			}
		}
		currTime = new Date();

		if (Constants.LottoType.MMC.getCode().equals(lotteryType)) {
			processMMCIssue(orders);
		}

		isWalletValid = isWalletValid(walletId);
		if (!isWalletValid) {
			return String.valueOf(Message.Error.ERROR_USER__WALLET_INVALID.getCode());
		}

		if (!Constants.LottoType.MMC.getCode().equals(lotteryType)) {
			isIssueValid = verifyIssue(orders, lotteryType);
			if (!isIssueValid.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
				return String.valueOf(isIssueValid);
			}
		}

		isBalValid = verifyBal(orders, wallet, lotteryType, user);
		if (!isBalValid.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
			return isBalValid;
		}

		if (accOpetioans == null || accOpetioans.size() == 0) {
			return String.valueOf(Message.Error.ERROR_COMMON_NO_ACCOUNT_OPERATION.getCode());
		}


		for (OrderInfo order : orders) {

			seqVal = Utils.gen16DigitsSeq(getSeq());
			order.setWalletId(walletId);
			order.setOrderNum(seqVal);
			order.setUserId(user.getId());
			order.setCreateTime(currTime);
			order.setState(Constants.OrderState.WAITTING_PAYOUT.getCode());
			order.setDelayPayoutFlag(OrderDelayState.NON_DEPLAY.getCode());
			orderDao.saveOrders(order);

			UserAccountDetails userDetails = new UserAccountDetails();
			userDetails.setUserId(user.getId());
			userDetails.setAmount(order.getBetAmount());
			userDetails.setCreateTime(currTime);

			SysCode bettingCode = accOpetioans.get(opeType);
			userDetails.setOperationType(bettingCode.getCodeName());
			userDetails.setOrderId(order.getId());
			float postAmount = MathUtil.subtract(wallet.getBalance().floatValue(), order.getBetAmount(), Float.class);

			userDetails.setPostAmount(postAmount);
			userDetails.setPreAmount(wallet.getBalance().floatValue());
			userDetails.setUserId(order.getUserId());
			userDetails.setWalletId(walletId);
			userDetails.setDataItemType(Constants.DataItemType.BALANCE.getCode());
			accDetailsServ.saveAccDetails(userDetails);

			wallet.setBalance(postAmount);

			// update the statistic in cache
			QueueManager.getInstance().exeThread(new Runnable() {
				@Override
				public void run() {
					cacheServ.statGroupByBettingNum(lotteryType, order, user);
				}
			});
		}

		// update balance
		walletServ.updateWallet(wallet);

		if (Constants.LottoType.MMC.getCode().equals(lotteryType)) {
			int issueId = orders.get(0).getIssueId();
			Issue issue = issueServ.getIssueById(issueId);
			issue.setState(Constants.IssueState.END_ISSUE.getCode());
			issueServ.saveIssue(issue);
			
			BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lotteryType);
			if(bulletinBoard == null) {
				bulletinBoard = new BulletinBoard();
			}
			
			bulletinBoard.setLastIssue(issue);
			cacheServ.setBulletinBoard(lotteryType, bulletinBoard);
			
			final String message = lotteryType + "|" + issue.getIssueNum();
			cacheServ.publishMessage(Constants.TOPIC_WINNING_NUMBER, message);

		}
		
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
		logger.debug(String.format("user_id   %s  wallet_user_id  %s  wallet_id %s  wallet state %s", 
				user.getId(),
				wallet.getUserId(),
				walletId,				
				wallet.getState()));
		if(wallet != null 
				&& wallet.getUserId() == user.getId() 
				&& wallet.getState().intValue() == Constants.WalletState.NORMAL.getCode()) {
			return true;
		}
		
		return false;
	}

	private String verifyBal(List<OrderInfo> orders, UserAccount wallet, String lotteryType, UserInfo user) {
		BigDecimal bal = new BigDecimal(wallet.getBalance());
		BigDecimal totalAmount = new BigDecimal(0);
		PlayTypeFacade playTypeFacade = null;
		Integer playTypeId = null;
		PlayType playType = null;
		String playTypeName = null;
		Map<String, Object> params = null;
		Map<String, Object> betInfo = null;
		boolean isBetNumValid = false;
		
		for(OrderInfo order : orders) {
			
//			if(playTypeFacade == null) {
				playTypeId = order.getPlayType();
				if(playTypeId == null) {
					return String.valueOf(Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
				}
				playType = playTypeServ.queryById(playTypeId);
				if(playType == null) {
					return String.valueOf(Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
				}
				
				if(playType.getPtName().equals("fs")) {
					playTypeName = playType.getClassification() + "/fs";
				}else if(playType.getPtName().equals("ds")){
					playTypeName = playType.getClassification() + "/ds";
				}else {
					playTypeName = playType.getClassification() + "/" + playType.getPtName();
				}
				playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(playTypeName);
//			}
			
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
	
	private Issue verifyIssueCache(Integer issueId,String lotteryType) {
		List<Issue> list=cacheServ.getPlan(lotteryType);
		Issue verify=null;
		for(Issue issue:list) {
			Integer issueIdCache=issue.getId();
			if(issueIdCache.intValue()==issueId.intValue()) {
				verify=issue;
				break;
			}
		}
		return verify;
	}
	
	private String verifyIssue(List<OrderInfo> orders, String lotteryType) {
		//获取当前期次
		BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lotteryType);
		if(bulletinBoard == null) {
			return Message.Error.ERROR_GAME_EXPIRED_ISSUE.getCode();
		}
		Integer issueId = null;
		Integer isZh=orders.get(0).getIsZh();
		if(isZh.intValue()==Constants.ZhState.NON_ZH.getCode()) {
			for(OrderInfo order : orders) {
				if(order.getIssueId() == null) {
					return Message.Error.ERROR_GAME_EXPIRED_ISSUE.getCode();
				}
				if(issueId == null
						|| issueId.intValue() == order.getIssueId().intValue()) {
					issueId = order.getIssueId();
				}else if(issueId.intValue() != order.getIssueId().intValue()){
					return Message.Error.ERROR_GAME_EXPIRED_ISSUE.getCode();
				}
				//验证期次
				Issue currIssue = bulletinBoard.getCurrIssue();
				if(currIssue == null 
						|| currIssue.getId().intValue() != issueId
						|| currIssue.getState() != Constants.IssueState.BETTING.getCode()) {
					return Message.Error.ERROR_GAME_EXPIRED_ISSUE.getCode();
				}	
				Integer orderIsZh=order.getIsZh();
				if(orderIsZh==null||orderIsZh.intValue()!=Constants.ZhState.NON_ZH.getCode()) {
					return Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode();
				}
			}
		}else if(isZh.intValue()==Constants.ZhState.ZH.getCode()){
			for(OrderInfo order : orders) {
				if(order.getIssueId() == null) {
					return Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode();
				}
//				if(issueId == null
//						|| issueId.intValue() == order.getIssueId().intValue()) {
//					issueId = order.getIssueId();
//				}else if(issueId.intValue() != order.getIssueId().intValue()){
//					return Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode();
//				}
				issueId=order.getIssueId();
				//验证期次
				Issue issue=verifyIssueCache(issueId,lotteryType);
				if(issue==null) {
					return Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode();
				}
				Issue currIssue = bulletinBoard.getCurrIssue();
				if(currIssue == null 
						|| currIssue.getId().intValue() >= issueId.intValue()
						|| issue.getState() != Constants.IssueState.INIT.getCode()) {
					return Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode();
				}	
				Integer orderIsZh=order.getIsZh();
				if(orderIsZh==null||orderIsZh.intValue()!=Constants.ZhState.ZH.getCode()) {
					return Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode();
				}
			}
		}
		return String.valueOf(Message.status.SUCCESS.getCode());
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

	@Override
	public PageBean<OrderInfo> queryOrdersByPage(PageBean<OrderInfo> page) {
		return orderDao.queryOrdersByPage(page);
	}
}

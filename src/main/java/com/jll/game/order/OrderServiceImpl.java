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
import com.jll.game.cqssc.PlayTypeFactory;
import com.jll.game.playtype.PlayTypeFacade;
import com.jll.game.playtype.PlayTypeService;
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
	
	
	@Override
	public String saveOrders(List<OrderInfo> orders, int walletId, int zhFlag, String lotteryType) {
		boolean isIssueValid = false;
		boolean isBalValid = false;
		boolean isWalletValid = false;
		boolean isBetting = false;
		Date currTime = new Date();
		String opeType = Constants.AccOperationType.BETTING.getDesc();
		String sysCodeType = Constants.SysCodeTypes.FLOW_TYPES.getCode();
		Map<String, SysCode> accOpetioans = cacheServ.getSysCode(sysCodeType);
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		UserInfo user = userServ.getUserByUserName(userName);
		UserAccount wallet = walletServ.queryById(walletId);
		String seqVal = null;
		int bettingBlockTimes = 1000;
		int bettingBlockCounter = 0;
		PlayTypeFacade playTypeFacade = null;
		Integer playTypeId = null;
		PlayType playType = null;
		Map<String, Object> betInfo = null;
		Map<String, Object> params = null;
		String playTypeName = null;
		boolean isBetNumValid = false;
		
				
		isWalletValid = isWalletValid(walletId);
		if(!isWalletValid) {
			return String.valueOf(Message.Error.ERROR_USER__WALLET_INVALID.getCode());
		}
		
		isIssueValid = verifyIssue(orders, lotteryType);
		if(!isIssueValid) {
			return String.valueOf(Message.Error.ERROR_GAME_EXPIRED_ISSUE.getCode());
		}
		
		isBalValid = verifyBal(orders, wallet);
		if(!isBalValid) {
			return String.valueOf(Message.Error.ERROR_GAME_BAL_INSUFFICIENT.getCode());
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
			
			if(playTypeFacade == null) {
				playTypeId = order.getPlayType();
				if(playTypeId == null) {
					return String.valueOf(Message.Error.ERROR_GAME_NO_PLAY_TYPE.getCode());
				}
				playType = playTypeServ.queryById(playTypeId);
				if(playType == null) {
					return String.valueOf(Message.Error.ERROR_GAME_INVALID_PLAY_TYPE.getCode());
				}
				
				playTypeName = lotteryType + "/" + playType.getClassification() + "/" + playType.getPtName();
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
			seqVal = Utils.gen16DigitsSeq(getSeq());
			order.setWalletId(walletId);
			order.setOrderNum(seqVal);
			order.setUserId(user.getId());
			order.setCreateTime(currTime);
			order.setState(Constants.OrderState.WAITTING_PAYOUT.getCode());
			order.setBetAmount((Float)betInfo.get("betAmount"));
			order.setBetTotal((Integer)betInfo.get("betTotal"));
			order.setPrizeRate(new BigDecimal((Float)betInfo.get("prizeRate")));
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
			cacheServ.statGroupByBettingNum(lotteryType, order);
		}
		
		//update balance
		walletServ.updateWallet(wallet);
		
		cacheServ.releaseUserBettingFlag(user, orders.get(0));
		
		return String.valueOf(Message.status.SUCCESS.getCode());
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

	private boolean verifyBal(List<OrderInfo> orders, UserAccount wallet) {
		BigDecimal bal = wallet.getBalance();
		BigDecimal totalAmount = new BigDecimal(0);
		for(OrderInfo order : orders) {
			BigDecimal betAmount = new BigDecimal(order.getBetAmount());
			totalAmount = totalAmount.add(betAmount);
		}
		
		return bal.compareTo(totalAmount) == 1 ?true:false;
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
}

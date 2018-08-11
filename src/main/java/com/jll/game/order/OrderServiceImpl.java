package com.jll.game.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
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
		
	@Override
	public String saveOrders(List<OrderInfo> orders, int walletId, int zhFlag, String lotteryType) {
		boolean isIssueValid = false;
		boolean isBalValid = false;
		boolean isWalletValid = false;
		UserAccount wallet = null;				
		Date currTime = new Date();
		
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
		
		for(OrderInfo order : orders) {
			orderDao.saveOrders(order);
			
			UserAccountDetails userDetails = new UserAccountDetails();
			userDetails.setAmount(order.getBetAmount());
			userDetails.setCreateTime(currTime);
			userDetails.setOperationType(cacheServ.getSysCode(codeTypeName));
			userDetails.setOrderId(order.getId());
			userDetails.setPostAmount(postAmount);
			userDetails.setPreAmount(preAmount);
			userDetails.setUserId(order.getUserId());
			userDetails.setWalletId(walletId);
			
			//TODO account
			accDetailsServ.saveAccDetails(userDetails);
			//TODO balance
			walletServ.updateWallet(wallet);
			
			//TODO update the statistic in cache
			cacheServ.statGroupByBettingNum(order);
		}
		
		return String.valueOf(Message.status.SUCCESS.getCode());
	}

	private boolean isWalletValid(int walletId) {
		UserAccount wallet = walletServ.queryById(walletId);
		
		if(wallet.getState().intValue() == Constants.WalletState.NORMAL.getCode()) {
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
	
	
}

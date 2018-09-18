package com.jll.pay.order;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.DepositOrderState;
import com.jll.common.constants.Constants.WalletType;
import com.jll.common.utils.BigDecimalUtil;
import com.jll.dao.SupserDao;
import com.jll.entity.DepositApplication;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;

@Service
@Transactional
public class DepositOrderServiceImpl implements DepositOrderService
{
	@Resource
	DepositOrderDao depositOrderDao;
	
	@Resource
	SupserDao supserDao;

	@Override
	public DepositApplication queryDepositOrderById(String orderId) {
		DepositApplication depositOrder = depositOrderDao.queryDepositOrderById(orderId);
		return depositOrder;
	}

	@Override
	public void receiveDepositOrder(String orderId) {
		DepositApplication depositOrder = depositOrderDao.queryDepositOrderById(orderId);
		
		//主钱包
		UserAccount mainAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId", depositOrder.getUserId(), "accType", WalletType.MAIN_WALLET.getCode()).get(0);
		
		depositOrder.setState(DepositOrderState.END_ORDER.getCode());
		
		//明细
		UserAccountDetails addDtl = new UserAccountDetails();
		addDtl.setUserId(mainAcc.getUserId());
		addDtl.setCreateTime(new Date());
		addDtl.setAmount(depositOrder.getAmount().floatValue());
		addDtl.setPreAmount(mainAcc.getBalance().floatValue());
		addDtl.setPostAmount(Double.valueOf(BigDecimalUtil.add(addDtl.getPreAmount(),addDtl.getAmount())).floatValue());
		addDtl.setWalletId(mainAcc.getId());
		addDtl.setOperationType(Constants.AccOperationType.DEPOSIT.getCode());
		supserDao.save(addDtl);
		mainAcc.setBalance(new BigDecimal(addDtl.getPostAmount()));
		supserDao.update(mainAcc);
		supserDao.update(depositOrder);
	}

	@Override
	public boolean isOrderNotified(String orderId) {
		DepositApplication depositOrder = depositOrderDao.queryDepositOrderById(orderId);
		if(depositOrder.getState() ==  Constants.DepositOrderState.END_ORDER.getCode()) {
			return true;
		}
		return false;
	}
  
}

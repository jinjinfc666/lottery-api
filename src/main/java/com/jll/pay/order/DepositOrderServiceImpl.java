package com.jll.pay.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.DepositOrderState;
import com.jll.common.constants.Constants.WalletType;
import com.jll.common.utils.BigDecimalUtil;
import com.jll.common.utils.StringUtils;
import com.jll.dao.SupserDao;
import com.jll.entity.DepositApplication;
import com.jll.entity.PayType;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;

@Service
@Transactional
public class DepositOrderServiceImpl implements DepositOrderService
{
	@Resource
	DepositOrderDao depositOrderDao;
	
	@Resource
	CacheRedisService cacheServ;
	
	@Resource
	SupserDao supserDao;

	@Override
	public DepositApplication queryDepositOrderById(String orderId) {
		DepositApplication depositOrder = depositOrderDao.queryDepositOrderById(orderId);
		return depositOrder;
	}

	@Override
	public void processReceiveDepositOrder(String orderId,String remark) {
		DepositApplication depositOrder = depositOrderDao.queryDepositOrderById(orderId);
		
		//主钱包
		UserAccount mainAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId", depositOrder.getUserId(), "accType", WalletType.MAIN_WALLET.getCode()).get(0);
		
		depositOrder.setState(DepositOrderState.END_ORDER.getCode());
		if(StringUtils.isNotEmpty(remark)){
			depositOrder.setRemark(depositOrder.getRemark()+StringUtils.COMMA+remark);
		}
		//明细
		UserAccountDetails addDtl = new UserAccountDetails();
		addDtl.setUserId(mainAcc.getUserId());
		addDtl.setCreateTime(new Date());
		
		addDtl.setAmount(depositOrder.getAmount().floatValue());
		addDtl.setPreAmount(mainAcc.getBalance());
		addDtl.setPostAmount(Double.valueOf(BigDecimalUtil.add(addDtl.getPreAmount(),addDtl.getAmount())));
		addDtl.setWalletId(mainAcc.getId());
		addDtl.setOperationType(Constants.AccOperationType.DEPOSIT.getCode());
		addDtl.setOrderId(depositOrder.getId());
		supserDao.save(addDtl);
		mainAcc.setBalance(addDtl.getPostAmount());
		supserDao.update(mainAcc);
		
		depositOrder.setUpdateTime(new Date());
		supserDao.update(depositOrder);
	}

	@Override
	public boolean isOrderNotified(String orderId,com.jll.common.constants.Constants.PayType type) {
		DepositApplication depositOrder = depositOrderDao.queryDepositOrderById(orderId);
		List<PayType> payLists = cacheServ.getPayType(Constants.PayTypeName.PAY_TYPE.getCode());
		PayType curPay = null;
		for (PayType payType : payLists) {
			if(payType.getPlatId().equals(type.getCode())){
				curPay = payType;
			}
		}
		if(null == curPay
				|| null == depositOrder
				|| !curPay.getId().equals(depositOrder.getPayType())){
			return true;
		}
		
		if(depositOrder.getState().equals(Constants.DepositOrderState.END_ORDER.getCode())) {
			return true;
		}
		return false;
	}
  
}

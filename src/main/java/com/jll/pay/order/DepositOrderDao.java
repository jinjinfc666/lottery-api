package com.jll.pay.order;

import java.util.Date;

import com.jll.entity.DepositApplication;

public abstract interface DepositOrderDao
{
	/**
	 * save the deposit order
	 * @param userName
	 * @param rechargeType 
	 * @param amount
	 * @return
	 */
	DepositApplication saveDepositOrder(int payType,int payChannel, int userId, float amount, String comment, Date createTime,String platAccount);
	
	/**
	 * update the deposit order
	 * @param depositOrder
	 */
	void updateDepositOrder(DepositApplication depositOrder);
	
	DepositApplication queryDepositOrderById(String orderId);
}

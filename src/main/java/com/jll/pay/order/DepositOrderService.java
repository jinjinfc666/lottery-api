package com.jll.pay.order;

import com.jll.common.constants.Constants.PayType;
import com.jll.entity.DepositApplication;

public interface DepositOrderService
{
	DepositApplication queryDepositOrderById(String orderId);
	void processReceiveDepositOrder(String orderId,String remark);
	boolean isOrderNotified(String orderId,PayType type);
}

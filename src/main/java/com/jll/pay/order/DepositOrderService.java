package com.jll.pay.order;

import com.jll.entity.DepositApplication;

public interface DepositOrderService
{
	DepositApplication queryDepositOrderById(String orderId);
	void receiveDepositOrder(String orderId);
	boolean isOrderNotified(String orderId);
}

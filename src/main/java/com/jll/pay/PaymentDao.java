package com.jll.pay;

import com.jll.entity.DepositApplication;

public abstract interface PaymentDao
{
	
	
	public abstract long queryDepositTimes(int userId);
  
	/**
	 * query the specified deposit order 
	 * @param orderId the id or deposit order
	 * @return
	 */
	DepositApplication queryDepositOrder(String orderId);
  
}

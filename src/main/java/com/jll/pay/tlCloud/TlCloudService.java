package com.jll.pay.tlCloud;

import java.util.Map;

public interface TlCloudService
{
	 
		
	String cancelOrder(String orderId);


	String saveDepositOrder(Map<String, Object> params);
}

package com.jll.pay.zhihpay;

import java.util.Map;

import com.jll.entity.display.ZhihPayNotices;

public interface ZhihPayService
{
	
	/**
	 * @param params
	 * @return
	 */
	String processScanPay(Map<String, Object> params);
	
	/**
	 * @param params
	 * @return
	 */
	String processOnlineBankPay(Map<String, Object> params);
	
	/**
	 * @param params
	 * @return
	 */
	String receiveNoties(Map<String, Object> params);


	boolean isValid(Map<String, Object> params);
	
	boolean isNoticesValid(ZhihPayNotices notices);	

}

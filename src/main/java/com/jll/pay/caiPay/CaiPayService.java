package com.jll.pay.caiPay;

import java.util.Map;

import com.jll.entity.display.CaiPayNotices;

public interface CaiPayService 
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
	
	boolean isNoticesValid(CaiPayNotices notices);	
	
}

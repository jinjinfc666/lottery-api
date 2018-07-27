package com.jll.user.tp;

public interface SMSService
{
	/**
	 * send a 6 digits number to the phone number 
	 * and store the digits number in memory and the expired time is 120s
	 * @param phoneNum
	 * @return
	 */
	String sending6digitsNumbers(String phoneNum);
}

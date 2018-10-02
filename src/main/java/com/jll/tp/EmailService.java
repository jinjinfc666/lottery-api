package com.jll.tp;

import javax.mail.MessagingException;

import com.jll.entity.UserInfo;

public interface EmailService
{
	/**
	 * send a 6 digits number to the phone number 
	 * and store the digits number in memory and the expired time is 120s
	 * @param phoneNum
	 * @return
	 */
	String sendingEmail(UserInfo user, String host) throws MessagingException;

	boolean isEmailValid(UserInfo user, String sms);
}

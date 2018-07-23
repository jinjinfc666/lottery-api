package com.jll.user;


public interface UserInfoDao
{
	/**
	 * query the receiver bank card
	 * the bank card should be activated
	 * one bank only one bank account existing in the table
	 * @param bankCode
	 * @return
	 */
	int getUserId(String userName);
	long getCountUser(String userName);
}

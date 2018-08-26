package com.jll.user;

import java.math.BigDecimal;
import java.util.List;

import com.jll.dao.PageBean;
import com.jll.entity.UserInfo;

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
	
	UserInfo getUserByUserName(String userName);
	
	UserInfo getUserById(Integer userId);
	
	/**
	 * if the user is existing in database
	 * @param user
	 * @return
	 */
	boolean isUserExisting(UserInfo user);
	
	/**
	 * persist the user entity
	 * @param user
	 */
	void saveUser(UserInfo user);
	
	/**
	 * the general agency is unique in the system
	 * @return
	 */
	UserInfo getGeneralAgency();
	
	String queryUnSystemUsers();
	
	boolean checkUserIds(String UserIds);
	
	List<UserInfo> queryAllUserInfo(Integer id,String userName,Integer proxyId,String startTime,String endTime);
	
	PageBean<UserInfo> queryAllUserInfoByPage(PageBean<UserInfo> reqPage);
}

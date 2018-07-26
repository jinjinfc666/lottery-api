package com.jll.user;

import com.jll.entity.UserInfo;

public interface UserInfoService
{
	/**
	 * @param userName
	 * @return
	 */
	int getUserId(String userName);
	
	/**
	 * if specified user is existing
	 * @param userName
	 * @return
	 */
	boolean isUserInfo(String userName);
	
	/**
	 * query the user by userName
	 * @param name
	 * @return
	 */
	UserInfo getUserByUserName(String userName);

	/**
	 * valid the user information
	 * @param user
	 * @return 
	 *         Message.status.SUCCESS or
	 *         Message.Error
	 */
	String validUserInfo(UserInfo user);

	/**
	 * if the specified user is existing
	 * we consider the user is existing if the one of the followings is same:
	 * userName 
	 * email
	 * phone number
	 * 
	 * @param user
	 * @return
	 */
	boolean isUserExisting(UserInfo user);

	/**
	 * persist the user object
	 * @param user
	 * @return 
	 *		   Message.status.SUCCESS or
	 *         Message.Error
	 */
	void regUser(UserInfo user);

	/**
	 * query the general agency which is unique in the system
	 * @return
	 */
	UserInfo getGeneralAgency();
}

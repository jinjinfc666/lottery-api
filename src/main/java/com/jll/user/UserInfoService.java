package com.jll.user;

import java.util.Date;
import java.util.Map;

import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.UserBankCard;
import com.jll.entity.UserInfo;

public interface UserInfoService
{
	int getUserId(String userName);
	boolean isUserInfo(String userName);
	boolean isUserInfoByUid(int userId);
	Map<String, Object> updateFundPwd(String userName,String oldPwd,String newPwd);
	Map<String, Object> updateLoginPwd(String userName,String oldPwd,String newPwd);
	Map<String, Object> getUserInfoByUserName(String userName);
	Map<String, Object> updateUserInfoInfo(UserInfo userInfo);
	
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
	/**
	 * valid the user information
	 * @param user       the user should be verified
	 * @param superior   the superior of user
	 * @return
	 */
	String validUserInfo(UserInfo user, UserInfo superior);

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
		
	Map<String, Object> getUserBankLists(int userId);
	Map<String, Object> addUserBank(int userId, UserBankCard bank);
	Map<String, Object> getBankCodeList();
	Map<String, Object> verifyUserBankInfo(int userId, UserBankCard bank);
	Map<String, Object> getUserNotifyLists(int userId);
	Map<String, Object> getUserSiteMessageLists(int userId);
	Map<String, Object> showSiteMessageFeedback(int userId, int msgId);
	Map<String, Object> addSiteMessage(int userId, String sendIds, SiteMessage msg);
	Map<String, Object> siteMessageFeedback(int userId, int msgId, SiteMessFeedback back);
	void resetLoginPwd(UserInfo user);
	
	
	double getUserTotalDepostAmt(Date startDate,Date endDate,UserInfo user);
	double getUserTotalBetAmt(Date startDate,Date endDate,UserInfo user);
	Map<String, Object> exchangePoint(int userId, double amount);
}

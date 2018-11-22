package com.jll.user;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jll.dao.PageBean;
import com.jll.dao.PageQueryDao;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserBankCard;
import com.jll.entity.UserInfo;
import com.jll.entity.WithdrawApplication;

public interface UserInfoService
{
	int getUserId(String userName);
	boolean isUserInfo(String userName);
	boolean isUserInfoByUid(int userId); 
	Map<String, Object> updateFundPwd(String oldPwd, String newPwd, String checkPwd);
	Map<String, Object> updateLoginPwd(String oldPwd, String newPwd, String checkPwd);
	Map<String, Object> updateLoginPwdAdmin(String oldPwd, String newPwd, String checkPwd,Integer id);
	Map<String, Object> getUserInfo();
	Map<String, Object> updateUserInfo(UserInfo userInfo);
	
	Map<String, Object> processCancelBetOrder(String orderNum);
	
	void updateUser(UserInfo userInfo);
	 
	/**
	 * query the user by userName
	 * @param name
	 * @return
	 */
	UserInfo getUserByUserName(String userName);
	
	UserInfo getUserById(Integer userId);

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
	void regUser(UserInfo user,HttpServletRequest request);

	/**
	 * query the general agency which is unique in the system
	 * @return
	 */
	UserInfo getGeneralAgency();
		
	Map<String, Object> getUserBankLists(int userId);
	Map<String, Object> addUserBank(int userId, UserBankCard bank);
	Map<String, Object> getBankCodeList();
	Map<String, Object> verifyUserBankInfo(int userId, UserBankCard bank);
	Map<String, Object> getUserNotifyLists();
	Map<String, Object> saveSiteMessage(String sendIds, SiteMessage msg);
	Map<String, Object> updateMessageFeedbackStatus(SiteMessFeedback back);
	//重置登录密码
	void resetLoginPwd(UserInfo user);
	//重置支付密码
	void resetFundPwd(UserInfo user);
	//用户状态修改
	void updateUserType(UserInfo user);
	
	double getUserTotalDepostAmt(Date startDate,Date endDate,UserInfo user);
	double getUserTotalBetAmt(Date startDate,Date endDate,UserInfo user);
	//查询所有的用户
	Map<String,Object> queryAllUserInfo(Map<String,Object> map);
	//查询所有的代理
	Map<String,Object> queryAllAgent(Map<String,Object> map);
	Map<String, Object> processExchangePoint(double amount);
	
	//获取登陆用户信息，如果OK，返回用户信息
	UserInfo getCurLoginInfo();
	
	Map<String, Object> userProfitReport(PageQueryDao page);
	Map<String, Object> saveUpdateUserWithdrawApply(int bankId, double amount, String passoword);
	Map<String, Object> saveUpdateUserWithdrawNotices(WithdrawApplication wtd);
	Map<String, Object> processUserAmountTransfer(String fromUser, String toUser, double amount);
	
	//查询总代下面的所有一级代理
	List<UserInfo> queryAllAgent();
	//点击代理查询下一级代理
	Map<String,Object> queryAgentByAgent(Integer id,String startTime,String endTime,Integer pageIndex);
	Map<String,Object> queryAgentByAgentHou(Integer id,String startTime,String endTime);
	//通过id查看这个用户是否存在
	boolean isOrNoUserInfo(Integer id);
	
	Float calPrizePattern(UserInfo user, String lottoType);
	
	
	Map<String, Object> processUserRedWalletAmountTransfer(int bankId, double amount, String passoword);
	Map<String, Object> saveUpdateDirectOperationUserAmount(UserAccountDetails dtl);
	Map<String, Object> updateUserWalletLockStatus(UserAccount dtl);
	//用户登录后查询用户银行卡信息
	Map<String, Object> queryByUserNameBankList();
	//判断用户是否可以添加银行卡
	Map<String,Object> isOrAddBank();
	//前台用户自己添加银行卡
	Map<String, Object> addUserBank(UserBankCard bank);
	//通过用ID查询用户银行卡   给后台管理页面用的
	Map<String, Object> queryByUserIdBankList(Integer id);
	
	Map<String,Object> getUserNameById(Integer userId);
	
	Map<String, Object> saveRandomDemoUserInfo(HttpServletRequest request);
	
	Map<String, Object> updateDemoUserDisableLogin();
	
	//判断返点
	boolean verifRebate(UserInfo userInfo);
}

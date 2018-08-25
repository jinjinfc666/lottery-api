package com.jll.user.wallet;

import java.util.List;

import com.jll.entity.UserAccount;
import com.jll.entity.UserInfo;

public interface WalletDao
{
	
	/**
	 * create a wallet for user
	 * @param wallet UserAccount
	 */
	void createWallet(UserAccount wallet);

	UserAccount queryByUser(UserInfo user);

	UserAccount queryById(int walletId);
	//通过用户名(false)或时间去查询(true)
	List<?> queryUserAccount(String userName,String startTime,String endTime);
	//修改用户的状态
	void updateState(Integer userId,Integer state);
	//查找userId存不存在在userAccount表
	List<UserAccount> queryByUserId(Integer userId);
}

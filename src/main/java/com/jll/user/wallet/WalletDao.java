package com.jll.user.wallet;

import java.util.List;
import java.util.Map;

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
	Map<String,Object> queryUserAccount(String userName,String startTime,String endTime,Integer pageIndex,Integer pageSize);
	//修改用户的状态
	void updateState(Integer userId,Integer state);
	//查找userId存不存在在userAccount表
	List<UserAccount> queryByUserId(Integer userId);

	void updateWallet(UserAccount wallet);
	//通过id查询该用户是否存在
	List<UserAccount> queryById(Integer id);
	//通过id
	Map<String,Object> queryByIdUserAccount(Integer id);
	//通过用户ID查询当前用户的钱包
	Map<String,Object> queryByUserIdUserAccount(Integer userId);
}

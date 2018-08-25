package com.jll.user.wallet;

import java.util.List;
import java.util.Map;

import com.jll.entity.UserAccount;
import com.jll.entity.UserInfo;

public interface WalletService
{
	/**
	 * create a wallet for the specified user
	 * @param user
	 * @param walletType  wallet type,eg.1, main wallet;2,red packet wallet
	 * @return
	 */
	void createWallet(UserInfo user);

	void updateWallet(UserAccount wallet);

	UserAccount queryById(int walletId);

	/*UserAccount queryByUser(UserInfo user);*/
	//通过用户名(false)或时间去查询(true)
	Map<String,Object> queryUserAccount(Map<String,Object> ret);
	//修改用户的状态
	Map<String,Object> updateState(Integer userId,Integer state);
	//判断该用户在userAccount表存不存在
	boolean isOrNo(Integer userId);
}

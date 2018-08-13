package com.jll.user.wallet;

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
}

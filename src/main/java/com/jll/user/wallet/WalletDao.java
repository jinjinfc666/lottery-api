package com.jll.user.wallet;

import com.jll.entity.UserAccount;
import com.jll.entity.UserInfo;

public interface WalletDao
{
	
	/**
	 * create a wallet for user
	 * @param wallet UserAccount
	 */
	void createWallet(UserAccount wallet);
}

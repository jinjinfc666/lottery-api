package com.jll.user.wallet;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.UserAccount;

@Repository
public class WalletDaoImpl extends DefaultGenericDaoImpl<UserAccount> implements WalletDao
{
	private Logger logger = Logger.getLogger(WalletDaoImpl.class);
	
	@Override
	public void createWallet(UserAccount wallet) {
		this.saveOrUpdate(wallet);
	}
  
  
}

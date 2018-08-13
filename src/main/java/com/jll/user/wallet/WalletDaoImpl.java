package com.jll.user.wallet;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.UserAccount;
import com.jll.entity.UserInfo;

@Repository
public class WalletDaoImpl extends DefaultGenericDaoImpl<UserAccount> implements WalletDao
{
	private Logger logger = Logger.getLogger(WalletDaoImpl.class);
	
	@Override
	public void createWallet(UserAccount wallet) {
		this.saveOrUpdate(wallet);
	}

	@Override
	public UserAccount queryByUser(UserInfo user) {
		String sql = "from UserAccount where userId=?";
		List<UserAccount> ret = null;
		List<Object> params = new ArrayList<>();
		params.add(user.getId());
		
		ret = this.query(sql, params, UserAccount.class);
		
		if(ret == null || ret.size() == 0) {
			return null;
		}
		
		return ret.get(0);
	}

	@Override
	public UserAccount queryById(int walletId) {
		String sql = "from UserAccount where id=?";
		List<Object> params = new ArrayList<>();
		params.add(walletId);
		
		List<UserAccount> userAccounts = this.query(sql, params, UserAccount.class);
		
		if(userAccounts == null || userAccounts.size() == 0) {
			return null;
		}
		return userAccounts.get(0);
	}
  
  
}

package com.jll.user.wallet;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants.WalletType;
import com.jll.entity.UserAccount;
import com.jll.entity.UserInfo;


@Service
@Transactional
public class WalletServiceImpl implements WalletService
{
	private Logger logger = Logger.getLogger(WalletServiceImpl.class);
	
	@Resource
	WalletDao walletDao;

	@Override
	public void createWallet(UserInfo user) {
		if(user == null) {
			logger.error("Invalid user!!!");
			throw new RuntimeException("Invalid user!!!");
		}
		
		createWallet(user, WalletType.MAIN_WALLET);
		createWallet(user, WalletType.RED_PACKET_WALLET);
	}

	private void createWallet(UserInfo user, WalletType wt) {
		
		UserAccount wallet = new UserAccount();
		wallet.setAccName(wt.getDesc());
		wallet.setAccType(wt.getCode());
		wallet.setBalance(new BigDecimal(0.0F));
		wallet.setFreeze(new BigDecimal(0.0F));
		wallet.setPrize(new BigDecimal(0.0F));
		wallet.setRewardPoints(0L);
		wallet.setUserId(user.getId());
		
		walletDao.createWallet(wallet);
	}
}

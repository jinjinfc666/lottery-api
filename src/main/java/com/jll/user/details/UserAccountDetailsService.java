package com.jll.user.details;

import java.util.Date;

import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;

public interface UserAccountDetailsService
{

	void saveAccDetails(UserAccountDetails userDetails);
	
	double getUserOperAmountTotal(int userId,int walletId,String operationType,Date start,Date end);
	
	UserAccountDetails initCreidrRecord(int userId,UserAccount userAcc,double beforAmt,double addAmt,String operType);
}

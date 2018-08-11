package com.jll.user.details;

import java.util.Date;
import java.util.Map;

import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserBankCard;
import com.jll.entity.UserInfo;

public interface UserAccountDetailsService
{

	void saveAccDetails(UserAccountDetails userDetails);
	
}

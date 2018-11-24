package com.jll.user.account;

import java.util.Map;

import com.jll.dao.PageQueryDao;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.UserAccount;
import com.jll.entity.UserBankCard;
import com.jll.entity.UserInfo;

public interface UserAccountService
{
	
	void saveUserAccount(UserAccount userAccount);
	UserAccount getUserAccount(Integer userId);
}

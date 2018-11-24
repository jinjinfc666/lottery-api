package com.jll.user.account;

import java.util.List;
import java.util.Map;

import com.jll.dao.PageQueryDao;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.UserAccount;

public interface UserAccountDao
{
	public void save(UserAccount userAccount);
	UserAccount getUserAccount(Integer userId);
}

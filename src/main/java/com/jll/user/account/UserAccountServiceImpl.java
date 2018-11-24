package com.jll.user.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants.SiteMessageReadType;
import com.jll.common.constants.Message;
import com.jll.common.utils.PageQuery;
import com.jll.common.utils.StringUtils;
import com.jll.dao.PageQueryDao;
import com.jll.dao.SupserDao;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.UserAccount;
import com.jll.entity.UserInfo;
import com.jll.sysSettings.syscode.SysCodeService;
import com.jll.user.UserInfoDao;
import com.jll.user.UserInfoService;
import com.jll.user.wallet.WalletService;


@Service
@Transactional
public class UserAccountServiceImpl implements UserAccountService
{
	private Logger logger = Logger.getLogger(UserAccountServiceImpl.class);
	@Resource
	UserAccountDao userAccountDao;
	@Override
	public void saveUserAccount(UserAccount userAccount) {
		userAccountDao.save(userAccount);
	}
	@Override
	public UserAccount getUserAccount(Integer userId) {
		return userAccountDao.getUserAccount(userId);
	}
}

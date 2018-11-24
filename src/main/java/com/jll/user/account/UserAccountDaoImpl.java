package com.jll.user.account;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.constants.Constants.UserType;
import com.jll.common.utils.PageQuery;
import com.jll.common.utils.PagenationUtil;
import com.jll.common.utils.StringUtils;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageBean;
import com.jll.dao.PageQueryDao;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.UserAccount;
import com.jll.entity.UserBankCard;
import com.jll.entity.UserInfo;

@Repository
public class UserAccountDaoImpl extends DefaultGenericDaoImpl<UserAccount> implements UserAccountDao
{
	
	@Override
	public void save(UserAccount userAccount) {
		this.saveOrUpdate(userAccount);
	}
	@Override
	public UserAccount getUserAccount(Integer userId) {
		Integer accType=Constants.WalletType.RED_PACKET_WALLET.getCode();
		String sql = "from UserAccount  where userId =:userId and accType=:accType";
		Query<UserAccount> query = getSessionFactory().getCurrentSession().createQuery(sql,UserAccount.class);
	    query.setParameter("userId", userId);
	    query.setParameter("accType", accType);
	    List<UserAccount> list= query.list();
	    if(list!=null&&list.size()>0) {
	    	return list.get(0);
	    }
		return null;
	}
}

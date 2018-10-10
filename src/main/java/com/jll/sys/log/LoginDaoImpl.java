package com.jll.sys.log;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;//失败后添加登录失败次数和修改锁定时间
import com.jll.entity.UserInfo;

@Repository
public class LoginDaoImpl extends DefaultGenericDaoImpl<UserInfo> implements LoginDao
{
	private Logger logger = Logger.getLogger(UserInfo.class);

	//saveUpdate
	@Override
	public void saveUpdate(UserInfo userInfo) {
		this.saveOrUpdate(userInfo);
	}
  
}

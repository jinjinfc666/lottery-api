package com.jll.sys.log;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;//失败后添加登录失败次数和修改锁定时间
import com.jll.entity.SysLog;
import com.jll.entity.SysLogin;
import com.jll.entity.UserInfo;

@Repository
public class SysLoginDaoImpl extends DefaultGenericDaoImpl<SysLogin> implements SysLoginDao
{
	private Logger logger = Logger.getLogger(SysLoginDaoImpl.class);

	@Override
	public void saveUpdate(SysLogin sysLogin) {
		this.saveOrUpdate(sysLogin);
	}
  
}

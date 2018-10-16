package com.jll.sys.log;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;//失败后添加登录失败次数和修改锁定时间
import com.jll.entity.SysLog;
import com.jll.entity.UserInfo;

@Repository
public class SysLogDaoImpl extends DefaultGenericDaoImpl<SysLog> implements SysLogDao
{
	private Logger logger = Logger.getLogger(SysLogDaoImpl.class);

	@Override
	public void saveUpdate(SysLog sysLog) {
		this.saveOrUpdate(sysLog);
	}
  
}

package com.jll.sys.log;


import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.SysLogin;

@Service
@Transactional
public class SysLoginServiceImpl implements SysLoginService
{
	private Logger logger = Logger.getLogger(SysLoginServiceImpl.class);
	
	@Resource
	SysLoginDao sysLoginDao;

	@Override
	public void saveOrUpdate(SysLogin sysLogin) {
		sysLogin.setCreateTime(new Date());
		sysLoginDao.saveUpdate(sysLogin);
	}
	
}

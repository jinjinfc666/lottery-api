package com.jll.sys.log;


import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.SysLog;

@Service
@Transactional
public class SysLogServiceImpl implements SysLogService
{
	private Logger logger = Logger.getLogger(SysLogServiceImpl.class);
	
	@Resource
	SysLogDao sysLogDao;

	@Override
	public void saveOrUpdate(SysLog sysLog) {
		sysLog.setCreateTime(new Date());
		sysLogDao.saveUpdate(sysLog);
	}
	
}

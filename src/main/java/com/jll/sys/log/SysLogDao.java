package com.jll.sys.log;

import com.jll.dao.PageBean;
import com.jll.entity.SysLog;

public interface SysLogDao
{
	//saveOrUpdate
	public void saveUpdate(SysLog sysLog);
	public PageBean queryOperationlog(Integer type,Integer userId, String startTime, String endTime,Integer pageIndex,Integer pageSize);
}

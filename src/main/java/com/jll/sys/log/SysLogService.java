package com.jll.sys.log;

import java.util.Map;

import com.jll.dao.PageBean;
import com.jll.entity.SysLog;

public interface SysLogService
{
	//日志
	public void saveOrUpdate(SysLog sysLog);
	public Map<String,Object> queryOperationlog(Integer type,String userName, String startTime, String endTime,Integer pageIndex,Integer pageSize);
}

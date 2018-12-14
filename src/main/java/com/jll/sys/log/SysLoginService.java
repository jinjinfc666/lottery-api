package com.jll.sys.log;

import java.util.List;
import java.util.Map;

import com.jll.entity.IpBlackList;
import com.jll.entity.SysLogin;

public interface SysLoginService
{
	//日志
	public void saveOrUpdate(SysLogin sysLogin);
	public Map<String,Object> queryLoginlog(Integer type, String userName, String startTime, String endTime,Integer pageIndex,Integer pageSize);
	//查询不存在用户登录日志
	public Map<String,Object> queryLoginlog(String ip,String startTime, String endTime,Integer pageIndex,Integer pageSize);
	//通过ip查询用户失败登录此时
	public long queryFailLoginCount(String ip);
	//删除
	public Map<String, Object> deleteLoginlog(Integer id);
	public SysLogin queryById(Integer id);
}

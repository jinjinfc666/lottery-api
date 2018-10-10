package com.jll.sys.log;

import com.jll.entity.UserInfo;

public interface LoginDao
{
//	//失败后添加登录失败次数
//	public void updateFailCount(Integer userId);
//	//查询登录失败次数
//	public Integer queryFailCount(Integer userId);
//	//失败后添加锁定时间
//	public void updateFailTime(Integer userId);
	//saveOrUpdate
	public void saveUpdate(UserInfo userInfo);
}

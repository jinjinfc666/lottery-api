package com.jll.sys.security.user;

import java.util.List;

import com.jll.entity.UserInfo;

public interface SysUserDao
{	
	//添加
	void saveOrUpdateUserInfo(UserInfo userInfo);
	//通过用户名查询UserInfo
	List<UserInfo> queryByUserName(String userName);
	//通过id查询用户
	List<UserInfo> queryById(Integer id);
} 

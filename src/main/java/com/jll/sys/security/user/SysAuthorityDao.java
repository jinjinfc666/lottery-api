package com.jll.sys.security.user;

import java.util.List;

import com.jll.entity.SysAuthority;

public interface SysAuthorityDao
{	
	//添加
	void saveOrUpdateSysAuthority(SysAuthority sysAuthority);
	//通过userId查询用户所拥有的权限
	List<SysAuthority> queryByUserId(Integer userId);
	//通过userId删除授权
	void deleteByUserId(Integer userId);
} 

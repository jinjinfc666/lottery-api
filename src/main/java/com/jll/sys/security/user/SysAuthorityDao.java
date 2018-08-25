package com.jll.sys.security.user;

import java.util.List;

import com.jll.entity.SysAuthority;

public interface SysAuthorityDao
{	
	//添加
	void saveOrUpdateSysAuthority(SysAuthority sysAuthority);
	//删除
	void deleteById(Integer id);
	//通过id查询
	List<SysAuthority> queryById(Integer id);
} 

package com.jll.sys.security.permission;

import java.util.List;

import com.jll.entity.SysRole;

public interface SysRoleDao
{	
	//添加
	void saveOrUpdateSysRole(SysRole sysRole);
	//通过id查询
	List<SysRole> queryById(Integer id);
	//查询
	List<?> query();
	//只查询当前表
	List<SysRole> querySysRole();
	//判断这条数据存不存在
	List<SysRole> queryBySysRole(SysRole sysRole);
}

package com.jll.sys.security.permission;

import java.util.List;
import java.util.Map;

import com.jll.entity.SysRole;
import com.jll.entity.UserInfo;

public interface SysRoleService
{	
	//添加
	Map<String,Object> addSysRole(Map<String,Object> ret);
	//修改
	Map<String,Object> updateSysRole(SysRole sysRole);
	//通过id查询
	SysRole queryById(Integer id);
	//查询
	List<?> query();
	//查询当前表
	List<SysRole> querySysRole();
	//判断这条数据存不存在
	boolean queryBySysRole(SysRole sysRole);
	SysRole queryByRoleName(String roleName);
}

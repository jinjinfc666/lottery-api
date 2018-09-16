package com.jll.sys.security.user;

import java.util.List;
import java.util.Map;

import com.jll.entity.SysAuthority;

public interface SysAuthorityService
{	
	//添加
	void updateSysAuthority(Map<String,Object> ret);
	//通过userId查询用户所拥有的权限
	List<SysAuthority> queryByUserId(Integer userId);
}

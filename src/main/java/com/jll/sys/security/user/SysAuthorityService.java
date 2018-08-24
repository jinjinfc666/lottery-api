package com.jll.sys.security.user;

import java.util.Map;

public interface SysAuthorityService
{	
	//添加
	void addSysAuthority(Map<String,Object> ret);
	//删除
	Map<String,Object> deleteById(Integer id);
	//判断这条数据存不存在
	boolean isOrNo(Integer id);
}

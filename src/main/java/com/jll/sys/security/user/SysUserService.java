package com.jll.sys.security.user;


import java.util.List;
import java.util.Map;

import com.jll.entity.UserInfo;

public interface SysUserService
{	
	//添加
	Map<String,Object> addUserInfo(UserInfo userInfo);
	//通过用户名查找UserInfo
	UserInfo queryByUserName(String userName);
	//修改
	Map<String,Object> updateSysUser(Map<String,Object> ret);
	//查询要添加的数据存不存在
	boolean isOrNo(String userName);
	//查询所有的管理员
	Map<String,Object> querySysUser(Map<String, Object> ret);
}

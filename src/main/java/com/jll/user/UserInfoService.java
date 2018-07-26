package com.jll.user;

import java.util.Map;

import com.jll.entity.UserInfo;

public interface UserInfoService
{
	int getUserId(String userName);
	boolean isUserInfo(String userName);
	boolean isUserInfoByUid(int userId);
	Map<String, Object> updateFundPwd(String userName,String oldPwd,String newPwd);
	Map<String, Object> updateLoginPwd(String userName,String oldPwd,String newPwd);
	Map<String, Object> getUserInfoByUserName(String userName);
	Map<String, Object> updateUserInfoInfo(UserInfo userInfo);
}

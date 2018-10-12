package com.jll.sys.log;


public interface LoginService
{
	//登录失败后的操作
	public void updateFailLogin(String userName);
	//登录成功后的操作
	public void updateSuccessLogin(String userName);
}

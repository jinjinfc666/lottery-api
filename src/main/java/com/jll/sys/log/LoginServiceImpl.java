package com.jll.sys.log;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.UserInfo;
import com.jll.user.UserInfoDao;

@Service
@Transactional
public class LoginServiceImpl implements LoginService
{
	private Logger logger = Logger.getLogger(LoginServiceImpl.class);
	
	@Resource
	LoginDao loginDao;
	@Resource
	UserInfoDao userInfoDao;
	//登录失败后的操作
	@Override
	public void failLogin() {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		UserInfo userInfo=userInfoDao.getUserByUserName(userName);
		Integer failLoginCount=userInfo.getFailLoginCount();
		if(failLoginCount<3) {
			Integer failLoginCountNew=failLoginCount+1;
			userInfo.setFailLoginCount(failLoginCountNew);
			if(failLoginCountNew==3) {
				Calendar calendar = new GregorianCalendar();
				Date date = new Date();
				calendar.setTime(date);
				calendar.add(calendar.MINUTE, 5);//把日期往后增加5分钟.整数往后推,负数往前移动
				date=calendar.getTime();
				userInfo.setUnlockTime(date);
			}
			loginDao.saveUpdate(userInfo);
		}
	}
	//登录成功后的操作
	@Override
	public void successLogin(String userName) {
		UserInfo userInfo=userInfoDao.getUserByUserName(userName);
		Integer failLoginCount=0;
		userInfo.setFailLoginCount(failLoginCount);
		Date date=null;
		userInfo.setUnlockTime(date);
		loginDao.saveUpdate(userInfo);
	}
	
}

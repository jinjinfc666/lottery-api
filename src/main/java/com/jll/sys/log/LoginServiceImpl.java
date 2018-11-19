package com.jll.sys.log;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
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
	@Resource
	CacheRedisService cacheRedisService;
	//登录失败后的操作
	@Override
	public void updateFailLogin(String userName) {
		UserInfo userInfo=userInfoDao.getUserByUserName(userName);
		Integer failLoginCount=userInfo.getFailLoginCount();
		if(failLoginCount==null) {
			failLoginCount=0;
		}
		String codeTypeName=Constants.SysCodeTypes.SYS_RUNTIME_ARGUMENT.getCode();
		String codeNameCount=Constants.SysRuntimeArgument.FAIL_LOGIN_COUNT.getCode();
		String codeNameLock=Constants.SysRuntimeArgument.LOCKING_TIME.getCode();
		Integer count=Integer.valueOf(cacheRedisService.getSysCode(codeTypeName, codeNameCount).getCodeVal());
		Integer lockTime=Integer.valueOf(cacheRedisService.getSysCode(codeTypeName, codeNameLock).getCodeVal());
		if(failLoginCount<count) {
			Integer failLoginCountNew=failLoginCount+1;
			userInfo.setFailLoginCount(failLoginCountNew);
			if(failLoginCountNew==count) {
				Integer state=1;
				Calendar calendar = new GregorianCalendar();
				Date date = new Date();
				calendar.setTime(date);
				calendar.add(calendar.MINUTE, lockTime);//把日期往后增加5分钟.整数往后推,负数往前移动
				date=calendar.getTime();
				userInfo.setUnlockTime(date);
				userInfo.setState(state);
			}
			loginDao.saveUpdate(userInfo);
		}
	}
	//登录成功后的操作
	@Override
	public void updateSuccessLogin(String userName) {
		Integer state=0;
		UserInfo userInfo=userInfoDao.getUserByUserName(userName);
		Integer failLoginCount=0;
		Integer loginCount=userInfo.getLoginCount();
		if(loginCount==null) {
			loginCount=0;
		}
		userInfo.setFailLoginCount(failLoginCount);
		userInfo.setLoginCount(loginCount+1);
		Date date=null;
		userInfo.setUnlockTime(date);
		userInfo.setState(state);
		loginDao.saveUpdate(userInfo);
	}
	
}

package com.jll.user;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService
{
	@Resource
	UserInfoDao userDao;

	@Override
	public int getUserId(String userName) {
		return userDao.getUserId(userName);
	}

	@Override
	public boolean isUserInfo(String userName) {
		long count=userDao.getCountUser(userName);
		return count == 0 ? false:true;
	}
}

package com.jll.sys.sig;


import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.SignupRec;

@Service
@Transactional
public class SignupRecServiceImpl implements SignupRecService
{
	private Logger logger = Logger.getLogger(SignupRecServiceImpl.class);
	
	@Resource
	SignupRecDao signupRecDao;
	@Override
	public void saveSignupRec(SignupRec signupRec) {
		signupRecDao.save(signupRec);
	}
	@Override
	public Integer getCount(Integer userId) {
		List<SignupRec> list=signupRecDao.getCount(userId);
		if(list==null||list.size()==0) {
			return 0;
		}
		return list.size();
	}
}

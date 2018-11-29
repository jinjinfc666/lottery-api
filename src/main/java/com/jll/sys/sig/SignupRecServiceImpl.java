package com.jll.sys.sig;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Message;
import com.jll.dao.PageBean;
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
	@Override
	public Map<String, Object> queryRecord(Integer userId, String startTime, String endTime,Integer pageIndex,Integer pageSize) {
		Map<String,Object> map=new HashMap<String,Object>();
		PageBean pageBean=signupRecDao.queryRecord(userId, startTime, endTime, pageIndex, pageSize);
		map.put(Message.KEY_DATA, pageBean);
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
}

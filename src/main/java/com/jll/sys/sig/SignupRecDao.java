package com.jll.sys.sig;


import java.util.List;

import com.jll.dao.PageBean;
import com.jll.entity.SignupRec;

public interface SignupRecDao
{
	//签到
	void save(SignupRec signupRec);
	//查询用户签到的次数
	List<SignupRec> getCount(Integer userId);
	//查询用户的签到记录
	PageBean queryRecord(Integer userId,String startTime,String endTime,Integer pageIndex,Integer pageSize);
}

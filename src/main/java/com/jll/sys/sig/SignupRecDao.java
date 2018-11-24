package com.jll.sys.sig;


import java.util.List;

import com.jll.entity.SignupRec;

public interface SignupRecDao
{
	//签到
	void save(SignupRec signupRec);
	//查询用户签到的次数
	List<SignupRec> getCount(Integer userId);
}

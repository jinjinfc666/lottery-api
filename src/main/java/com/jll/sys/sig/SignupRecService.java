package com.jll.sys.sig;

import java.util.List;
import java.util.Map;

import com.jll.entity.SignupRec;

public interface SignupRecService
{
	void saveSignupRec(SignupRec signupRec);
	Integer getCount(Integer userId);
	Map<String,Object> queryRecord(Integer userId, String startTime, String endTime,Integer pageIndex,Integer pageSize);
	//查询当月的签到记录
	List<SignupRec> queryNowMonthRecord(Integer userId);
}

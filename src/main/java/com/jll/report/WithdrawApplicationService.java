package com.jll.report;

import java.util.Date;
import java.util.Map;

import com.jll.entity.WithdrawApplication;

public interface WithdrawApplicationService {
	WithdrawApplication queryDetails(Integer id);
	long getUserWithdrawCount(int userId, Date start, Date end);
	public Map<String,Object> updateState(Map<String,Object> ret);
	//判断是否存在
    boolean isNullById(Integer id);
}

package com.jll.report;

import java.util.List;

import com.jll.entity.SysCode;

public interface FlowDetailDao {
	
	public List<?> queryUserAccountDetails(String userName,String orderNum,Float amountStart,Float amountEnd,String operationType,String startTime,String endTime);
}

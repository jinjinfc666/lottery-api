package com.jll.backstage.report.flowdetail;

import java.util.List;

import com.jll.entity.SysCode;

public interface FlowDetailDao {
	
	public List<?> queryUserAccountDetails(String userName,Integer orderId,Float amountStart,Float amountEnd,String operationType,String startTime,String endTime);
	public List<SysCode> queryType();
}

package com.jll.report;

import java.util.Map;


public interface FlowDetailDao {
	
	public Map<String,Object> queryUserAccountDetails(Integer codeTypeNameId,String userName,String orderNum,Float amountStart,Float amountEnd,String operationType,String startTime,String endTime);
}

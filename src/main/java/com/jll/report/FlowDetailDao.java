package com.jll.report;

import java.util.Map;


public interface FlowDetailDao {
	
	public Map<String,Object> queryUserAccountDetails(Integer codeTypeNameId,String userName,Float amountStart,Float amountEnd,String operationType,String startTime,String endTime,Integer pageIndex,Integer pageSize);
	//代理的转账记录查询  
	Map<String, Object> queryAgentTransfer(Integer agentId, String startTime, String endTime);
}

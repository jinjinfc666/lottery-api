package com.jll.report;

import java.util.Map;


public interface FlowDetailService {
	Map<String,Object> queryUserAccountDetails(Map<String,Object> ret);
	//代理的转账记录查询  
	Map<String,Object> queryAgentTransfer(Integer agentId,String startTime,String endTime);
}

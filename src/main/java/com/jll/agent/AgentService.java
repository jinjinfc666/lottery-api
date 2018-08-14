package com.jll.agent;

import java.util.Map;

import com.jll.dao.PageQueryDao;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccountDetails;


public interface AgentService {
	
	Map<String, Object> getAgentLowerBetOrder(String userName, OrderInfo order,int agentId,PageQueryDao page);
	Map<String, Object> getAgentLowerCreditOrder(String userName, UserAccountDetails order,int agentId,PageQueryDao page);
	Map<String, Object> getAgentLowerProfitReport(String userName, Integer id, PageQueryDao page);

}

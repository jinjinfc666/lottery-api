package com.jll.agent;

import java.util.Map;

import com.jll.dao.PageQueryDao;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;


public interface AgentService {
	
	Map<String, Object> getAgentLowerBetOrder(String userName, OrderInfo order,UserInfo superior,PageQueryDao page);
	Map<String, Object> getAgentLowerCreditOrder(String userName, UserAccountDetails order,UserInfo superior,PageQueryDao page);
	Map<String, Object> getAgentLowerProfitReport(String userName, UserInfo superior, PageQueryDao page);

}

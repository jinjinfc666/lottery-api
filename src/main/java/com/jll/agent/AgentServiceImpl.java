package com.jll.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jll.common.constants.Message;
import com.jll.common.utils.DateUtil;
import com.jll.common.utils.PageQuery;
import com.jll.common.utils.StringUtils;
import com.jll.dao.PageQueryDao;
import com.jll.dao.SupserDao;
import com.jll.entity.MemberPlReport;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccountDetails;

@Service
public class AgentServiceImpl implements AgentService{

	@Resource
	SupserDao  supserDao;

	@Override
	public Map<String, Object> getAgentLowerBetOrder(String userName, OrderInfo order,int agentId,PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer querySql = new StringBuffer("SELECT o.* FROM  order_info o WHERE o.user_id IN(SELECT u.id FROM  user_info u WHERE u.superior=? OR u.superior LIKE ?");
		List<Object> parmsList = new ArrayList<>();
		parmsList.add(agentId);
		parmsList.add(StringUtils.COMMA+agentId);
		
		if(!StringUtils.isEmpty(userName)){
			querySql.append(" and u.user_name =? ");
			parmsList.add(userName);
		}
		querySql.append( ") where 1=1 ");
		
		if(order.getIssueId() > 0){
			querySql.append(" and o.is_zh = ?");
			parmsList.add(order.getIssueId());
		}
		querySql.append(" and o.create_time >= ?");	
		querySql.append(" and o.create_time <= ?");	
		parmsList.add(DateUtil.fmtYmdHis(page.getStartDate()));
		parmsList.add(DateUtil.fmtYmdHis(page.getEndDate()));

		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenationBySql(querySql.toString(), OrderInfo.class, parmsList, page.getPageIndex(), page.getPageSize()));
		return ret;
	}

	@Override
	public Map<String, Object> getAgentLowerCreditOrder(String userName, UserAccountDetails order,int agentId,PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer querySql = new StringBuffer("SELECT o.* FROM  user_account_details o WHERE o.user_id IN(SELECT u.id FROM  user_info u WHERE u.superior=? OR u.superior LIKE ?");
		List<Object> parmsList = new ArrayList<>();
		parmsList.add(agentId);
		parmsList.add(StringUtils.COMMA+agentId);
		
		if(!StringUtils.isEmpty(userName)){
			querySql.append(" and u.user_name =? ");
			parmsList.add(userName);
		}
		querySql.append( ") where 1=1 ");
		
		if(!StringUtils.isEmpty(order.getOperationType())){
			querySql.append(" and o.operation_type = ?");
			parmsList.add(order.getOperationType());
		}
		querySql.append(" and o.create_time >= ?");	
		querySql.append(" and o.create_time <= ?");	
		parmsList.add(DateUtil.fmtYmdHis(page.getStartDate()));
		parmsList.add(DateUtil.fmtYmdHis(page.getEndDate()));
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenationBySql(querySql.toString(), UserAccountDetails.class, parmsList, page.getPageIndex(), page.getPageSize()));
		return ret;
	}

	@Override
	public Map<String, Object> getAgentLowerProfitReport(String userName, Integer agentId, PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer querySql = new StringBuffer("SELECT o.* FROM  member_pl_report o WHERE o.user_id IN(SELECT u.id FROM  user_info u WHERE u.superior=? OR u.superior LIKE ?");
		List<Object> parmsList = new ArrayList<>();
		parmsList.add(agentId);
		parmsList.add(StringUtils.COMMA+agentId);
		
		if(!StringUtils.isEmpty(userName)){
			querySql.append(" and u.user_name =? ");
			parmsList.add(userName);
		}
		querySql.append( ") where 1=1 ");
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenationBySql(querySql.toString(), MemberPlReport.class, parmsList, page.getPageIndex(), page.getPageSize()));
		return ret;
	}

}

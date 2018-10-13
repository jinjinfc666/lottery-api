package com.jll.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.utils.PageQuery;
import com.jll.common.utils.StringUtils;
import com.jll.dao.PageBean;
import com.jll.dao.PageQueryDao;
import com.jll.dao.SupserDao;
import com.jll.entity.MemberPlReport;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.game.order.OrderDao;

@Service
public class AgentServiceImpl implements AgentService{

	@Resource
	SupserDao  supserDao;
	
	@Resource
	OrderDao  orderDao;
	
	@Resource
	CacheRedisService cacheRedisService;
	
	@Override
	public Map<String, Object> getAgentLowerBetOrder(String userName, OrderInfo order,UserInfo superior,PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer querySql = new StringBuffer("FROM  OrderInfo o,UserInfo u1,SysCode sys,PlayType pl WHERE o.userId IN(SELECT u.id FROM  UserInfo u WHERE u.superior=? ");
		List<Object> parmsList = new ArrayList<>();
		
		if(!StringUtils.isEmpty(superior.getSuperior())){
			parmsList.add(""+superior.getId()+StringUtils.COMMA+superior.getSuperior());
		}else{
			parmsList.add(""+superior.getId());
		}
		
		if(!StringUtils.isEmpty(userName)){
			querySql.append(" and u.userName =? ");
			parmsList.add(userName);
		}
		
		querySql.append(" ) and o.createTime >= ?  and o.createTime <= ?");	
		parmsList.add(page.getStartDate());
		parmsList.add(page.getEndDate());
		
		if(order.getIssueId() > 0){
			querySql.append(" and o.issueId = ?");
			parmsList.add(order.getIssueId());
		}
		
		if(order.getIsZh() > 0){
			querySql.append(" and o.isZh = ?");
			parmsList.add(order.getIsZh());
		}
		
		querySql.append(" and u1.id = o.userId ")
				.append(" and o.playType=pl.id ")
				.append(" and pl.lotteryType=sys.codeName and sys.codeType=? ");	
		querySql.append(" order by o.id desc");
		
		parmsList.add(cacheRedisService.getSysCode(Constants.SysCodeTypes.LOTTERY_TYPES.getCode(),Constants.SysCodeTypes.LOTTERY_TYPES.getCode()).getId());
		

		PageBean<OrderInfo> reqPage = new PageBean<>();
		reqPage.setPageIndex(page.getPageIndex());
		reqPage.setPageSize(page.getPageSize());
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenationByHql(supserDao,querySql.toString(), Object.class, parmsList, page.getPageIndex(), page.getPageSize()));
		return ret;
	}

	@Override
	public Map<String, Object> getAgentLowerCreditOrder(String userName, UserAccountDetails order,UserInfo superior,PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>();
		
		StringBuffer querySql = new StringBuffer("FROM  UserAccountDetails o,UserInfo u1,SysCode sys WHERE o.userId IN(SELECT u.id FROM  UserInfo u WHERE u.superior=? ");
		List<Object> parmsList = new ArrayList<>();
		
		if(!StringUtils.isEmpty(superior.getSuperior())){
			parmsList.add(""+superior.getId()+StringUtils.COMMA+superior.getSuperior());
		}else{
			parmsList.add(""+superior.getId());
		}
		
		if(!StringUtils.isEmpty(userName)){
			querySql.append(" and u.userName =? ");
			parmsList.add(userName);
		}
		
		querySql.append(" ) and o.createTime >= ?  and o.createTime <= ?");	
		parmsList.add(page.getStartDate());
		parmsList.add(page.getEndDate());
		
		if(!StringUtils.isEmpty(order.getOperationType())){
			querySql.append(" and o.operationType = ?");
			parmsList.add(order.getOperationType());
		}
		querySql.append(" and u1.id = o.userId ")
				.append(" and o.operationType=sys.codeName and sys.codeType=? ");	
		querySql.append(" order by o.id desc");
		
		parmsList.add(cacheRedisService.getSysCode(Constants.SysCodeTypes.FLOW_TYPES.getCode(),Constants.SysCodeTypes.FLOW_TYPES.getCode()).getId());
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenationByHql(supserDao,querySql.toString(), UserAccountDetails.class, parmsList, page.getPageIndex(), page.getPageSize()));
		return ret;
	}

	@Override
	public Map<String, Object> getAgentLowerProfitReport(String userName, UserInfo superior, PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer querySql = new StringBuffer("SELECT o FROM  MemberPlReport o WHERE o.userName IN(SELECT u.userName FROM  UserInfo u WHERE u.superior=? ");
		List<Object> parmsList = new ArrayList<>();
		
		if(!StringUtils.isEmpty(superior.getSuperior())){
			parmsList.add(""+superior.getId()+StringUtils.COMMA+superior.getSuperior());
		}else{
			parmsList.add(""+superior.getId());
		}
		
		if(!StringUtils.isEmpty(userName)){
			querySql.append(" and u.userName =? ");
			parmsList.add(userName);
		}
		
		querySql.append(" )  and o.createTime >= ?");	
		querySql.append(" and o.createTime <= ?");	
		querySql.append(" order by o.id desc");
		parmsList.add(page.getStartDate());
		parmsList.add(page.getEndDate());
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenationByHql(supserDao,querySql.toString(), MemberPlReport.class, parmsList, page.getPageIndex(), page.getPageSize()));
		return ret;
	}

}

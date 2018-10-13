package com.jll.record;

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
import com.jll.common.utils.Utils;
import com.jll.dao.PageBean;
import com.jll.dao.PageQueryDao;
import com.jll.dao.SupserDao;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccountDetails;

@Service
public class UserRecordServiceImpl implements UserRecordService{

	@Resource
	SupserDao  supserDao; 
	
	@Resource
	CacheRedisService cacheRedisService;
	
	@Override
	public Map<String, Object> getUserBetRecord(OrderInfo order,PageQueryDao page,Map<String, Object> parms) {
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer querySql = new StringBuffer("FROM  OrderInfo o,UserInfo u1,SysCode sys,PlayType pl,Issue iu WHERE o.userId =? ");
		List<Object> parmsList = new ArrayList<>();
		
		parmsList.add(order.getUserId());
		
		
		querySql.append(" and o.createTime >= ?  and o.createTime <= ?");	
		parmsList.add(page.getStartDate());
		parmsList.add(page.getEndDate());
		
		if(order.getIsZh() > 0){
			querySql.append(" and o.isZh = ?");
			parmsList.add(order.getIsZh());
		}
		
		querySql.append(" and u1.id = o.userId ")
				.append(" and o.issueId=iu.id ")
				.append(" and o.playType=pl.id ")
				.append(" and pl.lotteryType=sys.codeName and sys.codeType=? ");	
		
		parmsList.add(cacheRedisService.getSysCode(Constants.SysCodeTypes.LOTTERY_TYPES.getCode(),Constants.SysCodeTypes.LOTTERY_TYPES.getCode()).getId());
		
		
		if(!StringUtils.isEmpty(Utils.toString(parms.get("issueNum")))){
			querySql.append(" and iu.issueNum = ? ");
			parmsList.add(Utils.toString(parms.get("issueNum")));
		}
		if(!StringUtils.isEmpty(Utils.toString(parms.get("lotteryType")))){
			querySql.append(" and pl.lotteryType = ? ");
			parmsList.add(Utils.toString(parms.get("lotteryType")));
		}
		if(!StringUtils.isEmpty(Utils.toString(parms.get("playType")))){
			querySql.append(" and pl.ptName = ? ");
			parmsList.add(Utils.toString(parms.get("playType")));
		}
		
		querySql.append(" order by o.id desc");
		
		

		PageBean<OrderInfo> reqPage = new PageBean<>();
		reqPage.setPageIndex(page.getPageIndex());
		reqPage.setPageSize(page.getPageSize());
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenationByHql(supserDao,querySql.toString(), Object.class, parmsList, page.getPageIndex(), page.getPageSize()));
		return ret;
	}

	@Override
	public Map<String, Object> getUserBetType() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,cacheRedisService.getSysCode(Constants.SysCodeTypes.LOTTERY_TYPES.getCode()));
		return ret;
	}

	@Override
	public Map<String, Object> getUserCreditType() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,cacheRedisService.getSysCode(Constants.SysCodeTypes.FLOW_TYPES.getCode()));
		return ret;
	}

	@Override
	public Map<String, Object> getUserCreditRecord(UserAccountDetails query, PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>();
		
		StringBuffer querySql = new StringBuffer("FROM  UserAccountDetails o,UserInfo u1,SysCode sys WHERE o.userId =? ");
		List<Object> parmsList = new ArrayList<>();
		parmsList.add(query.getUserId());
		
		querySql.append(" and o.createTime >= ?  and o.createTime <= ?");	
		parmsList.add(page.getStartDate());
		parmsList.add(page.getEndDate());
		
		if(!StringUtils.isEmpty(query.getOperationType())){
			querySql.append(" and o.operationType = ?");
			parmsList.add(query.getOperationType());
		}
		querySql.append(" and u1.id = o.userId ")
				.append(" and o.operationType=sys.codeName and sys.codeType=? ");	
		querySql.append(" order by o.id desc");
		
		parmsList.add(cacheRedisService.getSysCode(Constants.SysCodeTypes.FLOW_TYPES.getCode(),Constants.SysCodeTypes.FLOW_TYPES.getCode()).getId());
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenationByHql(supserDao,querySql.toString(), UserAccountDetails.class, parmsList, page.getPageIndex(), page.getPageSize()));
		return ret;
	}
	

}

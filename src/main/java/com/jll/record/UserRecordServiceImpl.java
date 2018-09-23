package com.jll.record;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.utils.PageQuery;
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
	public Map<String, Object> getUserBetRecord(OrderInfo pramsInfo,PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>();
		DetachedCriteria dc = DetachedCriteria.forClass(OrderInfo.class);
		dc.add(Restrictions.eq("userId",pramsInfo.getUserId()));
		/*if(!StringUtils.isEmpty(pramsInfo.getPlayType())){
			dc.add(Restrictions.eq("playType",pramsInfo.getPlayType()));
		}
		if(!StringUtils.isEmpty(pramsInfo.getBetNum())){
			dc.add(Restrictions.eq("betNum",pramsInfo.getBetNum()));
		}*/
		if(null != pramsInfo.getIsZh()){
			dc.add(Restrictions.eq("isZh",pramsInfo.getIsZh()));
		}
		dc.add(Restrictions.le("createTime",page.getEndDate()));
		dc.add(Restrictions.ge("createTime",page.getStartDate()));
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenation(supserDao.getHibernateTemplate(), dc, page.getPageIndex(), page.getPageSize()));
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
		DetachedCriteria dc = DetachedCriteria.forClass(UserAccountDetails.class);
		dc.add(Restrictions.eq("userId",query.getUserId()));
		if(!StringUtils.isEmpty(query.getOperationType())){
			dc.add(Restrictions.eq("operationType",query.getOperationType()));
		}
		if(null != query.getOrderId()){
			dc.add(Restrictions.eq("id",query.getOrderId()));
		}
		dc.add(Restrictions.le("createTime",page.getEndDate()));
		dc.add(Restrictions.ge("createTime",page.getStartDate()));
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenation(supserDao.getHibernateTemplate(), dc, page.getPageIndex(), page.getPageSize()));
		return ret;
	}
	

}

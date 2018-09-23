package com.jll.help;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.jll.common.constants.Constants.IssueState;
import com.jll.common.constants.Constants.SysCodeState;
import com.jll.common.constants.Message;
import com.jll.common.utils.PageQuery;
import com.jll.dao.PageQueryDao;
import com.jll.dao.SupserDao;
import com.jll.entity.Issue;
import com.jll.entity.PlayType;

@Service
public class HelpServiceImpl implements HelpService{

	@Resource
	SupserDao  supserDao; 
	
	@Override
	public Map<String, Object> getHistoryOpenRecord(String lotteryType, PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>();
		DetachedCriteria dc = DetachedCriteria.forClass(Issue.class);
		dc.add(Restrictions.eq("lotteryType",lotteryType));
		dc.add(Restrictions.eq("state",IssueState.LOTTO_DARW.getCode()));
		dc.add(Restrictions.le("createTime",new Date()));
		dc.addOrder(Order.desc("createTime"));
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenation(supserDao.getHibernateTemplate(), dc, page.getPageIndex(), page.getPageSize()));
		return ret;
	}

	@Override
	public Map<String, Object> getLottoryPlayType(String lotteryType, PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>();
		DetachedCriteria dc = DetachedCriteria.forClass(PlayType.class);
		if(!StringUtils.isEmpty(lotteryType)){
			dc.add(Restrictions.eq("lotteryType",lotteryType));
		}
		dc.add(Restrictions.eq("state",SysCodeState.VALID_STATE.getCode()));
		dc.add(Restrictions.le("createTime",new Date()));
		dc.addOrder(Order.desc("seq"));
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenation(supserDao.getHibernateTemplate(), dc, page.getPageIndex(), page.getPageSize()));
		return ret;
	}
	

}

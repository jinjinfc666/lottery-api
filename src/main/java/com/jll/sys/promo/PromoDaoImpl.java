package com.jll.sys.promo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.jll.common.utils.DateUtil;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageBean;
import com.jll.entity.PromoClaim;

@Repository
public class PromoDaoImpl extends DefaultGenericDaoImpl<PromoClaim> implements PromoDao
{
	@Override
	public PageBean queryRecord(Integer userId, String startTime, String endTime,Integer pageIndex,Integer pageSize) {
		Map<String,Object> map=new HashMap<String,Object>();
		String userIdSql="";
		if(userId!=null) {
			userIdSql="and  a.userId=:userId";
			map.put("userId", userId);
		}
		Date beginDate = DateUtil.fmtYmdHisToDate(startTime);
	    Date endDate = DateUtil.fmtYmdHisToDate(endTime);
		map.put("startTime", beginDate);
		map.put("endTime", endDate);
		String sql="";
		sql="from PromoClaim a,UserInfo b,Promo c where a.userId=b.id and a.promoId=c.id "+userIdSql+" and a.claimTime>=:startTime and a.claimTime<:endTime ORDER BY a.id DESC";
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		return pageBean;
	}
}

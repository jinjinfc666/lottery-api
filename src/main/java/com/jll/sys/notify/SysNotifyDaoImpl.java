package com.jll.sys.notify;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.dao.PageBean;
import com.jll.entity.SysNotification;
import com.jll.common.utils.StringUtils;
import com.jll.dao.DefaultGenericDaoImpl;


@Service
@Transactional
public class SysNotifyDaoImpl extends DefaultGenericDaoImpl<SysNotification> implements SysNotifyDao
{
	private Logger logger = Logger.getLogger(SysNotifyDaoImpl.class);

	@Override
	public PageBean queryNotificationLists(String startTime, String endTime, Integer pageIndex,
			Integer pageSize) {
		Map<String,Object> map=new HashMap<String,Object>();
//		String userIdSql="";
//		if(userId!=null) {
//			userIdSql="and  a.receiver=:receiver";
//			map.put("receiver", userId);
//		}
		String timeSql="";
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql="and a.createTime>=:startTime and a.createTime<:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		String sql="";
		sql="from SysNotification a,UserInfo b where a.creator=b.id "+timeSql+"  ORDER BY a.id DESC";
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		return pageBean;
	}

	
}

package com.jll.sys.notify;

import java.util.Map;

import com.jll.dao.PageBean;
import com.jll.dao.PageQueryDao;
import com.jll.entity.SysNotification;

public interface SysNotifyDao
{
	PageBean queryNotificationLists(String startTime,String endTime,Integer pageIndex,Integer pageSize);
}

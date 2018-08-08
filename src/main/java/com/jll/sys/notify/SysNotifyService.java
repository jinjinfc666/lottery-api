package com.jll.sys.notify;

import java.util.Map;

import com.jll.dao.PageQueryDao;
import com.jll.entity.SysNotification;

public interface SysNotifyService
{
	Map<String, Object> updateSysNotify(SysNotification notify);
	Map<String, Object> addSysNotify(String sendIds,SysNotification notify);
	Map<String, Object> getSysNotifyLists(String userName, PageQueryDao page);
	Map<String, Object> setSysNotifyExpire(int notifyId);
}

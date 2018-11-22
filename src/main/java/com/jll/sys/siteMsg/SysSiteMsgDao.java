package com.jll.sys.siteMsg;

import java.util.List;
import java.util.Map;

import com.jll.dao.PageQueryDao;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;

public interface SysSiteMsgDao
{
	public void save(SiteMessage siteMessage);
	List<SiteMessage> getSiteMessageLists(String userName, PageQueryDao page);
	//查询站内信列表
	Map<String, Object> querySiteMessage(Map<String, String> params);
	Map<String, Object> updateUserSiteMessageRead(Map<String, String> params);
	//查询反馈
	List<?> querySiteMessFeedback(Integer mesId);
	List<?> querySiteMessageById(Integer id);
}

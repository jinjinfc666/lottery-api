package com.jll.sys.siteMsg;

import java.util.List;

import com.jll.dao.PageQueryDao;
import com.jll.entity.SiteMessage;

public interface SysSiteMsgDao
{
	List<SiteMessage> getSiteMessageLists(String userName, PageQueryDao page);
}

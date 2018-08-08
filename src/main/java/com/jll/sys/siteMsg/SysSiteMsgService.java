package com.jll.sys.siteMsg;

import java.util.Map;

import com.jll.dao.PageQueryDao;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.UserBankCard;
import com.jll.entity.UserInfo;

public interface SysSiteMsgService
{
	
	Map<String, Object> showSiteMessageFeedback(int msgId);
	Map<String, Object> getSiteMessageLists(String userName, PageQueryDao page);
}

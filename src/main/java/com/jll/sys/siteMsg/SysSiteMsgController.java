package com.jll.sys.siteMsg;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.utils.Utils;
import com.jll.dao.PageQueryDao;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.SysNotification;
import com.jll.user.UserController;
import com.jll.user.UserInfoService;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "sysSiteMsg", name = "Sys Site Msg")
@ApiComment(seeClass = SiteMessage.class)
@RestController
@RequestMapping({ "/sys/site-msg" })
public class SysSiteMsgController {
	
	private Logger logger = Logger.getLogger(UserController.class);
	
	@Resource
	UserInfoService userInfoService;
	
	@Resource
	SysSiteMsgService sysSiteMsgService;
	
	
	@ApiComment("Get Site Msg Lists")
	@RequestMapping(value="/lists", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> list(@RequestBody Map<String, String> params) {
		String userName = Utils.toString(params.get("userName"));
		PageQueryDao page = new PageQueryDao(Utils.toDate(params.get("startDate")),Utils.toDate(params.get("endDate")),Utils.toInteger(params.get("pageIndex")),
				Utils.toInteger(params.get("pageSize")));
		return sysSiteMsgService.getSiteMessageLists(userName,page);
	}
	
	@ApiComment("Show Site Msg History")
	@RequestMapping(value="/{msgId}/history-feedback", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> siteMsgDesc(@PathVariable("msgId") int msgId) {
		return sysSiteMsgService.showSiteMessageFeedback(msgId);
	}

	
	@ApiComment("Feedback Site Message ")
	@RequestMapping(value="/feedback", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> siteMessageFeedback(@RequestBody SiteMessFeedback back) {
		return userInfoService.updateMessageFeedbackStatus(back);
	}
	
	/**
	 * 
	 * Add Site Message 
	 * 
	 * @param userId
	 * @param msg
	 * @param sendIds value is ALL, if user type is agent,then send all agent low user or agent, if user type is system user ,then send all user
	 *  value is 0001,then send user id value is 0001 user,
	 *  value is 0001,002 ,then send  user id value is 0001 or 0002 user
	 *  value is empty ,then send to system
	 *  
	 * @return
	 */
	
	@ApiComment("Add Site Message ")
	@RequestMapping(value="/add", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> addSiteMessage(@RequestBody Map<String, String> params) {
		String sendIds = Utils.toString(params.get("sendIds"));
		SiteMessage msg = new SiteMessage();
		msg.setContent( Utils.toString(params.get("content")));
		msg.setTitle( Utils.toString(params.get("title")));
		return userInfoService.saveSiteMessage(sendIds,msg);
	}
	//给后台用的
	@RequestMapping(value="/siteMessageListsB", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserSiteMessageListsB(@RequestBody Map<String, Object> params) {
		return sysSiteMsgService.getUserSiteMessageListsB(params);
	}
}

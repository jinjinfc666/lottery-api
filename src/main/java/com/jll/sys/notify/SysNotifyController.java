package com.jll.sys.notify;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.dao.PageQueryDao;
import com.jll.entity.SysNotification;
import com.jll.sys.siteMsg.SysSiteMsgService;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "sysNotify", name = "Sys Notify")
@ApiComment(seeClass = SysNotification.class)
@RestController
@RequestMapping({ "/sys/notify" })
public class SysNotifyController {
	
	@Resource
	SysNotifyService sysNotifyService;

	@ApiComment("Get Notify Lists")
	@RequestMapping(value="/lists", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> list(
			@RequestParam("userName") String userName,
			@RequestBody PageQueryDao page) {
		return sysNotifyService.getSysNotifyLists(userName, page);
	}
	
	@ApiComment("Add Notify Lists")
	@RequestMapping(value="/add", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> addSysNotify(
			@RequestParam("sendIds") String sendIds,
			@RequestBody SysNotification notify) {
		return sysNotifyService.addSysNotify(sendIds, notify);
	}
	
	
	@ApiComment("Update Notify Info")
	@RequestMapping(value="/update", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> updateSysNotify(
			@RequestBody SysNotification notify) {
		return sysNotifyService.updateSysNotify(notify);
	}
	
	@ApiComment("Set Sys Notify Expire")
	@RequestMapping(value="/cancel", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> setSysNotifyExpire(
			@RequestParam("notifyId") int notifyId) {
		return sysNotifyService.setSysNotifyExpire(notifyId);
	}
}

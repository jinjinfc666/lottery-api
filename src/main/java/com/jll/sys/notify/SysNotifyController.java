package com.jll.sys.notify;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.utils.Utils;
import com.jll.dao.PageQueryDao;
import com.jll.entity.SysNotification;
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
	public Map<String, Object> list(@RequestBody Map<String, String> params) {
		String userName = Utils.toString(params.get("userName"));
		PageQueryDao page = new PageQueryDao(Utils.toDate(params.get("startDate")),Utils.toDate(params.get("endDate")),Utils.toInteger(params.get("pageIndex")),
				Utils.toInteger(params.get("pageSize")));
		return sysNotifyService.getSysNotifyLists(userName, page);
	}
	
	@ApiComment("Add Notify Lists")
	@RequestMapping(value="/add", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> addSysNotify(@RequestBody Map<String, String> params) {
		String sendIds = Utils.toString(params.get("sendIds"));
		SysNotification notify = new SysNotification();
		notify.setContent( Utils.toString(params.get("content")));
		notify.setTitle( Utils.toString(params.get("title")));
		notify.setReceiverType( Utils.toInteger(params.get("receiverType")));
		notify.setReceiver( Utils.toInteger(params.get("receiver")));
		return sysNotifyService.saveSysNotify(sendIds, notify);
	}
	
	@ApiComment("Update Notify Info")
	@RequestMapping(value="/update", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> updateSysNotify(@RequestBody Map<String, String> params) {
		SysNotification notify = new SysNotification();
		notify.setId(Utils.toInteger(params.get("notifyId")));
		notify.setContent( Utils.toString(params.get("content")));
		notify.setTitle( Utils.toString(params.get("title")));
		return sysNotifyService.updateSysNotify(notify);
	}
	
	@ApiComment("Set Sys Notify Expire")
	@RequestMapping(value="/cancel", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> setSysNotifyExpire(@RequestBody Map<String, String> params) {
		return sysNotifyService.updateSetSysNotifyExpire( Utils.toInteger(params.get("notifyId")));
	}
	
}

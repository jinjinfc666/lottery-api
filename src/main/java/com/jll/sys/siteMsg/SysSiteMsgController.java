package com.jll.sys.siteMsg;

import java.util.HashMap;
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

import com.jll.dao.PageQueryDao;
import com.jll.entity.SiteMessage;
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
	
	
	@RequestMapping(value="/lists}", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> list(
			@RequestParam("userName") String userName,
			@RequestBody PageQueryDao page) {
		return sysSiteMsgService.getSiteMessageLists(userName,page);
	}

}

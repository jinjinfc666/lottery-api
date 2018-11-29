package com.jll.sys.log;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "loginlog", name = "登陆日志")
@ApiComment(seeClass = LogController.class)
@RestController
@RequestMapping({"/login-log"})
public class LogController {
	private Logger logger = Logger.getLogger(LogController.class);
	@Resource
	CacheRedisService cacheRedisService;
	@Resource
	SysLoginService sysLoginService;
	@RequestMapping(value={"/queryLogFrontDesk"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryLogFrontDesk(@RequestParam(name = "userName", required = false) String userName,
			@RequestParam(name = "startTime", required = true) String startTime,
			@RequestParam(name = "endTime", required = true) String endTime,
			@RequestParam(name = "pageIndex", required = true) Integer pageIndex,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
			Integer type=1;
			ret=sysLoginService.queryLoginlog(type, userName, startTime, endTime, pageIndex, pageSize);
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	@RequestMapping(value={"/queryLogBackstage"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryLogBackstage(@RequestParam(name = "userName", required = false) String userName,
			@RequestParam(name = "startTime", required = true) String startTime,
			@RequestParam(name = "endTime", required = true) String endTime,
			@RequestParam(name = "pageIndex", required = true) Integer pageIndex,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
			Integer type=2;
			ret=sysLoginService.queryLoginlog(type, userName, startTime, endTime, pageIndex, pageSize);
			return ret;
		}catch(Exception e){
			e.printStackTrace();
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	@RequestMapping(value={"/queryLogInvalidUserName"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryLogInvalidUserName(@RequestParam(name = "startTime", required = true) String startTime,
			@RequestParam(name = "endTime", required = true) String endTime,
			@RequestParam(name = "pageIndex", required = true) Integer pageIndex,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
			Integer type=3;
			ret=sysLoginService.queryLoginlog(startTime, endTime, pageIndex, pageSize);
			return ret;
		}catch(Exception e){
			e.printStackTrace();
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
}

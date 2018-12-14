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
import com.jll.common.utils.DateUtil;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "operationlog", name = "操作日志")
@ApiComment(seeClass = OperationLogController.class)
@RestController
@RequestMapping({"/operation-log"})
public class OperationLogController {
	private Logger logger = Logger.getLogger(OperationLogController.class);
	@Resource
	CacheRedisService cacheRedisService;
	@Resource
	SysLogService sysLoService;
	@RequestMapping(value={"/queryOperationLog"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryLogFrontDesk(@RequestParam(name = "userName", required = false) String userName,
			@RequestParam(name = "startTime", required = true) String startTime,
			@RequestParam(name = "endTime", required = true) String endTime,
			@RequestParam(name = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(name = "type", required = true) Integer type,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(!DateUtil.isValidDate(startTime)||!DateUtil.isValidDate(endTime)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		try {
			Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
			ret=sysLoService.queryOperationlog(type,userName, startTime, endTime, pageIndex, pageSize);
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
}

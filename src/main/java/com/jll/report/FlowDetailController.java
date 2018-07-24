package com.jll.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.report.RedPackageService;
import com.jll.common.constants.Message;
import com.jll.entity.SysCode;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.user.UserInfoService;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "report", name = "Flow Detail")
@ApiComment(seeClass = UserAccountDetails.class)
@RestController
@RequestMapping({"/record"})
public class FlowDetailController {
	private Logger logger = Logger.getLogger(FlowDetailController.class);
	@Resource
	FlowDetailService flowDetailService;
	@Resource
	UserInfoService userInfoService;
	@Resource
	RedPackageService redPackageService;
	@RequestMapping(value={"/userFlowDetail"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryUserAccountDetails(@RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "orderId", required = false) Integer orderId,
			  @RequestParam(name = "amountStart", required = false) Float amountStart,
			  @RequestParam(name = "amountEnd", required = false) Float amountEnd,
			  @RequestParam(name = "operationType", required = false) String operationType,
			  @RequestParam(name = "startTime", required = true) String startTime,
			  @RequestParam(name = "endTime", required = true) String endTime,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(userName==null&&orderId==null&&amountStart==null&&amountEnd==null&&operationType==null&&startTime==null&&endTime==null) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date startTime1 = null;
//		Date endTime1 = null;
//		try {
//	       startTime1=sdf.parse(startTime);
//	       endTime1=sdf.parse(endTime);
//		}catch(ParseException e) {
//			
//			
//		}
		ret.put("userName", userName);
		ret.put("orderId", orderId);
		ret.put("amountStart", amountStart);
		ret.put("amountEnd", amountEnd);
		ret.put("operationType", operationType);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		List<?> flowDetailRecord = flowDetailService.queryUserAccountDetails(ret);
		logger.debug(flowDetailRecord+"------------------------------queryUserAccountDetails--------------------------------------");
		ret.clear();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", flowDetailRecord);
		return ret;
	}
	@RequestMapping(value={"/userFlowDetail/type"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryType(){
		Map<String, Object> ret = new HashMap<>();
		List<SysCode> types = flowDetailService.queryType();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", types);
		return ret;
	}
	@RequestMapping(value={"/redPackage"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryRedUserAccountDetails(@RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "startTime", required = false) String startTime,
			  @RequestParam(name = "endTime", required = false) String endTime,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
//		if(userName==null&&startTime==null&&endTime==null) {
//			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//			ret.put("data", "");
//	    	return ret;
//		}
		ret.put("userName", userName);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryRedUserAccountDetails--------------------------------------");
		List<?> redPackageRecord = redPackageService.queryRedUserAccountDetails(ret);
		logger.debug(redPackageRecord+"------------------------------queryRedUserAccountDetails--------------------------------------");
		ret.clear();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", redPackageRecord);
		return ret;
	}
	
}

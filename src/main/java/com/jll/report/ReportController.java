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
import com.jll.sysSettings.codeManagement.SysCodeService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.constants.Message;
import com.jll.entity.SysCode;
import com.jll.entity.UserAccountDetails;
import com.jll.user.UserInfoService;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "report", name = "Flow Detail")
@ApiComment(seeClass = UserAccountDetails.class)
@RestController
@RequestMapping({"/record"})
public class ReportController {
	private Logger logger = Logger.getLogger(ReportController.class);
	@Resource
	FlowDetailService flowDetailService;
	@Resource
	UserInfoService userInfoService;
	@Resource
	RedPackageService redPackageService;
	@Resource
	LoyTstService loyTstService;
	@Resource
	IssueService issueService;
	@Resource
	OrderInfoService orderInfoService;
	@Resource
	SysCodeService sysCodeService;
	@Resource
	DWDetailsService dWDetailsService;
	@Resource
	DepositApplicationService depositApplicationService;
	/**
	 *流水明细
	 * @author Silence
	 */
	@RequestMapping(value={"/userFlowDetail"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryUserAccountDetails(@RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "orderNum", required = false) String orderNum,
			  @RequestParam(name = "amountStart", required = false) Float amountStart,
			  @RequestParam(name = "amountEnd", required = false) Float amountEnd,
			  @RequestParam(name = "operationType", required = false) String operationType,
			  @RequestParam(name = "startTime", required = true) String startTime,
			  @RequestParam(name = "endTime", required = true) String endTime,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(startTime.equals("")||endTime.equals("")) {
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
//		String userName1="";
//		if(userName!=null&&!userName.equals("")) {
//			boolean isUserInfo=userInfoService.isUserInfo(userName);
//			if(isUserInfo) {
//				userName1=userName;
//			}
//		}
		ret.put("userName", userName);
		ret.put("orderNum", orderNum);
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
		List<SysCode> types = sysCodeService.queryType(SysCodeTypes.FLOW_TYPES.getCode());
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", types);
		return ret;
	}
	/**
	 *红包明细
	 * @author Silence
	 */
	@RequestMapping(value={"/redPackage"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryRedUserAccountDetails(@RequestParam(name = "userName", required = true) String userName,
			  @RequestParam(name = "startTime", required = true) String startTime,
			  @RequestParam(name = "endTime", required = true) String endTime,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(userName.equals("")||startTime.equals("")||endTime.equals("")) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
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
	/**
	 *彩票交易
	 * @author Silence
	 */
	@RequestMapping(value={"/loyTstRecord"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryLoyTst(@RequestParam(name = "lotteryType", required = false) String lotteryType,//彩种 String
			  @RequestParam(name = "isZh", required = false) Integer isZh,//是否追号 :0,非追号;1,追号
			  @RequestParam(name = "state", required = false) Integer state,//0,等待派奖;1,赢;2,输;3,用户取消订单;4,系统取消订单
			  @RequestParam(name = "terminalType", required = false) Integer terminalType,//来源   0,pc端;1,手机端
			  @RequestParam(name = "startTime", required = true) String startTime,//时间 String
			  @RequestParam(name = "endTime", required = true) String endTime,//时间 String
			  @RequestParam(name = "issueNum", required = false) String issueNum,//期号  String
			  @RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "orderNum", required = false) String orderNum,//订单号 String
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(startTime.equals("")||endTime.equals("")) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
//		String issueNum1="";
//		String userName1="";
//		String orderNum1="";
		ret.put("lotteryType", lotteryType);
		ret.put("isZh", isZh);
		ret.put("state", state);
		ret.put("terminalType", terminalType);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
//		if(issueNum!=null&&!issueNum.equals("")) {
//			boolean isIssue=issueService.isIssue(issueNum);
//			if(isIssue) {
//				issueNum1=issueNum;
//			}
//		}
//		if(userName!=null&&!userName.equals("")) {
//			boolean isUserInfo=userInfoService.isUserInfo(userName);
//			if(isUserInfo) {
//				userName1=userName;
//			}
//		}
//		if(orderNum!=null&&!orderNum.equals("")) {
//			boolean isOrderInfo=orderInfoService.isOrderInfo(orderNum);
//			if(isOrderInfo) {
//				orderNum1=orderNum;
//			}
//		}
		ret.put("issueNum", issueNum);   
		ret.put("userName", userName);
		ret.put("orderNum", orderNum);
		logger.debug(ret+"------------------------------queryLoyTst--------------------------------------");
		List<?> list = loyTstService.queryLoyTst(ret);
		logger.debug(list+"------------------------------queryLoyTst--------------------------------------");
		ret.clear();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", list);
		return ret;
	}
	//查询条件:彩种
	@RequestMapping(value={"/lotteTypes"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryLoyTstQueryConditions() {
		Map<String, Object> ret = new HashMap<>();
		List<SysCode> types = sysCodeService.queryType(SysCodeTypes.LOTTERY_TYPES.getCode());
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", types);
		return ret;
	}
	//查询条件:是否追号
	@RequestMapping(value={"/loyTstIsZh"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryLoyTstIsZh() {
		Map<String, Object> ret = new HashMap<>();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", Constants.IsZh.getIsZhByCode());
		return ret;
	}
	//查询条件:中奖情况
	@RequestMapping(value={"/LoyTstState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryLoyTstState() {
		Map<String, Object> ret = new HashMap<>();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", Constants.State.getStateByCode());
		return ret;
	}
	//查询条件:订单来源
	@RequestMapping(value={"/LoyTstTerminalType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryLoyTstTerminalType() {
		Map<String, Object> ret = new HashMap<>();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", Constants.TerminalType.getTerminalTypeByCode());
		return ret;
	}
	/**
	 *存取款明细
	 * @author Silence
	 */
	@RequestMapping(value={"/DWD"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryDWD(@RequestParam(name = "type", required = true) String type,//存款:1 取款:2
			  @RequestParam(name = "state", required = false) Integer state,
			  @RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "orderNum", required = false) String orderNum,//订单号 String
			  @RequestParam(name = "amountStart", required = false) Float amountStart,
			  @RequestParam(name = "amountEnd", required = false) Float amountEnd,
			  @RequestParam(name = "startTime", required = true) String startTime,//时间 String
			  @RequestParam(name = "endTime", required = true) String endTime,//时间 String
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(startTime.equals("")||endTime.equals("")||type.equals("")) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("type", type);
		ret.put("state", state);
		ret.put("userName", userName);
		ret.put("orderNum", orderNum);
		ret.put("amountStart", amountStart);
		ret.put("amountEnd", amountEnd);
		ret.put("startTime", startTime);   
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryDWD--------------------------------------");
		List<?> list = dWDetailsService.queryDetails(ret);
		logger.debug(list+"------------------------------queryDWD--------------------------------------");
		ret.clear();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", list);
		return ret;
	}
	//查询条件:存取类型
	@RequestMapping(value={"/DWType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryDWDType() {
		Map<String, Object> ret = new HashMap<>();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", Constants.DWType.getDWTypeByCode());
		return ret;
	}
	//查询条件:存取类型
	@RequestMapping(value={"/DWDState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryDWDState() {
		Map<String, Object> ret = new HashMap<>();
		Map<String, Object> ret1 = new HashMap<>();
		ret1.put("存款状态", Constants.DepositType.getDepositTypeByCode());
		ret1.put("取款状态", Constants.WithdrawType.getWithdrawTypeByCode());
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", ret1);
		return ret;
	}
	//存取款明细的详细信息
	@RequestMapping(value={"/DWDetails"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryDWDetails(@RequestParam(name = "type", required = true) String type,//存款:1 取款:2
			  @RequestParam(name = "id", required = true) Integer id,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(type.equals("")||id==null) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("type", type);
		ret.put("id", id);
		logger.debug(ret+"------------------------------queryDWD--------------------------------------");
		List<?> list = dWDetailsService.queryDWDetails(ret);
		logger.debug(list+"------------------------------queryDWD--------------------------------------");
		ret.clear();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", list);
		return ret;
	}
	/**
	 *充值明细
	 * @author Silence
	 */
	@RequestMapping(value={"/DepositApplication"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryDepositApplication(@RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "orderNum", required = false) String orderNum,//订单号 String
			  @RequestParam(name = "startTime", required = true) String startTime,//时间 String
			  @RequestParam(name = "endTime", required = true) String endTime,//时间 String
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(startTime.equals("")||endTime.equals("")) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("userName", userName);
		ret.put("orderNum", orderNum);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryDWD--------------------------------------");
		List<?> list = depositApplicationService.queryDetails(ret);
		logger.debug(list+"------------------------------queryDWD--------------------------------------");
		ret.clear();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", list);
		return ret;
	}
	//修改存款状态
	@RequestMapping(value={"/UpdateDepositState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> UpdateDepositState(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "state", required = true) Integer state,//状态
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(id==null||state==null) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("id", id);
		ret.put("state", state);
		logger.debug(ret+"------------------------------queryDWD--------------------------------------");
		depositApplicationService.updateState(ret);
//		logger.debug(list+"------------------------------queryDWD--------------------------------------");
		ret.clear();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//		ret.put("data", list);
		return ret;
	}
	
	
}

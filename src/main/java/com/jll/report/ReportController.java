package com.jll.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.report.RedPackageService;
import com.jll.sysSettings.codeManagement.SysCodeService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.constants.Message;
import com.jll.entity.LotteryPlReport;
import com.jll.entity.MemberPlReport;
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
	SysCodeService sysCodeService;
	@Resource
	DWDetailsService dWDetailsService;
	@Resource
	DepositApplicationService depositApplicationService;
	@Resource
	MReportService mReportService;
	@Resource
	LReportService lReportService;
	@Resource
	OrderSourceService orderSourceService;
	@Resource
	PPLService pPLService;
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
		Map<String, Object> map = new HashMap<>();
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("userName", userName);
		ret.put("orderNum", orderNum);
		ret.put("amountStart", amountStart);
		ret.put("amountEnd", amountEnd);
		ret.put("operationType", operationType);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		try {
			map= flowDetailService.queryUserAccountDetails(ret);
			logger.debug(map+"------------------------------queryUserAccountDetails--------------------------------------");
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", map);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	@RequestMapping(value={"/userFlowDetail/type"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryType(){
		Map<String, Object> ret = new HashMap<>();
		try {
			List<SysCode> types = sysCodeService.queryType(SysCodeTypes.FLOW_TYPES.getCode());
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", types);
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	/**
	 *红包明细
	 * @author Silence
	 */
//	@RequestMapping(value={"/redPackage"}, method={RequestMethod.POST}, produces={"application/json"})
//	public Map<String, Object> queryRedUserAccountDetails(@RequestParam(name = "userName", required = true) String userName,
//			  @RequestParam(name = "startTime", required = true) String startTime,
//			  @RequestParam(name = "endTime", required = true) String endTime,
//			  HttpServletRequest request) {
//		Map<String, Object> ret = new HashMap<>();
//		if(StringUtils.isBlank(userName)||StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)) {
//			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
//			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
//			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
//	    	return ret;
//		}
//		ret.put("userName", userName);
//		ret.put("startTime", startTime);
//		ret.put("endTime", endTime);
//		logger.debug(ret+"------------------------------queryRedUserAccountDetails--------------------------------------");
//		List<?> redPackageRecord = redPackageService.queryRedUserAccountDetails(ret);
//		logger.debug(redPackageRecord+"------------------------------queryRedUserAccountDetails--------------------------------------");
//		ret.clear();
//		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//		ret.put("data", redPackageRecord);
//		return ret;
//	}
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
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("lotteryType", lotteryType);
		ret.put("isZh", isZh);
		ret.put("state", state);
		ret.put("terminalType", terminalType);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		ret.put("issueNum", issueNum);   
		ret.put("userName", userName);
		ret.put("orderNum", orderNum);
		logger.debug(ret+"------------------------------queryLoyTst--------------------------------------");
		try {
			List<?> list = loyTstService.queryLoyTst(ret);
			logger.debug(list+"------------------------------queryLoyTst--------------------------------------");
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", list);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询条件:彩种
	@RequestMapping(value={"/loyTstRecord/lotteTypes"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryLoyTstQueryConditions() {
		Map<String, Object> ret = new HashMap<>();
		try {
			List<SysCode> types = sysCodeService.queryType(SysCodeTypes.LOTTERY_TYPES.getCode());
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", types);
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询条件:是否追号
	@RequestMapping(value={"/loyTstRecord/loyTstIsZh"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryLoyTstIsZh() {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", Constants.IsZh.getIsZhByCode());
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询条件:中奖情况
	@RequestMapping(value={"/loyTstRecord/LoyTstState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryLoyTstState() {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", Constants.State.getStateByCode());
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询条件:订单来源
	@RequestMapping(value={"/loyTstRecord/LoyTstTerminalType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryLoyTstTerminalType() {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", Constants.TerminalType.getTerminalTypeByCode());
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
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
		Map<String, Object> map = new HashMap<>();
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)||StringUtils.isBlank(type)) {
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
		try {
			map = dWDetailsService.queryDetails(ret);
			logger.debug(map+"------------------------------queryDWD--------------------------------------");
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", map);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	
	//查询条件:存取类型
	@RequestMapping(value={"/DWD/DWType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryDWDType() {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", Constants.DWType.getDWTypeByCode());
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询条件:存取类型
	@RequestMapping(value={"/DWD/DWDState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryDWDState() {
		Map<String, Object> ret = new HashMap<>();
		Map<String, Object> ret1 = new HashMap<>();
		try {
			ret1.put("存款状态", Constants.DepositType.getDepositTypeByCode());
			ret1.put("取款状态", Constants.WithdrawType.getWithdrawTypeByCode());
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", ret1);
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//存取款明细的详细信息
	@RequestMapping(value={"/DWD/DWDetails"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryDWDetails(@RequestParam(name = "type", required = true) String type,//存款:1 取款:2
			  @RequestParam(name = "id", required = true) Integer id,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(type)||id==null) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("type", type);
		ret.put("id", id);
		logger.debug(ret+"------------------------------queryDWDetails--------------------------------------");
		try {
			List<?> list = dWDetailsService.queryDWDetails(ret);
			logger.debug(list+"------------------------------queryDWDetails--------------------------------------");
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", list);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
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
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("userName", userName);
		ret.put("orderNum", orderNum);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryDepositApplication--------------------------------------");
		try {
			List<?> list = depositApplicationService.queryDetails(ret);
			logger.debug(list+"------------------------------queryDepositApplication--------------------------------------");
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", list);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//修改存款状态
	@RequestMapping(value={"/DepositApplication/UpdateDepositState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> UpdateDepositState(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "state", required = true) Integer state,//状态
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(id==null||state==null||state!=1||state!=2) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("id", id);
		ret.put("state", state);
		logger.debug(ret+"------------------------------UpdateDepositState--------------------------------------");
		try {
			depositApplicationService.updateState(ret);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	/**
	 *会员盈亏报表
	 * @author Silence
	 */
	@RequestMapping(value={"/MReport"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryMReport(@RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "startTime", required = true) String startTime,//时间 String
			  @RequestParam(name = "endTime", required = true) String endTime,//时间 String
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("userName", userName);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryMReport--------------------------------------");
		try {
			List<MemberPlReport> list = mReportService.queryAll(ret);
			logger.debug(list+"------------------------------queryMReport--------------------------------------");
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", list);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//总计
	@RequestMapping(value={"/MReportSum"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryMReportSum(@RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "startTime", required = true) String startTime,//时间 String
			  @RequestParam(name = "endTime", required = true) String endTime,//时间 String
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		UserInfo userinfo=new UserInfo();
		userinfo.setUserName(userName);
		if(!userInfoService.isUserExisting(userinfo)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("userName", userName);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryMReportSum--------------------------------------");
		Map<String,Object> list = null;
		try {
			list= mReportService.querySum(ret);
			logger.debug(list+"------------------------------queryMReportSum--------------------------------------");
//			ret.clear();
			list.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//			ret.put("data", list);
			return list;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	/**
	 *团队盈亏报表
	 * @author Silence
	 */
	@RequestMapping(value={"/MReportTeam"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryMReportTeam(@RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "startTime", required = true) String startTime,//时间 String
			  @RequestParam(name = "endTime", required = true) String endTime,//时间 String
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("userName", userName);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryMReportTeam--------------------------------------");
		try {
			List<MemberPlReport> list = mReportService.queryTeamAll(ret);
			logger.debug(list+"------------------------------queryMReportTeam--------------------------------------");
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", list);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//总计
	@RequestMapping(value={"/MReportSumTeam"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryMReportSumTeam(@RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "startTime", required = true) String startTime,//时间 String
			  @RequestParam(name = "endTime", required = true) String endTime,//时间 String
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		UserInfo userinfo=new UserInfo();
		userinfo.setUserName(userName);
		if(!userInfoService.isUserExisting(userinfo)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("userName", userName);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryMReportSumTeam--------------------------------------");
		Map<String,Object> list = null;
		try {
			list= mReportService.querySumTeam(ret);
			logger.debug(list+"------------------------------queryMReportSumTeam--------------------------------------");
			list.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return list;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//查找下级
	@RequestMapping(value={"/MReportNextTeam"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryMReportNextTeam(@RequestParam(name = "userName", required = true) String userName,
			  @RequestParam(name = "startTime", required = true) String startTime,//时间 String
			  @RequestParam(name = "endTime", required = true) String endTime,//时间 String
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)||StringUtils.isBlank(userName)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		UserInfo userinfo=new UserInfo();
		userinfo.setUserName(userName);
		if(!userInfoService.isUserExisting(userinfo)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("userName", userName);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryMReportTeam--------------------------------------");
		try {
			Map<String,Object> list = mReportService.queryNextTeamAll(ret);
			logger.debug(list+"------------------------------queryMReportTeam--------------------------------------");
//			ret.clear();
			list.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//			ret.put("data", list);
			return list;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	/**
	 *团队盈亏报表(按彩种查询)
	 * @author Silence
	 */
	@RequestMapping(value={"/LReportTeam"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryLReportTeam(@RequestParam(name = "userName", required = false) String userName,
			@RequestParam(name = "codeName", required = true) String codeName,//时间 String
			@RequestParam(name = "startTime", required = true) String startTime,//时间 String
			@RequestParam(name = "endTime", required = true) String endTime,//时间 String
			HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)||StringUtils.isBlank(codeName)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("codeName", codeName);
		ret.put("userName", userName);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryLReportTeam--------------------------------------");
		try {
			List<LotteryPlReport> list = lReportService.queryLReport(ret);
			logger.debug(list+"------------------------------queryLReportTeam--------------------------------------");
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", list);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//总计
	@RequestMapping(value={"/LReportTeamSum"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryLReportTeamSum(@RequestParam(name = "userName", required = false) String userName,
			@RequestParam(name = "codeName", required = true) String codeName,//时间 String
			@RequestParam(name = "startTime", required = true) String startTime,//时间 String
			@RequestParam(name = "endTime", required = true) String endTime,//时间 String
			HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)||StringUtils.isBlank(codeName)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		UserInfo userinfo=new UserInfo();
		userinfo.setUserName(userName);
		if(!userInfoService.isUserExisting(userinfo)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("codeName", codeName);
		ret.put("userName", userName);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryLReportTeamSum--------------------------------------");
		try {
			Map<String,Object> list = lReportService.queryLReportSum(ret);
			logger.debug(list+"------------------------------queryLReportTeamSum--------------------------------------");
//			ret.clear();
			list.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//			ret.put("data", list);
			return list;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//查找下级---------------------------------------------
	@RequestMapping(value={"/LReportNextTeam"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryLReportNextTeam(@RequestParam(name = "userName", required = false) String userName,
			@RequestParam(name = "codeName", required = true) String codeName,//时间 String
			@RequestParam(name = "startTime", required = true) String startTime,//时间 String
			@RequestParam(name = "endTime", required = true) String endTime,//时间 String
			HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)||StringUtils.isBlank(codeName)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		UserInfo userinfo=new UserInfo();
		userinfo.setUserName(userName);
		if(!userInfoService.isUserExisting(userinfo)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("codeName", codeName);
		ret.put("userName", userName);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryLReportTeam--------------------------------------");
		try {
			Map<String,Object> list = lReportService.queryLReportNext(ret);
			logger.debug(list+"------------------------------queryLReportTeam--------------------------------------");
//			ret.clear();
			list.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//			ret.put("data", list);
			return list;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	/**
	 *订单数据来源
	 * @author Silence
	 */
	@RequestMapping(value={"/OrderSource"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryOrderSource(@RequestParam(name = "codeName", required = false) String codeName,//时间 String
			@RequestParam(name = "startTime", required = true) String startTime,//时间 String
			@RequestParam(name = "endTime", required = true) String endTime,//时间 String
			HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("codeName", codeName);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryOrderSource--------------------------------------");
		try {
			Map<String,Object> list = orderSourceService.queryOrderSource(ret);
			logger.debug(list+"------------------------------queryOrderSource--------------------------------------");
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("sumData", list);
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	/**
	 *平台盈亏 资金汇总
	 * @author Silence
	 */
	@RequestMapping(value={"/PPL"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> queryPPL(@RequestParam(name = "codeName", required = false) String codeName,//时间 String
			@RequestParam(name = "issueNum", required = false) String issueNum,//时间 String
			@RequestParam(name = "playTypeid", required = false) String playTypeid,//时间 String
			@RequestParam(name = "startTime", required = true) String startTime,//时间 String
			@RequestParam(name = "endTime", required = true) String endTime,//时间 String
			HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		if(!StringUtils.isBlank(issueNum)&&StringUtils.isBlank(codeName)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		if(!StringUtils.isBlank(playTypeid)&&StringUtils.isBlank(codeName)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("codeName", codeName);
		ret.put("issueNum", issueNum);
		ret.put("playTypeid", playTypeid);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		logger.debug(ret+"------------------------------queryOrderSource--------------------------------------");
		try {
			List<?> list = pPLService.queryPPL(ret);
			logger.debug(list+"------------------------------queryOrderSource--------------------------------------");
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", list);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
}

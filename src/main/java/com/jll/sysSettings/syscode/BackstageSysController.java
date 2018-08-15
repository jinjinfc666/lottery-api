package com.jll.sysSettings.syscode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.entity.SysCode;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;


@Api2Doc(id = "sysSettings", name = "Settings code")
@ApiComment(seeClass = SysCode.class)
@RestController
@RequestMapping({"/settings"})
public class BackstageSysController {
	private Logger logger = Logger.getLogger(BackstageSysController.class);
	@Resource
	SysCodeService sysCodeService;
	@Resource
	CacheRedisService cacheRedisService;
	/**
	 *大类
	 * @author Silence 
	 */
	//增加大类
	@RequestMapping(value={"/addLotteryType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addLotteryType(@RequestParam(name = "codeName", required = true) String codeName,
			  @RequestParam(name = "codeVal", required = true) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		SysCode sysCode = new SysCode();
		sysCode.setCodeName(codeName);
		sysCode.setCodeVal(codeVal);
		sysCode.setState(Constants.SysCodeState.VALID_STATE.getCode());
		sysCode.setIsCodeType(1);
		if(!StringUtils.isBlank(remark)) {
			sysCode.setRemark(remark);
		}
		
		try {
			ret.clear();
			ret=sysCodeService.addSysCode(codeName,sysCode);
			List<SysCode> list=sysCodeService.queryByCodeNameBigType(codeName);
			SysCode sysCode1=null;
			if(list!=null&&list.size()>0) {
				sysCode1=list.get(0);
			}
			//存储到缓存
			cacheRedisService.setSysCode(codeName, sysCode1);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询所有的类型
	@RequestMapping(value={"/bigType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> quertBigType() {
		Map<String, Object> ret = new HashMap<>();
		try {
			List<SysCode> sysCode=sysCodeService.quertBigType();
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", sysCode);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//修改大类的值
	@RequestMapping(value={"/updateBigType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateBigType(@RequestParam(name = "id", required = false) Integer id,
			  @RequestParam(name = "codeName", required = true) String codeName,//和id一样不能修改只需要传过来
			  @RequestParam(name = "codeVal", required = false) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(codeVal)&&StringUtils.isBlank(remark)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("id", id);
		ret.put("codeVal", codeVal);
		ret.put("remark", remark);
		try {
			sysCodeService.updateBigType(ret);
			//存储到缓存
			SysCode sysCode1=sysCodeService.queryById(id);
			cacheRedisService.setSysCode(codeName, sysCode1);
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
	//修改大类的状态
	@RequestMapping(value={"/updateBigTypeState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateBigTypeState(@RequestParam(name = "id", required = true) Integer id,
			@RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			sysCodeService.updateBigTypeState(id,state);		
			SysCode sysCode=sysCodeService.queryById(id);
			// //TODO 
			cacheRedisService.setSysCode(codeName, sysCode);
			List<SysCode> list=sysCodeService.queryAllSmallType(codeName);
			if(list!=null&&list.size()>0) {
				Iterator<SysCode> it=list.iterator();
				while(it.hasNext()) {
					SysCode sysCode1=it.next();
					cacheRedisService.setSysCode(codeName, sysCode1);
				}
			}
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
	 *彩种类型的增删改查
	 * @author Silence 
	 */
	//增加彩种小类
	@RequestMapping(value={"/SmallLotteryType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSmallLotteryType(@RequestParam(name = "codeName", required = true) String codeName,
			  @RequestParam(name = "codeVal", required = true) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		SysCode sysCode = new SysCode();
		sysCode.setCodeName(codeName);
		sysCode.setCodeVal(codeVal);
		sysCode.setState(Constants.SysCodeState.VALID_STATE.getCode());
		sysCode.setIsCodeType(0);
		if(!StringUtils.isBlank(remark)) {
			sysCode.setRemark(remark);
		}
		String bigCodeName=Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
		try {
			Integer codeType=sysCodeService.queryByCodeName(bigCodeName);
			sysCode.setCodeType(codeType);	
			ret.clear();
			ret=sysCodeService.addSmallSysCode(bigCodeName,sysCode);
			//存储到缓存
			List<SysCode> sysCode1=sysCodeService.querySmallType(bigCodeName, codeName);
			SysCode sysCode2=sysCode1.get(0);
			cacheRedisService.setSysCode(bigCodeName, sysCode2);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询彩种类型下的所有值
	@RequestMapping(value={"/querySmallLotteryType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> querySmallLotteryType() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
		try {
			List<SysCode> sysCode=sysCodeService.queryAllSmallType(bigCodeName);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", sysCode);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除彩种类型下的某个小类
	@RequestMapping(value={"/updateSmallLotteryTypeState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallLotteryTypeState(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			SysCode sysCode=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(bigCodeName, sysCode);
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
	//修改彩种类型下的某一条数据
	@RequestMapping(value={"/updateSmallLotteryType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallLotteryType(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeVal", required = false) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(codeVal)&&StringUtils.isBlank(remark)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		String bigCodeName=Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
		ret.put("id", id);
		ret.put("codeVal", codeVal);
		ret.put("remark", remark);
		try {
			sysCodeService.updateSmallType(ret);
			SysCode sysCode2=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(bigCodeName, sysCode2);
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
	 *流水类型的增删改查
	 * @author Silence 
	 */
	//增加流水小类
	@RequestMapping(value={"/SmallFlowType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSmallFlowType(@RequestParam(name = "codeName", required = true) String codeName,
			  @RequestParam(name = "codeVal", required = true) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		SysCode sysCode = new SysCode();
		sysCode.setCodeName(codeName);
		sysCode.setCodeVal(codeVal);
		sysCode.setState(Constants.SysCodeState.VALID_STATE.getCode());
		sysCode.setIsCodeType(0);
		if(!StringUtils.isBlank(remark)) {
			sysCode.setRemark(remark);
		}
		String bigCodeName=Constants.SysCodeTypes.FLOW_TYPES.getCode();
		try {
			Integer codeType=sysCodeService.queryByCodeName(bigCodeName);
			sysCode.setCodeType(codeType);	
			ret.clear();
			ret=sysCodeService.addSmallSysCode(bigCodeName,sysCode);
			//存储到缓存
			List<SysCode> sysCode1=sysCodeService.querySmallType(bigCodeName, codeName);
			SysCode sysCode2=sysCode1.get(0);
			cacheRedisService.setSysCode(bigCodeName, sysCode2);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询流水类型下的所有值
	@RequestMapping(value={"/querySmallFlowType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> querySmallFlowType() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.FLOW_TYPES.getCode();
		try {
			List<SysCode> sysCode=sysCodeService.queryAllSmallType(bigCodeName);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", sysCode);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除流水类型下的某个小类
	@RequestMapping(value={"/updateSmallFlowTypeState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallFlowTypeState(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.FLOW_TYPES.getCode();
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			SysCode sysCode=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(bigCodeName, sysCode);
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
	//修改流水类型下的某一条数据
	@RequestMapping(value={"/updateSmallFlowType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallFlowType(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeVal", required = false) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(codeVal)&&StringUtils.isBlank(remark)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		String bigCodeName=Constants.SysCodeTypes.FLOW_TYPES.getCode();
		ret.put("id", id);
		ret.put("codeVal", codeVal);
		ret.put("remark", remark);
		try {
			sysCodeService.updateSmallType(ret);
			SysCode sysCode2=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(bigCodeName, sysCode2);
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
	 *幸运抽奖类型的增删改查
	 * @author Silence 
	 */
	//增加幸运抽奖小类
	@RequestMapping(value={"/SmallLuckyDraw"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSmallLuckyDraw(@RequestParam(name = "codeName", required = true) String codeName,
			  @RequestParam(name = "codeVal", required = true) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		SysCode sysCode = new SysCode();
		sysCode.setCodeName(codeName);
		sysCode.setCodeVal(codeVal);
		sysCode.setState(Constants.SysCodeState.VALID_STATE.getCode());
		sysCode.setIsCodeType(0);
		if(!StringUtils.isBlank(remark)) {
			sysCode.setRemark(remark);
		}
		String bigCodeName=Constants.SysCodeTypes.PAYMENT_PLATFORM.getCode();
		try {
			Integer codeType=sysCodeService.queryByCodeName(bigCodeName);
			sysCode.setCodeType(codeType);	
			ret.clear();
			ret=sysCodeService.addSmallSysCode(bigCodeName,sysCode);
			//存储到缓存
			List<SysCode> sysCode1=sysCodeService.querySmallType(bigCodeName, codeName);
			SysCode sysCode2=sysCode1.get(0);
			cacheRedisService.setSysCode(bigCodeName, sysCode2);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询幸运抽奖类型下的所有值
	@RequestMapping(value={"/querySmallLuckyDraw"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> querySmallLuckyDraw() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.PAYMENT_PLATFORM.getCode();
		try {
			List<SysCode> sysCode=sysCodeService.queryAllSmallType(bigCodeName);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", sysCode);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除幸运抽奖类型下的某个小类
	@RequestMapping(value={"/updateSmallLuckyDrawState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallLuckyDrawState(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.PAYMENT_PLATFORM.getCode();
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			SysCode sysCode=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(bigCodeName, sysCode);
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
	//修改幸运抽奖类型下的某一条数据
	@RequestMapping(value={"/updateSmallLuckyDraw"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallLuckyDraw(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeVal", required = false) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(codeVal)&&StringUtils.isBlank(remark)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		String bigCodeName=Constants.SysCodeTypes.PAYMENT_PLATFORM.getCode();
		ret.put("id", id);
		ret.put("codeVal", codeVal);
		ret.put("remark", remark);
		try {
			sysCodeService.updateSmallType(ret);
			SysCode sysCode2=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(bigCodeName, sysCode2);
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
	 *支付平台类型的增删改查
	 * @author Silence 
	 */
	//增加支付平台小类
	@RequestMapping(value={"/SmallPaymentPlatform"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSmallPaymentPlatform(@RequestParam(name = "codeName", required = true) String codeName,
			  @RequestParam(name = "codeVal", required = true) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		SysCode sysCode = new SysCode();
		sysCode.setCodeName(codeName);
		sysCode.setCodeVal(codeVal);
		sysCode.setState(Constants.SysCodeState.VALID_STATE.getCode());
		sysCode.setIsCodeType(0);
		if(!StringUtils.isBlank(remark)) {
			sysCode.setRemark(remark);
		}
		String bigCodeName=Constants.SysCodeTypes.LUCKY_DRAW.getCode();
		try {
			Integer codeType=sysCodeService.queryByCodeName(bigCodeName);
			sysCode.setCodeType(codeType);	
			ret.clear();
			ret=sysCodeService.addSmallSysCode(bigCodeName,sysCode);
			//存储到缓存
			List<SysCode> sysCode1=sysCodeService.querySmallType(bigCodeName, codeName);
			SysCode sysCode2=sysCode1.get(0);
			cacheRedisService.setSysCode(bigCodeName, sysCode2);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询支付平台类型下的所有值
	@RequestMapping(value={"/querySmallPaymentPlatform"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> querySmallPaymentPlatform() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.LUCKY_DRAW.getCode();
		try {
			List<SysCode> sysCode=sysCodeService.queryAllSmallType(bigCodeName);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", sysCode);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除支付平台类型下的某个小类
	@RequestMapping(value={"/updateSmallPaymentPlatformState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallPaymentPlatformState(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.LUCKY_DRAW.getCode();
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			SysCode sysCode=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(bigCodeName, sysCode);
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
	//修改支付平台类型下的某一条数据
	@RequestMapping(value={"/updateSmallPaymentPlatform"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallPaymentPlatform(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeVal", required = false) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(codeVal)&&StringUtils.isBlank(remark)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		String bigCodeName=Constants.SysCodeTypes.LUCKY_DRAW.getCode();
		ret.put("id", id);
		ret.put("codeVal", codeVal);
		ret.put("remark", remark);
		try {
			sysCodeService.updateSmallType(ret);
			SysCode sysCode2=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(bigCodeName, sysCode2);
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
	 *签到活动类型的增删改查
	 * @author Silence 
	 */
	//增加签到活动小类
	@RequestMapping(value={"/SmallSignInDay"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSmallSignInDay(@RequestParam(name = "codeName", required = true) String codeName,
			  @RequestParam(name = "codeVal", required = true) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		SysCode sysCode = new SysCode();
		sysCode.setCodeName(codeName);
		sysCode.setCodeVal(codeVal);
		sysCode.setState(Constants.SysCodeState.VALID_STATE.getCode());
		sysCode.setIsCodeType(0);
		if(!StringUtils.isBlank(remark)) {
			sysCode.setRemark(remark);
		}
		String bigCodeName=Constants.SysCodeTypes.SIGN_IN_DAY.getCode();
		try {
			Integer codeType=sysCodeService.queryByCodeName(bigCodeName);
			sysCode.setCodeType(codeType);	
			ret.clear();
			ret=sysCodeService.addSmallSysCode(bigCodeName,sysCode);
			//存储到缓存
			List<SysCode> sysCode1=sysCodeService.querySmallType(bigCodeName, codeName);
			SysCode sysCode2=sysCode1.get(0);
			cacheRedisService.setSysCode(bigCodeName, sysCode2);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询签到活动类型下的所有值
	@RequestMapping(value={"/querySmallSignInDay"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> querySmallSignInDay() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.SIGN_IN_DAY.getCode();
		try {
			List<SysCode> sysCode=sysCodeService.queryAllSmallType(bigCodeName);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", sysCode);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除签到活动类型下的某个小类
	@RequestMapping(value={"/updateSmallSignInDayState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallSignInDayState(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.SIGN_IN_DAY.getCode();
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			SysCode sysCode=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(bigCodeName, sysCode);
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
	//修改签到活动类型下的某一条数据
	@RequestMapping(value={"/updateSmallSignInDay"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallSignInDay(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeVal", required = false) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(codeVal)&&StringUtils.isBlank(remark)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		String bigCodeName=Constants.SysCodeTypes.SIGN_IN_DAY.getCode();
		ret.put("id", id);
		ret.put("codeVal", codeVal);
		ret.put("remark", remark);
		try {
			sysCodeService.updateSmallType(ret);
			SysCode sysCode2=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(bigCodeName, sysCode2);
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
	 *玩法类型的增删改查
	 * @author Silence 
	 */
	//增加玩法类型小类
	@RequestMapping(value={"/SmallPTypeClassicfication"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSmallPTypeClassicfication(@RequestParam(name = "codeName", required = true) String codeName,
			  @RequestParam(name = "codeVal", required = true) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		SysCode sysCode = new SysCode();
		sysCode.setCodeName(codeName);
		sysCode.setCodeVal(codeVal);
		sysCode.setState(Constants.SysCodeState.VALID_STATE.getCode());
		sysCode.setIsCodeType(0);
		if(!StringUtils.isBlank(remark)) {
			sysCode.setRemark(remark);
		}
		String bigCodeName=Constants.SysCodeTypes.CT_PLAY_TYPE_CLASSICFICATION.getCode();
		try {
			Integer codeType=sysCodeService.queryByCodeName(bigCodeName);
			sysCode.setCodeType(codeType);	
			ret.clear();
			ret=sysCodeService.addSmallSysCode(bigCodeName,sysCode);
			//存储到缓存
			List<SysCode> sysCode1=sysCodeService.querySmallType(bigCodeName, codeName);
			SysCode sysCode2=sysCode1.get(0);
			cacheRedisService.setSysCode(bigCodeName, sysCode2);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询玩法类型下的所有值
	@RequestMapping(value={"/querySmallPTypeClassicfication"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> querySmallPTypeClassicfication() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.CT_PLAY_TYPE_CLASSICFICATION.getCode();
		try {
			List<SysCode> sysCode=sysCodeService.queryAllSmallType(bigCodeName);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", sysCode);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除玩法类型下的某个小类
	@RequestMapping(value={"/updateSmallPTypeClassicficationState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallPTypeClassicficationState(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.CT_PLAY_TYPE_CLASSICFICATION.getCode();
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			SysCode sysCode=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(bigCodeName, sysCode);
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
	//修改玩法类型下的某一条数据
	@RequestMapping(value={"/updateSmallPTypeClassicfication"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallPTypeClassicfication(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeVal", required = false) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(codeVal)&&StringUtils.isBlank(remark)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		String bigCodeName=Constants.SysCodeTypes.CT_PLAY_TYPE_CLASSICFICATION.getCode();
		ret.put("id", id);
		ret.put("codeVal", codeVal);
		ret.put("remark", remark);
		try {
			sysCodeService.updateSmallType(ret);
			SysCode sysCode2=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(bigCodeName, sysCode2);
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
	 *彩种属性类型的增删改查
	 * @author Silence 
	 */
	//增加玩法类型小类
	@RequestMapping(value={"/SmallLotteryConfig"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSmallLotteryConfig(@RequestParam(name = "bigcodeName", required = true) String bigcodeName,
			  @RequestParam(name = "codeName", required = true) String codeName,
			  @RequestParam(name = "codeVal", required = true) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		SysCode sysCode = new SysCode();
		sysCode.setCodeName(codeName);
		sysCode.setCodeVal(codeVal);
		sysCode.setState(Constants.SysCodeState.VALID_STATE.getCode());
		sysCode.setIsCodeType(0);
		if(!StringUtils.isBlank(remark)) {
			sysCode.setRemark(remark);
		}
		String lotteryConfigCodeName=bigcodeName;
		try {
			Integer codeType=sysCodeService.queryByCodeName(lotteryConfigCodeName);
			sysCode.setCodeType(codeType);	
			ret.clear();
			ret=sysCodeService.addSmallSysCode(lotteryConfigCodeName,sysCode);
			//存储到缓存
			List<SysCode> sysCode1=sysCodeService.querySmallType(lotteryConfigCodeName, codeName);
			SysCode sysCode2=sysCode1.get(0);
			cacheRedisService.setSysCode(lotteryConfigCodeName, sysCode2);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询玩法类型下的所有值
	@RequestMapping(value={"/querySmallLotteryConfig"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> querySmallLotteryConfig(@RequestParam(name = "bigcodeName", required = true) String bigcodeName,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String lotteryConfigCodeName=bigcodeName;
		try {
			List<SysCode> sysCode=sysCodeService.queryAllSmallType(lotteryConfigCodeName);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", sysCode);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除玩法类型下的某个小类
	@RequestMapping(value={"/updateSmallLotteryConfigState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallLotteryConfigState(@RequestParam(name = "bigcodeName", required = true) String bigcodeName,
			  @RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String lotteryConfigCodeName=bigcodeName;
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			SysCode sysCode=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(lotteryConfigCodeName, sysCode);
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
	//修改玩法类型下的某一条数据
	@RequestMapping(value={"/updateSmallLotteryConfig"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSmallLotteryConfig(@RequestParam(name = "bigcodeName", required = true) String bigcodeName,
			  @RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeVal", required = false) String codeVal,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(codeVal)&&StringUtils.isBlank(remark)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		String lotteryConfigCodeName=bigcodeName;
		ret.put("id", id);
		ret.put("codeVal", codeVal);
		ret.put("remark", remark);
		try {
			sysCodeService.updateSmallType(ret);
			SysCode sysCode2=sysCodeService.queryById(id);
			//存储到缓存
			cacheRedisService.setSysCode(lotteryConfigCodeName, sysCode2);
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
}

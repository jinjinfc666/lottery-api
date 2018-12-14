package com.jll.sysSettings.syscode;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.type.DateType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.entity.PayChannel;
import com.jll.entity.PayType;
import com.jll.entity.SysCode;
import com.jll.sys.deposit.PayChannelService;
import com.jll.sys.deposit.PayTypeService;
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
	@Resource
	PayTypeService payTypeService;
	@Resource
	PayChannelService payChannelService;
	/**
	 *大类
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
	@RequestMapping(value={"/bigType"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryBigType() {
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
	@RequestMapping(value={"/updateBigType"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateBigType(@RequestParam(name = "id", required = true) Integer id,
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
			SysCode sysCode1=cacheRedisService.getSysCode(codeName, codeName);
			if(!StringUtils.isBlank(codeVal)) {
				sysCode1.setCodeVal(codeVal);
			}
			if(!StringUtils.isBlank(remark)) {
				sysCode1.setRemark(remark);
			}
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
	@RequestMapping(value={"/updateBigTypeState"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateBigTypeState(@RequestParam(name = "id", required = true) Integer id,
			@RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			sysCodeService.updateBigTypeState(id,state);	
			// //TODO 	
			SysCode sysCode=cacheRedisService.getSysCode(codeName, codeName);
			sysCode.setState(state);
			cacheRedisService.setSysCode(codeName, sysCode);
			
			Map<String,SysCode> map=cacheRedisService.getSysCode(codeName);
			if (map != null) {  
	            Set<String> keySet = map.keySet();  
	            for (String string : keySet) {  
	                SysCode sysCode1=cacheRedisService.getSysCode(codeName, string);
	                sysCode1.setState(state);
	                cacheRedisService.setSysCode(codeName, sysCode);
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
	@RequestMapping(value={"/querySmallLotteryType"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> querySmallLotteryType() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
		try {
			Map<String,SysCode> sysCodeMaps=cacheRedisService.getSysCode(bigCodeName);
			Map<Integer,SysCode> sysCodeMaps1=new HashMap<Integer, SysCode>();
			for(String key:sysCodeMaps.keySet()) {
				SysCode sysCode=sysCodeMaps.get(key);
				if(sysCode.getSeq()!=null) {
					sysCodeMaps1.put(sysCode.getSeq(), sysCode);
				}
			}
			TreeMap treemap = new TreeMap(sysCodeMaps1);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", treemap);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除彩种类型下的某个小类
	@RequestMapping(value={"/updateSmallLotteryTypeState"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallLotteryTypeState(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			//存储到缓存
			SysCode sysCode=cacheRedisService.getSysCode(bigCodeName, codeName);
			if(sysCode!=null) {
				sysCode.setState(state);
				cacheRedisService.setSysCode(bigCodeName, sysCode);	
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
	//修改彩种类型下的某一条数据
	@RequestMapping(value={"/updateSmallLotteryType"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallLotteryType(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
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
			//存储到缓存
			SysCode sysCode1=cacheRedisService.getSysCode(bigCodeName, codeName);
			if(sysCode1!=null) {
				if(!StringUtils.isBlank(codeVal)) {
					sysCode1.setCodeVal(codeVal);
				}
				if(!StringUtils.isBlank(remark)) {
					sysCode1.setRemark(remark);
				}
				cacheRedisService.setSysCode(bigCodeName, sysCode1);
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
	//修改排序
	@RequestMapping(value={"/updateSmallLotteryTypeSeq"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallLotteryTypeSeq(@RequestParam(name = "allId", required = true) String allId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
		try {
			Map<String,Object> map=sysCodeService.updateSmallTypeSeq(bigCodeName, allId);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	/**
	 *流水类型的增删改查
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
	@RequestMapping(value={"/querySmallFlowType"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> querySmallFlowType() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.FLOW_TYPES.getCode();
		try {
			Map<String,SysCode> sysCodeMaps=cacheRedisService.getSysCode(bigCodeName);
			Map<Integer,SysCode> sysCodeMaps1=new HashMap<Integer, SysCode>();
			for(String key:sysCodeMaps.keySet()) {
				SysCode sysCode=sysCodeMaps.get(key);
				if(sysCode.getSeq()!=null) {
					sysCodeMaps1.put(sysCode.getSeq(), sysCode);
				}
			}
			TreeMap treemap = new TreeMap(sysCodeMaps1);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", treemap);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除流水类型下的某个小类
	@RequestMapping(value={"/updateSmallFlowTypeState"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallFlowTypeState(@RequestParam(name = "id", required = true) Integer id,
			@RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.FLOW_TYPES.getCode();
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			//存储到缓存
			SysCode sysCode=cacheRedisService.getSysCode(bigCodeName, codeName);
			if(sysCode!=null) {
				sysCode.setState(state);
				cacheRedisService.setSysCode(bigCodeName, sysCode);	
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
	//修改流水类型下的某一条数据
	@RequestMapping(value={"/updateSmallFlowType"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallFlowType(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
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
			//存储到缓存
			SysCode sysCode1=cacheRedisService.getSysCode(bigCodeName, codeName);
			if(sysCode1!=null) {
				if(!StringUtils.isBlank(codeVal)) {
					sysCode1.setCodeVal(codeVal);
				}
				if(!StringUtils.isBlank(remark)) {
					sysCode1.setRemark(remark);
				}
				cacheRedisService.setSysCode(bigCodeName, sysCode1);
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
	//修改排序
	@RequestMapping(value={"/updateSmallFlowTypeSeq"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallFlowTypeSeq(@RequestParam(name = "allId", required = true) String allId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.FLOW_TYPES.getCode();
		try {
			Map<String,Object> map=sysCodeService.updateSmallTypeSeq(bigCodeName, allId);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	/**
	 *幸运抽奖类型的增删改查
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
	//查询幸运抽奖类型下的所有值
	@RequestMapping(value={"/querySmallLuckyDraw"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> querySmallLuckyDraw() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.LUCKY_DRAW.getCode();
		try {
			Map<String,SysCode> sysCodeMaps=cacheRedisService.getSysCode(bigCodeName);
			Map<Integer,SysCode> sysCodeMaps1=new HashMap<Integer, SysCode>();
			for(String key:sysCodeMaps.keySet()) {
				SysCode sysCode=sysCodeMaps.get(key);
				if(sysCode.getSeq()!=null) {
					sysCodeMaps1.put(sysCode.getSeq(), sysCode);
				}
			}
			TreeMap treemap = new TreeMap(sysCodeMaps1);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", treemap);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除幸运抽奖类型下的某个小类
	@RequestMapping(value={"/updateSmallLuckyDrawState"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallLuckyDrawState(@RequestParam(name = "id", required = true) Integer id,
			@RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.LUCKY_DRAW.getCode();
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			//存储到缓存
			SysCode sysCode=cacheRedisService.getSysCode(bigCodeName, codeName);
			if(sysCode!=null) {
				sysCode.setState(state);
				cacheRedisService.setSysCode(bigCodeName, sysCode);	
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
	//修改幸运抽奖类型下的某一条数据
	@RequestMapping(value={"/updateSmallLuckyDraw"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallLuckyDraw(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
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
			//存储到缓存
			SysCode sysCode1=cacheRedisService.getSysCode(bigCodeName, codeName);
			if(sysCode1!=null) {
				if(!StringUtils.isBlank(codeVal)) {
					sysCode1.setCodeVal(codeVal);
				}
				if(!StringUtils.isBlank(remark)) {
					sysCode1.setRemark(remark);
				}
				cacheRedisService.setSysCode(bigCodeName, sysCode1);
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
	//修改排序
	@RequestMapping(value={"/updateSmallLuckyDrawSeq"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallLuckyDrawSeq(@RequestParam(name = "allId", required = true) String allId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.LUCKY_DRAW.getCode();
		try {
			Map<String,Object> map=sysCodeService.updateSmallTypeSeq(bigCodeName, allId);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	/**
	 *支付平台类型的增删改查
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
	//查询支付平台类型下的所有值
	@RequestMapping(value={"/querySmallPaymentPlatform"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> querySmallPaymentPlatform() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.PAYMENT_PLATFORM.getCode();
		try {
			Map<String,SysCode> sysCodeMaps=cacheRedisService.getSysCode(bigCodeName);
			Map<Integer,SysCode> sysCodeMaps1=new HashMap<Integer, SysCode>();
			for(String key:sysCodeMaps.keySet()) {
				SysCode sysCode=sysCodeMaps.get(key);
				if(sysCode.getSeq()!=null) {
					sysCodeMaps1.put(sysCode.getSeq(), sysCode);
				}
			}
			TreeMap treemap = new TreeMap(sysCodeMaps1);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", treemap);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除支付平台类型下的某个小类
	@RequestMapping(value={"/updateSmallPaymentPlatformState"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallPaymentPlatformState(@RequestParam(name = "id", required = true) Integer id,
			@RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.PAYMENT_PLATFORM.getCode();
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			//存储到缓存
			SysCode sysCode=cacheRedisService.getSysCode(bigCodeName, codeName);
			if(sysCode!=null) {
				sysCode.setState(state);
				cacheRedisService.setSysCode(bigCodeName, sysCode);	
			}
			//修改充值平台下面的充值方式状态
			List<PayType> listPayTypes=payTypeService.queryByPlatId(codeName);
			if(listPayTypes!=null&&listPayTypes.size()>0) {
				for(int a=0;a<listPayTypes.size();a++) {
					PayType payType=listPayTypes.get(a);
					Integer payTypeId=payType.getId();
					PayType payTypeNew=new PayType();
					payTypeNew.setId(payTypeId);
					payTypeNew.setState(state);
					payTypeService.updatePayType(payTypeNew);
					//修改充值渠道的状态
					List<PayChannel> payChannelLists=payChannelService.queryByPayTypeIdPayChannel(payTypeId);
					if(payChannelLists!=null&&payChannelLists.size()>0) {
						for(int b=0;b<payChannelLists.size();b++) {
							PayChannel payChannel=payChannelLists.get(b);
							Integer payChannelId=payChannel.getId();
							payChannelService.updatePayChannelState(payChannelId,state);
						}
					}
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
	//修改支付平台类型下的某一条数据
	@RequestMapping(value={"/updateSmallPaymentPlatform"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallPaymentPlatform(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
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
			//存储到缓存
			SysCode sysCode1=cacheRedisService.getSysCode(bigCodeName, codeName);
			if(sysCode1!=null) {
				if(!StringUtils.isBlank(codeVal)) {
					sysCode1.setCodeVal(codeVal);
				}
				if(!StringUtils.isBlank(remark)) {
					sysCode1.setRemark(remark);
				}
				cacheRedisService.setSysCode(bigCodeName, sysCode1);
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
	//修改排序
	@RequestMapping(value={"/updateSmallPaymentPlatformSeq"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallPaymentPlatformSeq(@RequestParam(name = "allId", required = true) String allId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.PAYMENT_PLATFORM.getCode();
		try {
			Map<String,Object> map=sysCodeService.updateSmallTypeSeq(bigCodeName, allId);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	/**
	 *签到活动类型的增删改查
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
//			//存储到缓存
//			List<SysCode> sysCode1=sysCodeService.querySmallType(bigCodeName, codeName);
//			SysCode sysCode2=sysCode1.get(0);
//			cacheRedisService.setSysCode(bigCodeName, sysCode2);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询签到活动类型下的所有值
	@RequestMapping(value={"/querySmallSignInDay"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> querySmallSignInDay() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.SIGN_IN_DAY.getCode();
		try {
			List<SysCode> sysCodeMaps=sysCodeService.queryAllSmallType(bigCodeName);
			Map<Integer,SysCode> sysCodeMaps1=new HashMap<Integer, SysCode>();
			for(int i=0;i<sysCodeMaps.size();i++) {
				SysCode sysCode=sysCodeMaps.get(i);
				if(sysCode.getSeq()!=null) {
					sysCodeMaps1.put(sysCode.getSeq(), sysCode);
				}
			}
			TreeMap treemap = new TreeMap(sysCodeMaps1);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", treemap);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除签到活动类型下的某个小类
	@RequestMapping(value={"/updateSmallSignInDayState"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallSignInDayState(@RequestParam(name = "id", required = true) Integer id,
			@RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.SIGN_IN_DAY.getCode();
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
//			//存储到缓存
//			SysCode sysCode=cacheRedisService.getSysCode(bigCodeName, codeName);
//			if(sysCode!=null) {
//				sysCode.setState(state);
//				cacheRedisService.setSysCode(bigCodeName, sysCode);	
//			}
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
	@RequestMapping(value={"/updateSmallSignInDay"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallSignInDay(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
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
//			//存储到缓存
//			SysCode sysCode1=cacheRedisService.getSysCode(bigCodeName, codeName);
//			if(sysCode1!=null) {
//				if(!StringUtils.isBlank(codeVal)) {
//					sysCode1.setCodeVal(codeVal);
//				}
//				if(!StringUtils.isBlank(remark)) {
//					sysCode1.setRemark(remark);
//				}
//				cacheRedisService.setSysCode(bigCodeName, sysCode1);
//			}
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
	//修改排序
	@RequestMapping(value={"/updateSmallSignInDaySeq"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallSignInDaySeq(@RequestParam(name = "allId", required = true) String allId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.SIGN_IN_DAY.getCode();
		try {
			Map<String,Object> map=sysCodeService.updateSmallSignInDaySeq(bigCodeName, allId);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	/**
	 *玩法类型的增删改查
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
	@RequestMapping(value={"/querySmallPTypeClassicfication"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> querySmallPTypeClassicfication() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.CT_PLAY_TYPE_CLASSICFICATION.getCode();
		try {
			Map<String,SysCode> sysCodeMaps=cacheRedisService.getSysCode(bigCodeName);
			Map<Integer,SysCode> sysCodeMaps1=new HashMap<Integer, SysCode>();
			for(String key:sysCodeMaps.keySet()) {
				SysCode sysCode=sysCodeMaps.get(key);
				if(sysCode.getSeq()!=null) {
					sysCodeMaps1.put(sysCode.getSeq(), sysCode);
				}
			}
			TreeMap treemap = new TreeMap(sysCodeMaps1);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", treemap);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除玩法类型下的某个小类
	@RequestMapping(value={"/updateSmallPTypeClassicficationState"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallPTypeClassicficationState(@RequestParam(name = "id", required = true) Integer id,
			@RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.CT_PLAY_TYPE_CLASSICFICATION.getCode();
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			//存储到缓存
			SysCode sysCode=cacheRedisService.getSysCode(bigCodeName, codeName);
			if(sysCode!=null) {
				sysCode.setState(state);
				cacheRedisService.setSysCode(bigCodeName, sysCode);	
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
	//修改玩法类型下的某一条数据
	@RequestMapping(value={"/updateSmallPTypeClassicfication"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallPTypeClassicfication(@RequestParam(name = "id", required = true) Integer id,
			@RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
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
			//存储到缓存
			SysCode sysCode1=cacheRedisService.getSysCode(bigCodeName, codeName);
			if(sysCode1!=null) {
				if(!StringUtils.isBlank(codeVal)) {
					sysCode1.setCodeVal(codeVal);
				}
				if(!StringUtils.isBlank(remark)) {
					sysCode1.setRemark(remark);
				}
				cacheRedisService.setSysCode(bigCodeName, sysCode1);
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
	//修改排序
	@RequestMapping(value={"/updateSmallPTypeClassicficationSeq"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallPTypeClassicficationSeq(@RequestParam(name = "allId", required = true) String allId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.CT_PLAY_TYPE_CLASSICFICATION.getCode();
		try {
			Map<String,Object> map=sysCodeService.updateSmallTypeSeq(bigCodeName, allId);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	/**
	 *彩种属性类型的增删改查
	 */
	//增加彩种属性类型小类
	@RequestMapping(value={"/SmallLotteryConfig"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSmallLotteryConfig(@RequestParam(name = "bigCodeName", required = true) String bigCodeName,
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
		String lotteryConfigCodeName=Constants.KEY_LOTTO_ATTRI_PREFIX+bigCodeName;
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
	//查询彩种属性类型下的所有值
	@RequestMapping(value={"/querySmallLotteryConfig"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> querySmallLotteryConfig(@RequestParam(name = "bigCodeName", required = true) String bigCodeName,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String lotteryConfigCodeName=Constants.KEY_LOTTO_ATTRI_PREFIX+bigCodeName;
		try {
			Map<String,SysCode> sysCodeMaps=cacheRedisService.getSysCode(lotteryConfigCodeName);
			Map<Integer,SysCode> sysCodeMaps1=new HashMap<Integer, SysCode>();
			for(String key:sysCodeMaps.keySet()) {
				SysCode sysCode=sysCodeMaps.get(key);
				if(sysCode.getSeq()!=null) {
					sysCodeMaps1.put(sysCode.getSeq(), sysCode);
				}
			}
			TreeMap treemap = new TreeMap(sysCodeMaps1);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", treemap);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除彩种属性类型下的某个小类
	@RequestMapping(value={"/updateSmallLotteryConfigState"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallLotteryConfigState(@RequestParam(name = "bigCodeName", required = true) String bigCodeName,
			  @RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String lotteryConfigCodeName=Constants.KEY_LOTTO_ATTRI_PREFIX+bigCodeName;
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			//存储到缓存
			SysCode sysCode=cacheRedisService.getSysCode(lotteryConfigCodeName, codeName);
			if(sysCode!=null) {
				sysCode.setState(state);
				cacheRedisService.setSysCode(lotteryConfigCodeName, sysCode);	
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
	//修改彩种属性类型下的某一条数据
	@RequestMapping(value={"/updateSmallLotteryConfig"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallLotteryConfig(@RequestParam(name = "bigCodeName", required = true) String bigCodeName,
			  @RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
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
		String lotteryConfigCodeName=Constants.KEY_LOTTO_ATTRI_PREFIX+bigCodeName;
		ret.put("id", id);
		ret.put("codeVal", codeVal);
		ret.put("remark", remark);
		try {
			sysCodeService.updateSmallType(ret);
			//存储到缓存
			SysCode sysCode1=cacheRedisService.getSysCode(lotteryConfigCodeName, codeName);
			if(sysCode1!=null) {
				if(!StringUtils.isBlank(codeVal)) {
					sysCode1.setCodeVal(codeVal);
				}
				if(!StringUtils.isBlank(remark)) {
					sysCode1.setRemark(remark);
				}
				cacheRedisService.setSysCode(lotteryConfigCodeName, sysCode1);
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
	//查询彩种属性类型下的某一条数据
	@RequestMapping(value={"/querySmallLotteryConfigOne"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> querySmallLotteryConfigOne(@RequestParam(name = "bigCodeName", required = true) String bigCodeName,
			  @RequestParam(name = "codeName", required = true) String codeName,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String lotteryConfigCodeName=Constants.KEY_LOTTO_ATTRI_PREFIX+bigCodeName;
		try {
			SysCode sysCode1=cacheRedisService.getSysCode(lotteryConfigCodeName, codeName);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put(Message.KEY_DATA, sysCode1);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//修改排序
	@RequestMapping(value={"/updateSmallLotteryConfigSeq"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallLotteryConfigSeq(@RequestParam(name = "bigCodeName", required = true) String bigCodeName,
			@RequestParam(name = "allId", required = true) String allId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String lotteryConfigCodeName=Constants.KEY_LOTTO_ATTRI_PREFIX+bigCodeName;
		try {
			Map<String,Object> map=sysCodeService.updateSmallTypeSeq(lotteryConfigCodeName, allId);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	/**
	 *充值方式的增删改查
	 */
	//增加充值方式类型小类
	@RequestMapping(value={"/SmallPayType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSmallPayType(@RequestParam(name = "codeName", required = true) String codeName,
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
		String bigCodeName=Constants.SysCodeTypes.PAY_TYPE_CLASS.getCode();
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
	//查询充值方式类型下的所有值
	@RequestMapping(value={"/querySmallPayType"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> querySmallPayType() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.PAY_TYPE_CLASS.getCode();
		try {
			Map<String,SysCode> sysCodeMaps=cacheRedisService.getSysCode(bigCodeName);
			Map<Integer,SysCode> sysCodeMaps1=new HashMap<Integer, SysCode>();
			for(String key:sysCodeMaps.keySet()) {
				SysCode sysCode=sysCodeMaps.get(key);
				if(sysCode.getSeq()!=null) {
					sysCodeMaps1.put(sysCode.getSeq(), sysCode);
				}
			}
			TreeMap treemap = new TreeMap(sysCodeMaps1);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", treemap);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//软删除充值方式类型下的某个小类
	@RequestMapping(value={"/updateSmallPayTypeState"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallPayTypeState(@RequestParam(name = "id", required = true) Integer id,
			@RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.PAY_TYPE_CLASS.getCode();
		try {
			ret.put("id", id);
			ret.put("state", state);
			sysCodeService.updateSmallTypeState(id, state);	
			//存储到缓存
			SysCode sysCode=cacheRedisService.getSysCode(bigCodeName, codeName);
			if(sysCode!=null) {
				sysCode.setState(state);
				cacheRedisService.setSysCode(bigCodeName, sysCode);	
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
	//修改充值方式类型下的某一条数据
	@RequestMapping(value={"/updateSmallPayType"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallPayType(@RequestParam(name = "id", required = true) Integer id,
			@RequestParam(name = "codeName", required = true) String codeName,//不能修改只传值
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
		String bigCodeName=Constants.SysCodeTypes.PAY_TYPE_CLASS.getCode();
		ret.put("id", id);
		ret.put("codeVal", codeVal);
		ret.put("remark", remark);
		try {
			sysCodeService.updateSmallType(ret);
			//存储到缓存
			SysCode sysCode1=cacheRedisService.getSysCode(bigCodeName, codeName);
			if(sysCode1!=null) {
				if(!StringUtils.isBlank(codeVal)) {
					sysCode1.setCodeVal(codeVal);
				}
				if(!StringUtils.isBlank(remark)) {
					sysCode1.setRemark(remark);
				}
				cacheRedisService.setSysCode(bigCodeName, sysCode1);
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
	//修改排序
	@RequestMapping(value={"/updateSmallPayTypeSeq"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSmallPayTypeSeq(@RequestParam(name = "allId", required = true) String allId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.PAY_TYPE_CLASS.getCode();
		try {
			Map<String,Object> map=sysCodeService.updateSmallTypeSeq(bigCodeName, allId);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//查询充值方式类型下的某一个值
	@RequestMapping(value={"/queryPayTypeByCodeName"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryPayTypeByCodeName(@RequestParam(name = "codeName", required = true) String codeName,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.PAY_TYPE_CLASS.getCode();
		try {
			SysCode sysCodeMaps=cacheRedisService.getSysCode(bigCodeName, codeName);
			
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", sysCodeMaps);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	/**
	 *可选银行
	 */
	//查询所有的可选银行
	@RequestMapping(value={"/queryBankCodeList"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryBankCodeList() {
		Map<String, Object> ret = new HashMap<>();
		String bigCodeName=Constants.SysCodeTypes.BANK_CODE_LIST.getCode();
		try {
			Map<String,SysCode> sysCodeMaps=cacheRedisService.getSysCode(bigCodeName);
			Map<String,SysCode> sysCodeMaps1=new HashMap<String, SysCode>();
			for(String key:sysCodeMaps.keySet()) {
				SysCode sysCode=sysCodeMaps.get(key);
				if(sysCode.getState()==1&&sysCode.getCodeType()!=null) {
					sysCodeMaps1.put(key, sysCode);
				}
			}
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", sysCodeMaps1);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
}

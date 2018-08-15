package com.jll.game.playtype;

import java.util.HashMap;
import java.util.Iterator;
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

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;


@Api2Doc(id = "sysPlayType", name = "Settings playType")
@ApiComment(seeClass = SysCode.class)
@RestController
@RequestMapping({"/settingPlayType"})
public class SysPlayTypeController {
	private Logger logger = Logger.getLogger(SysPlayTypeController.class);
	@Resource
	PlayTypeService playTypeService;
	@Resource
	CacheRedisService cacheRedisService;
	//添加玩法
	@RequestMapping(value={"/addPlayType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addPlayType(@RequestBody PlayType playType) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(playType.getLotteryType())
				||StringUtils.isBlank(playType.getClassification())
				||StringUtils.isBlank(playType.getPtDesc())
				||StringUtils.isBlank(playType.getPtName())
				||playType.getMulSinFlag()==null||playType.getIsHidden()==null||playType.getState()==null) 
		{
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		try {
			ret.clear();
			ret=playTypeService.addPlayType(playType);
			int status=(int) ret.get(Message.KEY_STATUS);
			if(status==Message.status.SUCCESS.getCode()) {
				String lotteryType=playType.getLotteryType();
				List<PlayType> playTypes=playTypeService.queryByLotteryType(lotteryType);
				cacheRedisService.setPlayType(lotteryType, playTypes);
			}
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//选择:彩种
	@RequestMapping(value={"/lotteTypes"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryLotteryTypes() {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,SysCode> types = cacheRedisService.getSysCode(SysCodeTypes.LOTTERY_TYPES.getCode());
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", types);
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//选择:玩法
	@RequestMapping(value={"/sysCodePlayType"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryPlayType() {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,SysCode> types = cacheRedisService.getSysCode(Constants.SysCodePlayType.CT_PLAY_TYPE_CLASSICFICATION.getCode());
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", types);
		}catch(Exception e){
			e.printStackTrace();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//选择:是否有效
	@RequestMapping(value={"/isOrNo"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryisOrNo() {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<Integer,String> types = Constants.BankCardState.getIsOrNo();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", types);
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//选择:是单式还是复式
	@RequestMapping(value={"/singleOrDouble"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> querySingleOrDouble() {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,Object> types = Constants.SingleOrDouble.getMap();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", types);
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//选择:名称是否隐藏
	@RequestMapping(value={"/hideName"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryHideName() {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,Object> types = Constants.HidePlayName.getMap();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", types);
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//修改状态
	@RequestMapping(value={"/state"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateState(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "state", required = true) Integer state,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.clear();
			ret=playTypeService.updateState(id, state);
			int status=(int) ret.get(Message.KEY_STATUS);
			if(status==Message.status.SUCCESS.getCode()) {
				List<PlayType> playType=playTypeService.queryById(id);
				if(playType!=null&&playType.size()>0) {
					String lotteryType=playType.get(0).getLotteryType();
					List<PlayType> playTypes=playTypeService.queryByLotteryType(lotteryType);
					cacheRedisService.setPlayType(lotteryType, playTypes);
				}
			}
			return ret;
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//是否隐藏
	@RequestMapping(value={"/isHidden"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateIsHidden(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "isHidden", required = true) Integer isHidden,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.clear();
			ret=playTypeService.updateIsHidden(id, isHidden);
			int status=(int) ret.get(Message.KEY_STATUS);
			if(status==Message.status.SUCCESS.getCode()) {
				List<PlayType> playType=playTypeService.queryById(id);
				if(playType != null && playType.size() > 0) {
					String lotteryType=playType.get(0).getLotteryType();
					List<PlayType> playTypes=playTypeService.queryByLotteryType(lotteryType);
					cacheRedisService.setPlayType(lotteryType, playTypes);
				}
			}
			return ret;
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//选择单式还是复式
	@RequestMapping(value={"/mulSinFlag"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateMulSinFlag(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "mulSinFlag", required = true) Integer mulSinFlag,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.clear();
			ret=playTypeService.updateMulSinFlag(id, mulSinFlag);
			int status=(int) ret.get(Message.KEY_STATUS);
			if(status==Message.status.SUCCESS.getCode()) {
				List<PlayType> playType=playTypeService.queryById(id);
				if(playType != null && playType.size() > 0) {
					String lotteryType=playType.get(0).getLotteryType();
					List<PlayType> playTypes=playTypeService.queryByLotteryType(lotteryType);
					cacheRedisService.setPlayType(lotteryType, playTypes);
				}
			}
			return ret;
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//修改
	@RequestMapping(value={"/updatePlayType"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updatePlayType(@RequestBody PlayType playType) {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.clear();
			ret=playTypeService.updatePlayType(playType);
			int status=(int) ret.get(Message.KEY_STATUS);
			if(status==Message.status.SUCCESS.getCode()) {
				String lotteryType=playType.getLotteryType();
				List<PlayType> playTypes=playTypeService.queryByLotteryType(lotteryType);
				cacheRedisService.setPlayType(lotteryType, playTypes);
			}
			return ret;
		}catch(Exception e){
			e.printStackTrace();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	
}

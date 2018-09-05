package com.jll.game.playtype;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
public class PlayTypeController {
	private Logger logger = Logger.getLogger(PlayTypeController.class);
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
			return ret;
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
			return ret;
		}
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
			@RequestParam(name = "lotteryType", required = true) String lotteryType,//只传值不修改
			  @RequestParam(name = "state", required = true) Integer state,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.clear();
			ret=playTypeService.updateState(id, state);
			int status=(int) ret.get(Message.KEY_STATUS);
			if(status==Message.status.SUCCESS.getCode()) {
				/*PlayType playType=playTypeService.queryById(id);
				if(playType!=null) {
					String lotteryType = playType.getLotteryType();
					List<PlayType> playTypes=playTypeService.queryByLotteryType(lotteryType);
					cacheRedisService.setPlayType(lotteryType, playTypes);
				}*/
				SysCode sysCode=new SysCode();
				sysCode.setCodeName(lotteryType);
				List<PlayType> playTypeList=cacheRedisService.getPlayType(sysCode);
				if(playTypeList!=null&&playTypeList.size()>0) {
					Integer id1=null;
					for(int i=0; i<playTypeList.size();i++)    {   
					     PlayType playType=playTypeList.get(i);
					     id1=playType.getId();
					     if((int)id1==(int)id) {
							playType.setState(state);
							playTypeList.set(i, playType);
						}
					 }
					cacheRedisService.setPlayType(lotteryType, playTypeList);
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
			@RequestParam(name = "lotteryType", required = true) String lotteryType,//只传值不修改
			  @RequestParam(name = "isHidden", required = true) Integer isHidden,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.clear();
			ret=playTypeService.updateIsHidden(id, isHidden);
			int status=(int) ret.get(Message.KEY_STATUS);
			if(status==Message.status.SUCCESS.getCode()) {
				/*PlayType playType=playTypeService.queryById(id);
				if(playType != null) {
					String lotteryType = playType.getLotteryType();
					List<PlayType> playTypes=playTypeService.queryByLotteryType(lotteryType);
					cacheRedisService.setPlayType(lotteryType, playTypes);
				}*/
				SysCode sysCode=new SysCode();
				sysCode.setCodeName(lotteryType);
				List<PlayType> playTypeList=cacheRedisService.getPlayType(sysCode);
				if(playTypeList!=null&&playTypeList.size()>0) {
					Integer id1=null;
					for(int i=0; i<playTypeList.size();i++)    {   
					     PlayType playType=playTypeList.get(i);
					     id1=playType.getId();
					     if((int)id1==(int)id) {
							playType.setIsHidden(isHidden);
							playTypeList.set(i, playType);
						}
					 }
					cacheRedisService.setPlayType(lotteryType, playTypeList);
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
			@RequestParam(name = "lotteryType", required = true) String lotteryType,//只传值不修改
			  @RequestParam(name = "mulSinFlag", required = true) Integer mulSinFlag,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.clear();
			ret=playTypeService.updateMulSinFlag(id, mulSinFlag);
			int status=(int) ret.get(Message.KEY_STATUS);
			if(status==Message.status.SUCCESS.getCode()) {
				/*PlayType playType=playTypeService.queryById(id);
				if(playType != null) {
					String lotteryType = playType.getLotteryType();
					List<PlayType> playTypes=playTypeService.queryByLotteryType(lotteryType);
					cacheRedisService.setPlayType(lotteryType, playTypes);
				}*/
				SysCode sysCode=new SysCode();
				sysCode.setCodeName(lotteryType);
				List<PlayType> playTypeList=cacheRedisService.getPlayType(sysCode);
				if(playTypeList!=null&&playTypeList.size()>0) {
					Integer id1=null;
					for(int i=0; i<playTypeList.size();i++)    {   
					     PlayType playType=playTypeList.get(i);
					     id1=playType.getId();
					     if((int)id1==(int)id) {
							playType.setMulSinFlag(mulSinFlag);
							playTypeList.set(i, playType);
						}
					 }
					cacheRedisService.setPlayType(lotteryType, playTypeList);
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
	public Map<String, Object> updatePlayType(@RequestBody PlayType playType) {//实体里面id和LotteryType是必须传过来的，LotteryType是不能修改的
		Map<String, Object> ret = new HashMap<>();
		Integer id=playType.getId();
		String lotteryType=playType.getLotteryType();
		String classification=playType.getClassification();
		String ptDesc=playType.getPtDesc();
		String ptName=playType.getPtName();
		Integer mulSinFlag=playType.getMulSinFlag();
		Integer isHidden=playType.getIsHidden();
		Integer state=playType.getState();
		if(id==null||StringUtils.isBlank(lotteryType)) {
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
		if(StringUtils.isBlank(classification)&&StringUtils.isBlank(ptDesc)&&StringUtils.isBlank(ptName)&&mulSinFlag==null&&isHidden==null&&state==null) 
		{
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
		try {
			ret.clear();
			ret=playTypeService.updatePlayType(playType);
			int status=(int) ret.get(Message.KEY_STATUS);
			if(status==Message.status.SUCCESS.getCode()) {
				SysCode sysCode=new SysCode();
				sysCode.setCodeName(lotteryType);
				List<PlayType> playTypeList=cacheRedisService.getPlayType(sysCode);
				if(playTypeList!=null&&playTypeList.size()>0) {
					Integer id1=null;
					for(int i=0; i<playTypeList.size();i++)    {   
					     PlayType playType1=playTypeList.get(i);
					     id1=playType1.getId();
					     if((int)id1==(int)id) {
					    	if(!StringUtils.isBlank(classification)) {
					    		playType1.setClassification(classification);
					    	}
					    	if(!StringUtils.isBlank(ptDesc)) {
					    		playType1.setPtDesc(ptDesc);
					    	}
					    	if(!StringUtils.isBlank(ptName)) {
					    		playType1.setPtName(ptName);
					    	}
					    	if(mulSinFlag!=null) {
					    		playType1.setMulSinFlag(mulSinFlag);
					    	}
					    	if(isHidden!=null) {
					    		playType1.setIsHidden(isHidden);
					    	}
					    	if(state!=null) {
					    		playType1.setState(state);
					    	}
							playTypeList.set(i, playType1);
						}
					 }
					cacheRedisService.setPlayType(lotteryType, playTypeList);
				}
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
	//查询某个彩种下的所有玩法
	@RequestMapping(value={"/queryPlayType"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryPlayType(@RequestParam(name = "lotteryType", required = true) String lotteryType,//只传值
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			List<PlayType> list=playTypeService.queryByLotteryType(lotteryType);
			Map<Integer,PlayType> sysCodeMaps1=new HashMap<Integer, PlayType>();
			for(int i=0;i<list.size();i++) {
				PlayType sysCode=list.get(i);
				if(sysCode.getSeq()!=null) {
					sysCodeMaps1.put(sysCode.getSeq(), sysCode);
				}
			}
			TreeMap treemap = new TreeMap(sysCodeMaps1);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", treemap);
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//修改排序
	@RequestMapping(value={"/updatePlayTypeSeq"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updatePlayTypeSeq(@RequestParam(name = "lotteryType", required = true) String lotteryType,//只传值,
			@RequestParam(name = "allId", required = true) String allId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,Object> map=playTypeService.updatePlayTypeSeq(lotteryType, allId);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
}

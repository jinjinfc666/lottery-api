package com.jll.game.playtype;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Message;
import com.jll.entity.PlayType;

@Configuration
@PropertySource("classpath:sys-setting.properties")
@Service
@Transactional
public class PlayTypeServiceImpl implements PlayTypeService
{
	private Logger logger = Logger.getLogger(PlayTypeServiceImpl.class);

	@Resource
	PlayTypeDao playTypeDao;
	@Resource
	CacheRedisService cacheRedisService;

	
	@Override
	public List<PlayType> queryPlayType(String lotteryType) {
		List<PlayType> playTypes = playTypeDao.queryByLotteryType(lotteryType);
		return playTypes;
	}
	//添加
	@Override
	public Map<String,Object> addPlayType(PlayType playType) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean isNo=this.isNoPlayType(playType);
		if(!isNo) {
			Integer seq=playTypeDao.getCountSeq(playType.getLotteryType())+1;
			playType.setSeq(seq);
	        playType.setCreateTime(new Date());
			playTypeDao.addPlayType(playType);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PLAYTYPE_ALREADY_EXISTS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_PLAYTYPE_ALREADY_EXISTS.getErrorMes());
		}
		return map;
	}
	//修改状态
	@Override
	public Map<String,Object> updateState(Integer id, Integer state) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean isNo=this.isNoPlayType(id);
		if(isNo) {
			playTypeDao.updateState(id, state);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PLAYTYPE_DOES_NOT_EXIST.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_PLAYTYPE_DOES_NOT_EXIST.getErrorMes());
		}
		return map;
	}
	//设置是否隐藏
	@Override
	public Map<String,Object> updateIsHidden(Integer id, Integer isHidden) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean isNo=this.isNoPlayType(id);
		if(isNo) {
			playTypeDao.updateIsHidden(id, isHidden);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PLAYTYPE_DOES_NOT_EXIST.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_PLAYTYPE_DOES_NOT_EXIST.getErrorMes());
		}
		return map;
	}
	//选择单式还是复式
	@Override
	public Map<String,Object> updateMulSinFlag(Integer id, Integer mulSinFlag) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean isNo=this.isNoPlayType(id);
		if(isNo) {
			playTypeDao.updateMulSinFlag(id, mulSinFlag);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PLAYTYPE_DOES_NOT_EXIST.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_PLAYTYPE_DOES_NOT_EXIST.getErrorMes());
		}
		return map;
	}
	//修改
	@Override
	public Map<String,Object> updatePlayType(PlayType playType) {
		Integer id=playType.getId();
		String classification=playType.getClassification();
		String ptName=playType.getPtName();
		String ptDesc=playType.getPtDesc();
		Integer state=playType.getState();
		Integer mulSinFlag=playType.getMulSinFlag();
		Integer isHidden=playType.getIsHidden();
		Map<String,Object> map=new HashMap<String,Object>();
		boolean isNo=this.isNoPlayType(id);
		if(isNo) {
			PlayType playTypeNew=this.queryById(id);
			if(!StringUtils.isBlank(classification)) {
				playTypeNew.setClassification(classification);
			}
			if(!StringUtils.isBlank(ptName)) {
				playTypeNew.setPtName(ptName);				
			}
			if(!StringUtils.isBlank(ptDesc)) {
				playTypeNew.setPtDesc(ptDesc);
			}
			if(state!=null) {
				playTypeNew.setState(state);
			}
			if(mulSinFlag!=null) {
				playTypeNew.setMulSinFlag(mulSinFlag);		
			}
			if(isHidden!=null) {
				playTypeNew.setIsHidden(isHidden);
			}
			playTypeDao.updatePlayType(playTypeNew);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PLAYTYPE_DOES_NOT_EXIST.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_PLAYTYPE_DOES_NOT_EXIST.getErrorMes());
		}
		return map;
	}
	@Override
	public List<PlayType> queryByLotteryType(String lotteryType) {
		return playTypeDao.queryByLotteryType(lotteryType);
	}
	@Override
	public PlayType queryById(Integer id) {
		return playTypeDao.queryById(id);
	}
	//查询id是否存在
	@Override
	public boolean isNoPlayType(Integer id) {
		PlayType list=playTypeDao.queryById(id);
		if(list!=null) {
			return true;
		}
		return false;
	}
	//添加时的验证
	@Override
	public boolean isNoPlayType(PlayType playType) {
		List<PlayType> list=playTypeDao.queryByPlayType(playType);
		if(list!=null&&list.size()>0) {
			return true;
		}
		return false;
	}
	//修改排序
	@Override
	public Map<String, Object> updatePlayTypeSeq(String cacheCodeName, String allId) {
		Map<String,Object> map=new HashMap<String,Object>();
		String[] strArray = null;   
		strArray = allId.split(",");//把字符串转为String数组
		if(strArray.length>0) {
			for(int a=0;a<strArray.length;a++) {
				Integer id=Integer.valueOf(strArray[a]);
				PlayType playType=playTypeDao.queryById(id);
//				PlayType playType=null;
				List<PlayType> playTypeCacheLists=null;
				if(playType!=null) {
//					playType=list.get(0);
					playType.setSeq(a+1);
					playTypeDao.updatePlayTypeSeq(playType);
					/*playTypeCacheLists=cacheRedisService.getPlayType(cacheCodeName);
					if(playTypeCacheLists!=null&&playTypeCacheLists.size()>0) {
						Integer id1=null;
						for(int i=0; i<playTypeCacheLists.size();i++)    {   
						     PlayType playType1=playTypeCacheLists.get(i);
						     id1=playType1.getId();
						     if((int)id1==(int)id) {
						    	playType1.setSeq(a+1);
						    	playTypeCacheLists.set(i, playType1);
							}
						 }
						cacheRedisService.setPlayType(cacheCodeName, playTypeCacheLists);
					}*/
					List<PlayType> playTypes=playTypeDao.queryByLotteryType(cacheCodeName);
					cacheRedisService.setPlayType(cacheCodeName, playTypes);
				}
			}
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return map;
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return map;
		}
	}
}


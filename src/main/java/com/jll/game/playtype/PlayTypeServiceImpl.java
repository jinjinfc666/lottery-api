package com.jll.game.playtype;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
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
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
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
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
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
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return map;
	}
	//修改
	@Override
	public Map<String,Object> updatePlayType(PlayType playType) {
		Integer id=playType.getId();
		String classification=playType.getClassification();
		String pdName=playType.getPtName();
		String ptDesc=playType.getPtDesc();
		Integer state=playType.getState();
		Integer mulSinFlag=playType.getMulSinFlag();
		Integer isHidden=playType.getIsHidden();
		Map<String,Object> map=new HashMap<String,Object>();
		boolean isNo=this.isNoPlayType(id);
		if(isNo) {
			playTypeDao.updatePlayType(id,classification, pdName, ptDesc,state,mulSinFlag,isHidden);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
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
}


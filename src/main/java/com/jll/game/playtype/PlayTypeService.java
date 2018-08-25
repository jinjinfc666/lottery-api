package com.jll.game.playtype;

import java.util.List;
import java.util.Map;

import com.jll.entity.PlayType;

public interface PlayTypeService
{
	
	/**
	 * query the play type 
	 * @param lotteryType
	 * @return
	 */
	List<PlayType> queryPlayType(String lotteryType);
	
	Map<String,Object> addPlayType(PlayType playType);
	
	Map<String,Object> updateState(Integer id,Integer state);
	
	Map<String,Object> updateIsHidden(Integer id, Integer isHidden);
	
	Map<String,Object> updateMulSinFlag(Integer id, Integer mulSinFlag);
	
	Map<String,Object> updatePlayType(PlayType playType);
	
	List<PlayType> queryByLotteryType(String lotteryType);
	
	PlayType queryById(Integer id);
	
	//添加时的验证
	boolean isNoPlayType(PlayType playType);
	//查询id是否存在
	boolean isNoPlayType(Integer id);
	//修改排序
	Map<String,Object> updatePlayTypeSeq(String lotteryType,String allId);
}

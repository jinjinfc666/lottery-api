package com.jll.game.playtype;

import java.util.List;

import com.jll.entity.PlayType;

public interface PlayTypeDao
{

	/**
	 * query the play type by lottery type
	 * @param lotteryType
	 * @return
	 */
	List<PlayType> queryByLotteryType(String lotteryType);
	
	void addPlayType(PlayType playType);
	
	Integer getCountSeq(String lotteryType);
	
	void updateState(Integer id,Integer state);
	
	PlayType queryById(Integer id);
	
	void updateIsHidden(Integer id, Integer isHidden);
	
	void updateMulSinFlag(Integer id,Integer mulSinFlag);
	
	void updatePlayType(PlayType playType);
	
	List<PlayType> queryLotteryType(String lotteryType);
	
	//查询这个玩法是否存在
	List<PlayType> queryByPlayType(PlayType playType);
	
	//修改排序
	void updatePlayTypeSeq(PlayType playType);
}

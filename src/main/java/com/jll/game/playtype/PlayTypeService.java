package com.jll.game.playtype;

import java.util.List;

import com.jll.entity.PlayType;

public interface PlayTypeService
{
	
	/**
	 * query the play type 
	 * @param lotteryType
	 * @return
	 */
	List<PlayType> queryPlayType(String lotteryType);
	
	void addPlayType(PlayType playType);
	
	void updateState(Integer id,Integer state);
	
	void updateIsHidden(Integer id, Integer isHidden);
	
	void updateMulSinFlag(Integer id, Integer mulSinFlag);
	
	void updatePlayType(PlayType playType);
}

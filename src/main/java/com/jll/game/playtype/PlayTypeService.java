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
}

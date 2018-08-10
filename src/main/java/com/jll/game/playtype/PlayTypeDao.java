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
		
}

package com.jll.game;

import java.util.Map;

import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.entity.Issue;
import com.jll.entity.PlayType;

public interface LotteryCenterService
{
	/**
	 * make a plan for all kinds of lottery
	 * the data will store in cache and database 
	 */
	void makeAPlan();
	
	/**
	 * if there is more available issue
	 * @param lotteryType
	 * @return
	 */
	boolean hasMoreIssue(String lotteryType);

	/**
	 * if the system code is existing 
	 * @param lotteryTypes
	 * @param lotteryType
	 * @return
	 *//*
	boolean isCodeExisting(SysCodeTypes lotteryTypes, String lotteryType);*/

	/**
	 * query issue that is accepting bet
	 * @param lotteryType
	 * @return
	 */
	Issue queryBettingIssue(String lotteryType);

	/**
	 * query the last issue that is finished 
	 * @param lotteryType
	 * @return
	 */
	Issue queryLastIssue(String lotteryType);

	String PreBet(Map<String, Object> params, Map<String, Object> data);

	void exeScheduleIssue();

	/**
	 * query the next issue of specified issue
	 * @param lastIssue
	 * @return
	 */
	Issue queryNextIssue(Issue lastIssue);
}

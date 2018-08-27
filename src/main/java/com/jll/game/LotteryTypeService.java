package com.jll.game;

import java.util.List;

import com.jll.entity.Issue;

public interface LotteryTypeService
{
	List<Issue> makeAPlan();
	
	String getLotteryType();
	
	/**
	 * 获取开奖号码
	 */
	void queryWinningNum(String issueNum);

	void payout(String issueNum);
}

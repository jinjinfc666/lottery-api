package com.jll.game;

import java.util.List;

import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;

public interface LotteryTypeService
{
	List<Issue> makeAPlan();
	
	String getLotteryType();
	
	/**
	 * 获取开奖号码
	 */
	void queryWinningNum(String issueNum);

	void payout(String issueNum);
	
	void payout(OrderInfo order,Issue issue, boolean isAuto);
}

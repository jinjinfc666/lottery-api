package com.jll.game;

import java.util.List;

import com.jll.entity.Issue;

public interface LotteryTypeService
{
	List<Issue> makeAPlan();
	
	String getLotteryType();
}

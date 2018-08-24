package com.jll.game;

import java.util.List;

import com.jll.entity.Issue;

public interface IssueService
{
	
	/**
	 * @param issues
	 */
	void savePlan(List<Issue> issues);

	/**
	 * save or update the issue
	 * @param currIssue
	 */
	void saveIssue(Issue currIssue);

	Issue getIssueById(Integer id);

	Issue getIssueByIssueNum(String issueNum);
	
	//通过彩种和期次数量来查找
	List<Issue> queryByLTNumber(String lotteryType,Integer number);
}

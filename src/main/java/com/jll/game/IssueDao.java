package com.jll.game;

import java.util.Date;
import java.util.List;

import com.jll.entity.Issue;

public interface IssueDao
{
		
	void savePlan(List<Issue> issues);

	void saveIssue(Issue currIssue);

	Issue getIssueById(Integer id);

	Issue getIssueByIssueNum(String issueNum);
	
	//通过彩种和期次数量来查找
	List<Issue> queryByLTNumber(String lotteryType, Date time,Integer number);
	//通过当前时间和彩种    获得离当前时间最近的一期成功的期次时间
	List<Issue> queryByLTNumeber(String lotteryType);
}

package com.jll.game;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jll.entity.Issue;
import com.jll.entity.SysCode;

public interface IssueDao
{
		
	void savePlan(List<Issue> issues);

	void saveIssue(Issue currIssue);

	Issue getIssueById(Integer id);

	Issue getIssueByIssueNum(String lottoType, String issueNum);
	
	//通过彩种和期次数量来查找
	List<Issue> queryByLTNumber(String lotteryType, Date time,Integer number);
	//通过当前时间和彩种    获得离当前时间最近的一期成功的期次时间
	List<Issue> queryByLTNumeber(String lotteryType);
	//统一撤单需要的期号
	Map<String,Object> queryAllByIssue(String lotteryType,Integer state,String startTime,String endTime,Integer pageIndex,Integer pageSize,String issueNum,Map<String, SysCode> sysCodes);
	//追号需要的期号信息
	List<Issue> queryIsZhIssue(String lotteryType,Date startTime,Date endTime);
}

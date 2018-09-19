package com.jll.game;

import java.util.List;
import java.util.Map;

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

	Issue getIssueByIssueNum(String lottoType, String issueNum);
	
	//通过彩种和期次数量来查找
	List<Issue> queryByLTNumber(String lotteryType,Integer number);

	Map<String, Object> updateIssueOpenNum(String issueNum, Map<String, String> params);

	Map<String, Object> betOrderPayout(String issueNum);

	Map<String, Object> calcelIssuePayout(String issueNum);

	Map<String, Object> betOrderRePayout(String issueNum);

	Map<String, Object> issueDisbale(String issueNum);

	Map<String, Object> issueDelayePayout(String issueNum, Map<String, String> params);

	Map<String, Object> orderCancel(String orderNum);

	Map<String, Object> manualPayoutOrder(String orderNum);
}

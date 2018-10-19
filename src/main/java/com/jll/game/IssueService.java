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

	Map<String, Object> betOrderPayout(String lottoType, String issueNum, Map<String, String> params);

	Map<String, Object> calcelIssuePayout(String lottoType, String issueNum, Map<String, String> params);

	Map<String, Object> betOrderRePayout(String lottoType, String issueNum, Map<String, String> params);

	Map<String, Object> updateIssueDisbale(String lottoType, String issueNum, Map<String, String> params);

	Map<String, Object> issueDelayePayout(String issueNum, Map<String, String> params);

	Map<String, Object> orderCancel(String orderNum);

	Map<String, Object> manualPayoutOrder(String orderNum);

	/**
	 * 手动开奖
	 * @param lottoType
	 * @param issueNum
	 * @param winningNum
	 * @return
	 */
	String manualDrawResult(String lottoType, 
			String issueNum, 
			String winningNum);

	/**
	 * 判断是否允许手动开奖
	 * 对于cqssc, gd11x5, bjpk10 默认允许手动开奖
	 * 对于私彩，除非指定开奖模式，否则不许允许手动开奖
	 * @param lottoType
	 * @param issueNum
	 * @return
	 */
	boolean isManualPrieModel(String lottoType, String issueNum);
	//统一撤单需要的期号
	Map<String,Object> queryAllByIssue(String lotteryType, Integer state,String startTime, String endTime, Integer pageIndex,Integer pageSize, String issueNum);
	//追号需要的期号信息
	Map<String,Object> queryIsZhIssue(String lotteryType);
}

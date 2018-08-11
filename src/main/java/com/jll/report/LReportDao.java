package com.jll.report;

import java.util.List;
import java.util.Map;

import com.jll.entity.LotteryPlReport;

/**
 *对应LotteryPlReport实体
 * @author Silence 
 */
public interface LReportDao {
	//团队盈亏报表(按彩种查询)
	public List<LotteryPlReport> queryLReport(String codeName,String startTime,String endTime,String userName);
	//团队盈亏报表(按彩种查询)总计
	public Map<String,Object> queryLReportSum(String codeName,String startTime,String endTime,String userName);
	//团队盈亏报表(按彩种查询) 下级
	public Map<String,Object> queryLReportNext(String codeName,String startTime,String endTime,String userName);
}

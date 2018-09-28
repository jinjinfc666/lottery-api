package com.jll.report;

import java.util.List;
import java.util.Map;

import com.jll.dao.PageBean;
import com.jll.entity.LotteryPlReport;

/**
 *对应LotteryPlReport实体
 */
public interface LReportDao {
	//团队盈亏报表(按彩种查询)
	public PageBean queryLReport(String codeName,String startTime,String endTime,String userName,Integer pageIndex,Integer pageSize,List<?> userNameList);
	//团队盈亏报表(按彩种查询)总计
	public Map<String,Object> queryLReportSum(String codeName,String startTime,String endTime,String userName,List<?> userNameList);
	//团队盈亏报表(按彩种查询) 下级
	public Map<String,Object> queryLReportNext(String codeName,String startTime,String endTime,String userName);
}

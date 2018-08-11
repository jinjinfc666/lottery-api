package com.jll.report;

import java.util.List;
import java.util.Map;

import com.jll.entity.LotteryPlReport;

public interface LReportService {
	//团队盈亏报表(按彩种查询)
	public List<LotteryPlReport> queryLReport(Map<String,Object> ret);
	//团队盈亏报表(按彩种查询)总计
	public Map<String,Object> queryLReportSum(Map<String,Object> ret);
	//团队盈亏报表(按彩种查询) 下级
	public Map<String,Object> queryLReportNext(Map<String,Object> ret);
}

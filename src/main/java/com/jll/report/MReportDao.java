package com.jll.report;

import java.util.List;
import java.util.Map;

import com.jll.entity.MemberPlReport;

public interface MReportDao {
	//会员盈亏报表
	public List<MemberPlReport> queryAll(String startTime,String endTime,String userName);
	public Map<String,Object> querySum(String startTime,String endTime,String userName);
	//团队盈亏报表
	public List<MemberPlReport> queryTeamAll(String startTime,String endTime,String userName);
	public Map<String,Object> queryTeamSum(String startTime,String endTime,String userName);
	//查找下级
	public Map<String,Object> queryNextTeamAll(String startTime,String endTime,String userName);
}


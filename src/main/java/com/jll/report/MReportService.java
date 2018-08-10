package com.jll.report;

import java.util.List;
import java.util.Map;

import com.jll.entity.MemberPlReport;

public interface MReportService {
	//会员盈亏报表
	public List<MemberPlReport> queryAll(Map<String,Object> ret);
	public Map<String,Object> querySum(Map<String,Object> ret);
	//团队盈亏报表
	public List<MemberPlReport> queryTeamAll(Map<String,Object> ret);
	public Map<String,Object> querySumTeam(Map<String,Object> ret);
	//查找下级
	public Map<String,Object> queryNextTeamAll(Map<String,Object> ret);
}

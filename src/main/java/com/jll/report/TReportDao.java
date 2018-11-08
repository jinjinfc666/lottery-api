package com.jll.report;

import java.util.List;
import java.util.Map;

import com.jll.dao.PageBean;
import com.jll.entity.MemberPlReport;

public interface TReportDao {
	//团队盈亏报表
	public PageBean queryTeamAll(String startTime, String endTime, String userName,Integer pageIndex,Integer pageSize);
	//查找下级代理
	public Map<String,Object> queryNextTeamAll(String startTime, String endTime, String userName);
}


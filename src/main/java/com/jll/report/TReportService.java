package com.jll.report;

import java.util.Map;

import com.jll.dao.PageBean;

public interface TReportService {

	//团队盈亏报表
	public PageBean queryTeamAll(Map<String, Object> ret);
	//查找下级
	public Map<String,Object> queryNextTeamAll(Map<String, Object> ret);
}

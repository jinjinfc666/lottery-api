package com.jll.report;

import java.util.Map;

import com.jll.dao.PageBean;

public interface MReportService {
	//会员盈亏报表
	public PageBean queryAll(Map<String,Object> ret);
}

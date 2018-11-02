package com.jll.report;

import java.util.List;
import java.util.Map;

import com.jll.dao.PageBean;
import com.jll.entity.MemberPlReport;

public interface MReportDao {
	//会员盈亏报表
	public PageBean queryAll(String startTime,String endTime,String userName,Integer pageIndex,Integer pageSize);
}


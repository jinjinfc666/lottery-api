package com.jll.report;

import java.util.List;
import java.util.Map;

import com.jll.dao.PageBean;
import com.jll.entity.MemberPlReport;

public interface MReportDao {
	//会员盈亏报表
	public PageBean queryAll(String startTime,String endTime,String userName,Integer pageIndex,Integer pageSize);
	public Map<String,Object> querySum(String startTime,String endTime,String userName);
	//团队盈亏报表
	public PageBean queryTeamAll(String startTime,String endTime,String userName,Integer pageIndex,Integer pageSize,List<?> userNameList);
	public Map<String,Object> queryTeamSum(String startTime,String endTime,String userName,List<?> userNameList);
	//查找下级
	public Map<String,Object> queryNextTeamAll(String startTime,String endTime,String userName);
}


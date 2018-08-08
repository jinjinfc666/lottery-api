package com.jll.report;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.LotteryPlReport;



@Service
@Transactional
public class LReportServiceImpl implements LReportService {
	@Resource
	LReportDao lReportDao;
	//团队盈亏报表(按彩种查询)
	@Override
	public List<LotteryPlReport> queryLReport(Map<String, Object> ret) {
		String codeName=(String) ret.get("codeName");
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String userName=(String) ret.get("userName");
		return lReportDao.queryLReport(codeName,startTime, endTime, userName);
	}
	//团队盈亏报表(按彩种查询)总计
	@Override
	public Map<String,Object> queryLReportSum(Map<String, Object> ret) {
		String codeName=(String) ret.get("codeName");
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String userName=(String) ret.get("userName");
		return lReportDao.queryLReportSum(codeName,startTime, endTime, userName);
	}
	//团队盈亏报表(按彩种查询) 下级
	@Override
	public Map<String,Object> queryLReportNext(Map<String, Object> ret) {
		String codeName=(String) ret.get("codeName");
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String userName=(String) ret.get("userName");
		return lReportDao.queryLReportNext(codeName,startTime, endTime, userName);
	}
}

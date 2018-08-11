package com.jll.report;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.MemberPlReport;



@Service
@Transactional
public class MReportServiceImpl implements MReportService {
	@Resource
	MReportDao mReportDao;
	//会员盈亏报表
	@Override
	public List<MemberPlReport> queryAll(Map<String,Object> ret) {
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String userName=(String) ret.get("userName");
		return mReportDao.queryAll(startTime, endTime, userName);
	}
	@Override
	public Map<String, Object> querySum(Map<String, Object> ret) {
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String userName=(String) ret.get("userName");
		return mReportDao.querySum(startTime, endTime, userName);
	}
	//团队盈亏报表
	@Override
	public List<MemberPlReport> queryTeamAll(Map<String, Object> ret) {
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String userName=(String) ret.get("userName");
		return mReportDao.queryTeamAll(startTime, endTime, userName);
	}
	@Override
	public Map<String, Object> querySumTeam(Map<String, Object> ret) {
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String userName=(String) ret.get("userName");
		return mReportDao.queryTeamSum(startTime, endTime, userName);
	}
	//查找下级
	@Override
	public Map<String,Object> queryNextTeamAll(Map<String, Object> ret) {
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String userName=(String) ret.get("userName");
		return mReportDao.queryNextTeamAll(startTime, endTime, userName);
	}
}

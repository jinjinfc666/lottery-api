package com.jll.report;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.dao.PageBean;
import com.jll.entity.LotteryPlReport;
import com.jll.user.UserInfoDao;



@Service
@Transactional
public class LReportServiceImpl implements LReportService {
	@Resource
	LReportDao lReportDao;
	@Resource
	UserInfoDao userInfoDao;
	//团队盈亏报表(按彩种查询)
	@Override
	public PageBean queryLReport(Map<String, Object> ret) {
		String codeName=(String) ret.get("codeName");
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String userName=(String) ret.get("userName");
		Integer pageIndex=(Integer) ret.get("pageIndex");
		Integer pageSize=(Integer) ret.get("pageSize");
		List<?> userNameList=null;
		if(StringUtils.isBlank(userName)) {
			userNameList=userInfoDao.queryByAll();
		}
		return lReportDao.queryLReport(codeName,startTime, endTime, userName,pageIndex,pageSize,userNameList);
	}
	//团队盈亏报表(按彩种查询)总计
	@Override
	public Map<String,Object> queryLReportSum(Map<String, Object> ret) {
		String codeName=(String) ret.get("codeName");
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String userName=(String) ret.get("userName");
		List<?> userNameList=null;
		if(StringUtils.isBlank(userName)) {
			userNameList=userInfoDao.queryByAll();
		}
		return lReportDao.queryLReportSum(codeName,startTime, endTime, userName,userNameList);
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

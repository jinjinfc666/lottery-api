package com.jll.report;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.dao.PageBean;
import com.jll.user.UserInfoDao;



@Service
@Transactional
public class MReportServiceImpl implements MReportService {
	@Resource
	MReportDao mReportDao;
	@Resource
	UserInfoDao userInfoDao;
	//会员盈亏报表
	@Override
	public PageBean queryAll(Map<String,Object> ret) {
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String userName=(String) ret.get("userName");
		Integer pageIndex=(Integer) ret.get("pageIndex");
		Integer pageSize=(Integer) ret.get("pageSize");
		return mReportDao.queryAll(startTime, endTime, userName,pageIndex,pageSize);
	}
}

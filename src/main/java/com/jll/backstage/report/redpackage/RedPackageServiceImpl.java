package com.jll.backstage.report.redpackage;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class RedPackageServiceImpl implements RedPackageService {
	@Resource
	RedPackageDao redPackageDao;
	@Override
	public List<?> queryRedUserAccountDetails(Map<String, Object> ret) {
		String userName=(String)ret.get("userName");
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		return redPackageDao.queryRedUserAccountDetails(userName,startTime,endTime);
	}
}

package com.jll.report;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service
@Transactional
public class PPLServiceImpl implements PPLService {
	@Resource
	PPLDao pPLDao;
	//平台盈亏
	@Override
	public List<?> queryPPL(Map<String, Object> ret) {
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String codeName=(String) ret.get("codeName");
		String issueNum=(String) ret.get("issueNum");
		String playTypeid=(String) ret.get("playTypeid");
		return pPLDao.queryPPL(startTime, endTime, codeName, issueNum, playTypeid);
	}
}

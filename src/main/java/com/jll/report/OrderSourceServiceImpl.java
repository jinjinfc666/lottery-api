package com.jll.report;

import java.util.Map;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service
@Transactional
public class OrderSourceServiceImpl implements OrderSourceService {
	@Resource
	OrderSourceDao orderSourceDao;
	//添加
	@Override
	public Map<String,Object> queryOrderSource(Map<String, Object> ret) {
		String codeName=(String) ret.get("codeName");
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		return orderSourceDao.queryOrderSource(codeName,startTime, endTime);
	}
	//添加
	@Override
	public void addOrderSource() {
		
		
	}
}


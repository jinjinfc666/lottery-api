package com.jll.report;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class DWDetailsServiceImpl implements DWDetailsService {
	@Resource
	DWDetailsDao dWDetailsDao;
	@Override
	public List<?> queryDetails(Map<String, Object> ret) {
		String type=(String)ret.get("type");
		Integer state=(Integer) ret.get("state");
		String userName=(String) ret.get("userName");
		String orderNum=(String) ret.get("orderNum");
		Float amountStart=(Float) ret.get("amountStart");
		Float amountEnd=(Float) ret.get("amountEnd");
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		return dWDetailsDao.queryDetails(type,state,userName,orderNum,amountStart,amountEnd,startTime,endTime);
	}
	@Override
	public List<?> queryDWDetails(Map<String, Object> ret) {
		String type=(String)ret.get("type");
		Integer id=(Integer) ret.get("id");
		return dWDetailsDao.queryDWDetails(type, id);
	}
}

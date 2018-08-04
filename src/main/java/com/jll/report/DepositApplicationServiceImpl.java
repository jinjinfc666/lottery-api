package com.jll.report;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service
@Transactional
public class DepositApplicationServiceImpl implements DepositApplicationService {
	@Resource
	DepositApplicationDao depositApplicationDao;
	@Override
	public List<?> queryDetails(Map<String,Object> ret){
		String userName=(String) ret.get("userName");
		String orderNum=(String) ret.get("orderNum");
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		return depositApplicationDao.queryDetails(userName,orderNum,startTime,endTime);
	}
	@Override
	public void updateState(Map<String, Object> ret) {
		Integer id=(Integer) ret.get("id");
		Integer state=(Integer) ret.get("state");
		depositApplicationDao.updateState(id, state);
	}
}

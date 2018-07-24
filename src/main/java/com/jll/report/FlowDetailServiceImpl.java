package com.jll.report;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.SysCode;


@Service
@Transactional
public class FlowDetailServiceImpl implements FlowDetailService {
	@Resource
	FlowDetailDao flowDetailDao;
	@Override
	public List<?> queryUserAccountDetails(Map<String, Object> ret) {
		String userName=(String)ret.get("userName");
		Integer orderId=(Integer)ret.get("orderId");
		Float amountStart=(Float)ret.get("amountStart");
		Float amountEnd=(Float)ret.get("amountEnd");
		String operationType=(String)ret.get("operationType");
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		
		return flowDetailDao.queryUserAccountDetails(userName,orderId,amountStart,amountEnd,operationType,startTime,endTime);
	}
	@Override
	public List<SysCode> queryType() {
		return flowDetailDao.queryType();
	}
}

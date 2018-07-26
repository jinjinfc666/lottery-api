package com.jll.report;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class LoyTstServiceImpl implements LoyTstService {
	@Resource
	LoyTstDao loyTstDao;
	@Override
	public List<?> queryLoyTst(Map<String, Object> ret) {
		String lotteryType=(String)ret.get("lotteryType");
		Integer isZh=(Integer) ret.get("isZh");
		Integer state=(Integer) ret.get("state");
		Integer terminalType=(Integer)ret.get("terminalType");
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String issueNum=(String)ret.get("issueNum");
		String userName=(String) ret.get("userName");
		String orderNum=(String) ret.get("orderNum");
		return loyTstDao.queryLoyTst(lotteryType,isZh,state,terminalType,startTime,endTime,issueNum,userName,orderNum);
	}
}

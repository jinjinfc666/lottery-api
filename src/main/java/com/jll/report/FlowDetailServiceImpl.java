package com.jll.report;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.entity.SysCode;



@Service
@Transactional
public class FlowDetailServiceImpl implements FlowDetailService {
	@Resource
	FlowDetailDao flowDetailDao;
	@Resource
	CacheRedisService cacheRedisService;
	@Override
	public Map<String,Object> queryUserAccountDetails(Map<String, Object> ret) {
		String userName=(String)ret.get("userName");
		String orderNum=(String)ret.get("orderNum");
		Float amountStart=(Float)ret.get("amountStart");
		Float amountEnd=(Float)ret.get("amountEnd");
		String operationType=(String)ret.get("operationType");
		String startTime=(String) ret.get("startTime");
		String endTime=(String) ret.get("endTime");
		String codeTypeName=Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
		SysCode sysCode=cacheRedisService.getSysCode(codeTypeName,codeTypeName);
		Integer codeTypeNameId=sysCode.getId();
		return flowDetailDao.queryUserAccountDetails(codeTypeNameId,userName,orderNum,amountStart,amountEnd,operationType,startTime,endTime);
	}
	
}

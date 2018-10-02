package com.jll.report;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.entity.DepositApplication;
import com.jll.entity.WithdrawApplication;



@Service
@Transactional
public class WithdrawApplicationServiceImpl implements WithdrawApplicationService {
	@Resource
	WithdrawApplicationDao withdrawApplicationDao;
	@Override
	public WithdrawApplication queryDetails(Integer id){
		WithdrawApplication dep=withdrawApplicationDao.queryDetails(id);
		return dep;
	}
	
	@Override
	public long getUserWithdrawCount(int userId, Date start, Date end) {
		return withdrawApplicationDao.getUserWithdrawCount(userId,start,end);
	}
	@Override
	public Map<String,Object> updateState(Map<String, Object> ret) {
		Map<String,Object> map=new HashMap<String,Object>();
		Integer id=(Integer) ret.get("id");
		Integer state=(Integer) ret.get("state");
		Integer remarkInteger=(Integer) ret.get("remark");
		boolean isNo=this.isNullById(id);
		if(isNo) {
			String remark=Constants.Remark.getValueByCode(remarkInteger);
			withdrawApplicationDao.updateState(id, state, remark);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return map;
	}
	//判断是否存在
	@Override
	public boolean isNullById(Integer id) {
		WithdrawApplication list=withdrawApplicationDao.queryDetails(id);
		if(list!=null) {
			return true;
		}
		return false;
	}
}

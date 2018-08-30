package com.jll.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Message;
import com.jll.entity.DepositApplication;
import com.jll.entity.IpBlackList;




@Service
@Transactional
public class DepositApplicationServiceImpl implements DepositApplicationService {
	@Resource
	DepositApplicationDao depositApplicationDao;
//	@Override
//	public List<?> queryDetails(Map<String,Object> ret){
//		String userName=(String) ret.get("userName");
//		String orderNum=(String) ret.get("orderNum");
//		String startTime=(String) ret.get("startTime");
//		String endTime=(String) ret.get("endTime");
//		return depositApplicationDao.queryDetails(userName,orderNum,startTime,endTime);
//	}
	@Override
	public Map<String,Object> updateState(Map<String, Object> ret) {
		Map<String,Object> map=new HashMap<String,Object>();
		Integer id=(Integer) ret.get("id");
		Integer state=(Integer) ret.get("state");
		boolean isNo=this.isNullById(id);
		if(isNo) {
			depositApplicationDao.updateState(id, state);
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
		List<DepositApplication> list=depositApplicationDao.queryById(id);
		if(list!=null&&list.size()>0) {
			return true;
		}
		return false;
	}
}

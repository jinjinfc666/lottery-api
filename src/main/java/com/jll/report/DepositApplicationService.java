package com.jll.report;

import java.util.Map;

import com.jll.entity.DepositApplication;


public interface DepositApplicationService {
//	List<?> queryDetails(Map<String,Object> ret);
	public Map<String,Object> updateState(Map<String,Object> ret);
	//判断是否存在
	boolean isNullById(Integer id);
	
	Map<String, Object> getDepositInfoByOrderNum(String orderNum);
	
	public Map<String, Object> addAmountByDepositOrder(DepositApplication dep);
}

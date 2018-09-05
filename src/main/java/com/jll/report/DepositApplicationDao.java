package com.jll.report;

import java.util.List;

import com.jll.entity.DepositApplication;


public interface DepositApplicationDao {
//	public List<?> queryDetails(String userName,String orderNum,String startTime,String endTime) ;
	public void updateState(Integer id,Integer state);
	//查询是否存在
	List<DepositApplication> queryById(Integer id);
	DepositApplication getDepositInfoByOrderNum(String orderNum);
}

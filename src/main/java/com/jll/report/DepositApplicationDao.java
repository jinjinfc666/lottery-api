package com.jll.report;

import java.util.List;


public interface DepositApplicationDao {
	public List<?> queryDetails(String userName,String orderNum,String startTime,String endTime) ;
	public void updateState(Integer id,Integer state);
}

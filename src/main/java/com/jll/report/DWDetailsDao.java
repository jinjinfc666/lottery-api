package com.jll.report;


import java.util.List;

public interface DWDetailsDao {
	public List<?> queryDetails(String type,Integer state,String userName,String orderNum,Float amountStart,Float amountEnd,String startTime,String endTime);
	public List<?> queryDWDetails(String type,Integer id);
}

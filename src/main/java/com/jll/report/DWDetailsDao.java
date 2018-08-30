package com.jll.report;


import java.util.List;
import java.util.Map;

public interface DWDetailsDao {
	public Map<String,Object> queryDetails(String type,Integer state,String userName,String orderNum,Float amountStart,Float amountEnd,String startTime,String endTime,Integer pageIndex,Integer pageSize);
//	public List<?> queryDWDetails(String type,Integer id);
}

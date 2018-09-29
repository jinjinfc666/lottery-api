package com.jll.report;

import java.util.Map;

import com.jll.entity.OrderSource;


/**
 *对应LotteryPlReport实体
 */
public interface OrderSourceDao {
	//查询
	public Map<String,Object> queryOrderSource(String codeName,String startTime,String endTime);
	
}

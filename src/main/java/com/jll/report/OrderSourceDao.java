package com.jll.report;

import java.util.Map;

import com.jll.entity.OrderSource;


/**
 *对应LotteryPlReport实体
 * @author Silence 
 */
public interface OrderSourceDao {
	//查询
	public Map<String,Object> queryOrderSource(String codeName,String startTime,String endTime);
	//添加
	public void addOrderSource(OrderSource orderSource);
	
}

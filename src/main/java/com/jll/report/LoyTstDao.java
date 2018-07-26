package com.jll.report;


import java.util.List;

public interface LoyTstDao {
	public List<?> queryLoyTst(String lotteryType,Integer isZh,Integer state,Integer terminalType,String startTime,String endTime,String issueNum,String userName,String orderNum);
}

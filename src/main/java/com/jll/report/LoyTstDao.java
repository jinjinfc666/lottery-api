package com.jll.report;


import java.util.List;

import com.jll.dao.PageBean;

public interface LoyTstDao {
	public PageBean queryLoyTst(Integer codeTypeNameId,String lotteryType,Integer isZh,Integer state,Integer terminalType,String startTime,String endTime,String issueNum,String userName,String orderNum,Integer pageIndex,Integer pageSize);
}

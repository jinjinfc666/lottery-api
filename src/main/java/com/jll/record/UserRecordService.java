package com.jll.record;

import java.util.Map;

import com.jll.dao.PageQueryDao;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccountDetails;


public interface UserRecordService {
	
	Map<String, Object> getUserBetRecord(OrderInfo pramsInfo,PageQueryDao query);
	Map<String, Object> getUserBetType();
	Map<String, Object> getUserCreditType();
	Map<String, Object> getUserCreditRecord(UserAccountDetails query, PageQueryDao page);

}

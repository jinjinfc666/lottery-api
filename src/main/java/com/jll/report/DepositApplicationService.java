package com.jll.report;

import java.util.List;
import java.util.Map;


public interface DepositApplicationService {
	List<?> queryDetails(Map<String,Object> ret);
	public Map<String,Object> updateState(Map<String,Object> ret);
	//判断是否存在
	boolean isNullById(Integer id);
}

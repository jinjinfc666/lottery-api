package com.jll.report;

import java.util.List;
import java.util.Map;


public interface DepositApplicationService {
	List<?> queryDetails(Map<String,Object> ret);
	public void updateState(Map<String,Object> ret);
}

package com.jll.report;

import java.util.List;
import java.util.Map;

import com.jll.entity.SysCode;

public interface FlowDetailService {
	List<?> queryUserAccountDetails(Map<String,Object> ret);
	List<SysCode> queryType();
}

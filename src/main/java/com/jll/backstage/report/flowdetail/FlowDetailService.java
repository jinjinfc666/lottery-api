package com.jll.backstage.report.flowdetail;

import java.util.List;
import java.util.Map;

import com.jll.entity.SysCode;

public interface FlowDetailService {
	List<?> queryUserAccountDetails(Map<String,Object> ret);
	List<SysCode> queryType();
}

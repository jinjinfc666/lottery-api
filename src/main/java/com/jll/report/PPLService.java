package com.jll.report;

import java.util.List;
import java.util.Map;


public interface PPLService {
	//平台盈亏
	public List<?> queryPPL(Map<String,Object> ret);
}

package com.jll.report;

import java.util.Map;


public interface OrderSourceService {
	public Map<String,Object> queryOrderSource(Map<String,Object> ret);
	public void addOrderSource();
}

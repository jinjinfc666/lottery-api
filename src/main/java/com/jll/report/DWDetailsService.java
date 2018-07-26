package com.jll.report;


import java.util.List;
import java.util.Map;


public interface DWDetailsService {
	List<?> queryDetails(Map<String,Object> ret);
	List<?> queryDWDetails(Map<String,Object> ret);
}

package com.jll.help;

import java.util.Map;

import com.jll.dao.PageQueryDao;


public interface HelpService {
	
	Map<String, Object> getHistoryOpenRecord(String lotteryType, PageQueryDao page);

	Map<String, Object> getLottoryPlayType(String lotteryType, PageQueryDao page);
}

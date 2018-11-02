package com.jll.report;

import java.util.List;

public interface PPLDao {
	//平台盈亏
	public List<?> queryPPL(String startTime, String endTime, String codeName, String issueNum, String playTypeid);
}


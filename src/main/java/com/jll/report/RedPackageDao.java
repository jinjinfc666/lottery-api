package com.jll.report;


import java.util.List;



public interface RedPackageDao {
	
	public List<?> queryRedUserAccountDetails(String userName,String startTime,String endTime);
}

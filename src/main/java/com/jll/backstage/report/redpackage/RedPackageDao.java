package com.jll.backstage.report.redpackage;


import java.util.List;



public interface RedPackageDao {
	
	public List<?> queryRedUserAccountDetails(String userName,String startTime,String endTime);
}

package com.jll.sys.promo;


import java.util.List;

import com.jll.dao.PageBean;
import com.jll.entity.SignupRec;

public interface PromoDao
{
	//查询用户的活动记录
	PageBean queryRecord(Integer userId,String startTime,String endTime,Integer pageIndex,Integer pageSize);
}

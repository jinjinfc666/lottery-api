package com.jll.report;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class OrderInfoServiceImpl implements OrderInfoService {
	@Resource
	OrderInfoDao orderInfoDao;
	@Override
	public boolean isOrderInfo(String orderNum) {
		long count=orderInfoDao.getCountOrderInfo(orderNum);
		return count == 0 ? false:true;
	}
}

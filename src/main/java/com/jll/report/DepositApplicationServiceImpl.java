package com.jll.report;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.DepositApplication;



@Service
@Transactional
public class DepositApplicationServiceImpl implements DepositApplicationService {
	@Resource
	DepositApplicationDao depositApplicationDao;
	@Override
	public DepositApplication queryDetails(Integer id){
		DepositApplication dep=depositApplicationDao.queryDetails(id);
		return dep;
	}
}

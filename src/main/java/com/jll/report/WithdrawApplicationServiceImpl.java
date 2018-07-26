package com.jll.report;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.WithdrawApplication;



@Service
@Transactional
public class WithdrawApplicationServiceImpl implements WithdrawApplicationService {
	@Resource
	WithdrawApplicationDao withdrawApplicationDao;
	@Override
	public WithdrawApplication queryDetails(Integer id){
		WithdrawApplication dep=withdrawApplicationDao.queryDetails(id);
		return dep;
	}
}

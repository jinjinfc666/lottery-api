package com.jll.report;

import java.util.Date;

import com.jll.entity.WithdrawApplication;

public interface WithdrawApplicationService {
	WithdrawApplication queryDetails(Integer id);
	long getUserWithdrawCount(int userId, Date start, Date end);
}

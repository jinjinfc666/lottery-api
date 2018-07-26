package com.jll.report;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class IssueServiceImpl implements IssueService {
	@Resource
	IssueDao issueDao;
	@Override
	public boolean isIssue(String issueNum) {
		long count=issueDao.getCountIssue(issueNum);
		return count == 0 ? false:true;
	}
}

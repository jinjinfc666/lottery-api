package com.jll.game;


import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.Issue;

@Repository
public class IssueDaoImpl extends DefaultGenericDaoImpl<Issue> implements IssueDao
{
	private Logger logger = Logger.getLogger(IssueDaoImpl.class);

	@Override
	public void savePlan(List<Issue> issues) {
		for(Issue issue : issues) {
			this.saveOrUpdate(issue);
		}
	}


}

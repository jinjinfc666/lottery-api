package com.jll.game;


import java.util.ArrayList;
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

	@Override
	public void saveIssue(Issue currIssue) {
		this.saveOrUpdate(currIssue);
	}

	@Override
	public Issue getIssueById(Integer id) {
		String sql = "from Issue where id=?";
		List<Object> params = new ArrayList<>();
		params.add(id);
		List<Issue> result = query(sql, params, Issue.class);
		if(result == null || result.size() == 0) {
			return null;
		}
		
		return result.get(0);
	}

	@Override
	public Issue getIssueByIssueNum(String issueNum) {
		String sql = "from Issue where issueNum =?";
		List<Object> params = new ArrayList<>();
		params.add(issueNum);
		
		List<Issue> result = query(sql, params, Issue.class);
		if(result == null || result.size() == 0) {
			return null;
		}
		
		return result.get(0);
	}


}

package com.jll.game;

import java.util.List;

import com.jll.entity.Issue;

public interface IssueDao
{
		
	void savePlan(List<Issue> issues);

	void saveIssue(Issue currIssue);
}

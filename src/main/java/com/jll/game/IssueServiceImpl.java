package com.jll.game;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.Issue;

@Configuration
@PropertySource("classpath:sys-setting.properties")
@Service
@Transactional
public class IssueServiceImpl implements IssueService
{
	private Logger logger = Logger.getLogger(IssueServiceImpl.class);

	@Resource
	IssueDao issueDao;
	
	@Override
	public void savePlan(List<Issue> issues) {
		if(issues == null || issues.size() == 0) {
			return ;
		}
		
		issueDao.savePlan(issues);
	}

	@Override
	public void saveIssue(Issue currIssue) {
		issueDao.saveIssue(currIssue);
	}

	@Override
	public Issue getIssueById(Integer id) {
		return issueDao.getIssueById(id);
	}
	
	
}

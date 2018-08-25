package com.jll.game;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.IpBlackList;
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


	//通过彩种和期次数量来查找
	@Override
	public List<Issue> queryByLTNumber(String lotteryType,Date time, Integer number) {
		String sql="from Issue  where lotteryType=:lotteryType AND startTime<=:time ORDER BY id DESC";
		Query<Issue> query = getSessionFactory().getCurrentSession().createQuery(sql,Issue.class);
		query.setFirstResult(0);
		query.setMaxResults(number);
	    query.setParameter("lotteryType", lotteryType);
	    query.setParameter("time", time,TimestampType.INSTANCE);
	    List<Issue> list = query.list();
		return list;
	}
	//通过当前时间和彩种    获得离当前时间最近的一期成功的期次时间
	@Override
	public List<Issue> queryByLTNumeber(String lotteryType) {
		Integer state=4;
		String sql="from Issue  where state=:state and lotteryType=:lotteryType AND startTime<=:time ORDER BY id DESC";
		Query<Issue> query = getSessionFactory().getCurrentSession().createQuery(sql,Issue.class);
		query.setFirstResult(0);
		query.setMaxResults(1);
		query.setParameter("state", state);
	    query.setParameter("lotteryType", lotteryType);
	    query.setParameter("time", new Date(),TimestampType.INSTANCE);
	    List<Issue> list = query.list();
		return list;
	}
}

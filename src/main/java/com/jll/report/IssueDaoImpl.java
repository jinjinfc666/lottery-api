package com.jll.report;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;





@Repository
public class IssueDaoImpl extends HibernateDaoSupport implements IssueDao {
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	@Override
	public long getCountIssue(String issueNum) {
		String sql = "select count(*) from Issue where issueNum=?";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter(0, issueNum);
	    long count = ((Number)query.iterate().next()).longValue();
	    return count;
	}
	
}


package com.jll.pay.zhihpay;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class ZhihPayDaoImpl extends HibernateDaoSupport implements ZhihPayDao
{
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory)
	{
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public long queryWhiteListCount(String ip, String roleName) {
		String sql = "select count(*) from WhiteList where ip=? and roleName =?";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter(0, ip);
	    query.setParameter(1, roleName);
	    long count = ((Number)query.iterate().next()).longValue();
	    return count;
	}
	
	@Override
	public long queryDepositOrderCount(int orderId) {
		String sql = "select count(*) from MoneyInInfo where recordID=?";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter(0, orderId);
	    long count = ((Number)query.iterate().next()).longValue();
	    return count;
	}
}

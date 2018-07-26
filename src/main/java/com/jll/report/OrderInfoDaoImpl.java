package com.jll.report;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;





@Repository
public class OrderInfoDaoImpl extends HibernateDaoSupport implements OrderInfoDao {
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	@Override
	public long getCountOrderInfo(String orderNum) {
		String sql = "select count(*) from OrderInfo where orderNum=?";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter(0, orderNum);
	    long count = ((Number)query.iterate().next()).longValue();
	    return count;
	}
	
}


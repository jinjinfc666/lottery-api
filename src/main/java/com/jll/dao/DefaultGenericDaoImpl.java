package com.jll.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

public class DefaultGenericDaoImpl<T> extends HibernateDaoSupport implements GenericDaoIf<T> {

	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory)
	{
	    super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public void saveOrUpdate(T entity) {
		getSessionFactory().getCurrentSession().saveOrUpdate(entity);
	}

	@Override
	public void remove(T entity) {
		getSessionFactory().getCurrentSession().delete(entity);
	}

	@Override
	public List<T> query(String HQL, List<Object> params, Class<T> clazz) {
		String sql = HQL;
		
	    Query<T> query = getSessionFactory().getCurrentSession().createQuery(sql, clazz);

	    if(params != null) {
	    	int indx = 0;
	    	for(Object para : params) {
	    		query.setParameter(indx, para);
	    		
	    		indx++;
	    	}
	    }
	    return query.list();
	}

	@Override
	public long queryCount(String HQL, List<Object> params, Class<T> clazz) {
		String sql = HQL;
		
	    Query<Long> query = getSessionFactory().getCurrentSession().createQuery(sql, Long.class);

	    if(params != null) {
	    	int indx = 0;
	    	for(Object para : params) {
	    		query.setParameter(indx, para);
	    		
	    		indx++;
	    	}
	    }
	    
	    return query.getSingleResult();
	}

}

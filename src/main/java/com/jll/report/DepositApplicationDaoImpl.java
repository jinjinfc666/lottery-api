package com.jll.report;


import javax.persistence.NoResultException;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.entity.DepositApplication;






@Repository
public class DepositApplicationDaoImpl extends HibernateDaoSupport implements DepositApplicationDao {
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	@Override
	public DepositApplication queryDetails(Integer id) {
		String sql = "from DepositApplication where id=?";
		DepositApplication dep = null;
		Query<DepositApplication> query = currentSession().createQuery(sql, DepositApplication.class);
		query.setParameter(0, id);
		try {
			dep = query.getSingleResult();
		}catch(NoResultException ex) {
			
		}
		return dep;
	}
	
}


package com.jll.report;

import javax.persistence.NoResultException;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.entity.WithdrawApplication;






@Repository
public class WithdrawApplicationDaoImpl extends HibernateDaoSupport implements WithdrawApplicationDao {
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	@Override
	public WithdrawApplication queryDetails(Integer id) {
		String sql = "from WithdrawApplication where id=:id";
		WithdrawApplication dep = null;
		Query<WithdrawApplication> query = currentSession().createQuery(sql, WithdrawApplication.class);
		query.setParameter("id", id);
		try {
			dep = query.getSingleResult();
		}catch(NoResultException ex) {
			
		}
		return dep;
	}
	
}


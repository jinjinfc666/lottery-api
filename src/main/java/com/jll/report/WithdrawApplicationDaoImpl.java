package com.jll.report;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.WithdrawApplication;






@Repository
public class WithdrawApplicationDaoImpl extends DefaultGenericDaoImpl<WithdrawApplication> implements WithdrawApplicationDao {
	
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
	
	@Override
	public long getUserWithdrawCount(int userId, java.util.Date start, java.util.Date end) {
		StringBuffer sql = new StringBuffer();
		
		List<Object> params = new ArrayList<>();
		
		sql.append("select count(*) from WithdrawApplication where ");
		
		sql.append(" userId = ?  ");
		params.add(userId);
		
		sql.append(" and  createTime >=  ?");
		params.add(start);
		
		sql.append(" and  createTime <=  ? ");
		params.add(end);
		
		return queryCount(sql.toString(), params, WithdrawApplication.class);
	}
	
}


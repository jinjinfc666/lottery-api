package com.jll.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants.WithdrawOrderState;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
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
		dep = query.getSingleResult();
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
		
		return queryCount(sql.toString(), params);
	}
	@Override
	public void updateState(Integer id, Integer state,String remark) {
		Session session=getSessionFactory().getCurrentSession();
		String remarkSql="";
		if(!StringUtils.isBlank(remark)) {
			remarkSql=",remark=:remark";
		}
		String hql = ("update WithdrawApplication set state=:state,updateTime=:updateTime"+remarkSql+" where id=:id");  
		Query query = session.createQuery(hql);
		query.setParameter("state", state);
		if(!StringUtils.isBlank(remark)) {
			query.setParameter("remark", remark);
		}
		query.setParameter("updateTime", new Date());
		query.setParameter("id", id);;
		query.executeUpdate();
	}

	@Override
	public double getUserWithdrawAmountTotal(int userId, int walletId, Date start, Date end) {
		String sql = "select sum(amount) from WithdrawApplication where userId=? and walletId=? and ( state=? or state=? ) and createTime >= ? and createTime < ?";
		List<Object> params = new ArrayList<>();
		params.add(userId);
		params.add(walletId);
		params.add(WithdrawOrderState.ORDER_END.getCode());
		params.add(WithdrawOrderState.ORDER_INIT.getCode());
		params.add(start);
		params.add(end);
		 Query<Double> query = getSessionFactory().getCurrentSession().createQuery(sql, Double.class);
	    if(params != null) {
	    	int indx = 0;
	    	for(Object para : params) {
	    		query.setParameter(indx, para);
	    		
	    		indx++;
	    	}
	    }
		 return Utils.toDouble(query.getSingleResult());
	
	}

	
}


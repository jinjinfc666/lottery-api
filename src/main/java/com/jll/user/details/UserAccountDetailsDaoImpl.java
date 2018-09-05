package com.jll.user.details;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.common.utils.Utils;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.UserAccountDetails;

@Repository
public class UserAccountDetailsDaoImpl extends DefaultGenericDaoImpl<UserAccountDetails> implements UserAccountDetailsDao
{
	private Logger logger = Logger.getLogger(UserAccountDetailsDaoImpl.class);

	@Override
	public void saveAccDetails(UserAccountDetails userDetails) {
		this.saveOrUpdate(userDetails);
	}

	@Override
	public double getUserOperAmountTotal(int userId,int walletId, String operationType, Date start, Date end) {
		String sql = "select sum(amount) from UserAccountDetails where userId=? and walletId=? and operationType=? and createTime >= ? and createTime < ?";
		List<Object> params = new ArrayList<>();
		params.add(userId);
		params.add(walletId);
		params.add(operationType);
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

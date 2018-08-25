package com.jll.pay;

import javax.persistence.NoResultException;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.entity.DepositApplication;

@Repository
public class PaymentDaoImpl extends HibernateDaoSupport implements PaymentDao
{
	  @Autowired
	  public void setSuperSessionFactory(SessionFactory sessionFactory){
		  super.setSessionFactory(sessionFactory);
	  }
  
	  public long queryDepositTimes(int userId) {
	    //String sql = "select count(*) from DepositApplication where userId=? and state=? and payType =? and payChannel <> ?  && payChannel <> ? ";
	    String sql = "select count(*) from DepositApplication where userId=? and state=? ";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter(0, userId);
	    query.setParameter(1, Constants.DepositOrderState.END_ORDER.getCode());
	/*    query.setParameter(2, Constants.PayType.SYS_PAY.getCode());
	    query.setParameter(3, Constants.PayChannelType.ADMIN_SEND_USER.getCode());
	    query.setParameter(4, Constants.PayChannelType.AGENT_SEND_USER.getCode());*/
	    
	    long count = ((Number)query.iterate().next()).longValue();
	    return count;
	  }
	@Override
	public DepositApplication queryDepositOrder(String orderId) {
		String sql = "from DepositApplication where orderNum=?";
		DepositApplication depositOrder = null;
		Query<DepositApplication> query = currentSession().createQuery(sql, DepositApplication.class);
		query.setParameter(0, orderId);
		try {
			depositOrder = query.getSingleResult();
		}catch(NoResultException ex) {
			ex.printStackTrace();
		}
		return depositOrder;
	}

}

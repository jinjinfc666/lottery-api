package com.jll.user;


import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.backstage.report.flowdetail.FlowDetailController;
import com.jll.entity.SysCode;
import com.jll.entity.UserInfo;

@Repository
public class UserInfoDaoImpl extends HibernateDaoSupport implements UserInfoDao
{
	private Logger logger = Logger.getLogger(FlowDetailController.class);
  @Autowired
  public void setSuperSessionFactory(SessionFactory sessionFactory)
  {
    super.setSessionFactory(sessionFactory);
  }

	@Override
	public int getUserId(String userName) {
		UserInfo userInfo=null;
		String sql = "from UserInfo where userName=?";
	    Query<UserInfo> query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter(0, userName);
	    userInfo = query.getSingleResult();
	    int id=userInfo.getId();
	    return id;
	}

	@Override
	public long getCountUser(String userName) {
		Integer userType=2;
		String sql = "select count(*) from UserInfo where userName=? and userType !=?";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
//	    Query query = getSessionFactory().getCurrentSession().createSQLQuery(sql);
	    query.setParameter(0, userName);
	    query.setParameter(1, userType);
	    long count = ((Number)query.iterate().next()).longValue();
	    return count;
	}
  
  
}

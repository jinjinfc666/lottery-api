package com.jll.user;



import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.UserInfo;

@Repository
public class UserInfoDaoImpl extends DefaultGenericDaoImpl<UserInfo> implements UserInfoDao
{
	private Logger logger = Logger.getLogger(UserInfoDaoImpl.class);
	

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

	@Override
	public UserInfo getUserByUserName(String userName) {
		String sql = "from UserInfo where userName = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(userName);
		
		List<UserInfo> rets = query(sql, params, UserInfo.class);
		
		return rets.size() > 0?rets.get(0) : null;
	}

	@Override
	public boolean isUserExisting(UserInfo user) {
		StringBuffer sql = new StringBuffer();
		
		StringBuffer condition = new StringBuffer();
		
		List<Object> params = new ArrayList<>();
		
		sql.append("select count(*) from UserInfo ");
		
		if(!StringUtils.isBlank(user.getUserName())) {
			condition.append(" userName = ? and ");
			params.add(user.getUserName());
		}
		
		if(!StringUtils.isBlank(user.getEmail())) {
			condition.append(" email = ? and ");
			params.add(user.getEmail());
		}
		
		if(!StringUtils.isBlank(user.getPhoneNum())) {
			condition.append(" phoneNum = ? and ");
			params.add(user.getPhoneNum());
		}
		
		if(condition.length() > 0) {
			condition.insert(0, " where ");
			int indx = condition.lastIndexOf(" and ");
			condition.replace(indx, condition.length(), "");
			
			logger.debug("condition ::::" + condition.toString());
			
			sql.append(condition.toString());
		}
		
		long count = queryCount(sql.toString(), params, UserInfo.class);
		return count > 0;
	}

	@Override
	public void saveUser(UserInfo user) {
		saveOrUpdate(user);
	}

	@Override
	public UserInfo getGeneralAgency() {
		//user_type = 3 means general agency
		String sql = "from UserInfo where userType = 3";
		
		List<UserInfo> generalAgencys  = query(sql, null, UserInfo.class);
		if(generalAgencys == null || generalAgencys.size() == 0) {
			return null;
		}
		
		return generalAgencys.get(0);
	}
  
  
}

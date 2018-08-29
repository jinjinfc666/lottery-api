package com.jll.user;


import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants.UserType;
import com.jll.common.utils.StringUtils;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.MemberPlReport;
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
	public UserInfo getUserById(Integer userId) {
		String sql = "from UserInfo where id = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(userId);
		
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

	@Override
	public String queryUnSystemUsers() {
		String sql = "select group_concat(id) from UserInfo where userType <> ?";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter(0, UserType.SYS_ADMIN.getCode());
	    return query.iterate().next().toString();
	}

	@Override
	public boolean checkUserIds(String userIds) {
		String sql = "select count(*) from UserInfo userType <> ? and id in(?)";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter(0, UserType.SYS_ADMIN.getCode());
	    query.setParameter(1, userIds);
	    long count = ((Number)query.iterate().next()).longValue();
		return count ==  userIds.split(StringUtils.COMMA).length;
	}

	@Override
	public List<UserInfo> queryAllUserInfo(Integer id,String userName,Integer proxyId,String startTime,String endTime) {
		Map<String,Object> map=new HashMap<String,Object>();
		Date beginDate = java.sql.Date.valueOf(startTime);
	    Date endDate = java.sql.Date.valueOf(endTime);
		map.put("startTime", beginDate);
		map.put("endTime", endDate);
		String hql="";
		if(proxyId!=null) {
			hql=("select a.* from (select *,FIND_IN_SET(:proxyId,superior) as aa from user_info)a where a.aa=1 and a.create_time>:startTime and a.create_time<=:endTime");
			Query<UserInfo> query=null;
			try {
				query= getSessionFactory().getCurrentSession().createNativeQuery(hql).addEntity(UserInfo.class);
			}catch(Exception e) {
				e.printStackTrace();
			}
			query.setParameter("proxyId", proxyId);  
			query.setParameter("startTime", beginDate,DateType.INSTANCE);
			query.setParameter("endTime", endDate,DateType.INSTANCE); 
			List<UserInfo> list = new ArrayList<UserInfo>();	
			list = query.list();
			return list;
		}else {
			String userNameSql="";
			String idSql="";
			if(id!=null) {
				idSql=" and id=:id";
				map.put("id", id);
			}
			if(!StringUtils.isBlank(userName)) {
				userNameSql=" and userName=:userName";
				map.put("userName", userName);
			}
			String timeSql=" where create_time >:startTime and create_time <=:endTime";
			hql=("from UserInfo"+timeSql+idSql+userNameSql);
			Query<UserInfo> query = getSessionFactory().getCurrentSession().createQuery(hql,UserInfo.class);
			if (map != null) {  
	            Set<String> keySet = map.keySet();  
	            for (String string : keySet) {  
	                Object obj = map.get(string);  
	            	if(obj instanceof Date){  
	                	query.setParameter(string, (Date)obj,DateType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
	                }else if(obj instanceof Object[]){  
	                    query.setParameterList(string, (Object[])obj);  
	                }else{  
	                    query.setParameter(string, obj);  
	                }  
	            }  
	        }
			List<UserInfo> list = new ArrayList<UserInfo>();	
			list = query.list();
			return list;
		}
	}
	//查询总代下面的所有一级代理
	@Override
	public List<UserInfo> queryAllAgent(Integer id) {
		String sql="select * from(select *,FIND_IN_SET(:id,superior) as aa from user_info)a where a.aa=1";
		Query<UserInfo> query1 = getSessionFactory().getCurrentSession().createNativeQuery(sql,UserInfo.class);
	    query1.setParameter("id", id);
	    List<UserInfo> list=query1.list();
	    return list;
	}
	//点击代理查询下一级代理
	@Override
	public List<UserInfo> queryAgentByAgent(Integer id) {
		String sql="select * from(select *,FIND_IN_SET(:id,superior) as aa from user_info)a where a.aa=1";
		Query<UserInfo> query1 = getSessionFactory().getCurrentSession().createNativeQuery(sql,UserInfo.class);
	    query1.setParameter("id", id);
	    List<UserInfo> list=query1.list();
	    return list;
	}
	//查询总代
	@Override
	public UserInfo querySumAgent() {
		List<Object> params = new ArrayList<>();
		String sql="from UserInfo where userType=?";
		params.add(3);
		List<UserInfo> list=query(sql, params, UserInfo.class);
		return list.get(0);
	}
	
  
}

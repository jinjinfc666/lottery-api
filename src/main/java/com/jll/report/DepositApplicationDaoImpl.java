package com.jll.report;


import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.DepositApplication;








@Repository
public class DepositApplicationDaoImpl extends DefaultGenericDaoImpl<DepositApplication> implements DepositApplicationDao {
//	@Override
//	public List<?> queryDetails(String userName,String orderNum,String startTime,String endTime) {
//		String userNameSql="";
//		String orderNumSql="";
//		String timeSql="";
//		Map<String,Object> map=new HashMap();
//		if(!StringUtils.isBlank(userName)) {
//			userNameSql=" and b.userName=:userName";
//			map.put("userName", userName);
//		}
//		if(!StringUtils.isBlank(orderNum)) {
//			orderNumSql=" and d.orderNum=:orderNum";
//			map.put("orderNum", orderNum);
//		}
//		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
//			timeSql=" and a.createTime >:startTime and a.createTime <=:endTime";
//			Date beginDate = java.sql.Date.valueOf(startTime);
//		    Date endDate = java.sql.Date.valueOf(endTime);
//			map.put("startTime", beginDate);
//			map.put("endTime", endDate);
//		}
//		String sql = "from DepositApplication a,UserInfo b,PayChannel c,PayType d where a.userId=b.id and a.payType=d.id and a.payChannel=c.id "+userNameSql+orderNumSql+timeSql+" order by a.id";
//		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
//		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
//		if (map != null) {  
//            Set<String> keySet = map.keySet();  
//            for (String string : keySet) {  
//                Object obj = map.get(string);  
//            	if(obj instanceof Date){  
//                	query.setParameter(string, (Date)obj,DateType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
//                }else if(obj instanceof Object[]){  
//                    query.setParameterList(string, (Object[])obj);  
//                }else{  
//                    query.setParameter(string, obj);  
//                }  
//            }  
//        }
//		List<?> list1 = new ArrayList<>();
//		try {			
//			list1 = query.list();
//		}catch(NoResultException ex) {
//			
//		}
//		return list1;
//	}
	@Override
	public void updateState(Integer id, Integer state) {
		Session session=getSessionFactory().getCurrentSession();
		String hql = ("update DepositApplication set state=:state where id=:id");  
		Query query = session.createQuery(hql);
		query.setParameter("state", state);
		query.setParameter("id", id);;
		query.executeUpdate();
	}
	//查询是否存在
	@Override
	public List<DepositApplication> queryById(Integer id) {
		String sql = "from DepositApplication where id=:id";
	    Query<DepositApplication> query = getSessionFactory().getCurrentSession().createQuery(sql,DepositApplication.class);
	    query.setParameter("id", id);
	    List<DepositApplication> list = query.list();
	    return list;
	}
	@Override
	public DepositApplication getDepositInfoByOrderNum(String orderNum) {
		String sql = "from DepositApplication where orderNum=?";
		List<Object> params = new ArrayList<>();
		params.add(orderNum);
		List<DepositApplication> ret =  this.query(sql, params, DepositApplication.class);
		if(ret != null && !ret.isEmpty()){
			return ret.get(0);
		}
		return null;
	}
	
}


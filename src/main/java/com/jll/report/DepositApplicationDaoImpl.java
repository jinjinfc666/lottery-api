package com.jll.report;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;







@Repository
public class DepositApplicationDaoImpl extends HibernateDaoSupport implements DepositApplicationDao {
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	@Override
	public List<?> queryDetails(String userName,String orderNum,String startTime,String endTime) {
		List<Object> list=new ArrayList<Object>();
		String userNameSql="";
		String orderNumSql="";
		String timeSql="";
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and b.userName=?";
			list.add(userName);
		}
		if(!StringUtils.isBlank(orderNum)) {
			orderNumSql=" and a.orderNum=?";
			list.add(orderNum);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" and a.createTime >'"+startTime+"' and a.createTime <='"+endTime+"'";
		}
		String sql = "from DepositApplication a,UserInfo b,PayChannel c,PayType d where a.userId=b.id and a.payType=d.id and a.payChannel=c.id "+userNameSql+orderNumSql+timeSql+" order by a.id";
		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
		Iterator<Object> it = list.iterator();
		int a=0;
        while(it.hasNext()){
        	query.setParameter(a, it.next());
        	a++;
        }
		List<?> list1 = new ArrayList<>();
		try {			
			list1 = query.list();
		}catch(NoResultException ex) {
			
		}
		return list1;
	}
	@Override
	public void updateState(Integer id, Integer state) {
		Session session=getSessionFactory().getCurrentSession();
		String hql = ("update DepositApplication set state=? where id=?");  
		Query query = session.createQuery(hql);
		query.setParameter(0, state);
		query.setParameter(1, id);;
		query.executeUpdate();
	}
	
}


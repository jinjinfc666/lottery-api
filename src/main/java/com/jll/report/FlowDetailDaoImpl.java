package com.jll.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;


@Repository
public class FlowDetailDaoImpl extends HibernateDaoSupport implements FlowDetailDao {
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	@Override
	public List<?> queryUserAccountDetails(String userName,String orderNum,Float amountStart,Float amountEnd,String operationType,String startTime,String endTime) {
		String userNameSql="";
		String orderNumSql="";
		String amountStartSql="";
		String amountEndSql="";
		String operationTypeSql="";
		String timeSql="";
		List<Object> list=new ArrayList<Object>();
		if(!userName.equals("")) {
			userNameSql=" and b.userName=?";
			list.add(userName);
		}
		if(!orderNum.equals("")) {
//			orderIdSql=" and a.orderId=:orderId";
			orderNumSql=" and d.orderNum=?";
			list.add(orderNum);
		}
		if(amountStart!=null) {
//			amountStartSql=" and a.amount>:amountStart";
			amountStartSql=" and a.amount>?";
			list.add(amountStart);
		}
		if(amountEnd!=null) {
//			amountEndSql=" and a.amount<:amountEnd";
			amountEndSql=" and a.amount<=?";
			list.add(amountEnd);
		}
		if(!operationType.equals("")) {
//			operationTypeSql=" and a.operationType=:operationType";
			operationTypeSql=" and a.operationType=?";
			list.add(operationType);
		}
		if(!startTime.equals("")&&!endTime.equals("")) {
			timeSql=" and a.createTime >'"+startTime+"' and a.createTime <='"+endTime+"'";
		}
		Integer userType=2;
		String sql="from UserAccountDetails a,UserInfo b,SysCode c,OrderInfo d where a.userId=b.id and a.operationType=c.codeName and a.orderId=d.id and b.userType !=?"+userNameSql+orderNumSql+amountStartSql+amountEndSql+operationTypeSql+timeSql+" order by a.id";
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
		query.setParameter(0, userType);
		Iterator<Object> it = list.iterator();
		int a=1;
        while(it.hasNext()){
        	query.setParameter(a, it.next());
        	a++;
        }
		List<?> cards = new ArrayList<>();
		try {			
			cards = query.list();
		}catch(NoResultException ex) {
			
		}
		return cards;
	}
	
}


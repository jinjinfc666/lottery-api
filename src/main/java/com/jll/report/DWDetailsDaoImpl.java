package com.jll.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;





@Repository
public class DWDetailsDaoImpl extends HibernateDaoSupport implements DWDetailsDao {
	private Logger logger = Logger.getLogger(DWDetailsDaoImpl.class);
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	@Override
	public List<?> queryDetails(String type,Integer state,String userName,String orderNum,Float amountStart,Float amountEnd,String startTime,String endTime){
		String stateSql="";
		String userNameSql="";
		String orderNumSql="";
		String amountStartSql="";
		String amountEndSql="";
		String timeSql="";
		List<Object> list=new ArrayList<Object>();
		if(state!=null) {
			stateSql=" and a.state=?";
			list.add(state);
		}
		if(!userName.equals("")) {
			userNameSql=" and b.userName=?";
			list.add(userName);
		}
		if(!orderNum.equals("")) {
			orderNumSql=" and a.orderNum=?";
			list.add(orderNum);
		}
		if(amountStart!=null) {
			amountStartSql=" and a.amount>?";
			list.add(amountStart);
		}
		if(amountEnd!=null) {
			amountEndSql=" and a.amount<=?";
			list.add(amountEnd);
		}
		if(!startTime.equals("")&&!endTime.equals("")) {
			timeSql=" and a.createTime >'"+startTime+"' and a.createTime <='"+endTime+"'";
		}
		String sql="";
		if(type.equals("1")) {
			sql="from DepositApplication a,UserInfo b where a.userId=b.id "+stateSql+userNameSql+orderNumSql+amountStartSql+amountEndSql+timeSql+"order by a.id";
		}else if(type.equals("2")){
			sql="from WithdrawApplication a,UserInfo b where a.userId=b.id "+stateSql+userNameSql+orderNumSql+amountStartSql+amountEndSql+timeSql+"order by a.id";
		}
		
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
	public List<?> queryDWDetails(String type, Integer id) {
		String sql="";
		if(type.equals("1")) {
			sql="from DepositApplication a,UserInfo b where a.userId=b.id and a.id=?";
		}else if(type.equals("2")){
			sql="from WithdrawApplication a,UserInfo b where a.userId=b.id and a.id=?";
		}
		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
        query.setParameter(0, id);
		List<?> list1 = new ArrayList<>();
		try {			
			list1 = query.list();
		}catch(NoResultException ex) {
			
		}
		return list1;
	}
	
}


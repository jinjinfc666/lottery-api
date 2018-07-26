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
public class LoyTstDaoImpl extends HibernateDaoSupport implements LoyTstDao {
	private Logger logger = Logger.getLogger(LoyTstDaoImpl.class);
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	@Override
	public List<?> queryLoyTst(String lotteryType,Integer isZh,Integer state,Integer terminalType,String startTime,String endTime,String issueNum,String userName,String orderNum) {
		String lotteryTypeSql="";
		String isZhSql="";
		String stateSql="";
		String terminalTypeSql="";
		String timeSql="";
		String issueNumSql="";
		String userNameSql="";
		String orderNumSql="";
		List<Object> list=new ArrayList<Object>();
		if(lotteryType!=null&&!lotteryType.equals("")) {
			lotteryTypeSql=" and e.codeName=?"; 
			list.add(lotteryType);
		}
		if(isZh!=null) {
			isZhSql=" and a.isZh=?";
			list.add(isZh);
		}
		if(state!=null) {
			stateSql=" and a.state=?";
			list.add(state);
		}
		if(terminalType!=null) {
			terminalTypeSql=" and a.terminalType=?";
			list.add(terminalType);
		}
		if(startTime!=null&&endTime!=null&&!startTime.equals("")&&!endTime.equals("")) {
			timeSql=" and a.createTime >'"+startTime+"' and a.createTime <='"+endTime+"'";
		}
		if(issueNum!=null&&!issueNum.equals("")) {
			issueNumSql=" and d.issueNum=?";		
			list.add(issueNum);
		}
		if(userName!=null&&!userName.equals("")) {
			userNameSql=" and b.userName=?";
			list.add(userName);
		}
		if(orderNum!=null&&!orderNum.equals("")) {
			orderNumSql=" and a.orderNum=?";
			list.add(orderNum);
		}
		String sql="from OrderInfo a,UserInfo b,UserAccountDetails c,Issue d,SysCode e where a.userId=b.id and a.issueId=d.id and a.id=c.orderId and d.lotteryType=e.codeName "+lotteryTypeSql+isZhSql+stateSql+terminalTypeSql+timeSql+issueNumSql+userNameSql+orderNumSql+"order by a.id";
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
	
}


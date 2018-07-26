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
public class RedPackageDaoImpl extends HibernateDaoSupport implements RedPackageDao {
	private Logger logger = Logger.getLogger(RedPackageDaoImpl.class);
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	@Override
	public List<?> queryRedUserAccountDetails(String userName,String startTime,String endTime) {
		String userNameSql="";
		String timeSql="";
		List<Object> list=new ArrayList<Object>();
		if(!userName.equals("")) {
			userNameSql=" and c.userName=?";
			list.add(userName);
		}
		if(!startTime.equals("")&&!endTime.equals("")) {
			timeSql=" and a.createTime >'"+startTime+"' and a.createTime <='"+endTime+"'";
		}
		Integer userType=2;
		Integer accType=2;
		String sql="from UserAccountDetails a,UserAccount b,UserInfo c,SysCode d where a.walletId=b.id and a.userId=c.id and a.operationType=d.codeName  and c.userType !=? and b.accType=?  "+userNameSql+timeSql+" order by a.id";
		logger.debug(sql+"-----------------------------RedPackageDaoImpl----SQL--------------------------------");
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
		query.setParameter(0, userType);
		query.setParameter(1, accType);
		Iterator<Object> it = list.iterator();
		int a=2;
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


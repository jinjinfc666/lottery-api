package com.jll.backstage.report.flowdetail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.entity.SysCode;
import com.jll.user.UserInfoService;




@Repository
public class FlowDetailDaoImpl extends HibernateDaoSupport implements FlowDetailDao {
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	@Resource
	UserInfoService userInfoService;
	@Override
	public List<?> queryUserAccountDetails(String userName,Integer orderId,Float amountStart,Float amountEnd,String operationType,String startTime,String endTime) {
		String userNameSql="";
		String orderIdSql="";
		String amountStartSql="";
		String amountEndSql="";
		String operationTypeSql="";
		String timeSql="";
		List<Object> list=new ArrayList<Object>();
		if(userName!=null&&!userName.equals("")) {
			boolean isUserInfo=userInfoService.isUserInfo(userName);
			if(isUserInfo) {
//				userNameSql=" and a.userId=:userId";
				userNameSql=" and a.userId=?";
				Integer userId=userInfoService.getUserId(userName);
				list.add(userId);
			}
		}
		if(orderId!=null) {
//			orderIdSql=" and a.orderId=:orderId";
			orderIdSql=" and a.orderId=?";
			list.add(orderId);
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
		if(operationType!=null&&!operationType.equals("")) {
//			operationTypeSql=" and a.operationType=:operationType";
			operationTypeSql=" and a.operationType=?";
			list.add(operationType);
		}
		if(startTime!=null&&endTime!=null&&!startTime.equals("")&&!endTime.equals("")) {
			timeSql=" and a.createTime >'"+startTime+"' and a.createTime <='"+endTime+"'";
		}
		Integer userType=2;
		String sql="from UserAccountDetails a,UserInfo b,SysCode c where a.userId=b.id and a.operationType=c.codeName and b.userType !=?"+userNameSql+orderIdSql+amountStartSql+amountEndSql+operationTypeSql+timeSql+"order by a.id";
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
	@Override
	public List<SysCode> queryType() {
	    String sql="select * from sys_code where code_type=(select id from sys_code where code_name='acc_ope_type') and state='1' order by seq";
//	    Query<SysCode> query = getSessionFactory().getCurrentSession().createQuery(sql,SysCode.class);
	    Query<SysCode> query = getSessionFactory().getCurrentSession().createSQLQuery(sql).addEntity(SysCode.class);
	    List<SysCode> types = new ArrayList<>();
	    try {			
	    	types = query.list();
		}catch(NoResultException ex) {
			
		}
		return types;
	}
	
}


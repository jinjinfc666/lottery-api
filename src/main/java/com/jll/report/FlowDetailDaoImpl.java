package com.jll.report;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
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
		Map<String,Object> map=new HashMap();
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and b.userName=:userName";
			map.put("userName", userName);
		}
		if(!StringUtils.isBlank(orderNum)) {
			orderNumSql=" and d.orderNum=:orderNum";
			map.put("orderNum", orderNum);
		}
		if(amountStart!=null) {
			amountStartSql=" and a.amount>:amount";
			map.put("amountStart", amountStart);
		}
		if(amountEnd!=null) {
			amountEndSql=" and a.amount<=:amount";
			map.put("amountEnd", amountEnd);
		}
		if(!StringUtils.isBlank(operationType)) {
			operationTypeSql=" and a.operationType in(:operationType)";
			String[] strarr = operationType.split(",");
			for(int b=0;b<strarr.length;b++) {
				list.add(strarr[b]);
			}
			map.put("operationType", list);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" and a.createTime >:startTime and a.createTime <=:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		Integer userType=2;
		String sql="from UserAccountDetails a,UserInfo b,SysCode c,OrderInfo d where a.userId=b.id and a.operationType=c.codeName and a.orderId=d.id and b.userType !=:userType"+userNameSql+orderNumSql+amountStartSql+amountEndSql+operationTypeSql+timeSql+" order by a.id";
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
		query.setParameter("userType", userType);
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
		List<?> cards = new ArrayList<>();
		try {			
			cards = query.list();
		}catch(NoResultException ex) {
			
		}
		return cards;
	}
	
}


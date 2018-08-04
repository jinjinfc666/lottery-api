package com.jll.report;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
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
		Map<String,Object> map=new HashMap();
		if(!StringUtils.isBlank(lotteryType)) {
			lotteryTypeSql=" and e.codeName=:lotteryType"; 
			map.put("lotteryType", lotteryType);
		}
		if(isZh!=null) {
			isZhSql=" and a.isZh=:isZh";
			map.put("isZh", isZh);
		}
		if(state!=null) {
			stateSql=" and a.state=:state";
			map.put("state", state);
		}
		if(terminalType!=null) {
			terminalTypeSql=" and a.terminalType=:terminalType";
			map.put("terminalType", terminalType);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" and a.createTime >:startTime and a.createTime <=:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		if(!StringUtils.isBlank(issueNum)) {
			issueNumSql=" and d.issueNum=:issueNum";		
			map.put("issueNum", issueNum);
		}
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and b.userName=:userName";
			map.put("userName", userName);
		}
		if(!StringUtils.isBlank(orderNum)) {
			orderNumSql=" and a.orderNum=:orderNum";
			map.put("orderNum", orderNum);
		}
		String sql="from OrderInfo a,UserInfo b,UserAccountDetails c,Issue d,SysCode e,PlayType f where a.userId=b.id and a.issueId=d.id and a.id=c.orderId and d.lotteryType=e.codeName and a.playType=f.id "+lotteryTypeSql+isZhSql+stateSql+terminalTypeSql+timeSql+issueNumSql+userNameSql+orderNumSql+"order by a.id";
		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
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
		List<?> list1 = new ArrayList<>();
		try {			
			list1 = query.list();
		}catch(NoResultException ex) {
			
		}
		return list1;
	}
	
}


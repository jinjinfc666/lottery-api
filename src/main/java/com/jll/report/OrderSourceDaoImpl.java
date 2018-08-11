package com.jll.report;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;






@Repository
public class OrderSourceDaoImpl extends HibernateDaoSupport implements OrderSourceDao {
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	//查询
	@Override
	public Map<String,Object> queryOrderSource(String codeName, String startTime, String endTime) {
		String timeSql="";
		String codeNameSql="";
		Map<String,Object> map=new HashMap<String,Object>();
		if(!StringUtils.isBlank(codeName)) {
			codeNameSql=" and code_name=:codeName";
			map.put("codeName", codeName);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql="create_time >:startTime and create_time <=:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		String sql = "select IFNULL(mobile_amount,0) as mobile_amount,IFNULL(mobile_proportion,0) as mobile_proportion,IFNULL(pc_amount,0) as pc_amount,IFNULL(pc_proportion,0) as pc_proportion,IFNULL(sum_amount,0) as sum_amount from(select mobile_amount,ROUND(ROUND(mobile_amount/sum_amount,4)*100,2) as mobile_proportion,pc_amount,ROUND(ROUND(pc_amount/sum_amount,4)*100,2) as pc_proportion,sum_amount from (select sum(mobile_amount) as mobile_amount,sum(pc_amount) as pc_amount,sum(sum_amount) as sum_amount from order_source where "+timeSql+codeNameSql+")a)aa";
		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
		Query<?> query = getSessionFactory().getCurrentSession().createSQLQuery(sql);
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
		List<?> list1 = new ArrayList();	
		list1 = query.list();
		Iterator<?> itSum=list1.iterator();
		Object[] obj=(Object[]) itSum.next();
		map.clear();
		if(obj[0]!=null) {
			map.put("mobileAmount", obj[0]);
			map.put("mobileProportion", obj[1]);
			map.put("pcAmount", obj[2]);
			map.put("pcProportion", obj[3]);
			map.put("sumAmount", obj[4]);
		}
		return map;
	}
}


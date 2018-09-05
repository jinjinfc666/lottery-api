package com.jll.report;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.jll.common.constants.Constants;





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
		Map<String,Object> map=new HashMap();
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and c.userName=:userName";
			map.put("userName", userName);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" and a.createTime >:startTime and a.createTime <=:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		Integer userType=Constants.UserTypes.SYSTEM_USER.getCode();
		Integer accType=Constants.WalletType.RED_PACKET_WALLET.getCode();
		String sql="from UserAccountDetails a,UserAccount b,UserInfo c,SysCode d where a.walletId=b.id and a.userId=c.id and a.operationType=d.codeName  and c.userType !=:userType and b.accType=:accType  "+userNameSql+timeSql+" order by a.id";
		logger.debug(sql+"-----------------------------RedPackageDaoImpl----SQL--------------------------------");
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
		query.setParameter("userType", userType);
		query.setParameter("accType", accType);
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
		cards = query.list();
		return cards;
	}
	
}


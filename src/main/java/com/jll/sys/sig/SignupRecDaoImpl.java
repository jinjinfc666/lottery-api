package com.jll.sys.sig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.SignupRec;
import com.jll.entity.UserAccount;

@Repository
public class SignupRecDaoImpl extends DefaultGenericDaoImpl<SignupRec> implements SignupRecDao
{

	@Override
	public void save(SignupRec signupRec) {
		this.saveOrUpdate(signupRec);
	}

	@Override
	public List<SignupRec> getCount(Integer userId) {
		Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例  
	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
	    ca.setTime(new Date()); // 设置时间为当前时间  
	    Date resultDate = ca.getTime(); // 结果   
	    String nowTime=sdf1.format(resultDate); 
	    ca.add(Calendar.DAY_OF_MONTH, 1);
	    Date resultDate1 = ca.getTime(); // 结果   
	    String nextTime=sdf1.format(resultDate1);
	    Date beginDate =null;
		Date endDate=null;
	    try {
			beginDate = (Date) sdf1.parse(nowTime);
			endDate = (Date) sdf1.parse(nextTime); 
		}catch(ParseException  ex) {
			
		}
		String sql="from SignupRec where userId=:userId and createTime>=:startTime and createTime<:endTime";
		Query<SignupRec> query = getSessionFactory().getCurrentSession().createQuery(sql,SignupRec.class);
	    query.setParameter("userId", userId);
	    query.setParameter("startTime", beginDate,DateType.INSTANCE);
	    query.setParameter("endTime", endDate,DateType.INSTANCE);
	    List<SignupRec> list= query.list();
	    if(list!=null&&list.size()>0) {
	    	return list;
	    }
		return null;
	}
}
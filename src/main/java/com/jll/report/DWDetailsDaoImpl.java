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
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageBean;
import com.jll.entity.DepositApplication;





@Repository
public class DWDetailsDaoImpl extends DefaultGenericDaoImpl<DepositApplication> implements DWDetailsDao {
	private Logger logger = Logger.getLogger(DWDetailsDaoImpl.class);
	@Override
	public Map<String,Object> queryDetails(String type,Integer state,String userName,String orderNum,Float amountStart,Float amountEnd,String startTime,String endTime,Integer pageIndex,Integer pageSize){
		String stateSql="";
		String userNameSql="";
		String orderNumSql="";
		String amountStartSql="";
		String amountEndSql="";
		String timeSql="";
		Map<String,Object> map=new HashMap();
		if(state!=null) {
			stateSql=" and a.state=:state ";
			map.put("state", state);
		}
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and b.userName=:userName ";
			map.put("userName", userName);
		}
		if(!StringUtils.isBlank(orderNum)) {
			orderNumSql=" and a.orderNum=:orderNum ";
			map.put("orderNum", orderNum);
		}
		if(amountStart!=null) {
			amountStartSql=" and a.amount>:amountStart ";
			map.put("amountStart", amountStart);
		}
		if(amountEnd!=null) {
			amountEndSql=" and a.amount<=:amountEnd ";
			map.put("amountEnd", amountEnd);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" and a.createTime >=:startTime and a.createTime <:endTime ";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		String sql="";
		String sql1="";
		if(type.equals("1")) {
			sql="from DepositApplication a,UserInfo b,PayChannel e,PayType f where a.userId=b.id and a.payType=f.id and a.payChannel=e.id "+stateSql+userNameSql+orderNumSql+amountStartSql+amountEndSql+timeSql+" order by a.id";
			sql1="select coalesce(SUM(a.amount),0) from DepositApplication a,UserInfo b where a.userId=b.id  "+stateSql+userNameSql+orderNumSql+amountStartSql+amountEndSql+timeSql+" order by a.id";
		}else if(type.equals("2")){
			sql="from WithdrawApplication a,UserInfo b,UserAccount e,UserBankCard f where a.userId=b.id and a.walletId=e.id and a.bankCardId=f.id "
					+ ""+stateSql+userNameSql+orderNumSql+amountStartSql+amountEndSql+timeSql+" order by a.id";
			sql1="select coalesce(SUM(a.amount),0) from WithdrawApplication a,UserInfo b where a.userId=b.id "+stateSql+userNameSql+orderNumSql+amountStartSql+amountEndSql+timeSql+" order by a.id";
		}
		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
		logger.debug(sql1+"-----------------------------queryLoyTst----SQL--------------------------------");
		Query<?> query1 = getSessionFactory().getCurrentSession().createQuery(sql1);
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		map.remove("codeName");
		if (map != null) {  
            Set<String> keySet = map.keySet();  
            for (String string : keySet) {  
                Object obj = map.get(string);  
            	if(obj instanceof Date) {
                	query1.setParameter(string, (Date)obj,DateType.INSTANCE);//query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
                }else if(obj instanceof Object[]){ 
                    query1.setParameterList(string, (Object[])obj);  
                }else{
                    query1.setParameter(string, obj); 
                }  
            }  
        }
		map.clear();
		Float sumAmount=null;
		sumAmount = ((Number)query1.iterate().next()).floatValue();
		map.put("sumAmount", sumAmount);
		map.put("data", pageBean);
		return map;
	}
//	@Override
//	public List<?> queryDWDetails(String type, Integer id) {
//		String sql="";
//		if(type.equals("1")) {
//			sql="from DepositApplication a,UserInfo b where a.userId=b.id and a.id=:id";
//		}else if(type.equals("2")){
//			sql="from WithdrawApplication a,UserInfo b where a.userId=b.id and a.id=:id";
//		}
//		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
//		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
//        query.setParameter("id", id);
//		List<?> list1 = new ArrayList<>();
//		try {			
//			list1 = query.list();
//		}catch(NoResultException ex) {
//			
//		}
//		return list1;
//	}
}


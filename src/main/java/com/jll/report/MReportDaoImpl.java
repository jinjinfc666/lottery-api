package com.jll.report;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import com.jll.common.constants.Constants;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageBean;
import com.jll.entity.MemberPlReport;
import com.jll.entity.UserInfo;

@Repository
public class MReportDaoImpl extends DefaultGenericDaoImpl<MemberPlReport> implements MReportDao {
	//会员盈亏报表
	@Override
	public PageBean queryAll(String startTime,String endTime,String userName,Integer pageIndex,Integer pageSize) {
		String userNameSql="";
		String timeSql="";
		Map<String,Object> map=new HashMap<String,Object>();
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and userName=:userName";
			map.put("userName", userName);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" where createTime >:startTime and createTime <=:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		String sql = "from MemberPlReport "+timeSql+userNameSql+" order by id";
		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page, sql,map);
		return pageBean;
		
//		Query<MemberPlReport> query = getSessionFactory().getCurrentSession().createQuery(sql,MemberPlReport.class);
//		if (map != null) {  
//            Set<String> keySet = map.keySet();  
//            for (String string : keySet) {  
//                Object obj = map.get(string);  
//            	if(obj instanceof Date){  
//                	query.setParameter(string, (Date)obj,DateType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
//                }else if(obj instanceof Object[]){  
//                    query.setParameterList(string, (Object[])obj);  
//                }else{  
//                    query.setParameter(string, obj);  
//                }  
//            }  
//        }
//		List<MemberPlReport> list1 = new ArrayList<MemberPlReport>();
//		try {			
//			list1 = query.list();
//		}catch(NoResultException ex) {
//			
//		}
//		return list1;
	}
	@Override
	public Map<String, Object> querySum(String startTime, String endTime, String userName) {
		String userNameSql="";
		String timeSql="";
		Map<String,Object> map=new HashMap<String,Object>();
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and user_name=:userName";
			map.put("userName", userName);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" where create_time >:startTime and create_time <=:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		String sql="SELECT sum(deposit) as sumDeposit,sum(system_recharge) as sumSystemRecharge, sum(withdrawal) as sumWithdrawal, sum(deduction) as sumDeduction, sum(consumption) as sumConsumption, sum(cancel_amount) as sumCancelAmount, sum(return_prize) as sumReturnPrize, sum(rebate) as sumRebate, sum(current_balance) as sumCurrentBalance,sum(profit) as sumProfit FROM member_pl_report "+timeSql+userNameSql;
		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
	    Query<?> query = getSessionFactory().getCurrentSession().createNativeQuery(sql);
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
	    List<MemberPlReport> listRecordSum=new ArrayList<MemberPlReport>();
	    MemberPlReport m=new MemberPlReport();
		Object[] obj=(Object[]) itSum.next();
		if(obj[0]!=null) {
			BigDecimal bd0 = (BigDecimal) obj[0];
			m.setDeposit(bd0);
			BigDecimal bd1 = (BigDecimal) obj[1];
			m.setSystemRecharge(bd1);
			BigDecimal bd2 = (BigDecimal) obj[2];
			m.setWithdrawal(bd2);
			BigDecimal bd3 = (BigDecimal) obj[3];
			m.setDeduction(bd3);
			BigDecimal bd4 = (BigDecimal) obj[4];
			m.setConsumption(bd4);
			BigDecimal bd5 = (BigDecimal) obj[5];
			m.setCancelAmount(bd5);
			BigDecimal bd6 = (BigDecimal) obj[6];
			m.setReturnPrize(bd6);
			BigDecimal bd7 = (BigDecimal) obj[7];
			m.setRebate(bd7);
			BigDecimal bd8 = (BigDecimal) obj[8];
			m.setCurrentBalance(bd8);
			BigDecimal bd9 = (BigDecimal) obj[9];
			m.setProfit(bd9);
			listRecordSum.add(m);
		}
		map.clear();
		map.put("sumData", listRecordSum);
		return map;
	}
	//团队盈亏报表
	@Override
	public PageBean queryTeamAll(String startTime, String endTime, String userName,Integer pageIndex,Integer pageSize,List<?> userNameList) {
		String userNameSql="";
		String timeSql="";
		Map<String,Object> map=new HashMap<String,Object>();
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and userName=:userName";
			map.put("userName", userName);
		}else {
			userNameSql=" and  user_name in(:userNameList)";
			map.put("userNameList", userNameList);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql="createTime >:startTime and createTime <=:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		String sql=null;
		if(!StringUtils.isBlank(userName)) {
			sql="from MemberPlReport  where "+timeSql+userNameSql+" order by id";
		}else {
			sql= "from MemberPlReport  where "+timeSql+userNameSql+" order by id";
		}
		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page, sql,map);
		return pageBean;
//		Query<MemberPlReport> query = getSessionFactory().getCurrentSession().createQuery(sql,MemberPlReport.class);
//		if (map != null) {  
//            Set<String> keySet = map.keySet();  
//            for (String string : keySet) {  
//                Object obj = map.get(string);  
//            	if(obj instanceof Date){  
//                	query.setParameter(string, (Date)obj,DateType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
//                }else if(obj instanceof Object[]){  
//                    query.setParameterList(string, (Object[])obj);  
//                }else{  
//                    query.setParameter(string, obj);  
//                }  
//            }  
//        }
//		List<MemberPlReport> list1 = new ArrayList<MemberPlReport>();	
//		list1 = query.list();
//		return list1;
	}
	@Override
	public Map<String, Object> queryTeamSum(String startTime, String endTime, String userName,List<?> userNameList) {
		String userNameSql="";
		String timeSql="";
		Map<String,Object> map=new HashMap<String,Object>();
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and user_name=:userName";
			map.put("userName", userName);
		}else{
			userNameSql=" and  user_name in(:userNameList)";
			map.put("userNameList", userNameList);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" create_time >:startTime and create_time <=:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		String sql=null;
		if(!StringUtils.isBlank(userName)) {
			sql="SELECT sum(deposit) as sumDeposit,sum(system_recharge) as sumSystemRecharge, sum(withdrawal) as sumWithdrawal, sum(deduction) as sumDeduction, sum(consumption) as sumConsumption, sum(cancel_amount) as sumCancelAmount, sum(return_prize) as sumReturnPrize, sum(rebate) as sumRebate, sum(current_balance) as sumCurrentBalance,sum(recharge_member) as sumRechargeMember,sum(new_members) as sumNewMembers,sum(profit) as sumProfit FROM member_pl_report where "+timeSql+userNameSql;
		}else {
			sql="SELECT sum(deposit) as sumDeposit,sum(system_recharge) as sumSystemRecharge, sum(withdrawal) as sumWithdrawal, sum(deduction) as sumDeduction, sum(consumption) as sumConsumption, sum(cancel_amount) as sumCancelAmount, sum(return_prize) as sumReturnPrize, sum(rebate) as sumRebate, sum(current_balance) as sumCurrentBalance,sum(recharge_member) as sumRechargeMember,sum(new_members) as sumNewMembers,sum(profit) as sumProfit FROM member_pl_report where "+timeSql+userNameSql;
		}
		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
	    Query<?> query = getSessionFactory().getCurrentSession().createNativeQuery(sql);
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
	    List<MemberPlReport> listRecordSum=new ArrayList<MemberPlReport>();
	    MemberPlReport m=new MemberPlReport();
		Object[] obj=(Object[]) itSum.next();
		if(obj[0]!=null) {
			BigDecimal bd0 = (BigDecimal) obj[0];
			m.setDeposit(bd0);
			BigDecimal bd1 = (BigDecimal) obj[1];
			m.setSystemRecharge(bd1);
			BigDecimal bd2 = (BigDecimal) obj[2];
			m.setWithdrawal(bd2);
			BigDecimal bd3 = (BigDecimal) obj[3];
			m.setDeduction(bd3);
			BigDecimal bd4 = (BigDecimal) obj[4];
			m.setConsumption(bd4);
			BigDecimal bd5 = (BigDecimal) obj[5];
			m.setCancelAmount(bd5);
			BigDecimal bd6 = (BigDecimal) obj[6];
			m.setReturnPrize(bd6);
			BigDecimal bd7 = (BigDecimal) obj[7];
			m.setRebate(bd7);
			BigDecimal bd8 = (BigDecimal) obj[8];
			m.setCurrentBalance(bd8);
			m.setRechargeMember(Integer.parseInt(String.valueOf(obj[9])));
			m.setNewMembers(Integer.parseInt(String.valueOf(obj[10])));
			BigDecimal bd11 = (BigDecimal) obj[11];
			m.setProfit(bd11);
			listRecordSum.add(m);
		}
		map.clear();
		map.put("sumData", listRecordSum);
		return map;
	}
	//查找下级代理
	@Override
	public Map<String,Object> queryNextTeamAll(String startTime, String endTime, String userName) {
		Map<String,Object> map=new HashMap<String,Object>();
		String sql = "from UserInfo where userName = :userName";
		logger.debug(sql+"-----------------------------queryNextTeamAll----SQL--------------------------------");
	    Query<UserInfo> query = getSessionFactory().getCurrentSession().createQuery(sql,UserInfo.class);
	    query.setParameter("userName", userName);
	    List<UserInfo> list = query.list();
	    Integer id=list.get(0).getId();
	    String sql1="select a.user_name from(select *,FIND_IN_SET(:id,superior) as aa from user_info)a where a.aa=1";
	    Query<?> query1 = getSessionFactory().getCurrentSession().createNativeQuery(sql1);
	    query1.setParameter("id", id);
	    List<?> userNameList=query1.list();
	    String sql2="select user_name,SUM(deposit) as deposit,SUM(system_recharge) as system_recharge,SUM(withdrawal) as withdrawal,SUM(deduction) as deduction,SUM(consumption) as consumption,SUM(cancel_amount) as cancel_amount,SUM(return_prize) as return_prize,SUM(rebate) as rebate,SUM(current_balance) as current_balance,SUM(recharge_member) as recharge_member,SUM(new_members) as new_members,SUM(profit) as profit from member_pl_report where user_name in(:userNameList)  and create_time>:startTime and create_time<=:endTime GROUP BY user_name";
	    Query<?> query2=getSessionFactory().getCurrentSession().createNativeQuery(sql2);
	    query2.setParameterList("userNameList", userNameList);
	    Date beginDate = java.sql.Date.valueOf(startTime);
	    Date endDate = java.sql.Date.valueOf(endTime);
	    query2.setParameter("startTime", beginDate,DateType.INSTANCE);
	    query2.setParameter("endTime", endDate,DateType.INSTANCE);
    	List<?> memberPlReportList=null;
    	memberPlReportList=query2.list();
	    logger.debug(memberPlReportList.size()+"-----------------------------queryNextTeamAll----SQL--------------------------------");
	    Iterator<?> it=memberPlReportList.iterator();
	    List<MemberPlReport> listRecord=new ArrayList<MemberPlReport>();
		while(it.hasNext()) {
		    MemberPlReport m=new MemberPlReport();
			Object[] obj=(Object[]) it.next();
			m.setUserName((String)obj[0]);
			BigDecimal bd1 = (BigDecimal) obj[1];
			m.setDeposit(bd1);
			BigDecimal bd2 = (BigDecimal) obj[2];
			m.setSystemRecharge(bd2);
			BigDecimal bd3 = (BigDecimal) obj[3];
			m.setWithdrawal(bd3);
			BigDecimal bd4 = (BigDecimal) obj[4];
			m.setDeduction(bd4);
			BigDecimal bd5 = (BigDecimal) obj[5];
			m.setConsumption(bd5);
			BigDecimal bd6 = (BigDecimal) obj[6];
			m.setCancelAmount(bd6);
			BigDecimal bd7 = (BigDecimal) obj[7];
			m.setReturnPrize(bd7);
			BigDecimal bd8 = (BigDecimal) obj[8];
			m.setRebate(bd8);
			BigDecimal bd9 = (BigDecimal) obj[9];
			m.setCurrentBalance(bd9);
			m.setRechargeMember(Integer.parseInt(String.valueOf(obj[10])));
			m.setNewMembers(Integer.parseInt(String.valueOf(obj[11])));
			BigDecimal bd12 = (BigDecimal) obj[12];
			m.setProfit(bd12);
		    listRecord.add(m);
		}
		map.put("data", listRecord);
		String sqlsum="select SUM(deposit) as deposit,SUM(system_recharge) as system_recharge,SUM(withdrawal) as withdrawal,SUM(deduction) as deduction,SUM(consumption) as consumption,SUM(cancel_amount) as cancel_amount,SUM(return_prize) as return_prize,SUM(rebate) as rebate,SUM(current_balance) as current_balance,SUM(recharge_member) as recharge_member,SUM(new_members) as new_members,SUM(profit) as profit from member_pl_report where user_name in(:userNameList)  and create_time>:startTime and create_time<=:endTime";
		Query<?> querysum=getSessionFactory().getCurrentSession().createNativeQuery(sqlsum);
		querysum.setParameterList("userNameList", userNameList);
		querysum.setParameter("startTime", beginDate,DateType.INSTANCE);
		querysum.setParameter("endTime", endDate,DateType.INSTANCE);
    	List<?> ListSum=null;
	    try {
	    	ListSum=querysum.list();
		    logger.debug(ListSum.size()+"-----------------------------queryNextTeamAll----SQL--------------------------------");
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
	    Iterator<?> itSum=ListSum.iterator();
	    List<MemberPlReport> listRecordSum=new ArrayList<MemberPlReport>();
	    MemberPlReport m=new MemberPlReport();
		Object[] obj=(Object[]) itSum.next();
		if(obj[0]!=null) {
			BigDecimal bd0 = (BigDecimal) obj[0];
			m.setDeposit(bd0);
			BigDecimal bd1 = (BigDecimal) obj[1];
			m.setSystemRecharge(bd1);
			BigDecimal bd2 = (BigDecimal) obj[2];
			m.setWithdrawal(bd2);
			BigDecimal bd3 = (BigDecimal) obj[3];
			m.setDeduction(bd3);
			BigDecimal bd4 = (BigDecimal) obj[4];
			m.setConsumption(bd4);
			BigDecimal bd5 = (BigDecimal) obj[5];
			m.setCancelAmount(bd5);
			BigDecimal bd6 = (BigDecimal) obj[6];
			m.setReturnPrize(bd6);
			BigDecimal bd7 = (BigDecimal) obj[7];
			m.setRebate(bd7);
			BigDecimal bd8 = (BigDecimal) obj[8];
			m.setCurrentBalance(bd8);
			m.setRechargeMember(Integer.parseInt(String.valueOf(obj[9])));
			m.setNewMembers(Integer.parseInt(String.valueOf(obj[10])));
			BigDecimal bd11 = (BigDecimal) obj[11];
			m.setProfit(bd11);
			listRecordSum.add(m);
		}
	    map.put("sumData", listRecordSum);
		return map;
	}
}




































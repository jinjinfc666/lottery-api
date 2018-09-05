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
import com.jll.entity.LotteryPlReport;
import com.jll.entity.MemberPlReport;
import com.jll.entity.UserInfo;





@Repository
public class LReportDaoImpl extends DefaultGenericDaoImpl<LotteryPlReport> implements LReportDao {
	//团队盈亏报表(按彩种查询)
	
	@Override
	public PageBean queryLReport(String codeName, String startTime, String endTime, String userName,Integer pageIndex,Integer pageSize,List<?> userNameList) {
		String userNameSql="";
		String timeSql="";
		String codeNameSql="";
		Map<String,Object> map=new HashMap<String,Object>();
		if(!StringUtils.isBlank(codeName)) {
			codeNameSql=" and codeName=:codeName";
			map.put("codeName", codeName);
		}
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and userName=:userName";
			map.put("userName", userName);
		}else {
			userNameSql=" and  userName in(:userNameList)";
			map.put("userNameList", userNameList);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" createTime >:startTime and createTime <=:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		String sql=null;
		if(!StringUtils.isBlank(userName)) {
			sql="from LotteryPlReport  where "+timeSql+userNameSql+codeNameSql+" order by id";
		}else {
			sql = "from LotteryPlReport where "+timeSql+userNameSql+codeNameSql+" order by id";
		} 
		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page, sql,map);
		return pageBean;
		
		
//		Query<LotteryPlReport> query = getSessionFactory().getCurrentSession().createQuery(sql,LotteryPlReport.class);
//		if(StringUtils.isBlank(userName)) {
//			Integer userType=Constants.UserTypes.GENERAL_AGENT.getCode();
//			query.setParameter("userType", userType);  
//		}
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
//		List<LotteryPlReport> list1 = new ArrayList<LotteryPlReport>();	
//		list1 = query.list();
//		return list1;
	}
	//团队盈亏报表(按彩种查询)总计
	@Override
	public Map<String,Object> queryLReportSum(String codeName, String startTime, String endTime, String userName,List<?> userNameList) {
		String userNameSql="";
		String timeSql="";
		String codeNameSql="";
		Map<String,Object> map=new HashMap<String,Object>();
		if(!StringUtils.isBlank(codeName)) {
			codeNameSql=" and code_name=:codeName";
			map.put("codeName", codeName);
		}
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and user_name=:userName";
			map.put("userName", userName);
		}else {
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
			sql="select sum(consumption) as sumConsumption,sum(cancel_amount) as sumCancelAmount,sum(return_prize) as sumSeturnPrize,sum(rebate) as sumRebate,sum(profit) as sumProfit from lottery_pl_report where "+timeSql+userNameSql+codeNameSql;
		}else {
			sql = "select sum(consumption) as sumConsumption,sum(cancel_amount) as sumCancelAmount,sum(return_prize) as sumSeturnPrize,sum(rebate) as sumRebate,sum(profit) as sumProfit from lottery_pl_report where "+timeSql+userNameSql+codeNameSql;
		} 
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
	    List<LotteryPlReport> listRecordSum=new ArrayList<LotteryPlReport>();
	    LotteryPlReport l=new LotteryPlReport();
		Object[] obj=(Object[]) itSum.next();
		if(obj[0]!=null) {
			BigDecimal bd0 = (BigDecimal) obj[0];
			l.setConsumption(bd0);
			BigDecimal bd1 = (BigDecimal) obj[1];
			l.setCancelAmount(bd1);
			BigDecimal bd2 = (BigDecimal) obj[2];
			l.setReturnPrize(bd2);
			BigDecimal bd3 = (BigDecimal) obj[3];
			l.setRebate(bd3);
			BigDecimal bd4 = (BigDecimal) obj[4];
			l.setProfit(bd4);
			listRecordSum.add(l);
		}
		map.clear();
	    map.put("sumData", listRecordSum);
		return map;
	}
	//团队盈亏报表(按彩种查询) 下级
	@Override
	public Map<String,Object> queryLReportNext(String codeName, String startTime, String endTime, String userName) {
		Map<String,Object> map=new HashMap<String,Object>();
		String sql = "from UserInfo where userName = :userName";
		logger.debug(sql+"-----------------------------queryNextTeamAll----SQL--------------------------------");
	    Query<UserInfo> query = getSessionFactory().getCurrentSession().createQuery(sql,UserInfo.class);
	    query.setParameter("userName", userName);
	    List<UserInfo> list = query.list();
	    Integer id=list.get(0).getId();
	    String sql1="select a.user_name from (select *,FIND_IN_SET(:id,superior) as aa from user_info)a where a.aa=1";
	    Query<?> query1 = getSessionFactory().getCurrentSession().createNativeQuery(sql1);
	    query1.setParameter("id", id);
	    List<?> userNameList=query1.list();
	    String sql2="select user_name,SUM(consumption) as consumption,SUM(cancel_amount) as cancel_amount,SUM(return_prize) as return_prize,SUM(rebate) as rebate, SUM(profit) as profit from lottery_pl_report where create_time>:startTime and create_time<=:endTime and  user_name in(:userNameList) and code_name=:codeName GROUP BY user_name";
	    Query<?> query2=getSessionFactory().getCurrentSession().createNativeQuery(sql2);
	    query2.setParameterList("userNameList", userNameList);
	    query2.setParameter("codeName",codeName);
	    Date beginDate = java.sql.Date.valueOf(startTime);
	    Date endDate = java.sql.Date.valueOf(endTime);
	    query2.setParameter("startTime", beginDate,DateType.INSTANCE);
	    query2.setParameter("endTime", endDate,DateType.INSTANCE);
    	List<?> LReportList=null;
    	LReportList=query2.list();
	    logger.debug(LReportList.size()+"-----------------------------queryNextTeamAll----SQL--------------------------------");	    
	    Iterator<?> it=LReportList.iterator();
	    List<LotteryPlReport> listRecord=new ArrayList<LotteryPlReport>();
		while(it.hasNext()) {
		    LotteryPlReport l=new LotteryPlReport();
			Object[] obj=(Object[]) it.next();
			l.setUserName((String)obj[0]);
			BigDecimal bd1 = (BigDecimal) obj[1];
			l.setConsumption(bd1);
			BigDecimal bd2 = (BigDecimal) obj[2];
			l.setCancelAmount(bd2);
			BigDecimal bd3 = (BigDecimal) obj[3];
			l.setReturnPrize(bd3);
			BigDecimal bd4 = (BigDecimal) obj[4];
			l.setRebate(bd4);
			BigDecimal bd5 = (BigDecimal) obj[5];
			l.setProfit(bd5);
		    listRecord.add(l);
		}
		map.put("data", listRecord);
		
		String sqlsum="select SUM(consumption) as consumption,SUM(cancel_amount) as cancel_amount,SUM(return_prize) as return_prize,SUM(rebate) as rebate, SUM(profit) as profit from lottery_pl_report where create_time>:startTime and create_time<=:endTime and  user_name in(:userNameList) and code_name=:codeName";
		Query<?> querysum=getSessionFactory().getCurrentSession().createNativeQuery(sqlsum);
		querysum.setParameterList("userNameList", userNameList);
		querysum.setParameter("codeName",codeName);
		querysum.setParameter("startTime", beginDate,DateType.INSTANCE);
		querysum.setParameter("endTime", endDate,DateType.INSTANCE);
    	List<?> ListSum=null;
    	ListSum=querysum.list();
	    logger.debug(ListSum.size()+"-----------------------------queryNextTeamAll----SQL--------------------------------");
	    Iterator<?> itSum=ListSum.iterator();
	    List<LotteryPlReport> listRecordSum=new ArrayList<LotteryPlReport>();
	    LotteryPlReport l=new LotteryPlReport();
		Object[] obj=(Object[]) itSum.next();
		if(obj[0]!=null) {
			BigDecimal bd0 = (BigDecimal) obj[0];
			l.setConsumption(bd0);
			BigDecimal bd1 = (BigDecimal) obj[1];
			l.setCancelAmount(bd1);
			BigDecimal bd2 = (BigDecimal) obj[2];
			l.setReturnPrize(bd2);
			BigDecimal bd3 = (BigDecimal) obj[3];
			l.setRebate(bd3);
			BigDecimal bd4 = (BigDecimal) obj[4];
			l.setProfit(bd4);
			listRecordSum.add(l);
		}
	    map.put("sumData", listRecordSum);
		return map;
	}
	
}


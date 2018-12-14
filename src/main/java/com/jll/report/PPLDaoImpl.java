package com.jll.report;

import java.math.BigDecimal;
//import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;


import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.common.utils.DateUtil;
import com.jll.entity.display.PlatformSummary;

@Repository
public class PPLDaoImpl extends HibernateDaoSupport implements PPLDao {
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	//平台盈亏
	@Override
	public List<?> queryPPL(String startTime, String endTime, String codeName, String issueNum, String playTypeid) {
		Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例  
	    ca.setTime(new Date()); // 设置时间为当前时间  
	    Date resultDate = ca.getTime(); // 结果  
	    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");  
	    String startTime1=sdf2.format(resultDate); 
	    Date today = DateUtil.fmtYmdToDate(startTime1);
	    Date endDate1 = DateUtil.fmtYmdToDate(endTime);
	    int compareToBefore=endDate1.compareTo(today);
	    Date queryTime=today;
	    if(compareToBefore>0||compareToBefore==0) {
	    	queryTime=today;
	    }else if(compareToBefore<0) {
	    	queryTime=endDate1;
	    }
		String sql = "select freezing_funds,freezing_red_funds,all_balances,all_red_balances from platform_fund_summary where create_time=:createTime";
		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
		Query<?> query = getSessionFactory().getCurrentSession().createNativeQuery(sql);
		query.setParameter("createTime", queryTime,DateType.INSTANCE); 
		List<?> list = new ArrayList();
		list = query.list();
		List<PlatformSummary> list1=new ArrayList<PlatformSummary>();
		PlatformSummary p=new PlatformSummary();
		Iterator it=list.iterator();
		while(it.hasNext()) {
			Object[] obj=(Object[]) it.next();
			BigDecimal bd = (BigDecimal) obj[0];
			String sbd=null;
			if(bd==null) {
				sbd="0";
			}else {
				sbd=bd.toString();
			}
			p.setFreezingFunds(sbd);
			
			BigDecimal bd1 = (BigDecimal) obj[1];
			String sbd1=null;
			if(bd1==null) {
				sbd1="0";
			}else {
				sbd1=bd1.toString();
			}
			p.setFreezingRedFunds(sbd1);
			
			BigDecimal bd2 = (BigDecimal)obj[2];
			String sbd2=null;
			if(bd2==null) {
				sbd2="0";
			}else {
				sbd2=bd2.toString();
			}
			p.setAllBalances(sbd2);
			
			BigDecimal bd3 = (BigDecimal)obj[3];
			String sbd3=null;
			if(bd3==null) {
				sbd3="0";
			}else {
				sbd3=bd3.toString();
			}
			p.setAllRedBalances(sbd3);
		}
	
		
		
		
		
		
		
		Map<String,Object> map=new HashMap<String,Object>();
		String timeSql=" where create_time >=:startTime and create_time <=:endTime";
		Date beginDate = DateUtil.fmtYmdToDate(startTime);
	    Date endDate = DateUtil.fmtYmdToDate(endTime);
		map.put("startTime", beginDate);
		map.put("endTime", endDate);
		
		String sqlRecharge="select sum(recharge) as recharge,sum(withdraw) as withdraw from platform_fund_summary where create_time >=:startTime and create_time <=:endTime";
		Query<?> queryRecharge = getSessionFactory().getCurrentSession().createNativeQuery(sqlRecharge);
		queryRecharge.setParameter("startTime", beginDate,DateType.INSTANCE);
		queryRecharge.setParameter("endTime", endDate,DateType.INSTANCE);
		List<?> listRecharge = new ArrayList();
		listRecharge = queryRecharge.list();
		Iterator itRecharge=listRecharge.iterator();
		while(itRecharge.hasNext()) {
			Object[] obj=(Object[]) itRecharge.next();
			BigDecimal bd0 = (BigDecimal)obj[0];
			String sbd0=null;
			if(bd0==null) {
				sbd0="0";
			}else {
				sbd0=bd0.toString();
			}
			p.setRecharge(sbd0);
			
			BigDecimal bd1 = (BigDecimal)obj[1];
			String sbd1=null;
			if(bd1==null) {
				sbd1="0";
			}else {
				sbd1=bd1.toString();
			}
			p.setWithdraw(sbd1);
		}
		
		String codeNameSql="";
		String issueNumSql="";
		String playTypeidSql="";
		if(!StringUtils.isBlank(codeName)) {
			codeNameSql=" and code_name=:codeName";
			map.put("codeName", codeName);
		}
		if(!StringUtils.isBlank(issueNum)) {
			issueNumSql=" and issue_num=:issueNum";
			map.put("issueNum", issueNum);
		}
		if(!StringUtils.isBlank(playTypeid)) {
			playTypeidSql=" and play_typeid=:playTypeid";
			map.put("playTypeid", playTypeid);
		}
		String sql1 = "select sum(betting) as betting,SUM(cancel_amount) as cancel_amount,SUM(winning) as winning,SUM(rebate) as rebate,SUM(platform_profit) as platform_profit from lottery_platform_profit "+timeSql+codeNameSql+issueNumSql+playTypeidSql;
		logger.debug(sql1+"-----------------------------queryLoyTst----SQL--------------------------------");
		Query<?> query1 = getSessionFactory().getCurrentSession().createNativeQuery(sql1);
		if (map != null) {  
            Set<String> keySet = map.keySet();  
            for (String string : keySet) {  
                Object obj = map.get(string);  
            	if(obj instanceof Date){  
            		query1.setParameter(string, (Date)obj,DateType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
                }else if(obj instanceof Object[]){  
                	query1.setParameterList(string, (Object[])obj);  
                }else{  
                	query1.setParameter(string, obj);  
                }  
            }  
        }
		List<?> list2 = new ArrayList();
		list2 = query1.list();
		Iterator it1=list2.iterator();
		while(it1.hasNext()) {
			Object[] obj=(Object[]) it1.next();
			BigDecimal bd = (BigDecimal) obj[0];
			String sbd=null;
			if(bd==null) {
				sbd="0";
			}else {
				sbd=bd.toString();
			}
			p.setBetting(sbd);
			
			BigDecimal bd1 = (BigDecimal)obj[1];
			String sbd1=null;
			if(bd1==null) {
				sbd1="0";
			}else {
				sbd1=bd1.toString();
			}
			p.setCancelAmount(sbd1);
			
			BigDecimal bd2 = (BigDecimal)obj[2];
			String sbd2=null;
			if(bd2==null) {
				sbd2="0";
			}else {
				sbd2=bd2.toString();
			}
			p.setWinning(sbd2);
			
			BigDecimal bd3 = (BigDecimal)obj[3];
			String sbd3=null;
			if(bd3==null) {
				sbd3="0";
			}else {
				sbd3=bd3.toString();
			}
			p.setRebate(sbd3);
			
			BigDecimal bd4 = (BigDecimal)obj[4];
			String sbd4=null;
			if(bd4==null) {
				sbd4="0";
			}else {
				sbd4=bd4.toString();
			}
			p.setPlatformProfit(sbd4);
		}
		list1.add(p);
		return list1;
	}
}


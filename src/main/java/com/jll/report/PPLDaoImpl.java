package com.jll.report;

import java.math.BigDecimal;
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
		String timeSql="";
		Map<String,Object> map=new HashMap<String,Object>();
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" where create_time >:startTime and create_time <=:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		String sql = "select sum(freezing_funds) as freezing_funds, sum(all_balances) as all_balances,SUM(recharge) as recharge,sum(withdraw) as withdraw from platform_fund_summary "+timeSql;
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
		List<?> list = new ArrayList();
		try {
			list = query.list();
		}catch(Exception e) {
			e.printStackTrace();
		}
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
			BigDecimal bd1 = (BigDecimal)obj[1];
			String sbd1=null;
			if(bd1==null) {
				sbd1="0";
			}else {
				sbd1=bd1.toString();
			}
			p.setAllBalances(sbd1);
			BigDecimal bd2 = (BigDecimal)obj[2];
			String sbd2=null;
			if(bd2==null) {
				sbd2="0";
			}else {
				sbd2=bd2.toString();
			}
			p.setRecharge(sbd2);
			BigDecimal bd3 = (BigDecimal)obj[3];
			String sbd3=null;
			if(bd3==null) {
				sbd3="0";
			}else {
				sbd3=bd3.toString();
			}
			p.setWithdraw(sbd3);
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
		Query<?> query1 = getSessionFactory().getCurrentSession().createSQLQuery(sql1);
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
		try {
			list2 = query1.list();
		}catch(Exception e) {
			e.printStackTrace();
		}
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


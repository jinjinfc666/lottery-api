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
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageBean;
import com.jll.entity.MemberPlReport;
import com.jll.entity.TeamPlReport;
import com.jll.entity.UserInfo;
import com.jll.user.UserInfoServiceImpl;

@Repository
public class TReportDaoImpl extends DefaultGenericDaoImpl<TeamPlReport> implements TReportDao {
	//团队盈亏报表
	@Override
	public PageBean queryTeamAll(String startTime, String endTime, String userName,Integer pageIndex,Integer pageSize) {
		String userNameSql="";
		String timeSql="";
		Map<String,Object> map=new HashMap<String,Object>();
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and user_name=:userName";
			map.put("userName", userName);
		}else {
			userNameSql=" and  user_type=:userName";
			map.put("userName", Constants.UserType.AGENCY.getCode());
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql="where create_time >=:startTime and create_time <:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		String sql=null;
		sql="select user_name,SUM(deposit) as deposit,SUM(withdrawal) as withdrawal,SUM(deduction) as deduction,sum(consumption) as consumption,SUM(cancel_amount) as cancel_amount,SUM(return_prize) as return_prize,SUM(rebate) as rebate,SUM(recharge_member) AS recharge_member,SUM(new_members) as new_members,sum(profit) as profit,user_type from team_pl_report "+timeSql+userNameSql+" GROUP BY user_name,user_type";
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryBySqlPagination(page, sql,map);
		return pageBean;
	}
	//查找下级代理
	@Override
	public Map<String,Object> queryNextTeamAll(String startTime, String endTime, String userName) {
		Map<String,Object> map=new HashMap<String,Object>();
		String sql = "from UserInfo where userName = :userName";
	    Query<UserInfo> query = getSessionFactory().getCurrentSession().createQuery(sql,UserInfo.class);
	    query.setParameter("userName", userName);
	    List<UserInfo> list = query.list();
	    Integer id=list.get(0).getId();
	    String sql1="select a.user_name from(select *,FIND_IN_SET(:id,superior) as aa from user_info)a where a.aa=1";
	    Query<?> query1 = getSessionFactory().getCurrentSession().createNativeQuery(sql1);
	    query1.setParameter("id", id);
	    List<?> userNameList=query1.list();
	    if(userNameList==null||userNameList.size()<=0) {
	    	map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_AGENCY.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_AGENCY.getErrorMes());
			return map;
	    }
	    String sql2="select user_name,SUM(deposit) as deposit,SUM(withdrawal) as withdrawal,SUM(deduction) as deduction,SUM(consumption) as consumption,SUM(cancel_amount) as cancel_amount,SUM(return_prize) as return_prize,SUM(rebate) as rebate,SUM(recharge_member) as recharge_member,SUM(new_members) as new_members,SUM(profit) as profit,user_type from team_pl_report where user_name in(:userNameList)  and create_time>=:startTime and create_time<:endTime GROUP BY user_name,user_type";
	    Query<?> query2=getSessionFactory().getCurrentSession().createNativeQuery(sql2);
	    query2.setParameterList("userNameList", userNameList);
	    Date beginDate = java.sql.Date.valueOf(startTime);
	    Date endDate = java.sql.Date.valueOf(endTime);
	    query2.setParameter("startTime", beginDate,DateType.INSTANCE);
	    query2.setParameter("endTime", endDate,DateType.INSTANCE);
    	List<?> memberPlReportList=null;
    	memberPlReportList=query2.list();
	    Iterator<?> it=memberPlReportList.iterator();
	    List<TeamPlReport> listRecord=new ArrayList<TeamPlReport>();
		while(it.hasNext()) {
			TeamPlReport m=new TeamPlReport();
			Object[] obj=(Object[]) it.next();
			m.setUserName((String)obj[0]);
			BigDecimal bd1 = (BigDecimal) obj[1];
			m.setDeposit(bd1);
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
			BigDecimal bd8=(BigDecimal)obj[8];
			m.setRechargeMember((Integer)bd8.intValue());
			BigDecimal bd9=(BigDecimal)obj[9];
			m.setNewMembers((Integer)bd9.intValue());
			BigDecimal bd10 = (BigDecimal) obj[10];
			m.setProfit(bd10);
			m.setUserType((Integer)obj[11]);
		    listRecord.add(m);
		}
		map.put("data", listRecord);
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
}




































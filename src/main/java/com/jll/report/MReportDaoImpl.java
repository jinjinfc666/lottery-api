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
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageBean;
import com.jll.entity.MemberPlReport;
import com.jll.entity.UserInfo;
import com.jll.user.UserInfoServiceImpl;

@Repository
public class MReportDaoImpl extends DefaultGenericDaoImpl<MemberPlReport> implements MReportDao {
	private Logger logger = Logger.getLogger(UserInfoServiceImpl.class);
	//会员盈亏报表
	@Override
	public PageBean queryAll(String startTime,String endTime,String userName,Integer pageIndex,Integer pageSize) {
		String userNameSql="";
		String timeSql="";
		Map<String,Object> map=new HashMap<String,Object>();
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and user_name=:userName";
			map.put("userName", userName);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" where create_time >=:startTime and create_time <:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		String sql = "select user_name,SUM(deposit) as deposit,SUM(withdrawal) as withdrawal,SUM(deduction) as deduction,sum(consumption) as consumption,SUM(cancel_amount) as cancel_amount,SUM(return_prize) as return_prize,SUM(rebate) as rebate,sum(profit) as profit,user_type from member_pl_report "+timeSql+userNameSql+" GROUP BY user_name,user_type";
//		String sql = "select a.user_name,a.deposit,a.withdrawal,a.deduction,a.consumption,a.cancel_amount,a.return_prize,a.rebate,a.profit,b.user_type from (SELECT	user_name,SUM(deposit) AS deposit,SUM(withdrawal) AS withdrawal,SUM(deduction) AS deduction,sum(consumption) AS consumption,SUM(cancel_amount) AS cancel_amount,SUM(return_prize) AS return_prize,SUM(rebate) AS rebate,sum(profit) AS profit FROM member_pl_report "+userNameSql+timeSql+" GROUP BY user_name) a LEFT JOIN (select DISTINCT(user_name),user_type from member_pl_report "+userNameSql+timeSql+") b ON a.user_name=b.user_name";
		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryBySqlPagination(page, sql,map);
		return pageBean;
	}
}




































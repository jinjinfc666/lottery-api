package com.jll.report;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccountDetails;





@Repository
public class LoyTstDaoImpl extends DefaultGenericDaoImpl<OrderInfo> implements LoyTstDao {
	private Logger logger = Logger.getLogger(LoyTstDaoImpl.class);
	@Override
	public PageBean queryLoyTst(Integer codeTypeNameId,String lotteryType,Integer isZh,Integer state,Integer terminalType,String startTime,String endTime,String issueNum,String userName,String orderNum,Integer pageIndex,Integer pageSize) {
		String lotteryTypeSql="";
		String isZhSql="";
		String stateSql="";
		String terminalTypeSql="";
		String timeSql="";
		String issueNumSql="";
		String userNameSql="";
		String orderNumSql="";
		Map<String,Object> map=new HashMap();
		if(!StringUtils.isBlank(lotteryType)) {
			lotteryTypeSql=" and d.codeName=:lotteryType"; 
			map.put("lotteryType", lotteryType);
		}
		if(isZh!=null) {
			isZhSql=" and a.isZh=:isZh";
			map.put("isZh", isZh);
		}
		if(state!=null) {
			stateSql=" and a.state=:state";
			map.put("state", state);
		}
		if(terminalType!=null) {
			terminalTypeSql=" and a.terminalType=:terminalType";
			map.put("terminalType", terminalType);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" and a.createTime >=:startTime and a.createTime <:endTime";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		if(!StringUtils.isBlank(issueNum)) {
			issueNumSql=" and d.issueNum=:issueNum";		
			map.put("issueNum", issueNum);
		}
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and b.userName=:userName";
			map.put("userName", userName);
		}
		if(!StringUtils.isBlank(orderNum)) {
			orderNumSql=" and a.orderNum=:orderNum";
			map.put("orderNum", orderNum);
		}
		String betting=Constants.AccOperationType.BETTING.getCode();
//		String sql="from OrderInfo a,UserInfo b,UserAccountDetails c,Issue d,SysCode e,PlayType f where a.userId=b.id and a.issueId=d.id and a.id=c.orderId and d.lotteryType=e.codeName and e.codeType=:codeTypeNameId and a.playType=f.id and c.operationType=:betting "+lotteryTypeSql+isZhSql+stateSql+terminalTypeSql+timeSql+issueNumSql+userNameSql+orderNumSql+" group by a.id order by a.id desc";
		String sql="from OrderInfo a,UserInfo b,Issue c,SysCode d,PlayType e,UserAccount f where a.userId=b.id and a.issueId=c.id and a.playType=e.id and a.walletId=f.id and c.lotteryType=d.codeName and d.codeType=:codeTypeNameId and b.userType!=:userType "+lotteryTypeSql+isZhSql+stateSql+terminalTypeSql+timeSql+issueNumSql+userNameSql+orderNumSql+" order by a.id desc";
		logger.debug(sql+"-----------------------------queryLoyTst----SQL--------------------------------");
		map.put("codeTypeNameId", codeTypeNameId);
		map.put("userType", Constants.UserType.DEMO_PLAYER.getCode());
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		return pageBean;
	}
	
}


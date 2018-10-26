package com.jll.game;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageBean;
import com.jll.entity.IpBlackList;
import com.jll.entity.Issue;
import com.jll.entity.SysCode;

@Repository
public class IssueDaoImpl extends DefaultGenericDaoImpl<Issue> implements IssueDao
{
	private Logger logger = Logger.getLogger(IssueDaoImpl.class);

	@Override
	public void savePlan(List<Issue> issues) {
		for(Issue issue : issues) {
			this.saveOrUpdate(issue);
		}
	}

	@Override
	public void saveIssue(Issue currIssue) {
		this.saveOrUpdate(currIssue);
	}

	@Override
	public Issue getIssueById(Integer id) {
		String sql = "from Issue where id=?";
		List<Object> params = new ArrayList<>();
		params.add(id);
		List<Issue> result = query(sql, params, Issue.class);
		if(result == null || result.size() == 0) {
			return null;
		}
		return result.get(0);
	}

	@Override
	public Issue getIssueByIssueNum(String lottoType, String issueNum) {
		String sql = "from Issue where lotteryType = ? and issueNum =?";
		List<Object> params = new ArrayList<>();
		params.add(lottoType);
		params.add(issueNum);
		
		List<Issue> result = query(sql, params, Issue.class);
		if(result == null || result.size() == 0) {
			return null;
		}
		
		return result.get(0);
	}


	//通过彩种和期次数量来查找
	@Override
	public List<Issue> queryByLTNumber(String lotteryType,Date time, Integer number) {
		String sql="from Issue  where lotteryType=:lotteryType AND startTime<=:time ORDER BY id DESC";
		Query<Issue> query = getSessionFactory().getCurrentSession().createQuery(sql,Issue.class);
		query.setFirstResult(0);
		query.setMaxResults(number);
	    query.setParameter("lotteryType", lotteryType);
	    query.setParameter("time", time,TimestampType.INSTANCE);
	    List<Issue> list = query.list();
		return list;
	}
	//通过当前时间和彩种    获得离当前时间最近的一期成功的期次时间
	@Override
	public List<Issue> queryByLTNumeber(String lotteryType) {
		Integer state=3;
		String sql="from Issue  where state>:state and lotteryType=:lotteryType AND startTime<=:time ORDER BY id DESC";
		Query<Issue> query = getSessionFactory().getCurrentSession().createQuery(sql,Issue.class);
		query.setFirstResult(0);
		query.setMaxResults(1);
		query.setParameter("state", state);
	    query.setParameter("lotteryType", lotteryType);
	    query.setParameter("time", new Date(),TimestampType.INSTANCE);
	    List<Issue> list = query.list();
		return list;
	}
	//统一撤单需要的期号
	@Override
	public Map<String,Object> queryAllByIssue(String lotteryType,Integer state, String startTime, String endTime, Integer pageIndex,
			Integer pageSize, String issueNum,Map<String, SysCode> sysCodes) {
		String lotteryTypeSql="";
		String issueNumSql="";
		String timeSql="";
		String stateSql="";
		Map<String,Object> map=new HashMap();
		if(!StringUtils.isBlank(lotteryType)) {
			lotteryTypeSql=" and lotteryType=:lotteryType ";
			map.put("lotteryType", lotteryType);
		}
		if(!StringUtils.isBlank(issueNum)) {
			issueNumSql=" and issueNum=:issueNum ";
			map.put("issueNum", issueNum);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" where startTime>=:startTime and startTime<:endTime ";
		    String startTime1=startTime+" 00:00:00";//开始时间
		    //结束时间
		    Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例  
		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
		    ca.setTime(new Date()); // 设置时间为当前时间  
		    Date resultDate = ca.getTime(); // 结果   
		    String nowTime=sdf1.format(resultDate); 
		    Date nowTime1 = java.sql.Date.valueOf(nowTime);//当前时间
		    Date oldTime = java.sql.Date.valueOf(endTime);//前端传过来的时间
		    boolean b=DateUtils.isSameDay(nowTime1, oldTime);
		    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		    String nowTime2=sdf2.format(resultDate);//当前时间  时分秒
		    Date beginDate =null;
			Date endDate=null;
			String endTime1=null;
		    if(b) {
		    	endTime1=nowTime2;
		    }else {
		    	endTime1=endTime+" 00:00:00";
		    }
		    try {
				beginDate = (Date) sdf2.parse(startTime1);
				endDate = (Date) sdf2.parse(endTime1); 
			}catch(ParseException  ex) {
				
			}
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		
		if(state!=null) {
			stateSql=" and state=:state ";
			map.put("state", state);
		}
		String sql="From Issue "+timeSql+lotteryTypeSql+issueNumSql+stateSql+"ORDER BY id DESC";
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByTimePagination(page,sql,map);
		List<Issue> issues=pageBean.getContent();
		for(Issue issue:issues) {
			String codeName=issue.getLotteryType();
			SysCode sysCode=sysCodes.get(codeName);
			String lotteryTypeOld=issue.getLotteryType();
			issue.setLotteryType(lotteryTypeOld+"/"+sysCode.getRemark());
		}
		map.clear();
		map.put("data",pageBean);
	    return map;
	}
	//追号需要的期号信息
	@Override
	public List<Issue> queryIsZhIssue(String lotteryType, Date startTime, Date endTime) {
		String sql="From Issue where lotteryType=:lotteryType and startTime>:startTime and startTime<=:endTime";
		Query<Issue> query = getSessionFactory().getCurrentSession().createQuery(sql,Issue.class);
		query.setParameter("lotteryType", lotteryType);
	    query.setParameter("startTime", startTime,TimestampType.INSTANCE);
	    query.setParameter("endTime", endTime,TimestampType.INSTANCE);
	    List<Issue> list = query.list();
	    if(list!=null&& list.size()>0) {
	    	return list;
	    }
		return null;
	}
	
}

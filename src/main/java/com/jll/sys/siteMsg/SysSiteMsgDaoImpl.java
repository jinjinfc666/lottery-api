package com.jll.sys.siteMsg;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.constants.Constants.UserType;
import com.jll.common.utils.PageQuery;
import com.jll.common.utils.PagenationUtil;
import com.jll.common.utils.StringUtils;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageBean;
import com.jll.dao.PageQueryDao;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.UserBankCard;
import com.jll.entity.UserInfo;
import com.jll.user.UserInfoDao;

@Repository
public class SysSiteMsgDaoImpl extends DefaultGenericDaoImpl<SiteMessage> implements SysSiteMsgDao
{
	@Resource
	UserInfoDao userDao;
	@Override
	public void save(SiteMessage siteMessage) {
		this.saveOrUpdate(siteMessage);
	}
	@Override
	public List<SiteMessage> getSiteMessageLists(String userName, PageQueryDao page) {
		
		String sql = "from SiteMessage sit,UserInfo u where u.id = sit.userId ";
		List<Object> list=new ArrayList<Object>();
		if(!StringUtils.isEmpty(userName)){
			sql += " and u.userName = ?";
			list.add(userName);
		}
		List<SiteMessage> generalAgencys  = query(sql, list, SiteMessage.class);
		if(generalAgencys == null || generalAgencys.size() == 0) {
			return null;
		}
		return generalAgencys;
	}
	@Override
	public Map<String, Object> querySiteMessage(Map<String, String> params) {
		Map<String,Object> map=new HashMap<String,Object>();
		Integer id=Integer.parseInt(params.get("id"));
		Integer pageIndex=Integer.parseInt(params.get("pageIndex"));
		Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
		map.put("receiver", id);
		String isReadSql="";
		if(params.containsKey("isRead")) {
			Integer isRead=Integer.parseInt(params.get("isRead"));
			isReadSql=" and a.isRead = :isRead ";
			map.put("isRead", isRead);
		}
		String startTime=(String)params.get("startTime");
		String endTime=(String)params.get("endTime");
		String timeSql="";
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" and a.createTime>=:startTime and a.createTime<:endTime ";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
		    map.put("startTime", beginDate);
		    map.put("endTime", endDate);
		}
		String sql="";
		sql="from SiteMessage a,UserInfo b where a.creator=b.id and a.receiver =:receiver "+isReadSql+timeSql+" ORDER BY a.id DESC";
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		map.clear();
		map.put("data", pageBean);
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
	@Override
	public Map<String, Object> updateUserSiteMessageRead(Map<String, String> params) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean contains = params.containsKey("id");
		if(!contains) {
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return map;
			
		}else {	
			Integer id=Integer.parseInt(params.get("id"));
			Integer isRead=0;
			Integer isReada=1;
			SiteMessage siteMessage=querySiteMessageIsNo(id);
			if(siteMessage==null) {
				map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
				map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
				return map;
			}else {
				siteMessage.setIsRead(isRead);
				this.saveOrUpdate(siteMessage);
				Session session=getSessionFactory().getCurrentSession();
				String sql="update site_mess_feedback set is_read=:isRead where mes_id=:id and is_read=:isReada";
				Query query = session.createNativeQuery(sql);
				query.setParameter("isRead", isRead);
				query.setParameter("id", id);
				query.setParameter("isReada", isReada);
				query.executeUpdate();
				map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
				return map;
			}
		}
	}
	
	private SiteMessage querySiteMessageIsNo(Integer id) {
		String sql="from SiteMessage where id=:id";
		Query<SiteMessage> query = getSessionFactory().getCurrentSession().createQuery(sql,SiteMessage.class);
	    query.setParameter("id", id);
	    List<SiteMessage> list= query.list();
	    if(list!=null&&list.size()>0) {
	    	return list.get(0);
	    }
		return null;
	}
	@Override
	public List<?> querySiteMessFeedback(Integer mesId) {
		String sql="from SiteMessFeedback a,UserInfo b where a.mesId=:mesId and a.fbUserId=b.id";
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter("mesId", mesId);
	    List<?> list= query.list();
	    if(list!=null&&list.size()>0) {
	    	return list;
	    }
		return null;
	}
	@Override
	public List<?> querySiteMessageById(Integer id) {
		Map<String,Object> map=new HashMap<String,Object>();
		String sql="";
		sql="from SiteMessage a,UserInfo b where a.creator=b.id and a.id =:id ";
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter("id", id);
	    List<?> list= query.list();
	    if(list==null&&list.size()==0) {
	    	list=null;
	    }
		return list;
	}
	@Override
	public Map<String, Object> querySiteMessageB(Map<String, Object> params) {
		Map<String,Object> map=new HashMap<String,Object>();
		String userName=(String)params.get("userName");
		Integer userId=null;
		if(!StringUtils.isBlank(userName)) {
			UserInfo user=userDao.getUserByUserName(userName);
			if(null == user){
				map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
				map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
				return map;
			}
			userId=user.getId();
		}
		Integer pageIndex=Integer.parseInt(params.get("pageIndex").toString());
		Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
		String receiverSql="";
		if(userId!=null) {
			receiverSql="and a.receiver =:receiver ";
			map.put("receiver", userId);
		}
		String isReadSql="";
		if(params.containsKey("isRead")) {
			Integer isRead=Integer.parseInt(params.get("isRead").toString());
			isReadSql=" and a.isRead = :isRead ";
			map.put("isRead", isRead);
		}
		String startTime=(String)params.get("startTime");
		String endTime=(String)params.get("endTime");
		String timeSql="";
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" and a.createTime>=:startTime and a.createTime<:endTime ";
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
		    map.put("startTime", beginDate);
		    map.put("endTime", endDate);
		}
		String sql="";
		sql="from SiteMessage a,UserInfo b where a.receiver=b.id  "+receiverSql+isReadSql+timeSql+" ORDER BY a.id DESC";
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		map.clear();
		map.put("data", pageBean);
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
}

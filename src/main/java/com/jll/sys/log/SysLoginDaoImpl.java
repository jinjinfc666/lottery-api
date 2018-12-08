package com.jll.sys.log;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
import com.jll.dao.DefaultGenericDaoImpl;//失败后添加登录失败次数和修改锁定时间
import com.jll.dao.PageBean;
import com.jll.entity.SysLogin;

@Repository
public class SysLoginDaoImpl extends DefaultGenericDaoImpl<SysLogin> implements SysLoginDao
{
	private Logger logger = Logger.getLogger(SysLoginDaoImpl.class);

	@Override
	public void saveUpdate(SysLogin sysLogin) {
		this.saveOrUpdate(sysLogin);
	}
	@Override
	public PageBean queryLoginlog(Integer type, Integer userId, String startTime, String endTime,Integer pageIndex,Integer pageSize) {
		Map<String,Object> map=new HashMap<String,Object>();
		String userIdSql="";
		if(userId!=null) {
			userIdSql="and  a.userId=:userId";
			map.put("userId", userId);
		}
		Date beginDate = java.sql.Date.valueOf(startTime);
	    Date endDate = java.sql.Date.valueOf(endTime);
		map.put("startTime", beginDate);
		map.put("endTime", endDate);
		String userTypeSql="";
		if(type==1) {//前端
			userTypeSql=" and b.userType!=:userType";
			map.put("userType", Constants.UserType.SYS_ADMIN.getCode());
		}else if(type==2) {//后台
			userTypeSql=" and b.userType=:userType";
			map.put("userType",Constants.UserType.SYS_ADMIN.getCode());
		}
		String sql="from SysLogin a,UserInfo b where a.userId=b.id  "+userIdSql+" and a.createTime>=:startTime and a.createTime<:endTime "+userTypeSql+" ORDER BY a.id DESC";
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		return pageBean;
	}
	//查询不存在用户登录日志
	@Override
	public PageBean queryLoginlog(String startTime, String endTime, Integer pageIndex, Integer pageSize) {
		Map<String,Object> map=new HashMap<String,Object>();
		Date beginDate = java.sql.Date.valueOf(startTime);
	    Date endDate = java.sql.Date.valueOf(endTime);
		map.put("startTime", beginDate);
		map.put("endTime", endDate);
		String sql="from SysLogin where createTime>=:startTime and createTime<:endTime and userId is Null  ORDER BY id DESC";
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		return pageBean;
	}
	//通过ip查询用户失败登录此时
	@Override
	public List<SysLogin> queryFailLoginCount(String ip) {
		String sql="from SysLogin where LOCATE(:ip, logData)>0 and logType=:logType";
		Query<SysLogin> query = getSessionFactory().getCurrentSession().createQuery(sql,SysLogin.class);
	    query.setParameter("ip", ip);
	    query.setParameter("logType",StringUtils.OPE_LOG_USER_FAILURE);
	    List<SysLogin> list=query.list();
	    return list;
	}
  
}

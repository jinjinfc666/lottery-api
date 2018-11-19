package com.jll.sys.security.user;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageBean;
import com.jll.entity.UserInfo;
@Repository
public class SysUserDaoImpl extends DefaultGenericDaoImpl<UserInfo> implements SysUserDao
{
	private Logger logger = Logger.getLogger(SysUserDaoImpl.class);
	//添加
	@Override
	public void saveOrUpdateUserInfo(UserInfo userInfo) {
		this.saveOrUpdate(userInfo);
	}
	//通过用户名查询UserInfo
	@Override
	public List<UserInfo> queryByUserName(String userName) {
		String sql="from UserInfo where userName=:userName";
		Query<UserInfo> query = getSessionFactory().getCurrentSession().createQuery(sql,UserInfo.class);
		query.setParameter("userName", userName);
		List<UserInfo> list = query.list();
		return list;
	}
	//通过id查询用户
	@Override
	public List<UserInfo> queryById(Integer id) {
		String sql="from UserInfo where id=:id";
		Query<UserInfo> query = getSessionFactory().getCurrentSession().createQuery(sql,UserInfo.class);
		query.setParameter("id", id);
		List<UserInfo> list = query.list();
		return list;
	}
	@Override
	public Map<String, Object> querySysUser(String userName, Integer pageIndex, Integer pageSize) {
		String userNameSql="";
		List<Object> list=new ArrayList<Object>();
		Map<String,Object> map=new HashMap();
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and a.userName=:userName";
			map.put("userName", userName);
		}
		Integer userType=Constants.UserType.SYS_ADMIN.getCode();
		map.put("userType", userType);
		String sql="from UserInfo a where a.userType=:userType "+userNameSql+" order by a.id";
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		map.clear();
		map.put("data", pageBean);
		return map;
	}
	
}

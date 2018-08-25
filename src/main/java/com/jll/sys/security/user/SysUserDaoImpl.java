package com.jll.sys.security.user;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
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
	
}

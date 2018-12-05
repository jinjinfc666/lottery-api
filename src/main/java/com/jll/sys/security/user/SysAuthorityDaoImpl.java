package com.jll.sys.security.user;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.IpBlackList;
import com.jll.entity.SysAuthority;
@Repository
public class SysAuthorityDaoImpl extends DefaultGenericDaoImpl<SysAuthority> implements SysAuthorityDao
{
	private Logger logger = Logger.getLogger(SysAuthorityDaoImpl.class);
	//添加
	@Override
	public void saveOrUpdateSysAuthority(SysAuthority sysAuthority) {
		this.saveOrUpdate(sysAuthority);
		
	}
	//通过userId查询用户所拥有的权限
	@Override
	public List<SysAuthority> queryByUserId(Integer userId) {
		String sql="from SysAuthority where userId=:userId";
		Query<SysAuthority> query = getSessionFactory().getCurrentSession().createQuery(sql,SysAuthority.class);
		query.setParameter("userId", userId);
		List<SysAuthority> list=query.list();
		return list;
	}
	//通过userId删除授权
	@Override
	public void deleteByUserId(Integer userId) {
		String sql="delete SysAuthority where userId=:userId";
		Query query = getSessionFactory().getCurrentSession().createQuery(sql);
		query.setParameter("userId", userId);
		query.executeUpdate();
	}
	//通过roleId删除授权
	@Override
	public void deleteByRoleId(Integer roleId) {
		String sql="delete SysAuthority where roleId=:roleId";
		Query query = getSessionFactory().getCurrentSession().createQuery(sql);
		query.setParameter("roleId", roleId);
		query.executeUpdate();
	}
	//通过userId获得用户的权限列表
	@Override
	public List<String> queryGetByUserId(Integer userId) {
		String sql="select b.roleName from SysAuthority a,SysRole b where a.roleId=b.id and a.userId=:userId";
		Query query = getSessionFactory().getCurrentSession().createQuery(sql);
		query.setParameter("userId", userId);
		List<String> list=query.list();
		return list;
	}
}

package com.jll.sys.security.permission;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.IpBlackList;
import com.jll.entity.SysRole;
import com.jll.entity.UserInfo;
@Repository
public class SysRoleDaoImpl extends DefaultGenericDaoImpl<SysRole> implements SysRoleDao
{
	private Logger logger = Logger.getLogger(SysRoleDaoImpl.class);
	//添加
	@Override
	public void saveOrUpdateSysRole(SysRole sysRole) {
		this.saveOrUpdate(sysRole);
	}
	//通过id查询
	@Override
	public List<SysRole> queryById(Integer id) {
		String sql="from SysRole where id=:id";
		Query<SysRole> query = getSessionFactory().getCurrentSession().createQuery(sql,SysRole.class);
		query.setParameter("id", id);
		List<SysRole> list = query.list();
		return list;
	}
	@Override
	public List<?> query() {
		String sql="from SysRole a,UserInfo b where a.creator=b.id";
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
		List<?> list = query.list();
		return list;
	}
	//只查询当前表
	@Override
	public List<SysRole> querySysRole() {
		String sql="from SysRole where state=:state";
		Query<SysRole> query = getSessionFactory().getCurrentSession().createQuery(sql,SysRole.class);
		query.setParameter("state", Constants.BankCardState.ENABLED.getCode());
		List<SysRole> list = query.list();
		return list;
	}
	//判断这条数据存不存在
	@Override
	public List<SysRole> queryBySysRole(SysRole sysRole) {
		String roleName=sysRole.getRoleName();
		String roleDesc=sysRole.getRoleDesc();
		Integer state=sysRole.getState();
		Integer creator=sysRole.getCreator();
		String sql="from SysRole where roleName=:roleName and roleDesc=:roleDesc and state=:state and creator=:creator";
		Query<SysRole> query = getSessionFactory().getCurrentSession().createQuery(sql,SysRole.class);
		query.setParameter("roleName",roleName);
		query.setParameter("roleDesc", roleDesc);
		query.setParameter("state", state);
		query.setParameter("creator", creator);
		List<SysRole> list = query.list();
		return list;
	}
	
}

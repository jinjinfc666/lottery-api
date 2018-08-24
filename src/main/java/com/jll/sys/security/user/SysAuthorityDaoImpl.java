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
	//删除
	@Override
	public void deleteById(Integer id) {
		String sql="delete from SysAuthority where id=:id";
		Query query = getSessionFactory().getCurrentSession().createQuery(sql);
		query.setParameter("id", id);
		query.executeUpdate();
	}
	//通过id查询
	@Override
	public List<SysAuthority> queryById(Integer id) {
		String sql = "from SysAuthority where id=:id";
	    Query<SysAuthority> query = getSessionFactory().getCurrentSession().createQuery(sql,SysAuthority.class);
	    query.setParameter("id", id);
	    List<SysAuthority> list = query.list();
	    return list;
	}

	
}

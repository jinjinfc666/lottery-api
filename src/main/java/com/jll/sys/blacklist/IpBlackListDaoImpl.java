package com.jll.sys.blacklist;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageBean;
import com.jll.entity.IpBlackList;
@Repository
public class IpBlackListDaoImpl extends DefaultGenericDaoImpl<IpBlackList> implements IpBlackListDao
{
	private Logger logger = Logger.getLogger(IpBlackListDaoImpl.class);
	
	//添加
	@Override
	public void addIp(IpBlackList ipRestrictions) {
		this.saveOrUpdate(ipRestrictions);
	}
	//查询
	@Override
	public IpBlackList query(String ip) {
		String sql = "from IpBlackList where ip=:ip";
	    Query<IpBlackList> query = getSessionFactory().getCurrentSession().createQuery(sql,IpBlackList.class);
	    query.setParameter("ip", ip);
	    List<IpBlackList> list = query.list();
	    IpBlackList ipBlackList=null;
	    if(list!=null&&list.size()>0) {
	    	ipBlackList=list.get(0);
	    }
	    return ipBlackList;
	}
	//查询通过id
	@Override
	public IpBlackList query(Integer id) {
		String sql = "from IpBlackList where id=:id";
	    Query<IpBlackList> query = getSessionFactory().getCurrentSession().createQuery(sql,IpBlackList.class);
	    query.setParameter("id", id);
	    List<IpBlackList> list = query.list();
	    IpBlackList ipBlackList=null;
	    if(list!=null&&list.size()>0) {
	    	ipBlackList=list.get(0);
	    }
	    return ipBlackList;
	}
	//查询所有
	@Override
	public Map<String,Object> queryByPageIndex(Integer pageIndex,Integer pageSize) {
		Map<String,Object> map=new HashMap<String,Object>();
		String sql = "from IpBlackList";
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		map.clear();
		map.put("data",pageBean);
	    return map;
	}
	//修改
	@Override
	public void updateIp(IpBlackList ipBlackList) {
		this.saveOrUpdate(ipBlackList);
	}
	//删除
	@Override
	public void deleteIpBlackList(Integer id) {
		String sql="delete from IpBlackList where id=:id";
		Query query = getSessionFactory().getCurrentSession().createQuery(sql);
		query.setParameter("id", id);
		query.executeUpdate();
	}
	@Override
	public List<IpBlackList> query() {
		String sql = "from IpBlackList";
	    Query<IpBlackList> query = getSessionFactory().getCurrentSession().createQuery(sql,IpBlackList.class);
	    List<IpBlackList> list = query.list();
	    return list;
	}
	@Override
	public Map<String,Object> query(Integer pageIndex, Integer pageSize, String ip) {
		Map<String,Object> map=new HashMap<String,Object>();
		String sql = "from IpBlackList where ip=:ip";
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
	    map.put("ip", ip);
		PageBean pageBean=queryByPagination(page,sql,map);
		map.clear();
		map.put("data",pageBean);
	    return map;
	}
	
}

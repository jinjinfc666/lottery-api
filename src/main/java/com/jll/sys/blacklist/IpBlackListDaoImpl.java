package com.jll.sys.blacklist;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageBean;
import com.jll.entity.IpBlackList;
@Repository
public class IpBlackListDaoImpl extends DefaultGenericDaoImpl<IpBlackList> implements IpBlackListDao
{
	private Logger logger = Logger.getLogger(IpBlackListDaoImpl.class);
	
	//添加
	@Override
	public void saveIp(IpBlackList ipRestrictions) {
		this.saveOrUpdate(ipRestrictions);
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
//	//修改
//	@Override
//	public void updateIp(IpBlackList ipBlackList) {
//		this.saveOrUpdate(ipBlackList);
//	}
	//删除
	@Override
	public void deleteIpBlackList(IpBlackList ipBlackList) {
		delete(ipBlackList);
	}
	@Override
	public List<IpBlackList> query() {
		String sql = "from IpBlackList";
	    Query<IpBlackList> query = getSessionFactory().getCurrentSession().createQuery(sql,IpBlackList.class);
	    List<IpBlackList> list = query.list();
	    return list;
	}
	@Override
	public List<IpBlackList> queryByIp(String ipLong,Integer type) {
		String sql="from IpBlackList ab where ab.type=:type and SUBSTRING_INDEX(ab.ipLong, ',', 1)<=:ipLong and SUBSTRING_INDEX(ab.ipLong, ',', -1)>=:ipLong";
		Query<IpBlackList> query = getSessionFactory().getCurrentSession().createQuery(sql,IpBlackList.class);
		query.setParameter("ipLong", ipLong);
		query.setParameter("type", type);
	    List<IpBlackList> list = query.list();
	    return list;
	}
	@Override
	public PageBean queryByIp(Integer pageIndex, Integer pageSize, String ipLong,Integer type) {
		Map<String,Object> map=new HashMap<String,Object>();
		String ipSql="";
		if(!StringUtils.isBlank(ipLong)) {
			ipSql=" ab where ab.type=:type and SUBSTRING_INDEX(ab.ipLong, ',', 1)<=:ipLong and SUBSTRING_INDEX(ab.ipLong, ',', -1)>=:ipLong";
		    map.put("ipLong", ipLong);
		    map.put("type", type);
		}
		String sql = "from IpBlackList "+ipSql;
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		return pageBean;
	}
	
}

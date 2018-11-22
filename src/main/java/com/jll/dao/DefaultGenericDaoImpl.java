package com.jll.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.hibernate.type.TimestampType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import com.jll.game.LotteryCenterServiceImpl;

public class DefaultGenericDaoImpl<T> extends HibernateDaoSupport implements GenericDaoIf<T> {

	private Logger logger = Logger.getLogger(LotteryCenterServiceImpl.class);
	
	@Autowired
	@DependsOn("sessionFactory")
	public void setSuperSessionFactory(SessionFactory sessionFactory)
	{
	    super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public void saveOrUpdate(T entity) {
		logger.debug(String.format("Try to save the entity...", ""));
		getSessionFactory().getCurrentSession().saveOrUpdate(entity);
	}

	@Override
	public void delete(T entity) {
		getSessionFactory().getCurrentSession().delete(entity);
	}

	@Override
	public List<T> query(String HQL, List<Object> params, Class<T> clazz) {
		String sql = HQL;
		
	    Query<T> query = getSessionFactory().getCurrentSession().createQuery(sql, clazz);

	    if(params != null) {
	    	int indx = 0;
	    	for(Object para : params) {
	    		query.setParameter(indx, para);
	    		
	    		indx++;
	    	}
	    }
	    return query.list();
	}

	@Override
	public long queryCount(String HQL, List<Object> params, Class<T> clazz) {
		String sql = HQL;
		
	    Query<Long> query = getSessionFactory().getCurrentSession().createQuery(sql, Long.class);

	    if(params != null) {
	    	int indx = 0;
	    	for(Object para : params) {
	    		query.setParameter(indx, para);
	    		
	    		indx++;
	    	}
	    }
	    
	    return query.getSingleResult();
	}

	@Override
	public boolean add(List<T> entities) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PageBean<T> queryByPagination(PageBean<T> page, String HQL, List<Object> params, Class<T> clazz) {
		//PageBean<T> ret = new PageBean<>();
		List<T> content = null;
		int entityNameStartInd = 0;
		String sql = HQL;
		StringBuffer sqlCount = new StringBuffer("select count(*) ");
		Long totalPages =  null;
		Integer pageIndex = page.getPageIndex();
		Integer pageSize = page.getPageSize();
		Integer startPosition = pageIndex * pageSize;
	    Query<T> query = getSessionFactory().getCurrentSession().createQuery(sql, clazz);

	    entityNameStartInd = HQL.indexOf("from");
	    if(entityNameStartInd < 0) {
	    	entityNameStartInd = HQL.indexOf("FROM");
	    }
	    
	    sqlCount.append(HQL.substring(entityNameStartInd));
	    totalPages =  queryCount(sqlCount.toString(), params, clazz);
	    
	    if(totalPages % pageSize == 0) {
	    	totalPages = totalPages / pageSize;
	    }else {
	    	totalPages = totalPages / pageSize + 1; 
	    }
	    if(pageIndex.intValue() > (totalPages.intValue() - 1)) {
			return page;
		}
	    
	    if(params != null) {
	    	int indx = 0;
	    	for(Object para : params) {
	    		query.setParameter(indx, para);
	    		
	    		indx++;
	    	}
	    }
	    
	    query.setFirstResult(startPosition);
	    query.setMaxResults(pageSize);
	    content = query.list();
	    
	    page.setContent(content);
	    //ret.setPageIndex(pageIndex);
	    page.setPageSize(pageSize);
	    page.setTotalPages(totalPages);
	    
		return page;
	}
	
	@Override
	public PageBean queryByPagination(PageBean page, String HQL, Map<String,Object> params) {
		PageBean ret = new PageBean();
		List<?> content = null;
		String sql = HQL;
		Long totalPages =  null;
		Long totalNumber=null;
		Integer pageIndex = page.getPageIndex();
		Integer pageSize = page.getPageSize();
		Query  query=null;
		query= getSessionFactory().getCurrentSession().createQuery(sql);
	    
	    totalNumber =  queryCount(HQL, params);
	    
	    if(totalNumber % pageSize == 0) {
	    	totalPages = totalNumber / pageSize;
	    }else {
	    	totalPages = totalNumber / pageSize + 1; 
	    }
	    if(params != null) {
	    	Set<String> keySet = params.keySet();  
            for (String string : keySet) {  
                Object obj = params.get(string);  
            	if(obj instanceof Date){  
                	query.setParameter(string, (Date)obj,DateType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
                }else if(obj instanceof Object[]){  
                    query.setParameterList(string, (Object[])obj);  
                }else{  
                    query.setParameter(string, obj);  
                }  
            }
	    }
	    

		Integer startPosition = pageIndex==1 ? 0 : pageIndex*pageSize-pageSize;
	    
	    
	    query.setFirstResult(startPosition);
	    query.setMaxResults(pageSize);
	    content = query.list();
	    
	    ret.setContent(content);
	    ret.setPageIndex(pageIndex);
	    ret.setPageSize(pageSize);
	    ret.setTotalPages(totalPages);
	    ret.setTotalNumber(totalNumber);
	    
		return ret;
	}
	@Override
	public long queryCount(String HQL, Map<String,Object> params) {
		String sql = HQL;
		
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);

	    if(params != null) {
	    	Set<String> keySet = params.keySet();  
            for (String string : keySet) {  
                Object obj = params.get(string);  
            	if(obj instanceof Date){  
                	query.setParameter(string, (Date)obj,DateType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
                }else if(obj instanceof Object[]){  
                    query.setParameterList(string, (Object[])obj);  
                }else{  
                    query.setParameter(string, obj);  
                }  
            }
	    }
	    List<?> list=query.list();
	    return list.size();
	}
	//通过sql查询
	@Override
	public PageBean queryBySqlPagination(PageBean page, String SQL, Map<String,Object> params) {
		PageBean ret = new PageBean();
		List<?> content = null;
		String sql = SQL;
		Long totalPages =  null;
		Long totalNumber=null;
		Integer pageIndex = page.getPageIndex();
		Integer pageSize = page.getPageSize();
	    Query query = getSessionFactory().getCurrentSession().createNativeQuery(sql);
	    
	    
	    totalNumber =  querySqlCount(SQL, params);
	    
	    if(totalNumber % pageSize == 0) {
	    	totalPages = totalNumber / pageSize;
	    }else {
	    	totalPages = totalNumber / pageSize + 1; 
	    }
	    if(params != null) {
	    	Set<String> keySet = params.keySet();  
            for (String string : keySet) {  
                Object obj = params.get(string);  
            	if(obj instanceof Date){  
                	query.setParameter(string, (Date)obj,DateType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
                }else if(obj instanceof Object[]){  
                    query.setParameterList(string, (Object[])obj);  
                }else{  
                    query.setParameter(string, obj);  
                }  
            }
	    }
	    

		Integer startPosition = pageIndex==1 ? 0 : pageIndex*pageSize-pageSize;
	    
	    
	    query.setFirstResult(startPosition);
	    query.setMaxResults(pageSize);
	    content = query.list();
	    
	    ret.setContent(content);
	    ret.setPageIndex(pageIndex);
	    ret.setPageSize(pageSize);
	    ret.setTotalPages(totalPages);
	    ret.setTotalNumber(totalNumber);
	    
		return ret;
	}
	//通过sql+clazz查询
	@Override
	public PageBean<T> queryBySqlClazzPagination(PageBean<T> page, String SQL, Map<String,Object> params,Class<T> clazz) {
		PageBean<T> ret = new PageBean<>();
		List<T> content = null;
		String sql = SQL;
		Long totalPages =  null;
		Long totalNumber=null;
		Integer pageIndex = page.getPageIndex();
		Integer pageSize = page.getPageSize();
	    Query<T> query = getSessionFactory().getCurrentSession().createNativeQuery(sql,clazz);
	    
	    
	    totalNumber =  querySqlCount(SQL, params);
	    
	    if(totalNumber % pageSize == 0) {
	    	totalPages = totalNumber / pageSize;
	    }else {
	    	totalPages = totalNumber / pageSize + 1; 
	    }
	    if(params != null) {
	    	Set<String> keySet = params.keySet();  
            for (String string : keySet) {  
                Object obj = params.get(string);  
            	if(obj instanceof Date){  
                	query.setParameter(string, (Date)obj,DateType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
                }else if(obj instanceof Object[]){  
                    query.setParameterList(string, (Object[])obj);  
                }else{  
                    query.setParameter(string, obj);  
                }  
            }
	    }
	    

		Integer startPosition = pageIndex==1 ? 0 : pageIndex*pageSize-pageSize;
	    
	    
	    query.setFirstResult(startPosition);
	    query.setMaxResults(pageSize);
	    content = query.list();
	    
	    ret.setContent(content);
	    ret.setPageIndex(pageIndex);
	    ret.setPageSize(pageSize);
	    ret.setTotalPages(totalPages);
	    ret.setTotalNumber(totalNumber);
	    
		return ret;
	}
	@Override
	public long querySqlCount(String HQL, Map<String,Object> params) {
		String sql = HQL;
		
	    Query query = getSessionFactory().getCurrentSession().createNativeQuery(sql);

	    if(params != null) {
	    	Set<String> keySet = params.keySet();  
            for (String string : keySet) {  
                Object obj = params.get(string);  
            	if(obj instanceof Date){  
                	query.setParameter(string, (Date)obj,DateType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
                }else if(obj instanceof Object[]){  
                    query.setParameterList(string, (Object[])obj);  
                }else{  
                    query.setParameter(string, obj);  
                }  
            }
	    }
	    List<?> list=query.list();
	    
	    return list.size();
	}
	//时分秒的时间查询
	@Override
	public PageBean queryByTimePagination(PageBean page, String HQL, Map<String,Object> params) {
		PageBean ret = new PageBean();
		List<?> content = null;
		String sql = HQL;
		Long totalPages =  null;
		Long totalNumber=null;
		Integer pageIndex = page.getPageIndex();
		Integer pageSize = page.getPageSize();
		Query  query=null;
		query= getSessionFactory().getCurrentSession().createQuery(sql);
	    
	    totalNumber =  queryTimeCount(HQL, params);
	    
	    if(totalNumber % pageSize == 0) {
	    	totalPages = totalNumber / pageSize;
	    }else {
	    	totalPages = totalNumber / pageSize + 1; 
	    }
	    if(params != null) {
	    	Set<String> keySet = params.keySet();  
            for (String string : keySet) {  
                Object obj = params.get(string);  
            	if(obj instanceof Date){  
                	query.setParameter(string, (Date)obj,TimestampType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
                }else if(obj instanceof Object[]){  
                    query.setParameterList(string, (Object[])obj);  
                }else{  
                    query.setParameter(string, obj);  
                }  
            }
	    }
	    

		Integer startPosition = pageIndex==1 ? 0 : pageIndex*pageSize-pageSize;
	    
	    
	    query.setFirstResult(startPosition);
	    query.setMaxResults(pageSize);
	    content = query.list();
	    
	    ret.setContent(content);
	    ret.setPageIndex(pageIndex);
	    ret.setPageSize(pageSize);
	    ret.setTotalPages(totalPages);
	    ret.setTotalNumber(totalNumber);
	    
		return ret;
	}
	@Override
	public long queryTimeCount(String HQL, Map<String,Object> params) {
		String sql = HQL;
		
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);

	    if(params != null) {
	    	Set<String> keySet = params.keySet();  
            for (String string : keySet) {  
                Object obj = params.get(string);  
            	if(obj instanceof Date){  
                	query.setParameter(string, (Date)obj,TimestampType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
                }else if(obj instanceof Object[]){  
                    query.setParameterList(string, (Object[])obj);  
                }else{  
                    query.setParameter(string, obj);  
                }  
            }
	    }
	    List<?> list=query.list();
	    return list.size();
	}
	
	
}

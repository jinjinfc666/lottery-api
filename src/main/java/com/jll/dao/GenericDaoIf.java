package com.jll.dao;

import java.util.List;
import java.util.Map;

public interface GenericDaoIf<T> {
	
	/**
	 * persist the entity
	 * @param entity
	 * @return 
	 *          Message.status.SUCCESS or
	 *          Message.Error
	 */
	void saveOrUpdate(T entity);
	
	/** 
     * delete batch entities 
     * <br>------------------------------<br> 
     * @param list 
     * @return 
     */  
    boolean add(List<T> entities);
    
    /** 
     * delete by id 
     * <br>------------------------------<br> 
     * @param key 
     */  
    boolean delete(String id);
    
	/**
	 * from the entity from the database
	 * @param entity
	 * @return
	 */
	void delete(T entity);
	
	/** 
     * query entity by id 
     * <br>------------------------------<br> 
     * @param keyId 
     * @return  
     */  
    T get(String id);  
    
	/**
	 * query the entities from the database
	 * @param HQL   
	 * @param params
	 * @return
	 */
	List<T> query(String HQL, List<Object> params, Class<T> clazz);
	
	PageBean<T> queryByPagination(PageBean<T> page, String HQL, List<Object> params, Class<T> clazz);
	
	/**
	 * count the entities in the database
	 * @param HQL  eg.select count(*) from entity
	 * @param params
	 * @return
	 */
	long queryCount(String HQL, List<Object> params, Class<T> clazz);
	
	PageBean queryByPagination(PageBean page, String HQL, Map<String,Object> params);
	
	long queryCount(String HQL, Map<String,Object> params);
	
	PageBean queryBySqlPagination(PageBean page, String HQL, Map<String,Object> params);
	
	long querySqlCount(String HQL, Map<String,Object> params);
	
	PageBean<T> queryBySqlClazzPagination(PageBean<T> page, String SQL, Map<String,Object> params,Class<T> clazz);
	
	PageBean queryByTimePagination(PageBean page, String HQL, Map<String,Object> params);
	
	long queryTimeCount(String HQL, Map<String,Object> params);
}

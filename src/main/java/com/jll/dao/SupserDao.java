package com.jll.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.common.utils.ConvertUtils;

@Repository
public class SupserDao extends HibernateDaoSupport{
	public SupserDao() {
	}
	
	
	 @Autowired
	  public void setSuperSessionFactory(SessionFactory sessionFactory)
	  {
	    super.setSessionFactory(sessionFactory);
	  }
	
	public void delete(Class clazz, Serializable id) {
		getHibernateTemplate().delete(get1(clazz, id));
	}

	public void delete(Object obj) {
		getHibernateTemplate().delete(obj);
	}
	
	public void saveList(List<?> addList) {
		Session session = null;
		try {
			session = getHibernateTemplate().getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			for (int index = 0; index < addList.size(); index++) {
				session.save(addList.get(index));
				if (index % 50 == 0) {
					// // 只是将Hibernate缓存中的数据提交到数据库，保持与数据库数据的同步
					session.flush();
					// // 清除内部缓存的全部数据，及时释放出占用的内存
					session.clear();
				}
			}
			tx.commit();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}


	public void deleteAll(Collection entities) {
		getHibernateTemplate().deleteAll(entities);
	}

	public List findByCriteria(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}

	public List findByCriteria(DetachedCriteria criteria, int firstResult, int maxResult) {
		return getHibernateTemplate().findByCriteria(criteria, firstResult, maxResult);
	}

	public List findByNamedQuery(String sql, Map params) {
		int len = params.size();
		String paramNames[] = new String[len];
		Object paramValues[] = new Object[len];
		int index = 0;
		for (Iterator i = params.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			paramNames[index] = key;
			paramValues[index++] = params.get(key);
		}

		return getHibernateTemplate().findByNamedQueryAndNamedParam(sql, paramNames, paramValues);
	}

	public Object get1(Class clazz, Serializable id) {
		return getHibernateTemplate().get(clazz, id);
	}
	
	public Object get(Class clazz, Serializable id) {
		return getHibernateTemplate().get(clazz, id);
	}
	
	public Object get(Class clazz, String name, Object value) {
		List<Object> ret = findByName(clazz, name, value);
		if(ret == null || ret.isEmpty()){
			return null;
		}
		return ret.get(0);
	}

	public Object get(Class clazz, Serializable id, LockMode mode) {
		return getHibernateTemplate().get(clazz, id, mode);
	}

	public Integer getCount(Class clazz) {
		DetachedCriteria dc = DetachedCriteria.forClass(clazz);
		dc.setProjection(Projections.rowCount());
		Integer count = (Integer) getHibernateTemplate().findByCriteria(dc).get(0);
		return count;
	}

	public List loadAll(Class clazz) {
		return getHibernateTemplate().loadAll(clazz);
	}

	public Object save(Object o) {
		return getHibernateTemplate().save(o);
	}
	
	public void saveOrUpdate(Object o) {
		getHibernateTemplate().saveOrUpdate(o);
	}

	public void update(Object o) {
		getHibernateTemplate().update(o);
	}
	
	public void update(final Object entity, final LockMode lockMode){
		getHibernateTemplate().update(entity,lockMode);
	}

	private void setParam(SQLQuery sqlQuery, int index, Object param) {
		if (param instanceof Integer) {
			sqlQuery.setInteger(index, (Integer) param);
		} else if (param instanceof Long) {
			sqlQuery.setLong(index, (Long) param);
		} else if (param instanceof Double) {
			sqlQuery.setDouble(index, (Double) param);
		} else if (param instanceof String) {
			sqlQuery.setString(index, (String) param);
		} else if (param instanceof Date) {
			sqlQuery.setDate(index, (Date) param);
		}
	}

	public Object excuteSqlForUniqueResult(String sql, List paramsList) {
		final String excuteSql = sql;
		final List params = paramsList;
		return getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				if (params != null && params.size() > 0) {
					int length = params.size();
					for (int i = 0; i < length; i++) {
						setParam(query, i, params.get(i));
					}
				}
				return query.uniqueResult();
			}
		});
	}

	public Object excuteSqlForUniqueResult(String sql) {
		final String excuteSql = sql;
		return getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				return query.uniqueResult();
			}
		});
	}

	public int excuteSqlForUpdate(String sql, List paramsList) {
		final String excuteSql = sql;
		final List params = paramsList;
		return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				if (params != null && params.size() > 0) {
					int length = params.size();
					for (int i = 0; i < length; i++) {
						setParam(query, i, params.get(i));
					}
				}
				return query.executeUpdate();
			}
		});
	}
	
	public int excuteSqlForUpdate(String sql) {
		final String excuteSql = sql;
		return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				return query.executeUpdate();
			}
		});
	}
	
	

	public List excuteSqlForQuery(String sql, List paramsList) {
		final String excuteSql = sql;
		final List params = paramsList;
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				if (params != null && params.size() > 0) {
					int length = params.size();
					for (int i = 0; i < length; i++) {
						setParam(query, i, params.get(i));
					}
				}
				return query.list();
			}
		});
	}

	public List excuteSqlForQuery(String sql) {
		final String excuteSql = sql;
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				return query.list();
			}
		});
	}

	public List excuteSqlForQuery(String sql, Class c1) {
		final String excuteSql = sql;
		final Class class1 = c1;
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				String c1Name = class1.getSimpleName();
				query.addEntity(c1Name.substring(0, 1).toLowerCase(), class1);
				return query.list();
			}
		});
	}
	


	public List excuteuniteForQuery(Class c1, Class c2, List paramsList, String sql) {
		final String excuteSql = sql;
		final List params = paramsList;
		final Class class1 = c1;
		final Class class2 = c2;
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				String c1Name = class1.getSimpleName();
				String c2Name = class2.getSimpleName();
				query.addEntity(c1Name.substring(0, 1).toLowerCase(), class1);
				query.addEntity(c2Name.substring(0, 1).toLowerCase(), class2);
				if (params != null && params.size() > 0) {
					int length = params.size();
					for (int i = 0; i < length; i++) {
						setParam(query, i, params.get(i));
					}
				}
				return query.list();
			}
		});
	}

	public List excuteuniteForQuery(Class c1, Class c2, String sql) {
		final String excuteSql = sql;
		final Class class1 = c1;
		final Class class2 = c2;
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				String c1Name = class1.getSimpleName();
				String c2Name = class2.getSimpleName();
				query.addEntity(c1Name.substring(0, 1).toLowerCase(), class1);
				query.addEntity(c2Name.substring(0, 1).toLowerCase(), class2);
				return query.list();
			}
		});
	}

	public List excuteuniteForQuery(Class c1, Class c2, List paramsList, String sql, int firstResult, int maxResults) {
		final String excuteSql = sql;
		final List params = paramsList;
		final Class class1 = c1;
		final Class class2 = c2;
		final int pageindex = firstResult;
		final int pagesize = maxResults;
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				if (pageindex != 0 && pagesize != 0) {
					int startIndex = (pageindex - 1) * pagesize;
					query.setFirstResult(startIndex);
					query.setMaxResults(pagesize);
				}
				String c1Name = class1.getSimpleName();
				String c2Name = class2.getSimpleName();
				query.addEntity(c1Name.substring(0, 1).toLowerCase(), class1);
				query.addEntity(c2Name.substring(0, 1).toLowerCase(), class2);
				if (params != null && params.size() > 0) {
					int length = params.size();
					for (int i = 0; i < length; i++) {
						setParam(query, i, params.get(i));
					}
				}
				return query.list();
			}
		});
	}

	public List findByName(Class clazz, String name, Object value) {
		DetachedCriteria dCriteria = DetachedCriteria.forClass(clazz);
		dCriteria = dCriteria.add(Restrictions.eq(name, value));
		return getHibernateTemplate().findByCriteria(dCriteria);
	}

	public List findByName(Class clazz, String name1, Object value1, String name2, Object value2) {
		DetachedCriteria dCriteria = DetachedCriteria.forClass(clazz);
		dCriteria = dCriteria.add(Restrictions.eq(name1, value1)).add(Restrictions.eq(name2, value2));
		return getHibernateTemplate().findByCriteria(dCriteria);
	}

	public List findByName(Class clazz, String name1, Object value1, String name2, Object value2, String name3, Object value3) {
		DetachedCriteria dCriteria = DetachedCriteria.forClass(clazz);
		dCriteria = dCriteria.add(Restrictions.eq(name1, value1)).add(Restrictions.eq(name2, value2)).add(Restrictions.eq(name3, value3));
		return getHibernateTemplate().findByCriteria(dCriteria);
	}
	
	public List excuteSqlForQuery(String sql, Class entity, List paramsList) {
		final String excuteSql = sql;
		final List params = paramsList;
		final Class cls = entity;
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				if (params != null && params.size() > 0) {
					int length = params.size();
					for (int i = 0; i < length; i++) {
						setParam(query, i, params.get(i));
					}
				}
				query.setResultTransformer(Transformers.aliasToBean(cls));
				return query.list();
			}
		});
	}
	
	public List excuteSqlForQuery1(String sql, Class c1) {
		final String excuteSql = sql;
		final Class class1 = c1;
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);			
				query.setResultTransformer(Transformers.aliasToBean(class1));
				return query.list();
			}
		});
	}
	
	public List excuteSqlForQuery2(String sql, Class c1, List paramsList) {
		final String excuteSql = sql;
		final Class class1 = c1;
		final List params = paramsList;
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);		
				int i = 0;
				if (params != null && params.size() > 0) {					
					for (Object o : params) {
						query.setParameter(i++, o);
					}
				}
				query.setResultTransformer(Transformers.aliasToBean(class1));
				return query.list();
			}
		});
	}

	public int getCount(String hql){
		Integer totalcount = ConvertUtils.convertInteger(excuteSqlForUniqueResult(hql));			
		return totalcount;
	}
	
	public int excuteSqlForUpdate1(String sql, List paramsList) {
		final String excuteSql = sql;
		final List params = paramsList;
		return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				if (params != null && params.size() > 0) {
					int i = 0;
					for (Object o : params) {
						query.setParameter(i++, o);
					}
				}
				return query.executeUpdate();
			}
		});
	}
	
	public int excuteSqlForUpdate1(String sql, List paramsList, String strIn,List inParaList) {
		final String excuteSql = sql;
		final List params = paramsList;
		final String strIns = strIn;
		final List inParaLists = inParaList;
		return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				int i = 0;
				if (params != null && params.size() > 0) {					
					for (Object o : params) {
						query.setParameter(i++, o);
					}
				}
				query.setParameterList(strIns, inParaLists);
				return query.executeUpdate();
			}
		});
	}
	
	public Object excuteSqlForUniqueResult1(String sql, List paramsList) {
		final String excuteSql = sql;
		final List params = paramsList;
		return getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery query = session.createSQLQuery(excuteSql);
				int i = 0;
				for (Object o : params) {
					query.setParameter(i++, o);
				}
				return query.uniqueResult();
			}
		});
	}
	
	public int getCount(String sql, List paramsList){
		Integer totalcount = ConvertUtils.convertInteger(excuteSqlForUniqueResult1(sql,paramsList));		
		return totalcount;
	}
}

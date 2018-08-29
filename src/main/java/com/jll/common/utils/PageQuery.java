package com.jll.common.utils;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;

import com.jll.dao.SupserDao;

public class PageQuery {
	
	
	private static Logger log = Logger.getLogger(PageQuery.class);

	public static Integer getNextIndex(boolean fprev, boolean prev,
			boolean next, boolean fnext, Integer index, Integer lastIndex) {
		Integer nextIndex = Page.PAGE_BEGIN_INDEX;
		if ((index == null) || (lastIndex == null)) {
			return nextIndex;
		}
		if (fnext)
			nextIndex = lastIndex;
		else if ((next) && (index.compareTo(lastIndex) < 0))
			nextIndex = Integer.valueOf(index.intValue() + 1);
		else if ((prev) && (index.intValue() - 1 > 0)) {
			nextIndex = Integer.valueOf(index.intValue() - 1);
		}
		return nextIndex;
	}

	public static Integer queryForCount(HibernateTemplate hibernateTemplate,
			DetachedCriteria criteria) {
		Integer count = Integer.valueOf(0);
		List list = hibernateTemplate.findByCriteria(criteria
				.setProjection(Projections.rowCount()));
		if (list.size() > 0)
			count = (Integer) ConvertUtils.convert(list.get(0), Integer.class);

		return count;
	}

	public static Page queryForPagenation(List l, int pageIndex, int size,
			int total) {
		if (size <= 0) {
			size = Page.PAGE_DEFAULT_SIZE;
		}
		if (pageIndex <= 0) {
			pageIndex = Page.PAGE_BEGIN_INDEX;
		}
		Page page = new Page();
		page.setSize(size);
		try {
			Integer totalResults = total;
			log.debug("totalResults:" + totalResults);
			int pages = PagenationUtil.computeTotalPages(totalResults, size)
					.intValue();
			page.setTotalRecords(totalResults);
			page.setTotalPages(Integer.valueOf(pages));
			if (pageIndex > pages) {
				pageIndex = Page.PAGE_BEGIN_INDEX;
			}
			page.setPageNumber(pageIndex);
			page.setPageContents(l);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	
	
	public static Page queryForPagenationBySql(SupserDao  supserDao,String sql,Class<?> cls, List<?> paramsList, int pageIndex, int size) {
		Page page = new Page();
		try {
			String sql11 = "select count(*) from (" + sql + ") a ";
			int totalcount = 0;
			totalcount =Integer.valueOf(supserDao.excuteSqlForUniqueResult(sql11,paramsList).toString());
			if (totalcount <= 0) {
				return page;
			}
			String sql0 = sql + "  limit " + (pageIndex - 1) * size + ", " + size;
			System.out.println(sql);
			List l1 = supserDao.excuteSqlForQuery(sql0,cls,paramsList);
			page = queryForPagenation(l1, pageIndex, size, totalcount);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	
	}
	
	public static Page queryForPagenationByHql(SupserDao  supserDao,String sql,Class<?> cls, List<Object> paramsList, int pageIndex, int size) {
		Page page = new Page();
		try {
			//sql = sql.toUpperCase();
			int entityNameStartInd = sql.indexOf("from");
		    if(entityNameStartInd < 0) {
		    	entityNameStartInd = sql.indexOf("FROM");
		    }
		    
			String sql11 = "select count(*) "+ sql.substring(entityNameStartInd);
			int totalcount = 0;
			totalcount =Integer.valueOf(""+supserDao.queryCountByHQL(sql11, paramsList, cls));
			if (totalcount <= 0) {
				return page;
			}
			System.out.println(sql);
			List l1 = supserDao.excuteHqlForQuery(sql,cls,paramsList,pageIndex,size);
			page = queryForPagenation(l1, pageIndex, size, totalcount);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	
	}
	
	

	public static Page queryForPagenation(HibernateTemplate hibernateTemplate,
			DetachedCriteria criteria, Integer pageIndex, Integer size) {
		if ((size == null) || (size.intValue() == 0))
			size = Page.PAGE_DEFAULT_SIZE;
		if (pageIndex == null)
			pageIndex = Page.PAGE_BEGIN_INDEX;
		Page page = new Page();
		page.setSize(size);
		try {
			Object obj = hibernateTemplate.findByCriteria(
					criteria.setProjection(Projections.rowCount())).get(0);
			Integer totalResults = ConvertUtils.convertInteger(obj);
			log.debug("totalResults:" + totalResults);
			int pages = PagenationUtil.computeTotalPages(totalResults, size)
					.intValue();
			page.setTotalRecords(totalResults);
			page.setTotalPages(Integer.valueOf(pages));
			if (pageIndex.intValue() > pages)
				pageIndex = Page.PAGE_BEGIN_INDEX;
			page.setPageNumber(pageIndex);
			page.setPageContents(hibernateTemplate.findByCriteria(
					criteria.setProjection(null), (pageIndex.intValue() - 1)
							* size.intValue(), size.intValue()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return page;
	}
	
	public static Page queryForPagenationNew(HibernateTemplate hibernateTemplate,
			DetachedCriteria criteria,Integer start, Integer size) {
		if ((size == null) || (size.intValue() == 0))
			size = Page.PAGE_DEFAULT_SIZE;
		if ((start == null)) start = 0;
		Page page = new Page();
		page.setSize(size);
		try {
			Object obj = hibernateTemplate.findByCriteria(criteria.setProjection(Projections.rowCount())).get(0);
			Integer totalResults = ConvertUtils.convertInteger(obj);
			log.debug("totalResults:" + totalResults);
			int pages = PagenationUtil.computeTotalPages(totalResults, size).intValue();
			page.setTotalRecords(totalResults);
			page.setTotalPages(Integer.valueOf(pages));
			page.setPageContents(hibernateTemplate.findByCriteria(criteria.setProjection(null),start,size.intValue()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	
	public static Page queryForPagenationWithStatistics(
			HibernateTemplate hibernateTemplate, DetachedCriteria criteria,
			Integer pageIndex, Integer size) {
		if ((size == null) || (size.intValue() == 0))
			size = Page.PAGE_DEFAULT_SIZE;
		if (pageIndex == null)
			pageIndex = Page.PAGE_BEGIN_INDEX;
		log.debug("pageIndex:" + pageIndex);
		Page page = new Page();
		page.setPageNumber(pageIndex);
		page.setSize(size);
		try {
			ProjectionList pList = Projections.projectionList().add(
					Projections.rowCount());
			List list = hibernateTemplate.findByCriteria(criteria
					.setProjection(pList));
			Long arraysize = (Long) list.get(0);
			Integer totalResults = ConvertUtils.convertInteger(arraysize
					.intValue());
			int pages = PagenationUtil.computeTotalPages(totalResults, size)
					.intValue();
			log.debug("totalResults:" + totalResults);
			page.setTotalRecords(totalResults);
			page.setTotalPages(Integer.valueOf(pages));
			if (pageIndex.intValue() > pages) {
				pageIndex = Page.PAGE_BEGIN_INDEX;
			}
			page.setPageNumber(pageIndex);
			page.setPageContents(hibernateTemplate.findByCriteria(
					criteria.setProjection(null), (pageIndex.intValue() - 1)
							* size.intValue(), size.intValue()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	@SuppressWarnings("unchecked")
	public static Page queryForPagenationGroubByList(
			HibernateTemplate hibernateTemplate, Class c1, Class c2,
			String sql, List paramsList, Integer firstResult,
			Integer maxResults, String staticsFiled1, String staticsFiled2) {
		if ((maxResults == null) || (maxResults.intValue() == 0))
			maxResults = Page.PAGE_DEFAULT_SIZE;
		if (firstResult == null)
			firstResult = Page.PAGE_BEGIN_INDEX;
		log.debug("pageIndex:" + firstResult);
		Page page = new Page();
		page.setPageNumber(firstResult);
		page.setSize(maxResults);
		final String excuteSql = sql;
		final List params = paramsList;
		final Class class1 = c1;
		final Class class2 = c2;
		final int pageindex = firstResult;
		final int pagesize = maxResults;
		final String staticFiled1 = staticsFiled1;
		final String staticFiled2 = staticsFiled2;
		final String subSql = excuteSql.substring(excuteSql.indexOf("from"),
				excuteSql.length());
		List listPage = (List) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						SQLQuery query = session.createSQLQuery(excuteSql);
						int startIndex = (pageindex - 1) * pagesize;
						query.setFirstResult(startIndex);
						query.setMaxResults(pagesize);
						String c1Name = class1.getSimpleName();
						String c2Name = class2.getSimpleName();
						query.addEntity(c1Name.substring(0, 1).toLowerCase(),
								class1);
						query.addEntity(c2Name.substring(0, 1).toLowerCase(),
								class2);
						if (params != null && params.size() > 0) {
							int length = params.size();
							for (int i = 0; i < length; i++) {
								setParam(query, i, params.get(i));
							}
						}
						return query.list();
					}
				});
		List remitTotal = (List) hibernateTemplate
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						String sql = "select * ";
						if (StringUtils.isNotEmpty(staticFiled1)) {
							sql = sql + " sum(" + staticFiled1 + "),";
						}
						if (StringUtils.isNotEmpty(staticFiled2)) {
							sql = sql + " sum(" + staticFiled2 + "),";
						}
						sql = sql.substring(0, sql.length() - 1) + subSql;
						SQLQuery query = session.createSQLQuery(sql);
						if (params != null && params.size() > 0) {
							int length = params.size();
							for (int i = 0; i < length; i++) {
								setParam(query, i, params.get(i));
							}
						}
						return query.list();
					}
				});

		int pages = PagenationUtil.computeTotalPages(
				Integer.valueOf(remitTotal.size()), pagesize).intValue();
		log.debug("totalResults:" + pages);
		page.setTotalRecords(remitTotal.size());
		page.setTotalPages(Integer.valueOf(pages));
		if (firstResult.intValue() > pages)
			firstResult = Page.PAGE_BEGIN_INDEX;
		page.setPageNumber(pageindex);
		page.setPageContents(listPage);
		return page;
	}


	private static void setParam(SQLQuery sqlQuery, int index, Object param) {
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
}
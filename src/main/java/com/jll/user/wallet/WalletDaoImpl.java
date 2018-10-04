package com.jll.user.wallet;


import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageBean;
import com.jll.entity.UserAccount;
import com.jll.entity.UserInfo;

@Repository
public class WalletDaoImpl extends DefaultGenericDaoImpl<UserAccount> implements WalletDao
{
	private Logger logger = Logger.getLogger(WalletDaoImpl.class);
	
	@Override
	public void createWallet(UserAccount wallet) {
		this.saveOrUpdate(wallet);
	}

	@Override
	public UserAccount queryByUser(UserInfo user) {
		String sql = "from UserAccount where userId=?";
		List<UserAccount> ret = null;
		List<Object> params = new ArrayList<>();
		params.add(user.getId());
		
		ret = this.query(sql, params, UserAccount.class);
		
		if(ret == null || ret.size() == 0) {
			return null;
		}
		
		return ret.get(0);
	}

	@Override
	public UserAccount queryById(int walletId) {
		String sql = "from UserAccount where id=?";
		List<Object> params = new ArrayList<>();
		params.add(walletId);
		
		List<UserAccount> userAccounts = this.query(sql, params, UserAccount.class);
		
		if(userAccounts == null || userAccounts.size() == 0) {
			return null;
		}
		return userAccounts.get(0);
	}
	//通过用户名(false)或时间去查询(true)
	@Override
	public Map<String,Object> queryUserAccount(String userName, String startTime, String endTime,Integer pageIndex,Integer pageSize) {
		Map<String,Object> map=new HashMap<String,Object>();
		String userNameSql="";
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and a.userName=:userName";
			map.put("userName", userName);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			Date beginDate = java.sql.Date.valueOf(startTime);
		    Date endDate = java.sql.Date.valueOf(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		String sql="from UserInfo a,UserAccount b where a.id=b.userId and a.createTime >=:startTime and a.createTime <:endTime"+userNameSql+" order by b.userId";
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		map.clear();
		map.put("data", pageBean);
		return map;
	}
	//修改用户的状态
	@Override
	public void updateState(Integer userId, Integer state) {
		Session session=getSessionFactory().getCurrentSession();
		String sql="update UserAccount set state=:state where userId=:userId";
		Query query = session.createQuery(sql);
		query.setParameter("state", state);
		query.setParameter("userId", userId);
		query.executeUpdate();
	}
	//查找userId存不存在在userAccount表
	@Override
	public List<UserAccount> queryByUserId(Integer userId) {
		String sql = "from UserAccount where userId=?";
		List<Object> params = new ArrayList<>();
		params.add(userId);
		return this.query(sql, params, UserAccount.class);
	}
	@Override
	public void updateWallet(UserAccount wallet) {
		this.saveOrUpdate(wallet);
	}
	//通过id查询该用户是否存在
	@Override
	public List<UserAccount> queryById(Integer id) {
		String sql = "from UserAccount where id=?";
		List<Object> params = new ArrayList<>();
		params.add(id);
		return this.query(sql, params, UserAccount.class);
	}
	//通过id
	@Override
	public Map<String, Object> queryByIdUserAccount(Integer id) {
		Map<String,Object> map=new HashMap<String,Object>();
		String idSql="";
		if(id!=null) {
			idSql=" and b.id=:id";
			map.put("id", id);
		}
		String sql="from UserInfo a,UserAccount b where a.id=b.userId "+idSql;
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
		query.setParameter("id", id);
		List<?> list=query.list();
		map.put("data", list);
		return map;
	}
	//通过用户ID查询当前用户的钱包
	@Override
	public Map<String, Object> queryByUserIdUserAccount(Integer userId) {
		Map<String,Object> map=new HashMap<String,Object>();
		String sql="from UserAccount where userId=:userId";
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
		query.setParameter("userId", userId);
		List<?> list=query.list();
		if(list!=null&&list.size()>0) {
			map.put("data", list);
		}else {
			map.put("data", null);
		}
		return map;
	}
	//通过用户ID查询用户的主钱包
	@Override
	public Map<String, Object> queryUserAccount(Integer userId, Integer accType) {
		Map<String,Object> map=new HashMap<String,Object>();
		String sql="from UserAccount where userId=:userId and accType=:accType";
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("accType", accType);
		List<?> list=query.list();
		map.put("data", list);
		return map;
	}
}

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
	public List<?> queryUserAccount(String userName, String startTime, String endTime) {
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
		String sql="from UserInfo a,UserAccount b where a.id=b.userId and a.createTime >:startTime and a.createTime <=:endTime"+userNameSql;
		Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
		if (map != null) {  
            Set<String> keySet = map.keySet();  
            for (String string : keySet) {  
                Object obj = map.get(string);  
            	if(obj instanceof Date){  
                	query.setParameter(string, (Date)obj,DateType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
                }else if(obj instanceof Object[]){  
                    query.setParameterList(string, (Object[])obj);  
                }else{  
                    query.setParameter(string, obj); 
                }  
            }  
        }
		List<?> cards = query.list();
		return cards;
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
  
  
}

package com.jll.user.bank;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.UserBankCard;

@Repository
public class UserBankCardDaoImpl extends DefaultGenericDaoImpl<UserBankCard> implements UserBankCardDao
{
	private Logger logger = Logger.getLogger(UserBankCard.class);

	@Override
	public void save(UserBankCard userBankCard) {
		this.saveOrUpdate(userBankCard);
	}
	//通过ID删除银行卡
	@Override
	public void deleteBank(Integer id) {
		String sql="delete from UserBankCard where id=:id";
		Query<UserBankCard> query = getSessionFactory().getCurrentSession().createQuery(sql,UserBankCard.class);
		query.setParameter("id", id);
		query.executeUpdate();
	}
	//通过ID查询银行卡
	@Override
	public UserBankCard queryById(Integer id) {
		String sql="from UserBankCard where id=:id";
		Query<UserBankCard> query = getSessionFactory().getCurrentSession().createQuery(sql,UserBankCard.class);
	    query.setParameter("id", id);
	    List<UserBankCard> list= query.list();
	    if(list!=null&&list.size()>0) {
	    	return list.get(0);
	    }
		return null;
	}
  
}

package com.jll.pay.order;

import java.util.Date;

import javax.persistence.NoResultException;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.common.utils.DateUtil;
import com.jll.common.utils.StringUtils;
import com.jll.entity.DepositApplication;

@Repository
public class DepositOrderDaoImpl extends HibernateDaoSupport implements DepositOrderDao
{
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory)
	{
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public DepositApplication saveDepositOrder(int payType,int payChannel, int userId, float amount, String comment, Date createTime,String platAccount) {
		DepositApplication depositOrder = new DepositApplication();
		depositOrder.setAmount(amount);
		depositOrder.setCreateTime(createTime);
		//yyyyMMddHHmmss+Random string
		depositOrder.setOrderNum(DateUtil.fmtYmdHisEmp(createTime)+StringUtils.getRandomString(6));
		depositOrder.setRemark(comment);
		depositOrder.setPayChannel(payChannel);
		depositOrder.setPayType(payType);
		depositOrder.setState(Constants.DepositOrderState.INIT_OR_PUSHED.getCode());
		depositOrder.setUserId(userId);
		depositOrder.setPlatAccount(platAccount);
		currentSession().save(depositOrder);
		return depositOrder;
	}

	@Override
	public void updateDepositOrder(DepositApplication depositOrder) {
		currentSession().merge(depositOrder);
	}

	@Override
	public DepositApplication queryDepositOrderById(String orderId) {
		String sql = "from DepositApplication where orderNum=?";
		DepositApplication depositOrder = null;
		
		Query<DepositApplication> query = currentSession().createQuery(sql, DepositApplication.class);
		query.setParameter(0, orderId);
		try {
			depositOrder = query.getSingleResult();
		}catch(NoResultException ex) {
			
		}
		return depositOrder;
	}
	
  
  
}

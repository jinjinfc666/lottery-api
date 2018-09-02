package com.jll.game.order;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.alibaba.druid.util.StringUtils;
import com.jll.common.constants.Constants.OrderState;
import com.jll.common.utils.Utils;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.OrderInfo;

@Repository
public class OrderDaoImpl extends DefaultGenericDaoImpl<OrderInfo> implements OrderDao
{
	private Logger logger = Logger.getLogger(OrderDaoImpl.class);

	@Override
	public void saveOrders(OrderInfo order) {
		this.saveOrUpdate(order);
	}

	@Override
	public List<OrderInfo> queryOrdersByIssue(Integer issueId) {
		String sql = "from OrderInfo where issueId=?";
		List<Object> params = new ArrayList<>();
		params.add(issueId);
		
		return this.query(sql, params, OrderInfo.class);
	}

	@Override
	public double getUserBetTotalByDate(int walletId, int userId, Date start, Date end) {
		String sql = "select sum(betAmount) from OrderInfo where userId=? and walletId=? and createTime >= ? and createTime < ?  and ( state=? or state=?) ";
		List<Object> params = new ArrayList<>();
		params.add(userId);
		params.add(walletId);
		params.add(start);
		params.add(end);
		params.add(OrderState.WINNING.getCode());
		params.add(OrderState.LOSTING.getCode());
		
		 Query<Double> query = getSessionFactory().getCurrentSession().createQuery(sql, Double.class);
	    if(params != null) {
	    	int indx = 0;
	    	for(Object para : params) {
	    		query.setParameter(indx, para);
	    		
	    		indx++;
	    	}
	    }
		return Utils.toDouble(query.getSingleResult());
	}
	
}

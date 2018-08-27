package com.jll.game.order;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

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

	
}

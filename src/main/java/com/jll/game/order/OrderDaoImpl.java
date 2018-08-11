package com.jll.game.order;


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

	
}

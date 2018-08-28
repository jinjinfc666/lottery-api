package com.jll.game.order;

import java.util.List;

import com.jll.entity.OrderInfo;

public interface OrderDao
{

	void saveOrders(OrderInfo order);

	List<OrderInfo> queryOrdersByIssue(Integer issueId);
}

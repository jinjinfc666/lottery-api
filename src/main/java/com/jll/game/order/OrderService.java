package com.jll.game.order;

import java.util.List;

import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;

public interface OrderService
{

	/**
	 * 处理订单
	 * @param orders     提交的订单
	 * @param walletId   投注钱包
	 * @param zhFlag     是否追号:0,非追号;1,追号 
	 * @return
	 */
	String saveOrders(List<OrderInfo> orders, int walletId, int zhFlag, String lotteryType);
	
	
	
}

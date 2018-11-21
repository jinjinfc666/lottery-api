package com.jll.game.order;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jll.dao.PageBean;
import com.jll.entity.OrderInfo;

/**
 * @author Administrator
 *
 */
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

	List<OrderInfo> queryOrdersByIssue(Integer issueId);

	void saveOrder(OrderInfo order);	
	
	double getUserBetTotalByDate(int walletId,int userId,Date start,Date end);

	Map<String, Object> getOrderInfo(String orderNum);

	PageBean<OrderInfo> queryOrdersByPage(PageBean<OrderInfo> page);
	
	List<OrderInfo> queryZhOrder(String transactionNum);
	
}

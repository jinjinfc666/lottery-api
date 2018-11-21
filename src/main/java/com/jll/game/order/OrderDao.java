package com.jll.game.order;

import java.util.Date;
import java.util.List;

import com.jll.dao.PageBean;
import com.jll.entity.OrderInfo;

public interface OrderDao
{

	void saveOrders(OrderInfo order);

	List<OrderInfo> queryOrdersByIssue(Integer issueId);
	
	double getUserBetTotalByDate(int walletId,int userId,Date start,Date end);
	
	OrderInfo getOrderInfo(String orderNum);
	
	List<OrderInfo> getOrderInfoByPrams(Integer issueId, String userName, String orderNum,Integer delayPayoutFlag);
	
	List<OrderInfo> queryWinOrdersByIssue(Integer issueId);

	PageBean<OrderInfo> queryOrdersByPage(PageBean<OrderInfo> page);

	List<OrderInfo> queryZhOrder(String transactionNum);
}

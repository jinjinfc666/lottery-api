package com.jll.pay;

import java.util.List;
import java.util.Map;

import com.jll.dao.PageQueryDao;
import com.jll.entity.DepositApplication;
import com.jll.entity.PayChannel;
import com.jll.entity.PayType;

public abstract interface PaymentService
{
   public abstract long queryDepositTimes(int userId);
   public List<PayType> getSysPayType();
   public List<PayChannel> getSysPayChannelByType(int peyType) ;
   public List<PayChannel> getUserPayChannel();
   public Map<String, Object> getUserPayOrder(int userId, PageQueryDao page);
   public Map<String, Object> payOrderToSystem(int userId, DepositApplication info,Map<String, Object> pramsInfo);
}

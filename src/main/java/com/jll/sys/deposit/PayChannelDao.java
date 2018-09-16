package com.jll.sys.deposit;

import java.util.List;

import com.jll.entity.PayChannel;

public interface PayChannelDao
{	
	//添加
	void addPayChannel(PayChannel payChannel);
	//查找最后一条数据
	List<PayChannel> queryLast();
	//查询表中的排序
	Integer quertPayChannelSeq();
	//修改
	void updatePayChannel(PayChannel payChannel);
	//通过id查询
	List<PayChannel> queryById(Integer id);
	//查询所有 
	List<PayChannel> queryAll();
	//修排序
	void updatePayChannelState(Integer id,Integer state);
	//修改激活
	void updatePayChannelEnableMaxAmount(Integer id, Integer enableMaxAmount);
}

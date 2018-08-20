package com.jll.sys.deposit;

import java.util.List;
import java.util.Map;

import com.jll.entity.PayChannel;

public interface PayChannelService
{	
	//添加
	Map<String,Object> addPayChannel(PayChannel payChannel);
	//查询新增之后的最后条数据
	PayChannel queryLast();
	//修改
	Map<String,Object> updatePayChannel(PayChannel payChannel);
	//通过id查询
	PayChannel queryById(Integer id);
	//判断id这条数据存不存在
	boolean isNull(Integer id);
	//查询所有
	List<PayChannel> queryAll();
	//修改排序
	Map<String,Object> updatePayChannelSeq(String allId);
}

package com.jll.sys.deposit;

import java.util.List;
import java.util.Map;

import com.jll.entity.PayType;

public interface PayTypeService
{	
	//添加
	Map<String,Object> addPayType(Map<String,Object> ret);
	//判断存不存在
	boolean  isOrNo(String name, String nickName, String platId);
	//查询
	PayType queryBy(String name, String nickName, String platId);
	//修改
	Map<String,Object> updatePayType(PayType payType);
	//通过id查找存不存在
	boolean isNull(Integer id);
	//查询所有数据
	List<PayType> queryAllPayType();
	//修改排序
//	Map<String,Object> updatePayTypeState(Map<String,Object> ret);
	//通过id查找
	PayType queryById(Integer id);
}

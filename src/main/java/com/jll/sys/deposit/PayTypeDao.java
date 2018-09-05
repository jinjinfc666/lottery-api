package com.jll.sys.deposit;

import java.util.List;

import com.jll.entity.PayType;

public interface PayTypeDao
{	
	//添加
	void addPayType(PayType payType);
	//通过条件查询
	List<PayType> queryBy(String name,String nickName,String platId);
	//查询表中的排序
	Integer quertPayTypeSeq();
	//修改
	void updatePayType(PayType payType);
	//通过id查找存不存在
	List<PayType> queryById(Integer id);
	//查询所有数据
	List<?> queryAllPayType(Integer bigCodeNameId);
	//通过seq  查询数据
	PayType queryBySeq(Integer seq);
	//查询所有数据
	List<PayType> queryAllPayType();
	//通过id查询某一条数据
	List<?> queryPayTypeById(Integer id,Integer bigCodeNameId);
}

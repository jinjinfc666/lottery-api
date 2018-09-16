package com.jll.sys.blacklist;

import java.util.List;
import java.util.Map;

import com.jll.entity.IpBlackList;

public interface IpBlackListService
{	
	//添加
	Map<String,Object> addIp(IpBlackList ipBlackList);
	//判断ip在数据库中存不存在
	boolean isNull(String ip);
	//判断id在数据库中存不存在
	boolean isNullById(Integer id);
	//查询通过ip
	Map<String,Object> queryByIp(Integer pageIndex,Integer pageSize,String ip);
	//查询通过id
	IpBlackList query(Integer id);
	//查询所有
	Map<String,Object> queryByPageIndex(Integer pageIndex,Integer pageSize);
	//修改
	Map<String,Object> update(IpBlackList ipBlackList);
	//删除
	Map<String,Object> deleteIpBlackList(Integer id);
	//
	List<IpBlackList> query();
	//查询通过ip
	IpBlackList queryByIp(String ip);
}

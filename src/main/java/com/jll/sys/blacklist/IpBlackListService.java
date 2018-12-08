package com.jll.sys.blacklist;

import java.util.List;
import java.util.Map;

import com.jll.entity.IpBlackList;

public interface IpBlackListService
{	
	//添加
	void saveIp(IpBlackList ipBlackList);
	//判断id在数据库中存不存在
	boolean isNullById(Integer id);
	//查询通过id
	IpBlackList query(Integer id);
//	//修改
//	Map<String,Object> update(IpBlackList ipBlackList);
	//删除
	Map<String,Object> deleteIpBlackList(Integer id);
	//
	List<IpBlackList> query();
	//查询通过ip
	long queryByIp(String ipLong,Integer type);
	
	Map<String,Object> queryByIp(Integer pageIndex,Integer pageSize,String ipLong,Integer type);
}

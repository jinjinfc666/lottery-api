package com.jll.blacklist;

import java.util.List;

import com.jll.entity.IpBlackList;

public interface IpBlackListDao
{	
	//添加
	void addIp(IpBlackList ipRestrictions);
	//查询通过ip
	IpBlackList query(String ip);
	//查询通过id
	IpBlackList query(Integer id);
	//查询所有
	List<IpBlackList> query();
	//修改
	void updateIp(IpBlackList ipBlackList);
	//删除
	void deleteIpBlackList(Integer id);
}

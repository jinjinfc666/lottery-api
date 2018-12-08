package com.jll.sys.blacklist;

import java.util.List;
import java.util.Map;

import com.jll.dao.PageBean;
import com.jll.entity.IpBlackList;

public interface IpBlackListDao
{	
	//添加
	void saveIp(IpBlackList ipRestrictions);
	List<IpBlackList> query();
	//查询通过id
	IpBlackList query(Integer id);
//	//修改
//	void updateIp(IpBlackList ipBlackList);
	//删除
	void deleteIpBlackList(IpBlackList ipBlackList);
	//通过ip查询这个ip在表中是否存在或者是否存在于某个ip段
	List<IpBlackList> queryByIp(String ipLong,Integer type);
	PageBean queryByIp(Integer pageIndex,Integer pageSize,String ipLong,Integer type);
	
}

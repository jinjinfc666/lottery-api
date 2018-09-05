package com.jll.sys.blacklist;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.entity.IpBlackList;

@Service
@Transactional
public class IpBlackListServiceImpl implements IpBlackListService
{
	private Logger logger = Logger.getLogger(IpBlackListServiceImpl.class);

	@Resource
	IpBlackListDao iPRestrictionsDao;

	@Resource
	CacheRedisService cacheServ;
	//添加
	@Override
	public Map<String,Object> addIp(IpBlackList ipBlackList) {
		Map<String,Object> map=new HashMap<String,Object>();
		String ip=ipBlackList.getIp();
		boolean aa=this.isNull(ip);
		if(aa) {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}else {
			iPRestrictionsDao.addIp(ipBlackList);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}
		return map;
	}
	//判断ip在数据库中存不存在
	@Override
	public boolean isNull(String ip) {
		IpBlackList list=iPRestrictionsDao.query(ip);
		if(list==null) {
			return false;
		}
		return true;
	}
	//判断id在数据库中存不存在
	@Override
	public boolean isNullById(Integer id) {
		IpBlackList list=iPRestrictionsDao.query(id);
		if(list==null) {
			return false;
		}
		return true;
	}
	//通过ip查询
	@Override
	public Map<String,Object> queryByIp(Integer pageIndex,Integer pageSize,String ip) {
		return iPRestrictionsDao.query(pageIndex,pageSize,ip);
	}
	
	//通过id查询
	@Override
	public IpBlackList query(Integer id) {
		return iPRestrictionsDao.query(id);
	}
	//查询所有
	@Override
	public Map<String,Object> queryByPageIndex(Integer pageIndex,Integer pageSize) {
		return iPRestrictionsDao.queryByPageIndex(pageIndex,pageSize);
	}
	//修改
	@Override
	public Map<String, Object> update(IpBlackList ipBlackList) {
		Map<String,Object> map=new HashMap<String,Object>();
		String ip=ipBlackList.getIp();
		Integer id=ipBlackList.getId();
		boolean adid=this.isNullById(id);
		boolean aa=this.isNull(ip);
		if(adid&&!aa) {
			IpBlackList ipBlackLists=iPRestrictionsDao.query(id);
			ipBlackLists.setIp(ip);
			iPRestrictionsDao.updateIp(ipBlackLists);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return map;
	}
	//删除
	@Override
	public Map<String, Object> deleteIpBlackList(Integer id) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean aa=this.isNullById(id);
		if(aa) {
			iPRestrictionsDao.deleteIpBlackList(id);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return map;
	}
	@Override
	public List<IpBlackList> query() {
		return iPRestrictionsDao.query();
	}
	@Override
	public IpBlackList queryByIp(String ip) {
		return iPRestrictionsDao.query(ip);
	}
	
}


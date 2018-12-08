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
import com.jll.dao.PageBean;
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
	public void saveIp(IpBlackList ipBlackList) {
		iPRestrictionsDao.saveIp(ipBlackList);
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
	
	//通过id查询
	@Override
	public IpBlackList query(Integer id) {
		return iPRestrictionsDao.query(id);
	}
//	//修改
//	@Override
//	public Map<String, Object> update(IpBlackList ipBlackList) {
//		Map<String,Object> map=new HashMap<String,Object>();
//		String ip=ipBlackList.getIp();
//		Integer id=ipBlackList.getId();
//		boolean adid=this.isNullById(id);
//		boolean aa=this.isNull(ip);
//		if(adid&&!aa) {
//			IpBlackList ipBlackLists=iPRestrictionsDao.query(id);
//			ipBlackLists.setIp(ip);
//			iPRestrictionsDao.updateIp(ipBlackLists);
//			map.clear();
//			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//		}else {
//			map.clear();
//			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
//			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_IP_DATA_DOES_NOT_EXIST_MODIFIED_IP_ALREADY_EXISTS.getCode());
//			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_IP_DATA_DOES_NOT_EXIST_MODIFIED_IP_ALREADY_EXISTS.getErrorMes());
//		}
//		return map;
//	}
	//删除
	@Override
	public Map<String, Object> deleteIpBlackList(Integer id) {
		Map<String,Object> map=new HashMap<String,Object>();
		IpBlackList ipBlackList=this.query(id);
		if(ipBlackList!=null) {
			iPRestrictionsDao.deleteIpBlackList(ipBlackList);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_IP_DATA_DOES_NOT_EXIST_MODIFIED_IP_ALREADY_EXISTS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_IP_DATA_DOES_NOT_EXIST_MODIFIED_IP_ALREADY_EXISTS.getErrorMes());
		}
		return map;
	}
	@Override
	public List<IpBlackList> query() {
		return iPRestrictionsDao.query();
	}
	@Override
	public long queryByIp(String ipLong,Integer type) {
		List<IpBlackList> list=iPRestrictionsDao.queryByIp(ipLong,type);
		if(list!=null&&list.size()>0) {
			return list.size();
		}
		return 0;
	}
	@Override
	public Map<String, Object> queryByIp(Integer pageIndex,Integer pageSize,String ipLong,Integer type) {
		Map<String,Object> map=new HashMap<String,Object>();
		PageBean pageBean=iPRestrictionsDao.queryByIp(pageIndex, pageSize, ipLong,type);
		map.clear();
		map.put("data",pageBean);
		map.put(Message.KEY_STATUS,Message.status.SUCCESS.getCode());
	    return map;
	}
}


package com.jll.sys.log;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
import com.jll.dao.PageBean;
import com.jll.entity.IpBlackList;
import com.jll.entity.SysLogin;
import com.jll.entity.UserInfo;
import com.jll.user.UserInfoService;

@Service
@Transactional
public class SysLoginServiceImpl implements SysLoginService
{
	private Logger logger = Logger.getLogger(SysLoginServiceImpl.class);
	
	@Resource
	SysLoginDao sysLoginDao;
	@Resource
	UserInfoService userInfoService;

	@Override
	public void saveOrUpdate(SysLogin sysLogin) {
		sysLogin.setCreateTime(new Date());
		sysLoginDao.saveUpdate(sysLogin);
	}

	@Override
	public Map<String,Object> queryLoginlog(Integer type, String userName, String startTime, String endTime,Integer pageIndex,Integer pageSize) {
		Map<String,Object> map=new HashMap<String,Object>();
		Integer userId=null;
		if(!StringUtils.isBlank(userName)) {
			UserInfo userInfo=userInfoService.getUserByUserName(userName);
			if(userInfo==null) {
				map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_INVALID_USER_NAME.getCode());
				map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_INVALID_USER_NAME.getErrorMes());
				return map;
			}
			userId=userInfo.getId();
		}
		PageBean pageBean=sysLoginDao.queryLoginlog(type, userId, startTime, endTime,pageIndex,pageSize);
		map.put(Message.KEY_DATA, pageBean);
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
	//查询不存在用户登录日志
	@Override
	public Map<String, Object> queryLoginlog(String ip,String startTime, String endTime, Integer pageIndex,
			Integer pageSize) {
		Map<String,Object> map=new HashMap<String,Object>();
		PageBean pageBean=sysLoginDao.queryLoginlog(ip,startTime, endTime,pageIndex,pageSize);
		map.put(Message.KEY_DATA, pageBean);
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
	//通过ip查询用户失败登录此时
	@Override
	public long queryFailLoginCount(String ip) {
		List<SysLogin> list=sysLoginDao.queryFailLoginCount(ip);
		if(list!=null&&list.size()>0) {
			return list.size();
		}
		return 0;
	}
	//删除
	@Override
	public Map<String, Object> deleteLoginlog(Integer id) {
		Map<String,Object> map=new HashMap<String,Object>();
		SysLogin sysLogin=this.queryById(id);
		if(sysLogin!=null) {
			sysLoginDao.deleteSysLogin(sysLogin);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
		}
		return map;
	}
	@Override
	public SysLogin queryById(Integer id) {
		List<SysLogin> list=sysLoginDao.queryById(id);
		if(list!=null&&list.size()>0) {
			return list.get(0);
		}
		return null;
	}
}

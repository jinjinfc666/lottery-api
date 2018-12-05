package com.jll.sys.log;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
import com.jll.dao.PageBean;
import com.jll.entity.SysLog;
import com.jll.entity.UserInfo;
import com.jll.user.UserInfoService;

@Service
@Transactional
public class SysLogServiceImpl implements SysLogService
{
	private Logger logger = Logger.getLogger(SysLogServiceImpl.class);
	
	@Resource
	SysLogDao sysLogDao;
	@Resource
	UserInfoService userInfoService;

	@Override
	public void saveOrUpdate(SysLog sysLog) {
		sysLog.setCreateTime(new Date());
		sysLogDao.saveUpdate(sysLog);
	}

	@Override
	public Map<String,Object> queryOperationlog(Integer type,String userName, String startTime, String endTime,Integer pageIndex,Integer pageSize) {
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
		PageBean pageBean=sysLogDao.queryOperationlog(type,userId, startTime, endTime, pageIndex, pageSize);
		map.put(Message.KEY_DATA, pageBean);
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
	
}

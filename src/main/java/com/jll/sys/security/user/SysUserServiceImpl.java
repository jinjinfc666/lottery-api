package com.jll.sys.security.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
import com.jll.entity.UserInfo;
@Service
@Transactional
public class SysUserServiceImpl implements SysUserService
{
	private Logger logger = Logger.getLogger(SysUserServiceImpl.class);
	@Resource
	SysUserDao userDao;
	//添加
	@Override
	public Map<String,Object> addUserInfo(UserInfo userInfo) {
		Map<String,Object> map=new HashMap<String,Object>();
		String loginPwd=userInfo.getLoginPwd();
		String userName=SecurityContextHolder.getContext().getAuthentication().getName();//当前登录的用户
		Integer creator=userDao.queryByUserName(userName).get(0).getId();
//		Integer creator=1;
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();		
		userInfo.setLoginPwd(encoder.encode(loginPwd));
		userInfo.setCreateTime(new Date());
		userInfo.setCreator(creator);
		boolean isOrNo=this.isOrNo(userInfo.getUserName());
		if(isOrNo) {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_EXISTING.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_EXISTING.getErrorMes());
			return map;
		}else {
			userDao.saveOrUpdateUserInfo(userInfo);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return map;
		}
	}
	//通过用户名查找UserInfo
	@Override
	public UserInfo queryByUserName(String userName) {
		List<UserInfo> list=userDao.queryByUserName(userName);
		return list.get(0);
	}
	//修改
	@Override
	public Map<String, Object> updateSysUser(Map<String, Object> ret) {
		Map<String,Object> map=new HashMap<String,Object>();
		Integer id=(Integer) ret.get("id");
		String userName=(String) ret.get("userName");
		String loginPwd=(String) ret.get("loginPwd");
		Integer state=(Integer) ret.get("state");
		Integer userType=(Integer) ret.get("userType");
		if(StringUtils.isBlank(userName)||StringUtils.isBlank(loginPwd)||state==null||userType==null) {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return map;
		}
		UserInfo userInfo=userDao.queryById(id).get(0);
		if(!StringUtils.isBlank(userName)) {
			userInfo.setUserName(userName);
		}
		if(!StringUtils.isBlank(loginPwd)) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();		
			userInfo.setLoginPwd(encoder.encode(loginPwd));
		}
		if(state!=null) {
			userInfo.setState(state);
		}
		if(userType!=null) {
			userInfo.setUserType(userType);
		}
		userDao.saveOrUpdateUserInfo(userInfo);
		map.clear();
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
	//查询要添加的数据存不存在
	public boolean isOrNo(String userName) {
		List<UserInfo> list=userDao.queryByUserName(userName);
		if(list!=null&&list.size()>0) {
			return true;
		}
		return false;
	}
	//查询所有的管理员
	@Override
	public Map<String, Object> querySysUser(Map<String, Object> ret) {
		String userName=(String)ret.get("userName");
		Integer pageIndex=(Integer) ret.get("pageIndex");
		Integer pageSize=(Integer) ret.get("pageSize");
		return userDao.querySysUser(userName,pageIndex,pageSize); 
	}
	
}


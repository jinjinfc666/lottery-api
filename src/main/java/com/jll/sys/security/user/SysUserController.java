package com.jll.sys.security.user;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.constants.Constants.UserType;
import com.jll.entity.SysAuthority;
import com.jll.entity.SysRole;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "SysUserSetting", name = "系统用户设置")
@ApiComment(seeClass = UserAccountDetails.class)
@RestController
@RequestMapping({"/SysUser"})
public class SysUserController {
	private Logger logger = Logger.getLogger(SysUserController.class);
	
	@Value("${sys_reset_pwd_default_pwd}")
	String defaultPwd;
	@Resource
	SysUserService sysUserService;
	@Resource
	SysAuthorityService sysAuthorityService;
	//添加后台管理员
	@RequestMapping(value={"/addSysRole"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSysUser(@RequestParam(name = "userName", required = true) String userName,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Integer userType=UserType.SYS_ADMIN.getCode();
			Integer state=0;
			Integer loginCount=0;
			UserInfo userInfo=new UserInfo();
			userInfo.setUserName(userName);
			userInfo.setLoginPwd(defaultPwd);
			userInfo.setState(state);
			userInfo.setUserType(userType);
			userInfo.setLoginCount(loginCount);
			Map<String,Object> map=sysUserService.addUserInfo(userInfo);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//修改(可修改用户资料或者state)
	@RequestMapping(value={"/updateSysUser"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSysUser(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "loginPwd", required = false) String loginPwd,
			  @RequestParam(name = "state", required = false) Integer state,
			  @RequestParam(name = "userType", required = false) Integer userType,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("id", id);
		ret.put("userName", userName);
		ret.put("loginPwd", loginPwd);
		ret.put("state", state);
		ret.put("userType", userType);
		try {
			Map<String,Object> map=sysUserService.updateSysUser(ret);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//查询用户
	@RequestMapping(value={"/querySysUser"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> querySysUser(@RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "pageIndex", required = true) Integer pageIndex,//当前请求页
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("userName", userName);
		Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
		ret.put("pageSize", pageSize);
		ret.put("pageIndex", pageIndex);
		try {
			Map<String,Object> map=sysUserService.querySysUser(ret);
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//修改授权
	@RequestMapping(value={"/updateSysAuthority"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSysAuthority(@RequestParam(name = "userId", required = true) Integer userId,
			@RequestParam(name = "roleIds", required = true) String roleIds,
			HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			ret.clear();
			ret.put("userId", userId);
			ret.put("roleIds", roleIds);
			sysAuthorityService.updateSysAuthority(ret);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//通过userId查询这个用户的权限
	@RequestMapping(value={"/byUserId"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryByUserId(@RequestParam(name = "userId", required = true) Integer userId,
			HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			List<SysAuthority> list=sysAuthorityService.queryByUserId(userId);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", list);
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
}

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Message;
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
	@Resource
	SysUserService sysUserService;
	@Resource
	SysAuthorityService sysAuthorityService;
	//添加
	@RequestMapping(value={"/addSysRole"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSysUser(@RequestParam(name = "userName", required = true) String userName,
			  @RequestParam(name = "loginPwd", required = true) String loginPwd,
			  @RequestParam(name = "state", required = true) Integer state,
			  @RequestParam(name = "userType", required = true) Integer userType,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			UserInfo userInfo=new UserInfo();
			userInfo.setUserName(userName);
			userInfo.setLoginPwd(loginPwd);
			userInfo.setState(state);
			userInfo.setUserType(userType);
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
	//添加授权
	@RequestMapping(value={"/addSysAuthority/{userId}"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSysAuthority(@PathVariable("userId") Integer userId,
			@RequestBody List<SysRole> sysRoleList,
			HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<String, Object>();
		if(sysRoleList==null||sysRoleList.size()==0) {
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
		try {
			ret.clear();
			ret.put("userId", userId);
			ret.put("sysRoleList", sysRoleList);
			sysAuthorityService.addSysAuthority(ret);
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
	//删除
	@RequestMapping(value={"/deleteSysAuthority/{id}"}, method={RequestMethod.DELETE}, produces={"application/json"})
	public Map<String, Object> deleteSysAuthority(@PathVariable(name = "id", required = true) Integer id,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,Object> map=sysAuthorityService.deleteById(id);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
}

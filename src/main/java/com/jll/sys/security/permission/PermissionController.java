package com.jll.sys.security.permission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Message;
import com.jll.entity.SysRole;
import com.jll.entity.UserAccountDetails;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "pernission", name = "权限控制")
@ApiComment(seeClass = UserAccountDetails.class)
@RestController
@RequestMapping({"/permission-control"})
public class PermissionController {
	private Logger logger = Logger.getLogger(PermissionController.class);
	@Resource
	SysRoleService sysRoleService;
	//添加
	@RequestMapping(value={"/addSysRole"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSysRole(@RequestParam(name = "roleName", required = true) String roleName,
			  @RequestParam(name = "roleDesc", required = true) String roleDesc,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(roleName)
				&&StringUtils.isBlank(roleDesc)) 
		{
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
		try {
			Integer state=1;
			ret.clear();
			ret.put("roleName", roleName);
			ret.put("roleDesc", roleDesc);
			ret.put("state", state);
			Map<String,Object> map=sysRoleService.addSysRole(ret);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//修改
	@RequestMapping(value={"/updateSysRole"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateSysRole(@RequestBody SysRole sysRole) {
		Map<String, Object> ret = new HashMap<>();
		if(sysRole.getId()==null) {
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
		if(StringUtils.isBlank(sysRole.getRoleName())
				&&StringUtils.isBlank(sysRole.getRoleDesc())
				&&sysRole.getState()==null) 
		{
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
		try {
			ret.clear();
			ret=sysRoleService.updateSysRole(sysRole);
			return ret;
		}catch(Exception e){
			ret.clear();
			e.printStackTrace();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//查询
	@RequestMapping(value={"/querySysRoleUserInfo"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> querySysRoleUserInfo() {
		Map<String, Object> ret = new HashMap<>();
		try {
			List<?> list=sysRoleService.query();
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", list);
			return ret;
		}catch(Exception e){
			ret.clear();
			e.printStackTrace();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//只查询当前表
	@RequestMapping(value={"/querySysRole"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> querySysRole() {
		Map<String, Object> ret = new HashMap<>();
		try {
			List<SysRole> list=sysRoleService.querySysRole();
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", list);
			return ret;
		}catch(Exception e){
			ret.clear();
			e.printStackTrace();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
}

package com.jll.sys.security.permission;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
import com.jll.entity.SysRole;
import com.jll.sys.security.user.SysUserService;
@Service
@Transactional
public class SysRoleServiceImpl implements SysRoleService
{
	private Logger logger = Logger.getLogger(SysRoleServiceImpl.class);
	
	@Resource
	SysRoleDao sysRoleDao;
	@Resource
	SysUserService  sysUserService;
	//添加
	@Override
	public Map<String,Object> addSysRole(Map<String,Object> ret) {
		Map<String,Object> map=new HashMap<String,Object>();
		String roleName=(String) ret.get("roleName");
		String roleDesc=(String) ret.get("roleDesc");
		Integer state=(Integer) ret.get("state");
//		String userName=SecurityContextHolder.getContext().getAuthentication().getName();//当前登录的用户
//		Integer creator=sysUserService.queryByUserName(userName).getId();
		Integer creator=1;
		SysRole sysRole=new SysRole();
		sysRole.setRoleName(roleName);
		sysRole.setRoleDesc(roleDesc);
		sysRole.setState(state);
		sysRole.setCreator(creator);
		sysRole.setCreateTime(new Date());
		boolean isOrNo=this.queryBySysRole(sysRole);
		if(isOrNo) {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_SYSROLE_ALREADY_EXISTS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_SYSROLE_ALREADY_EXISTS.getErrorMes());
			return map;
		}else {
			sysRoleDao.saveOrUpdateSysRole(sysRole);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return map;
		}
	}
	//修改
	@Override
	public Map<String, Object> updateSysRole(SysRole sysRole) {
		Map<String,Object> map=new HashMap<String,Object>();
		Integer id=sysRole.getId();
		String roleName=sysRole.getRoleName();
		String roleDesc=sysRole.getRoleDesc();
		Integer state=sysRole.getState();
		SysRole sysRoleNew=this.queryById(id);
		if(sysRoleNew==null) {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_SYSROLE_DOES_NOT_EXIST.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_SYSROLE_DOES_NOT_EXIST.getErrorMes());
			return map;
		}
		if(!StringUtils.isBlank(roleName)) {
			sysRoleNew.setRoleName(roleName);
		}
		if(!StringUtils.isBlank(roleDesc)) {
			sysRoleNew.setRoleDesc(roleDesc);
		}
		if(state!=null) {
			sysRoleNew.setState(state);
		}
		sysRoleDao.saveOrUpdateSysRole(sysRoleNew);
		map.clear();
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
	//通过id查询
	@Override
	public SysRole queryById(Integer id) {
		List<SysRole> sysRole=sysRoleDao.queryById(id);
		if(sysRole!=null&&sysRole.size()>0) {
			return sysRole.get(0);
		}
		return null; 
	}
	//查询
	@Override
	public List<?> query() {
		return sysRoleDao.query();
	}
	//查询当前表
	@Override
	public List<SysRole> querySysRole() {
		List<SysRole> list=sysRoleDao.querySysRole();
		if(list!=null&& list.size()>0) {
			return list;
		}
		return null;
	}
	//判断这条数据存不存在
	public boolean queryBySysRole(SysRole sysRole) {
		List<SysRole> sysRoleList=sysRoleDao.queryBySysRole(sysRole);
		if(sysRoleList!=null&&sysRoleList.size()>0) {
			return true;
		}
		return false;
	}
	
}


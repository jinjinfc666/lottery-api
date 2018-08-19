package com.jll.sys.security.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Message;
import com.jll.entity.SysAuthority;
import com.jll.entity.SysRole;

@Service
@Transactional
public class SysAuthorityServiceImpl implements SysAuthorityService
{
	private Logger logger = Logger.getLogger(SysAuthorityServiceImpl.class);
	@Resource
	SysAuthorityDao sysAuthorityDao;
	//添加
	@Override
	public void addSysAuthority(Map<String, Object> ret) {
		Integer userId=(Integer)ret.get("userId");
		List<SysRole> sysRoleList=(List<SysRole>) ret.get("sysRoleList");
		for(SysRole sysRole:sysRoleList) {
			SysAuthority sysAuthority=new SysAuthority();
			sysAuthority.setUserId(userId);
			sysAuthority.setRoleId(sysRole.getId());
			sysAuthorityDao.saveOrUpdateSysAuthority(sysAuthority);
		}
	}
	//删除
	@Override
	public Map<String,Object> deleteById(Integer id) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean isOrNo=this.isOrNo(id);
		if(isOrNo) {
			sysAuthorityDao.deleteById(id);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return map;
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return map;
		}
	}
	//判断这条数据存不存在
	@Override
	public boolean isOrNo(Integer id) {
		List<SysAuthority> list=sysAuthorityDao.queryById(id);
		if(list!=null&list.size()>0) {
			return true;
		}
		return false;
	}
	
}


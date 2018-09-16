package com.jll.sys.security.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
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
	public void updateSysAuthority(Map<String, Object> ret) {
		Integer userId=(Integer)ret.get("userId");
		String roleIds=(String) ret.get("roleIds");
		List<SysAuthority> list=sysAuthorityDao.queryByUserId(userId);
		if(list!=null&&list.size()>0) {
			sysAuthorityDao.deleteByUserId(userId);
		}
		if(!StringUtils.isBlank(roleIds)) {
			String[] arr = roleIds.split(",");
			for(int i =0;i<arr.length;i++) {
				SysAuthority sysAuthority=new SysAuthority();
				sysAuthority.setUserId(userId);
				sysAuthority.setRoleId(Integer.valueOf(arr[i]));
				sysAuthorityDao.saveOrUpdateSysAuthority(sysAuthority);
			}
		}
	}
	//通过userId查询用户所拥有的权限
	@Override
	public List<SysAuthority> queryByUserId(Integer userId) {
		List<SysAuthority> list=sysAuthorityDao.queryByUserId(userId);
		if(list!=null&&list.size()>0) {
			return list;
		}
		return null;
	}
}


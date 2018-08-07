package com.jll.sys.init;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.entity.SysCode;
import com.jll.sysSettings.codeManagement.SysCodeService;

public class SysInitLoader {
	
	@Resource
	CacheRedisService cacheServ;
	
	@Resource
	SysCodeService sysCodeServ;
	
	public void init() {
		initSysCode();
	}
	
	private void initSysCode() {
		initCodeType();
	}
	
	private void initCodeType() {
		String codeTypeName = Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
		Map<String, SysCode> lottoTypes = cacheServ.getSysCode(codeTypeName);
		List<SysCode> sysCodes = null;
		
		if(lottoTypes == null || lottoTypes.size() == 0) {
			sysCodes = sysCodeServ.queryCacheType(codeTypeName);
			List<SysCode> sysCodeTypes = sysCodeServ.queryCacheTypeOnly(codeTypeName);
			
			if(sysCodes == null || sysCodes.size() == 0
					|| sysCodeTypes == null
					|| sysCodeTypes.size() == 0) {
				return ;
			}
			
			sysCodes.add(sysCodeTypes.get(0));
			
			cacheServ.setSysCode(codeTypeName, sysCodes);
		}
	}
}

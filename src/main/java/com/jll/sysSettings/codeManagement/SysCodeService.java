package com.jll.sysSettings.codeManagement;

import java.util.List;
import java.util.Map;

import com.jll.entity.SysCode;

public interface SysCodeService {
	void saveBigSysCode(Map<String,Object> ret);
	void saveSmallSysCode(Map<String,Object> ret);
	List<SysCode> quertBigType();
	List<SysCode> querySmallType(Integer id);
	void updateSyscode(Map<String,Object> ret);
	List<SysCode> queryType(String bigType);
	void updateBigState(Integer id,Integer state);
	void updateSmallState(Integer id,Integer state);
	List<SysCode> queryBigCodeName(Integer id);
	List<SysCode> querySmallCodeName(Integer id);
	boolean isNull(String codeName);
	List<SysCode> queryCacheType(String codeName);
	List<SysCode> queryCacheTypeOnly(String codeName);
	
	SysCode querySysCodeById(Integer sysCodeTypeId);
	
	void saveSysCode(SysCode sysCode);
}

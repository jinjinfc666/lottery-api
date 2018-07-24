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
	void updateState(Map<String,Object> ret);
}

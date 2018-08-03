package com.jll.sysSettings.codeManagement;

import java.util.List;

import com.jll.entity.SysCode;

public interface SysCodeDao {
	public void saveSysCode(Integer codeType,Integer isCodeType,String codeName,String codeVal,Integer seq,Integer state,String remark);
	public Integer quertSysCodeId(String typeCodeName);
	public Integer quertSysCodeSeq(Integer codeType);
	public List<SysCode> quertBigType();
	public List<SysCode> querySmallType(Integer id);
	public void updateSyscode(SysCode sysCode);
	public void updateBigState(Integer id,Integer state);
	public void updateSmallState(Integer id,Integer state);
	public List<SysCode> queryType(String bigType);
	public List<SysCode> queryBigCodeName(Integer id);
	public List<SysCode> querySmallCodeName(Integer id);
	public long getCount(String codeName);
	public List<SysCode> queryCacheType(String codeName);
	public List<SysCode> queryCacheTypeOnly(String codeName);
}

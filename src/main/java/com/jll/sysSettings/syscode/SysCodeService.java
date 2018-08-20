package com.jll.sysSettings.syscode;

import java.util.List;
import java.util.Map;

import com.jll.entity.SysCode;

public interface SysCodeService {
//	void saveBigSysCode(Map<String,Object> ret);
//	void saveSmallSysCode(Map<String,Object> ret);
//	List<SysCode> querySmallType(Integer id);
//	void updateSyscode(Map<String,Object> ret);
//	List<SysCode> queryType(String codeTypeName);
//	void updateBigState(Integer id,Integer state);
//	void updateSmallState(Integer id,Integer state);
//	List<SysCode> queryBigCodeName(Integer id);
//	List<SysCode> querySmallCodeName(Integer id);
//	boolean isNull(String codeName);
//	List<SysCode> queryCacheType(String codeName);
//	List<SysCode> queryCacheTypeOnly(String codeName);
//
//	void saveSysCode(Integer type,String codeTypeName,SysCode sysCode);
//
//	SysCode querySysCodeById(Integer sysCodeTypeId);
	//-----------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------------------------
	//添加大类
	Map<String,Object> addSysCode(String codeTypeName,SysCode sysCode);
	//通过大类的codeName查询这个大类的id
	Integer queryByCodeName(String codeName);
	//添加小类
	Map<String,Object> addSmallSysCode(String codeTypeName,SysCode sysCode);
	//查询某个大类下的所有小类
	List<SysCode> queryAllSmallType(String bigCodeName);
	//修改某个小类的状态
	void updateSmallTypeState(Integer id, Integer state);
	//通过id获得一个SysCode对象
	SysCode queryById(Integer id);
	//修改某个大类下的某一条数据
	void updateSmallType(Map<String,Object> ret);
	//修改某个大类
	void updateBigType(Map<String,Object> ret);
	//修改某个大类的状态
	void updateBigTypeState(Integer id, Integer state);
	//查询所有的大类
	List<SysCode> quertBigType();
	//通过codeName查询代码类型    这个codeName只能是大类的codeName
	List<SysCode> queryByCodeNameBigType(String codeName);
	//判断某个大类存不存在
	boolean isBigNull(String codeName);
	//判断某个小类存不存在
	boolean isSmallNull(String bigCodeName,String smallCodeName);
	//通过小类的codeName和大类的CodeName来查询出这条数据
	List<SysCode> querySmallType(String bigCodeName, String smallCodeName);
	//修改排序
	Map<String,Object> updateSmallTypeSeq(String BigcodeName,String allId);
}

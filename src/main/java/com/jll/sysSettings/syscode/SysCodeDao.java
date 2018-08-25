package com.jll.sysSettings.syscode;

import java.util.List;

import com.jll.entity.SysCode;

public interface SysCodeDao {
//	public Integer quertSysCodeId(String typeCodeName);
//	public List<SysCode> querySmallType(Integer id);
//	public void updateSyscode(SysCode sysCode);
//	public void updateBigState(Integer id,Integer state);
//	public void updateSmallState(Integer id,Integer state);
//	public List<SysCode> queryType(String codeTypeName);
//	public List<SysCode> queryBigCodeName(Integer id);
//	public List<SysCode> querySmallCodeName(Integer id);
//	public long getCount(String codeName);
//	public List<SysCode> queryCacheType(String codeName);
//	public List<SysCode> queryCacheTypeOnly(String codeName);

	//-------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------------------------
	//通过大类的codeName查询这个大类的id
	Integer queryByCodeName(String codeName);
	//通过codeType查询出当前seq字段的最后一个值
	public Integer quertSysCodeSeq(Integer codeType);
	//添加
	public void saveSysCode(SysCode sysCode);
	//通过小类的codeName和大类的CodeName来查询出这条数据
	List<SysCode> querySmallType(String bigCodeName,String smallCodeName);
	//查询某个大类下的所有小类
	List<SysCode> queryAllSmallType(String bigCodeName);
	//修改某个小类的状态
	void updateSmallTypeState(Integer id,Integer state);
	//通过id获得一个SysCode对象
	List<SysCode> queryById(Integer id);
	//修改某个大类下的某一条数据
	void updateSmallType(SysCode sysCode);
	//修改某个大类的状态
	void updateBigTypeState(Integer id, Integer state);
	//查询所有的大类
	public List<SysCode> quertBigType();
	//通过codeName查询代码类型    这个codeName只能是大类的codeName
	List<SysCode> queryByCodeNameBigType(String codeName);
	//修改排序
	public void updateSmallTypeSeq(SysCode sysCode);
}

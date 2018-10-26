package com.jll.sysSettings.syscode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Message;
import com.jll.entity.SysCode;
@Service
@Transactional
public class SysCodeServiceImpl implements SysCodeService {
	@Resource
	SysCodeDao sysCodeDao;
	@Resource
	SysCodeService sysCodeService;
	@Resource
	CacheRedisService cacheRedisService;
//	@Override
//	public List<SysCode> querySmallType(Integer id) {
//		List<SysCode> sysCode=sysCodeDao.querySmallType(id);
//		return sysCode;
//	}
//
//	@Override
//	public void updateSyscode(Map<String, Object> ret) {
//		Integer id=(Integer)ret.get("id");
//		Integer type=(Integer)ret.get("type");
//		String codeName=(String)ret.get("codeName");
//		String codeVal=(String)ret.get("codeVal");
//		String remark=(String)ret.get("remark");
////		Integer seq=(Integer) ret.get("seq");
//		SysCode sysCode=new SysCode();
//		sysCode.setId(id);
//		sysCode.setCodeName(codeName);
//		sysCode.setCodeVal(codeVal);
//		sysCode.setRemark(remark);
//		sysCodeDao.updateSyscode(sysCode);
//		if(type==1) {
//			SysCode sysCode1=sysCodeService.querySysCodeById(id);
//			String codeTypeName=sysCode1.getCodeName();
//			// //TODO 
//			cacheRedisService.setSysCode(codeTypeName, sysCode1);
//		}else if(type==0) {
//			List<SysCode> smallList=sysCodeService.querySmallCodeName(id);
//			String codeTypeName=smallList.get(0).getCodeName();
//			SysCode sysCode2=sysCodeService.querySysCodeById(id);
//			// //TODO 
//			cacheRedisService.setSysCode(codeTypeName, sysCode2);
//		}
//	}
//	@Override
//	public List<SysCode> queryType(String codeTypeName) {
//		return sysCodeDao.queryType(codeTypeName);
//	}
//
//	@Override
//	public void updateBigState(Integer id, Integer state) {
//		sysCodeDao.updateBigState(id, state);
//		SysCode sysCode=sysCodeService.querySysCodeById(id);
//		String codeTypeName=sysCode.getCodeName();
//		// //TODO 
//		cacheRedisService.setSysCode(codeTypeName, sysCode);
//		List<SysCode> list=sysCodeService.queryCacheType(sysCode.getCodeName());
//		if(list!=null&&list.size()>0) {
//			Iterator<SysCode> it=list.iterator();
//			while(it.hasNext()) {
//				SysCode sysCode1=it.next();
//				cacheRedisService.setSysCode(codeTypeName, sysCode1);
//			}
//		}
//	}
//
//	@Override
//	public void updateSmallState(Integer id, Integer state) {
//		sysCodeDao.updateSmallState(id, state);
//		List<SysCode> smallList=sysCodeService.querySmallCodeName(id);
//		String codeTypeName=smallList.get(0).getCodeName();
//		SysCode sysCode=sysCodeService.querySysCodeById(id);
//		// //TODO 
//		cacheRedisService.setSysCode(codeTypeName, sysCode);
//	}
//
//	@Override
//	public List<SysCode> queryBigCodeName(Integer id) {
//		return sysCodeDao.queryBigCodeName(id);
//	}
//
//	@Override
//	public List<SysCode> querySmallCodeName(Integer id) {
//		return sysCodeDao.querySmallCodeName(id);
//	}
//
//	@Override
//	public boolean isNull(String codeName) {
//		long count=sysCodeDao.getCount(codeName);
//		return count == 0 ? false:true;
//	}
//
//	@Override
//	public List<SysCode> queryCacheType(String codeTypeName) {
//		return sysCodeDao.queryCacheType(codeTypeName);
//	}
//
//	@Override
//	public List<SysCode> queryCacheTypeOnly(String codeName) {
//		return sysCodeDao.queryCacheTypeOnly(codeName);
//	}
//
//	@Override
//	public SysCode querySysCodeById(Integer id) {
//		List<SysCode> list=sysCodeDao.queryBigCodeName(id);
//		if(list==null||list.size()==0) {
//			return null;
//		}
//		return list.get(0);
//	}
//
//	@Override
//	public void saveSysCode(Integer type,String codeTypeName,SysCode sysCode) {
//		if(sysCode.getIsCodeType()==0) {
//			Integer codeType=sysCode.getCodeType();
//			Integer seq=sysCodeDao.quertSysCodeSeq(codeType)+1;
//			sysCode.setSeq(seq);
//		}
//		sysCodeDao.saveSysCode(sysCode);
//		if(type==1) {
//			cacheRedisService.setSysCode(codeTypeName, sysCode);
//		}else if(type==0){
//			List<SysCode> sysCode1=sysCodeService.queryCacheTypeOnly(sysCode.getCodeName());
//			SysCode sysCode2=sysCode1.get(0);
//			cacheRedisService.setSysCode(codeTypeName, sysCode2);
//		}
//	}
	//-------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------------------------
	//添加大类
	@Override
	public Map<String,Object> addSysCode(String codeTypeName,SysCode sysCode) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean sure=sysCodeService.isBigNull(codeTypeName);
		if(!sure) {
			sysCodeDao.saveSysCode(sysCode);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_SYSCODE_ALREADY_EXISTS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_SYSCODE_ALREADY_EXISTS.getErrorMes());
		}
		return map;
	}
	//通过大类的codeName查询这个大类的id
	@Override
	public Integer queryByCodeName(String codeName){
		return sysCodeDao.queryByCodeName(codeName);
	}
	//添加小类
	@Override
	public Map<String,Object> addSmallSysCode(String codeTypeName,SysCode sysCode) {
		Map<String,Object> map=new HashMap<String,Object>();
		String smallCodeName=sysCode.getCodeName();
		boolean sure=sysCodeService.isSmallNull(codeTypeName, smallCodeName);
		if(!sure) {
			if(sysCode.getIsCodeType()==0) {
				Integer codeType=sysCode.getCodeType();
				Integer seq=sysCodeDao.quertSysCodeSeq(codeType)+1;
				sysCode.setSeq(seq);
			}
			sysCodeDao.saveSysCode(sysCode);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_CODE_VALUE_ALREADY_EXISTS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_CODE_VALUE_ALREADY_EXISTS.getErrorMes());
		}
		return map;
	}
	//查询某个大类下的所有小类
	@Override
	public List<SysCode> queryAllSmallType(String bigCodeName) {
		return sysCodeDao.queryAllSmallType(bigCodeName);
	}
	//修改某个小类的状态
	@Override
	public void updateSmallTypeState(Integer id, Integer state) {
		sysCodeDao.updateSmallTypeState(id, state);
	}
	//通过id获得一个SysCode对象
	@Override
	public SysCode queryById(Integer id) {
		List<SysCode> list=sysCodeDao.queryById(id);
		if(list==null||list.size()==0) {
			return null;
		}
		return list.get(0);
	}
	//修改某个大类下的某一条数据
	@Override
	public void updateSmallType(Map<String,Object> ret) {
		Integer id=(Integer)ret.get("id");
		String codeVal=(String)ret.get("codeVal");
		String remark=(String)ret.get("remark");
		SysCode sysCode=new SysCode();
		sysCode.setId(id);
		sysCode.setCodeVal(codeVal);
		sysCode.setRemark(remark);
		sysCodeDao.updateSmallType(sysCode);
		
	}
	//修改某个大类
	@Override
	public void updateBigType(Map<String, Object> ret) {
		String codeVal=(String)ret.get("codeVal");
		String remark=(String)ret.get("remark");
		Integer id=(Integer) ret.get("id");
		SysCode sysCode=new SysCode();
		sysCode.setId(id);
		sysCode.setCodeVal(codeVal);
		sysCode.setRemark(remark);
		sysCodeDao.updateSmallType(sysCode);
	}
	//修改大类的状态
	@Override
	public void updateBigTypeState(Integer id,Integer state) {
		sysCodeDao.updateBigTypeState(id, state);
	}
	//查询所有的大类
	@Override
	public List<SysCode> quertBigType() {
		List<SysCode> sysCode=sysCodeDao.quertBigType();
		return sysCode;
	}
	//通过codeName查询代码类型    这个codeName只能是大类的codeName
	@Override
	public List<SysCode> queryByCodeNameBigType(String codeName) {
		return sysCodeDao.queryByCodeNameBigType(codeName);
	}
	//查看某个大类存不存在
	@Override
	public boolean isBigNull(String codeName) {
		List<SysCode> list=sysCodeDao.queryByCodeNameBigType(codeName);
		if(list!=null&&list.size()>0) {
			return true;
		}else {
			return false;
		}
	}
	//查看某个小类存不存在
	@Override
	public boolean isSmallNull(String bigCodeName,String smallCodeName) {
		List<SysCode> list=sysCodeDao.querySmallType(bigCodeName,smallCodeName);
		if(list!=null&&list.size()>0) {
			return true;
		}else {
			return false;
		}
	}
	//通过小类的codeName和大类的CodeName来查询出这条数据
	@Override
	public List<SysCode> querySmallType(String bigCodeName, String smallCodeName) {
		return sysCodeDao.querySmallType(bigCodeName,smallCodeName);
	}
	//修改排序
	@Override
	public Map<String, Object> updateSmallTypeSeq(String BigcodeName,String allId) {
		Map<String,Object> map=new HashMap<String,Object>();
		String[] strArray = null;   
		strArray = allId.split(",");//把字符串转为String数组
		if(strArray.length>0) {
			for(int a=0;a<strArray.length;a++) {
				Integer id=Integer.valueOf(strArray[a]);
				List<SysCode> list=sysCodeDao.queryById(id);
				SysCode sysCode=null;
				SysCode sysCodeCache=null;
				if(list!=null&&list.size()>=0) {
					sysCode=list.get(0);
					sysCode.setSeq(a+1);
					String codeName=sysCode.getCodeName();
					sysCodeDao.updateSmallTypeSeq(sysCode);
					sysCodeCache=cacheRedisService.getSysCode(BigcodeName, codeName);
					sysCodeCache.setSeq(a+1);
					cacheRedisService.setSysCode(BigcodeName, sysCodeCache);
				}
			}
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return map;
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return map;
		}
	}
	//修改签到活动的排序
	@Override
	public Map<String, Object> updateSmallSignInDaySeq(String BigcodeName, String allId) {
		Map<String,Object> map=new HashMap<String,Object>();
		String[] strArray = null;   
		strArray = allId.split(",");//把字符串转为String数组
		if(strArray.length>0) {
			for(int a=0;a<strArray.length;a++) {
				Integer id=Integer.valueOf(strArray[a]);
				List<SysCode> list=sysCodeDao.queryById(id);
				SysCode sysCode=null;
				SysCode sysCodeCache=null;
				if(list!=null&&list.size()>=0) {
					sysCode=list.get(0);
					sysCode.setSeq(a+1);
					String codeName=sysCode.getCodeName();
					sysCodeDao.updateSmallTypeSeq(sysCode);
//					sysCodeCache=cacheRedisService.getSysCode(BigcodeName, codeName);
//					sysCodeCache.setSeq(a+1);
//					cacheRedisService.setSysCode(BigcodeName, sysCodeCache);
				}
			}
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return map;
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return map;
		}
	}
	
	
}

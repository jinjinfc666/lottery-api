package com.jll.sysSettings.codeManagement;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants;
import com.jll.entity.SysCode;
@Service
@Transactional
public class SysCodeServiceImpl implements SysCodeService {
	@Resource
	SysCodeDao sysCodeDao;
	@Override
	public void saveBigSysCode(Map<String, Object> ret) {
		String codeName=(String) ret.get("codeName");
		String codeVal=(String) ret.get("codeVal");
		String remark=(String) ret.get("remark");
		Integer codeType=null;
		Integer isCodeType=1;
		Integer seq=null;
		Integer state=Constants.SysCodeState.VALID_STATE.getCode();
		sysCodeDao.saveSysCode(codeType, isCodeType, codeName, codeVal, seq, state, remark);
	}

	@Override
	public void saveSmallSysCode(Map<String, Object> ret) {
		String typeCodeName=(String) ret.get("typeCodeName");
		String codeName=(String) ret.get("codeName");
		String codeVal=(String) ret.get("codeVal");
		String remark=(String) ret.get("remark");
		Integer codeType=sysCodeDao.quertSysCodeId(typeCodeName);
		Integer seq=sysCodeDao.quertSysCodeSeq(codeType)+1;
		Integer isCodeType=0;
		Integer state=Constants.SysCodeState.VALID_STATE.getCode();
		sysCodeDao.saveSysCode(codeType, isCodeType, codeName, codeVal, seq, state, remark);
	}

	@Override
	public List<SysCode> quertBigType() {
		List<SysCode> sysCode=sysCodeDao.quertBigType();
		return sysCode;
	}

	@Override
	public List<SysCode> querySmallType(Integer id) {
		List<SysCode> sysCode=sysCodeDao.querySmallType(id);
		return sysCode;
	}

	@Override
	public void updateSyscode(Map<String, Object> ret) {
		Integer id=(Integer)ret.get("id");
		Integer type=(Integer)ret.get("type");
//		String codeName=(String)ret.get("codeName");
		String codeVal=(String)ret.get("codeVal");
		String remark=(String)ret.get("remark");
		Integer seq=(Integer) ret.get("seq");
		SysCode sysCode=new SysCode();
		sysCode.setId(id);
//		sysCode.setCodeName(codeName);
		sysCode.setCodeVal(codeVal);
		sysCode.setRemark(remark);
		if(type==2) {
			sysCode.setSeq(seq);
		}
		sysCodeDao.updateSyscode(sysCode);
	}
	@Override
	public List<SysCode> queryType(String bigType) {
		return sysCodeDao.queryType(bigType);
	}

	@Override
	public void updateBigState(Integer id, Integer state) {
		sysCodeDao.updateBigState(id, state);
	}

	@Override
	public void updateSmallState(Integer id, Integer state) {
		sysCodeDao.updateSmallState(id, state);
	}

	@Override
	public List<SysCode> queryBigCodeName(Integer id) {
		return sysCodeDao.queryBigCodeName(id);
	}

	@Override
	public List<SysCode> querySmallCodeName(Integer id) {
		return sysCodeDao.querySmallCodeName(id);
	}

	@Override
	public boolean isNull(String codeName) {
		long count=sysCodeDao.getCount(codeName);
		return count == 0 ? false:true;
	}

	@Override
	public List<SysCode> queryCacheType(String codeTypeName) {
		return sysCodeDao.queryCacheType(codeTypeName);
	}

	@Override
	public List<SysCode> queryCacheTypeOnly(String codeName) {
		return sysCodeDao.queryCacheTypeOnly(codeName);
	}

	@Override
	public SysCode querySysCodeById(Integer sysCodeTypeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveSysCode(SysCode sysCode) {
		// TODO Auto-generated method stub
		
	}
}

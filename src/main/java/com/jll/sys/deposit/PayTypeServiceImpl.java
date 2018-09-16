package com.jll.sys.deposit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.entity.PayType;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.sysSettings.syscode.SysCodeService;
@Service
@Transactional
public class PayTypeServiceImpl implements PayTypeService
{
	private Logger logger = Logger.getLogger(PayTypeServiceImpl.class);

	@Resource
	PayTypeDao payTypeDao;

	@Resource
	CacheRedisService cacheServ;
	//添加
	@Override
	public Map<String, Object> addPayType(Map<String, Object> ret) {
		Map<String, Object> map=new HashMap<String,Object>();
		String name=(String) ret.get("name");
		String nickName=(String) ret.get("nickName");
		Integer state=(Integer) ret.get("state");
		Integer isTp=(Integer) ret.get("isTp");
		String platId=(String) ret.get("platId");
		boolean isNo=this.isOrNo(name, nickName, platId);
		if(isNo) {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return map;
		}
		PayType payType=new PayType();
		payType.setName(name);
		payType.setNickName(nickName);
		payType.setState(state);
		payType.setIsTp(isTp);
		payType.setPlatId(platId);
		Integer seq=payTypeDao.quertPayTypeSeq()+1;
		payType.setSeq(seq);
		payTypeDao.addPayType(payType);
		map.clear();
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
	//判断存不存在
	@Override
	public boolean isOrNo(String name, String nickName, String platId) {
		List<PayType> list=payTypeDao.queryBy(name, nickName, platId);
		if(list!=null&&list.size()>0) {
			return true;
		}
		return false;
	}
	//查询
	@Override
	public PayType queryBy(String name, String nickName, String platId) {
		List<PayType> list=payTypeDao.queryBy(name, nickName, platId);
		PayType payType=null;
		if(list!=null&&list.size()>0) {
			payType=list.get(0);
		}
		return payType;
	}
	//修改
	@Override
	public Map<String, Object> updatePayType(PayType payTypep) {
		String name=payTypep.getName();
		String nickName=payTypep.getNickName();
		String typeClass=payTypep.getTypeClass();
		String platId=payTypep.getPlatId();
		Integer state=payTypep.getState();
		Integer isTp=payTypep.getIsTp();
		Map<String, Object> map=new HashMap<String,Object>();
		Integer id=payTypep.getId();
		boolean isOrNo=this.isNull(id);
		if(isOrNo) {
			String payTypeName=Constants.PayTypeName.PAY_TYPE_CLASS.getCode();
			List<PayType> playTypeList=cacheServ.getPayType(payTypeName);
			PayType payType=payTypeDao.queryById(id).get(0);
			if(!StringUtils.isBlank(name)) {
				payType.setName(name);
			}
			if(!StringUtils.isBlank(nickName)) {
				payType.setNickName(nickName);
			}
			if(!StringUtils.isBlank(typeClass)) {
				payType.setTypeClass(typeClass);
			}
			if(!StringUtils.isBlank(platId)) {
				payType.setPlatId(platId);
			}
			if(isTp!=null) {
				payType.setIsTp(isTp);
			}
			if(state!=null) {
				payType.setState(state);
			}
			payTypeDao.updatePayType(payType);
			if(playTypeList!=null&&playTypeList.size()>0) {
				Integer id1=null;
				for(int i=0; i<playTypeList.size();i++)    {   
					PayType playType1=playTypeList.get(i);
				     id1=playType1.getId();
				     if((int)id1==(int)id) {
						playTypeList.set(i, payType);
					}
				 }
				cacheServ.setPayType(payTypeName, playTypeList);
			}
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
	//通过id查找存不存在
	@Override
	public boolean isNull(Integer id) {
		List<PayType> list=payTypeDao.queryById(id);
		if(list!=null&&list.size()>0){
			return true;
		}
		return false;
	}
	//查询所有数据
	@Override
	public List<?> queryAllPayType(Integer bigCodeNameId) {
		return payTypeDao.queryAllPayType(bigCodeNameId);
	}
	//修改排序
	@Override
	public Map<String, Object> updatePayTypeState(String allId) {
		String payTypeName=Constants.PayTypeName.PAY_TYPE_CLASS.getCode();
		Map<String,Object> map=new HashMap<String,Object>();
		String[] strArray = null;   
		strArray = allId.split(",");//把字符串转为String数组
		if(strArray.length>0) {
			for(int a=0;a<strArray.length;a++) {
				Integer id=Integer.valueOf(strArray[a]);
				List<PayType> list=payTypeDao.queryById(id);
				PayType payType=null;
				List<PayType> payTypeCacheLists=null;
				if(list!=null&&list.size()>=0) {
					payType=list.get(0);
					payType.setSeq(a+1);
					payTypeDao.updatePayType(payType);
					payTypeCacheLists=cacheServ.getPayType(payTypeName);
					if(payTypeCacheLists!=null&&payTypeCacheLists.size()>0) {
						Integer id1=null;
						for(int i=0; i<payTypeCacheLists.size();i++)    {   
						     PayType payType1=payTypeCacheLists.get(i);
						     id1=payType1.getId();
						     if((int)id1==(int)id) {
						    	 payType1.setSeq(a+1);
						    	 payTypeCacheLists.set(i, payType1);
							}
						 }
						cacheServ.setPayType(payTypeName, payTypeCacheLists);
					}
				}
			}
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
	@Override
	public PayType queryById(Integer id) {
		List<PayType> list=payTypeDao.queryById(id);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	@Override
	public List<PayType> queryAllPayType() {
		return payTypeDao.queryAllPayType();
	}
	//通过id查询某一条数据
	@Override
	public List<?> queryPayTypeById(Integer id,Integer bigCodeNameId) {
		return payTypeDao.queryPayTypeById(id,bigCodeNameId);
	}
}


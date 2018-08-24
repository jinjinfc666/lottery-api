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
import com.jll.entity.IpBlackList;
import com.jll.entity.PayChannel;
import com.jll.entity.PayType;
import com.jll.entity.SysCode;
@Service
@Transactional
public class PayChannelServiceImpl implements PayChannelService
{
	private Logger logger = Logger.getLogger(PayChannelServiceImpl.class);

	@Resource
	PayChannelDao payChannelDao;
	
	@Resource
	PayTypeService payTypeService;
	
	@Resource
	CacheRedisService cacheServ;
	//添加
	@Override
	public Map<String, Object> addPayChannel(PayChannel payChannel) {
		Map<String,Object> map=new HashMap<String,Object>();
		String channelName=payChannel.getChannelName();
		String nickName=payChannel.getNickName();
		String typeClass=payChannel.getTypeClass();
		Integer payType=payChannel.getPayType();
		Float maxAmount=payChannel.getMaxAmount();
		Integer enableMaxAmount=payChannel.getEnableMaxAmount();
		Integer state=payChannel.getState();
		String remark=payChannel.getRemark();
		if(StringUtils.isBlank(channelName)
				||StringUtils.isBlank(nickName)
				||StringUtils.isBlank(typeClass)
				||payType==null||maxAmount==null||enableMaxAmount==null ||state==null ||StringUtils.isBlank(remark)) 
		{
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return map;
		}
		PayType payType1=payTypeService.queryById(payType);
		String qrUrl=payChannel.getQrUrl();
		String bankAcc=payChannel.getBankAcc();
		String bankName=payChannel.getBankName();
		if(payType1!=null) {
			if((int)payType1.getIsTp()==0) {
				if(StringUtils.isBlank(qrUrl)||StringUtils.isBlank(bankAcc)||StringUtils.isBlank(bankName)) {
					map.clear();
					map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
					map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
					map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
					return map;
				}
			}else {
				if(!StringUtils.isBlank(qrUrl)||!StringUtils.isBlank(bankAcc)||!StringUtils.isBlank(bankName)) {
					map.clear();
					map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
					map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
					map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
					return map;
				}
			}
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return map;
		}
		Integer seq=payChannelDao.quertPayChannelSeq()+1;
		payChannel.setSeq(seq);
		payChannelDao.addPayChannel(payChannel);
		map.clear();
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
	//查询新增之后的最后条数据
	@Override
	public PayChannel queryLast() {
		return payChannelDao.queryLast().get(0);
	}
	//修改
	@Override
	public Map<String, Object> updatePayChannel(PayChannel payChannel) {
		String channelName=payChannel.getChannelName();
		String nickName=payChannel.getNickName();
		String typeClass=payChannel.getTypeClass();
		Integer payType=payChannel.getPayType();
		Float maxAmount=payChannel.getMaxAmount();
		Integer enableMaxAmount=payChannel.getEnableMaxAmount();
		Integer state=payChannel.getState();
		String remark=payChannel.getRemark();
		String qrUrl=payChannel.getQrUrl();
		String bankAcc=payChannel.getBankAcc();
		String bankName=payChannel.getBankName();
		Integer id=payChannel.getId();
		Map<String, Object> map=new HashMap<String,Object>();
		boolean isOrNo=this.isNull(id);
		if(isOrNo) {
			PayType payType1=payTypeService.queryById(payType);
			if(payType1!=null) {
				if((int)payType1.getIsTp()==0) {
					if(StringUtils.isBlank(qrUrl)||StringUtils.isBlank(bankAcc)||StringUtils.isBlank(bankName)) {
						map.clear();
						map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
						map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
						map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
						return map;
					}
				}else {
					if(!StringUtils.isBlank(qrUrl)||!StringUtils.isBlank(bankAcc)||!StringUtils.isBlank(bankName)) {
						map.clear();
						map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
						map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
						map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
						return map;
					}
				}
			}else {
				map.clear();
				map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
				map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
				return map;
			}
			String payChannelName=Constants.PayChannel.PAY_CHANNEL.getCode();
			PayChannel payChannel1=payChannelDao.queryById(id).get(0);
			if(!StringUtils.isBlank(channelName)) {
				payChannel1.setChannelName(payChannelName);
			}
			if(!StringUtils.isBlank(nickName)) {
				payChannel1.setNickName(nickName);
			}
			if(!StringUtils.isBlank(typeClass)) {
				payChannel1.setTypeClass(typeClass);
			}
			if(payType!=null) {
				payChannel1.setPayType(payType);
			}
			if(maxAmount!=null) {
				payChannel1.setMaxAmount(maxAmount);
			}
			if(enableMaxAmount!=null) {
				payChannel1.setEnableMaxAmount(enableMaxAmount);
			}
			if(state!=null) {
				payChannel1.setState(state);
			}
			if(!StringUtils.isBlank(remark)) {
				payChannel1.setRemark(remark);
			}
			if(!StringUtils.isBlank(qrUrl)) {
				payChannel1.setQrUrl(qrUrl);
			}
			if(!StringUtils.isBlank(bankAcc)) {
				payChannel1.setBankAcc(bankAcc);
			}
			if(!StringUtils.isBlank(bankName)) {
				payChannel1.setBankName(bankName);
			}
			payChannelDao.updatePayChannel(payChannel1);
			cacheServ.setPayChannel(payChannelName, payChannel1);
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
	//通过id查询
	@Override
	public PayChannel queryById(Integer id) {
		List<PayChannel> payChannelList=payChannelDao.queryById(id);
		if(payChannelList!=null&&payChannelList.size()>0) {
			return payChannelList.get(0);
		}
		return null;
	}
	//通过id查找存不存在
	@Override
	public boolean isNull(Integer id) {
		List<PayChannel> list=payChannelDao.queryById(id);
		if(list!=null&&list.size()>0){
			return true;
		}
		return false;
	}
	//查询所有
	@Override
	public List<PayChannel> queryAll() {
		List<PayChannel> list=payChannelDao.queryAll();
		if(list!=null&&list.size()>0) {
			return list;
		}
		return null;
	}
	//修改排序
	@Override
	public Map<String, Object> updatePayChannelSeq(String allId) {
		String payChannelName=Constants.PayChannel.PAY_CHANNEL.getCode();
		Map<String,Object> map=new HashMap<String,Object>();
		String[] strArray = null;   
		strArray = allId.split(",");//把字符串转为String数组
		if(strArray.length>0) {
			for(int a=0;a<strArray.length;a++) {
				Integer id=Integer.valueOf(strArray[a]);
				List<PayChannel> list=payChannelDao.queryById(id);
				PayChannel payChannel=null;
				PayChannel payChannelCache=null;
				if(list!=null&&list.size()>=0) {
					payChannel=list.get(0);
					payChannel.setSeq(a+1);
					payChannelDao.updatePayChannel(payChannel);
					payChannelCache=cacheServ.getPayChannel(payChannelName, id);
					payChannelCache.setSeq(a+1);
					cacheServ.setPayChannel(payChannelName, payChannelCache);
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
}


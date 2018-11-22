package com.jll.pay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.DepositOrderState;
import com.jll.common.constants.Constants.PayChannelType;
import com.jll.common.constants.Constants.PayChannnelEMA;
import com.jll.common.constants.Constants.PayTypeState;
import com.jll.common.constants.Constants.UserType;
import com.jll.common.constants.Message;
import com.jll.common.utils.PageQuery;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
import com.jll.dao.PageQueryDao;
import com.jll.dao.SupserDao;
import com.jll.entity.DepositApplication;
import com.jll.entity.PayChannel;
import com.jll.entity.PayType;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.pay.caiPay.CaiPayService;
import com.jll.pay.order.DepositOrderDao;
import com.jll.pay.order.DepositOrderService;
import com.jll.pay.tlCloud.TlCloudService;
import com.jll.pay.zhihpay.ZhihPayService;
import com.jll.user.UserInfoService;

@Service
@Transactional
public class PaymentServiceImpl  implements PaymentService
{
  @Resource
  PaymentDao trendAnalysisDao;
  
  
  @Resource
  CacheRedisService cacheRedisService;
  
  @Resource
  CaiPayService caiPayService;
  
  @Resource
  UserInfoService userInfoService;
  
  @Resource
  ZhihPayService zhihPayService;
  
  @Resource
  TlCloudService tlCloudService;
  
  @Resource
  SupserDao supserDao;
  
  @Resource
  DepositOrderDao depositOrderDao;
  
  @Resource
  DepositOrderService depositOrderService;
  
  public long queryDepositTimes(int userId){
    return this.trendAnalysisDao.queryDepositTimes(userId);
  }

	@Override
	public List<PayType> getSysPayType() {
		return (List<PayType>) supserDao.findByName(PayType.class, "state",Constants.PayTypeState.VALID_STATE.getCode());
	}
	
	@Override
	public List<PayChannel> getSysPayChannelByType(int peyType) {
		return (List<PayChannel>) supserDao.findByName(PayChannel.class,"payType",peyType,"state",Constants.PayTypeState.VALID_STATE.getCode());
	}

	@Override
	public List<PayChannel> getUserPayChannel() {
	
		List<PayChannel> retLists = new ArrayList<>();
		
		Map<Integer, PayChannel> queryLists =cacheRedisService.getPayChannel(Constants.PayChannel.PAY_CHANNEL.getCode());
		
		for (Integer pKey : queryLists.keySet()) {
			PayChannel pc = queryLists.get(pKey);
			if(null == PayChannelType.getValueByCode(pc.getPayCode())
					&& pc.getState() == Constants.PayTypeState.VALID_STATE.getCode()){
				retLists.add(pc);
			}
		}		
		
		Collections.sort(retLists, new Comparator<PayChannel>() {
            public int compare(PayChannel o1, PayChannel o2) {
                return o1.getSeq().compareTo(o2.getSeq());
            }
        });
		
		return retLists;
	}

	@Override
	public Map<String, Object> getUserPayOrder(int userId, PageQueryDao page) {

		Map<String, Object> ret = new HashMap<String, Object>();
		
		StringBuffer querySql = new StringBuffer("FROM  DepositApplication o,UserInfo u1 WHERE o.userId = ? ");
		List<Object> parmsList = new ArrayList<>();
		
		parmsList.add(userId);
		if(!StringUtils.isEmpty(page.getBillNo())){
			querySql.append(" and o.orderNum =? ");
			parmsList.add(page.getBillNo());
		}
		
		querySql.append(" and o.createTime >= ?  and o.createTime <= ?");	
		parmsList.add(page.getStartDate());
		parmsList.add(page.getEndDate());
		
		querySql.append(" and u1.id = o.userId ")
				.append(" order by o.id desc");
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenationByHql(supserDao,querySql.toString(), UserAccountDetails.class, parmsList, page.getPageIndex(), page.getPageSize()));
		return ret;
	}
	
	
	private void getScanPayInfo(Map<String, Object> ret,String retCode, String qrCode){
		if(retCode.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
			if(qrCode == null || qrCode.length() == 0) {
				ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				ret.put(Message.KEY_ERROR_CODE, retCode);
				ret.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
			}
			ret.put(Message.KEY_DATA_QR_CODE, qrCode);
			//ret.put(Message.KEY_DATA_TYPE, "qrcode");
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, retCode);
			ret.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
		}
	}
	@Override
	public Map<String, Object> payOrderToSystem(int userId, DepositApplication info,Map<String, Object> pramsInfo) {
		
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		UserInfo dbInfo = (UserInfo) supserDao.get(UserInfo.class,userId);
		
		if(null == dbInfo
				|| null == info.getPayType()
				|| null == info.getPayChannel()
				|| (null == info.getAmount()|| info.getAmount() < 0)
				){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		if(UserType.DEMO_PLAYER.getCode() == dbInfo.getUserType()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_DEMO_USER_DISABLE_FUN.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_DEMO_USER_DISABLE_FUN.getErrorMes());
			return ret;
		}
		
		//验证支付渠道
		PayChannel pcInfo = cacheRedisService.getPayChannel(Constants.PayChannel.PAY_CHANNEL.getCode()).get(info.getPayChannel());
		if(null == pcInfo ||
				pcInfo.getState() == PayTypeState.INVALID_STATE.getCode()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_MESSAGE_PAY_TYPE_DISABLE.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_MESSAGE_PAY_TYPE_DISABLE.getErrorMes());
			return ret;
		}
		
		//验证支付渠道最大限制额度
		if(PayChannnelEMA.NO.getCode() == pcInfo.getEnableMaxAmount()
				&& info.getAmount() > pcInfo.getMaxAmount()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_MESSAGE_PAY_TYPE_DISABLE.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_MESSAGE_PAY_TYPE_DISABLE.getErrorMes());
			return ret;
		}
		
		PayType pt = cacheRedisService.getPayTypeInfo(info.getPayType());
		
		data.put(Message.KEY_DATA_TYPE, pcInfo.getShowType());
		//写入订单
		DepositApplication depositOrder = depositOrderDao.saveDepositOrder(info.getPayType(), info.getPayChannel(),userId, info.getAmount(), "",new Date(),"");
		pramsInfo.put("depositOrder", depositOrder);
		pramsInfo.put("userId", userId);
		pramsInfo.put("rechargeType", pcInfo.getPayCode());
		pramsInfo.put("amount", info.getAmount());
		if(pt.getPlatId().equals(Constants.PayType.CAI_PAY.getCode())){
			pramsInfo.remove("reqIP");
			pramsInfo.remove("payerName");
			pramsInfo.remove("payCardNumber");
			if(!pcInfo.getPayCode().equals("00026")){
				String retCode = caiPayService.processScanPay(pramsInfo);
				//getScanPayInfo(ret, retCode, StringUtils.getStringValue(pramsInfo.get("qrcode")));
				String qrCode = StringUtils.getStringValue(pramsInfo.get("qrcode"));
				if(retCode.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
					if(StringUtils.isBlank(qrCode)) {
						ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
						ret.put(Message.KEY_ERROR_CODE, retCode);
						ret.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
					}else {
						data.put(Message.KEY_DATA_QR_CODE, qrCode);
						ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());						
					}
				}else {
					ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
					ret.put(Message.KEY_ERROR_CODE, retCode);
					ret.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
				}
				
			}else{
				caiPayService.processOnlineBankPay(pramsInfo);
				data.put("isRedirect",true);
				data.put(Message.KEY_DATA_REDIRECT, pramsInfo.get("redirect"));
				ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			}
		}else if(pt.getPlatId().equals(Constants.PayType.WISDOM_PAYMENT.getCode())){
			pramsInfo.remove("payerName");
			pramsInfo.remove("payCardNumber");
			if(pcInfo.getPayCode().equals("b2c")){
				zhihPayService.processOnlineBankPay(pramsInfo);
				data.put("isRedirect",true);
				data.put(Message.KEY_DATA_REDIRECT, pramsInfo.get("redirect"));
				ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			}else{
				String retCode = zhihPayService.processScanPay(pramsInfo);
				//getScanPayInfo(ret, retCode, StringUtils.getStringValue(pramsInfo.get("qrcode")));
				String qrCode = StringUtils.getStringValue(pramsInfo.get("qrcode"));
				if(retCode.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
					if(StringUtils.isBlank(qrCode)) {
						ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
						ret.put(Message.KEY_ERROR_CODE, retCode);
						ret.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
					}else {
						data.put(Message.KEY_DATA_QR_CODE, qrCode);
						ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());						
					}
				}else {
					ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
					ret.put(Message.KEY_ERROR_CODE, retCode);
					ret.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
				}
			}
		}else if(pt.getPlatId().equals(Constants.PayType.TLY_PAY.getCode())){
			pramsInfo.put("card_number", pcInfo.getBankAcc());
			String retCode = tlCloudService.saveDepositOrder(pramsInfo);
			
			if(retCode.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
				ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
				//ret.put(Message.KEY_REMAKE, "SUCCESS");
			}else {
				ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				ret.put(Message.KEY_ERROR_CODE, retCode);
				ret.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
			}
		//按照系统支付来处理
		}else{
			
			if(pcInfo.getShowType().intValue() == Constants.PayChannelShowType.IMG_PATH.getCode()) {
				data.put(Message.KEY_DATA_QR_CODE, pcInfo.getQrUrl());
			}else if(pcInfo.getShowType().intValue() == Constants.PayChannelShowType.BANK_ACC.getCode()) {
				data.put(Message.KEY_DATA_BANK_ACC, pcInfo.getBankAcc());
			}
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}
		
		ret.put(Message.KEY_DATA,data);
		return ret;
	}

	@Override
	public Map<String, Object> processOrderEnd(Map<String, Object> params) {
		 Map<String, Object> ret = new HashMap<String, Object>();
		
		 UserInfo curInfo = userInfoService.getCurLoginInfo();
		 DepositApplication dbDep =  depositOrderDao.queryDepositOrderById(Utils.toString(params.get("orderNum")));
		 if(null == dbDep
				 ||  DepositOrderState.INIT_OR_PUSHED.getCode() != dbDep.getState()
				 || null == curInfo
				 || curInfo.getUserType() !=  UserType.SYS_ADMIN.getCode()){
				ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
				ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
				return ret;
		 }
		 depositOrderService.processReceiveDepositOrder(Utils.toString(params.get("orderNum")),"");
		 ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		 return ret;
	}
}

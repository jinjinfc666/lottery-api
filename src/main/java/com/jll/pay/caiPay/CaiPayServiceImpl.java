package com.jll.pay.caiPay;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.http.HttpRemoteStub;
import com.jll.common.utils.BigDecimalUtil;
import com.jll.common.utils.MD5Signature;
import com.jll.common.utils.StringUtils;
import com.jll.dao.SupserDao;
import com.jll.entity.DepositApplication;
import com.jll.entity.PayChannel;
import com.jll.entity.display.CaiPayNotices;
import com.jll.pay.PaymentDao;
import com.jll.pay.order.DepositOrderDao;



@Configuration
@PropertySource("classpath:caifu.properties")
@Service
@Transactional
public class CaiPayServiceImpl implements CaiPayService
{
	private Logger logger = Logger.getLogger(CaiPayServiceImpl.class);
	/*private final String FAILED_CODE = "1";
	
	private final String SUCCESS_CODE = "0";*/
	
	//private final String FAILED_CODE_ONLINE_PAY = "1";
	
	private final String SUCCESS_CODE = "1";
	
	 @Resource
	 CacheRedisService cacheRedisService;
	
	@Resource
	CaiPayDao tlCloudDao;
	
	@Resource
	PaymentDao payDao;
	
	@Resource
	SupserDao supserDao;
	
	@Resource
	DepositOrderDao depositOrderDao;
	
		  
	@Value("${caifu.api.server}")
	private String apiServer;
	  
	@Value("${api.scanQRPay}")
	private String apiScanQRPay;
	  
	@Value("${api.onLineBankPay}")
	private String apiOnLineBankPay;
	  
	@Value("${caifu.cons.versionId}")
	private String versionId;
	
	@Value("${caifu.cons.currency}")
	private String currency;
	
	@Value("${caifu.cons.transType}")
	private String transType;
	
	@Value("${caifu.cons.signType}")
	private String signType;
  
	/*@Value("${cons.receivableType}")
	private String receivableType;*/
	
	/*@Value("${cons.scan_pay.merId}")
	private String scanPayMerId;*/
	
	@Value("${api.scanQRPay.asynNotifyUrl}")
	private String scanPayAsynNOtifyURL;
	
	@Value("${api.scanQRPay.synNotifyUrl}")
	private String scanPaySynNOtifyURL;	
	
	/*@Value("${cons.scan_pay.key}")
	private String scanPayKey;*/
	
	/*@Value("${cons.online_bank_pay.merId}")
	private String onlineBankPayMerId;*/
	
	@Value("${api.onLineBankPay.asynNotifyUrl}")
	private String onlineBankPayAsynNotifyUrl;
	
	@Value("${api.onLineBankPay.synNotifyUrl}")
	private String onlineBankPaysynNotifyUrl;
	
	
	@Value("${merchant1.merId}")
	private String merchant1MerId;
	
	@Value("${merchant1.key}")
	private String merchant1Key;
	
	@Value("${merchant1.payMode}")
	private String merchant1PayMode;
	
	@Value("${merchant1.receivableType}")
	private String merchant1ReceivableType;
	
	
	@Value("${merchant2.merId}")
	private String merchant2MerId;
	
	@Value("${merchant2.key}")
	private String merchant2Key;
	
	@Value("${merchant2.payMode}")
	private String merchant2PayMode;
	
	@Value("${merchant2.receivableType}")
	private String merchant2ReceivableType;
	
	
	
	
	@Value("${merchant3.merId}")
	private String merchant3MerId;
	
	@Value("${merchant3.key}")
	private String merchant3Key;
	
	@Value("${merchant3.payMode}")
	private String merchant3PayMode;
	
	@Value("${merchant3.receivableType}")
	private String merchant3ReceivableType;
	
	@Value("${merchant4.merId}")
	private String merchant4MerId;
	
	@Value("${merchant4.key}")
	private String merchant4Key;
	
	@Value("${merchant4.payMode}")
	private String merchant4PayMode;
	
	@Value("${merchant4.receivableType}")
	private String merchant4ReceivableType;
	
	private List<Merchant> merchants/* = new ArrayList<>()*/;
	
	@PostConstruct
	public void init() {
		merchants = new ArrayList<>();
		
		Merchant merchant = new Merchant();
		merchant.setKey(merchant1Key);
		merchant.setMerId(merchant1MerId);
		List<String> payModes = Arrays.asList(merchant1PayMode.split("\\|"));
		merchant.setPayModes(payModes);
		merchant.setReceivableType(merchant1ReceivableType);
		merchants.add(merchant);
		
		Merchant merchant2 = new Merchant();
		merchant2.setKey(merchant2Key);
		merchant2.setMerId(merchant2MerId);
		List<String> payModes2 = Arrays.asList(merchant2PayMode.split("\\|"));
		merchant2.setPayModes(payModes2);
		merchant2.setReceivableType(merchant2ReceivableType);
		merchants.add(merchant2);
		
		Merchant merchant3 = new Merchant();
		merchant3.setKey(merchant3Key);
		merchant3.setMerId(merchant3MerId);
		List<String> payModes3 = Arrays.asList(merchant3PayMode.split("\\|"));
		merchant3.setPayModes(payModes3);
		merchant3.setReceivableType(merchant3ReceivableType);
		merchants.add(merchant3);
		
		Merchant merchant4 = new Merchant();
		merchant4.setKey(merchant4Key);
		merchant4.setMerId(merchant4MerId);
		List<String> payModes4 = Arrays.asList(merchant4PayMode.split("\\|"));
		merchant4.setPayModes(payModes4);
		merchant4.setReceivableType(merchant4ReceivableType);
		merchants.add(merchant4);
	}
	
	
	
	private boolean isResponseSuccess(Map<String, Object> response) {
		if(response.size() == 0) {
			logger.debug("Can't read response from the cai-pay server!!!");
			return false;
		}
		
		int status = (int)response.get("status");
		if(status != HttpStatus.SC_OK) {
			return false;
		}else {
			String body = (String)response.get("responseBody");
			
			logger.debug("the response is ::: " + (body == null?"":body));
			
			if(body != null && body.length() > 0) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					JsonNode node = mapper.readTree(body.getBytes("UTF-8"));
					if(node == null) {
						logger.debug("can't read the response!!!");
						return false;
					}
					
					node = node.get("retCode");
					if(node == null) {
						logger.debug("retCode is null");
						return false;
					}
					
					String code = node.asText();
					if(code == null 
							|| code.length() == 0) {
						logger.debug("retCode value is null");
						return false;
					}
					
					logger.debug("retCode value is :::" + code);
					if(code.equals(SUCCESS_CODE)) {
						return true;
					}
					
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}
	
	private boolean isOnlineBankResponseSuccess(Map<String, Object> response) {
		int status = (int)response.get("status");
		if(status != HttpStatus.SC_OK) {
			return false;
		}else {
			String body = (String)response.get("responseBody");
			if(body != null && body.length() > 0) {
				if(body.contains("pay_form")) {
					return true;
				}else {
					return false;
				}
			}
		}
		return false;
	}
	
	private SortedMap<String, Object> produceParamsOfScanQRPay(Map<String, Object> params){
		Merchant merchant = queryCurrMerchant((String)params.get("rechargeType"));
		
		if(merchant == null) {
			return null;
		}
		
		DecimalFormat numFormat = new DecimalFormat("##0");
		DepositApplication depositOrder = (DepositApplication)params.get("depositOrder");
		SortedMap<String, Object> pushParams = new TreeMap<>();
		pushParams.put("versionId", versionId);
		pushParams.put("orderAmount", numFormat.format(((Float)params.get("amount"))*100));
		pushParams.put("orderDate", params.get("createTime"));
		pushParams.put("currency", currency);
		pushParams.put("transType", transType);
		pushParams.put("asynNotifyUrl", params.get("asynNotifyURL"));
		pushParams.put("synNotifyUrl", params.get("synNotifyURL"));
		pushParams.put("signType", signType);
		pushParams.put("merId", merchant.getMerId());
		pushParams.put("prdOrdNo", String.valueOf(depositOrder.getOrderNum()));
		pushParams.put("payMode", (String)params.get("rechargeType"));
		pushParams.put("receivableType", merchant.getReceivableType());
		pushParams.put("prdAmt", numFormat.format(1));
		pushParams.put("prdName", "lottery");
		
		String sign = MD5Signature.createSign(pushParams, merchant.getKey());
		if(sign == null || sign.length() == 0) {
			return null;
		}
		depositOrder.setPlatAccount(merchant.getMerId());
		supserDao.update(depositOrder);
		pushParams.put("signData", sign);
		return pushParams;
	}

	private SortedMap<String, Object> produceParamsOfOnlinePay(Map<String, Object> params){
		DecimalFormat numFormat = new DecimalFormat("##0");
		DepositApplication depositOrder = (DepositApplication)params.get("depositOrder");
		SortedMap<String, Object> pushParams = new TreeMap<>();
		Merchant merchant = queryCurrMerchant((String)params.get("rechargeType"));
		
		if(merchant == null) {
			return null;
		}
		pushParams.put("versionId", versionId);
		pushParams.put("orderAmount", numFormat.format(((Float)params.get("amount"))*100));
		pushParams.put("orderDate", params.get("createTime"));
		pushParams.put("currency", currency);
		if(params.get("accNoType") != null) {
			pushParams.put("accNoType", params.get("accNoType"));
		}
		pushParams.put("accountType", params.get("accountType"));
		pushParams.put("transType", transType);
		pushParams.put("asynNotifyUrl", params.get("asynNotifyURL"));
		pushParams.put("synNotifyUrl", params.get("synNotifyURL"));
		if(params.get("bankCardNo") != null) {
			pushParams.put("bankCardNo", (String)params.get("bankCardNo"));
		}
		if(params.get("userName") != null) {
			pushParams.put("userName", (String)params.get("userName"));
		}
		if(params.get("phone") != null) {
			pushParams.put("phone", (String)params.get("phone"));
		}
		if(params.get("idNo") != null) {
			pushParams.put("idNo", (String)params.get("idNo"));
		}
		if(params.get("expireDate") != null) {
			pushParams.put("expireDate", (String)params.get("expireDate"));
		}
		if(params.get("cvn2") != null) {
			pushParams.put("cvn2", (String)params.get("cvn2"));
		}
		pushParams.put("signType", signType);
		pushParams.put("merId", merchant.getMerId());
		pushParams.put("prdOrdNo", String.valueOf(depositOrder.getOrderNum()));
		pushParams.put("payMode", (String)params.get("rechargeType"));
		pushParams.put("tranChannel", (String)params.get("tranChannel"));
		pushParams.put("receivableType", merchant.getReceivableType());
		pushParams.put("prdAmt", numFormat.format(1));
		pushParams.put("prdName", "lottery");
		pushParams.put("prdDesc", "lottery");
		pushParams.put("pnum", "1");
		String sign = MD5Signature.createSign(pushParams, merchant.getKey());
		if(sign == null || sign.length() == 0) {
			return null;
		}
		
		depositOrder.setPlatAccount(merchant.getMerId());
		supserDao.update(depositOrder);
		
		pushParams.put("signData", sign);
		return pushParams;
	}
	
	
	@Override
	public String processScanPay(Map<String, Object> params) {
		
		Date createTime = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		URI url = null;
		boolean isSuccess = true;
		SortedMap<String, Object> pushParams = new TreeMap<>();
		Map<String, String> reqHeaders = new HashMap<>();
		
		params.put("createTime", createTime);
		reqHeaders.put("Content-Type", "application/x-www-form-urlencoded");
		params.put("createTime", format.format(createTime));
		String reqHost = (String)params.get("reqHost");
		String reqContext = (String)params.get("reqContext");
		params.put("asynNotifyURL", scanPayAsynNOtifyURL.replace("{host}", reqHost).replace("{context}", reqContext));
		params.put("synNotifyURL", scanPaySynNOtifyURL.replace("{host}", reqHost).replace("{context}", reqContext));
		pushParams = produceParamsOfScanQRPay(params);
		if(pushParams == null || pushParams.size() == 0) {
			return Message.Error.ERROR_PAYMENT_CAIPAY_FAILED_SIGNATURE_PARAMS.getCode();
		}
		 try {
		 	url = new URI(apiServer + apiScanQRPay);
		 } catch (URISyntaxException e) {
		 	return Message.Error.ERROR_PAYMENT_CAIPAY_FAILED_CANCEL_ORDER.getCode();
		 }

		 Map<String, Object> response = HttpRemoteStub.synPost(url, reqHeaders, pushParams);
		 isSuccess = isResponseSuccess(response);
		 logger.debug("If request successful::::" + isSuccess);
		// depositOrder.setOrderNumber(String.valueOf(depositOrder.getRecordID()));
		 if(isSuccess) {
			// depositOrderDao.updateDepositOrder(depositOrder);
			 String qrcode = null;
			 String qrCodeKey = "qrcode";
			 String jsonStr = (String)response.get("responseBody");
			 qrcode = readJSON(qrCodeKey, jsonStr);
			 params.put("qrcode", qrcode);
		 	return String.valueOf(Message.status.SUCCESS.getCode());
		 }else {
			 //failed to push 
			 //depositOrder.setStatus(Constants.DepositOrderState.FAILED_PUSH.getCode());
			 //depositOrderDao.updateDepositOrder(depositOrder);
		 	return Message.Error.ERROR_PAYMENT_TLCLOUD_FAILED_PUSH_ORDER.getCode();
		 }
	}


	private String readJSON(String qrCodeKey, String jsonStr) {
		ObjectMapper mapper = new ObjectMapper();
		String qrcode = null;
		 try {
			 JsonNode node = mapper.readTree(jsonStr);
			 qrcode = node.findValue(qrCodeKey).asText();
			 //
		} catch (JsonProcessingException e) {
		} catch (IOException e) {                    			
		}
		return qrcode;
	}


	@Override
	public String processOnlineBankPay(Map<String, Object> params) {
		Date createTime = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		URI url = null;
		boolean isSuccess = true;
		SortedMap<String, Object> pushParams = new TreeMap<>();
		Map<String, String> reqHeaders = new HashMap<>();
		
		params.put("createTime", createTime);
		DepositApplication depositOrder = (DepositApplication)params.get("depositOrder");
		reqHeaders.put("Content-Type", "application/x-www-form-urlencoded");
		params.put("createTime", format.format(createTime));
		String reqHost = (String)params.get("reqHost");
		String reqContext = (String)params.get("reqContext");
		params.put("asynNotifyURL", onlineBankPayAsynNotifyUrl.replace("{host}", reqHost).replace("{context}", reqContext));
		params.put("synNotifyURL", onlineBankPaysynNotifyUrl.replace("{host}", reqHost).replace("{context}", reqContext));
		
		pushParams = produceParamsOfOnlinePay(params);
		if(pushParams == null || pushParams.size() == 0) {
			return Message.Error.ERROR_PAYMENT_CAIPAY_FAILED_SIGNATURE_PARAMS.getCode();
		}
		 try {
		 	url = new URI(apiServer + apiOnLineBankPay);
		 } catch (URISyntaxException e) {
		 	return Message.Error.ERROR_PAYMENT_CAIPAY_FAILED_CANCEL_ORDER.getCode();
		 }

		 Map<String, Object> response = HttpRemoteStub.synPost(url, reqHeaders, pushParams);
		    
		 isSuccess = isOnlineBankResponseSuccess(response);
		 
		 String redirect = (String)response.get("responseBody");
		 
		 params.put("redirect", redirect);
		 
		 if(isSuccess) {
			// depositOrderDao.updateDepositOrder(depositOrder);
		 	return String.valueOf(Message.status.SUCCESS.getCode());
		 }else {
			 //failed to push 
			 //depositOrder.setStatus(Constants.DepositOrderState.FAILED_PUSH.getCode());
			// depositOrderDao.updateDepositOrder(depositOrder);
		 	return Message.Error.ERROR_PAYMENT_TLCLOUD_FAILED_PUSH_ORDER.getCode();
		 }
	}


	@Override
	public String receiveNoties(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isValid(Map<String, Object> params) {
		String payMode = (String)params.get("rechargeType");
		if("00023".equals(payMode)) {
			return verifyQuickPayment(params);
		}
		return true;
	}
	
	private boolean verifyQuickPayment(Map<String, Object> params) {
		String bankCardNo = (String)params.get("bankCardNo");
		String userName = (String)params.get("userName");
		String phone = (String)params.get("phone");
		String idNo = (String)params.get("idNo");
		String expireDate = (String)params.get("expireDate");
		String CVn2 = (String)params.get("CVn2");
		if(bankCardNo == null || bankCardNo.length() == 0
				|| userName == null || userName.length() == 0
				|| phone == null || phone.length() == 0
				|| CVn2 == null || CVn2.length() == 0
				|| idNo == null || idNo.length() == 0
				|| expireDate == null || expireDate.length() == 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isNoticesValid(CaiPayNotices notices) {
		String sign = null;
		SortedMap<String, Object> pushParams = new TreeMap<>();
		pushParams.put("versionId", notices.getVersionId());
		pushParams.put("orderAmount", notices.getOrderAmount());
		pushParams.put("asynNotifyUrl", notices.getAsynNotifyUrl());
		pushParams.put("synNotifyUrl", notices.getSynNotifyUrl());
		pushParams.put("signType", notices.getSignType());
		pushParams.put("merId", notices.getMerId());
		pushParams.put("orderStatus", notices.getOrderStatus());
		pushParams.put("payId", notices.getPayId());
		pushParams.put("payTime", notices.getPayTime());
		pushParams.put("prdOrdNo", notices.getPrdOrdNo());
		pushParams.put("transType", notices.getTransType());
		
		DepositApplication depositOrder = depositOrderDao.queryDepositOrderById(notices.getPrdOrdNo());
		if(null == depositOrder
				|| BigDecimalUtil.div(Double.valueOf(notices.getOrderAmount()), 100.00,2) != depositOrder.getAmount()){
			return false;
		}
		PayChannel pcInfo = cacheRedisService.getPayChannel(Constants.PayChannel.PAY_CHANNEL.getCode(), depositOrder.getPayChannel());
		
		String channelCode = pcInfo.getChannelName();
		Merchant merchant = queryCurrMerchant(channelCode);
		sign = MD5Signature.createSign(pushParams, merchant.getKey());
		if(sign == null || sign.length() == 0
				|| notices.getSignData() == null
				|| notices.getSignData().length() == 0) {
			return false;
		}
		
		return sign.equals(notices.getSignData());
	}
	
	
	private Merchant queryCurrMerchant(String payMode) {
		for(Merchant mer : merchants) {
			if(mer.getPayModes().contains(payMode)) {
				return mer;
			}
		}
		return null;
	}
	
	class Merchant{
		private String merId;
		
		private String key;
		
		private List<String> payModes;
		
		private String receivableType;

		public String getMerId() {
			return merId;
		}

		public void setMerId(String merId) {
			this.merId = merId;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public List<String> getPayModes() {
			return payModes;
		}

		public void setPayModes(List<String> payModes) {
			this.payModes = payModes;
		}

		public String getReceivableType() {
			return receivableType;
		}

		public void setReceivableType(String receivableType) {
			this.receivableType = receivableType;
		}
		
		
	}

}

package com.jll.pay.zhihpay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jll.common.constants.Message;
import com.jll.common.http.HttpRemoteStub;
import com.jll.common.utils.RSAUtils;
import com.jll.common.utils.StringUtils;
import com.jll.dao.SupserDao;
import com.jll.entity.DepositApplication;
import com.jll.entity.display.ZhihPayNotices;
import com.jll.pay.PaymentDao;
import com.jll.pay.order.DepositOrderDao;

@Configuration
@PropertySource("classpath:zhih-pay.properties")
@Service
@Transactional
public class ZhihPayServiceImpl implements ZhihPayService
{
	private Logger logger = Logger.getLogger(ZhihPayServiceImpl.class);
	/*private final String FAILED_CODE = "1";
	
	private final String SUCCESS_CODE = "0";*/
	
	//private final String FAILED_CODE_ONLINE_PAY = "1";
	
	private final String SUCCESS_CODE = "SUCCESS";
	
	@Resource
	ZhihPayDao tlCloudDao;
	
	@Resource
	PaymentDao payDao;
	
	@Resource
	DepositOrderDao depositOrderDao;
	
	@Resource
	SupserDao supserDao;
		  
	@Value("${api.server}")
	private String apiServer;
	  
	@Value("${api.scan_pay}")
	private String apiScanQRPay;
	  
	@Value("${api.online_bank_pay}")
	private String apiOnLineBankPay;
	  
	@Value("${cons.versionId}")
	private String versionId;
		
	@Value("${cons.versionId.onlinebank}")
	private String onlineBankVersionId;
	
	@Value("${cons.signType}")
	private String signType;
	
	@Value("${api.scan_pay.notify_url}")
	private String scanPayAsynNOtifyURL;
	
	@Value("${api.online_bank_pay.notify_url}")
	private String onlineBankPayAsynNotifyUrl;	
	
	@Value("${merchant.merId}")
	private String merchantMerId;
	
	@Value("${merchant.key}")
	private String merchantKey;
	
	@Value("${merchant.public_key}")
	private String merchantPubKey;	
	
	@Value("${zhihf_pay.public_key}")
	private String zhihfPayPubKey;
	
	private boolean isResponseSuccess(Map<String, Object> response) {
		Map<String, String> resMap = new HashMap<>();
		if(response.size() == 0) {
			logger.debug("Can't read response from the zhih-pay server!!!");
			return false;
		}
		
		int status = (int)response.get("status");
		if(status != HttpStatus.SC_OK) {
			return false;
		}else {
			String body = (String)response.get("responseBody");
			
			logger.debug("the response is ::: " + (body == null?"":body));
			
			ByteArrayInputStream bis = null;
			if(body != null && body.length() > 0) {
				try {
					SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
					bis = new ByteArrayInputStream(body.getBytes("utf-8"));
					saxParser.parse(bis, new DefaultHandler() {
						private String key = null;
						private String val = null;
						public void startElement (String uri, String localName,
	                              String qName, Attributes attributes)
					        throws SAXException
					    {
							key = qName;
							val = null;
					    }
						
						public void characters (char ch[], int start, int length)
						        throws SAXException
						{
							if(val != null) {
								val = val + new String(ch, start, length);								
							}else {
								val = new String(ch, start, length);
							}
						}
						
						public void endElement (String uri, String localName, String qName)
						        throws SAXException
						{
							resMap.put(key, val);
						}
					});
					
					if(resMap.size() == 0) {
						logger.debug("can't read the response!!!");
						return false;
					}
					
					String respCode = resMap.get("resp_code");
					if(respCode == null) {
						logger.debug("retCode is null");
						return false;
					}
					
					
					if(respCode.equals(SUCCESS_CODE)) {
						return true;
					}
					
					return false;
				} catch (IOException | ParserConfigurationException | SAXException e) {
					e.printStackTrace();
					return false;
				}finally {
					if(bis != null) {
						try {
							bis.close();
						} catch (IOException e) {
						}
					}
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
				
		DecimalFormat numFormat = new DecimalFormat("##0.00");
		DepositApplication depositOrder = (DepositApplication)params.get("depositOrder");
		SortedMap<String, Object> pushParams = new TreeMap<>();
		StringBuffer buffer = new StringBuffer();
		
		pushParams.put("merchant_code", merchantMerId);
		pushParams.put("service_type", (String)params.get("rechargeType"));
		pushParams.put("notify_url", params.get("asynNotifyURL"));
		pushParams.put("interface_version", versionId);
		//pushParams.put("client_ip", params.get("reqIP"));
		pushParams.put("client_ip", "121.96.59.56");
		pushParams.put("sign_type", signType);
		pushParams.put("order_no", String.valueOf(depositOrder.getOrderNum()));
		pushParams.put("order_time", params.get("createTime"));
		pushParams.put("order_amount", numFormat.format(params.get("amount")));
		pushParams.put("product_name", "lottery");
		Iterator<String> keys = pushParams.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			Object valObj = pushParams.get(key);
			String v = "";
			if(valObj.getClass().getName().equals("java.lang.String")) {
				v = (String)valObj;
			}else if(valObj.getClass().getName().equals("java.lang.Integer")) {
				v = String.valueOf(((Integer)valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Float")) {
				v = String.valueOf(((Float) valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Long")) {
				v = String.valueOf(((Long) valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Boolean")) {
				v = ((Boolean) valObj).toString();
			}
			if (StringUtils.isNotEmpty(v) && !"sign".equals(key) && !"sign_type".equals(key)) {
				buffer.append(key + "=" + v + "&");
			}
		}
		
		if(buffer.toString().endsWith("&")) {
			buffer.deleteCharAt(buffer.length()-1);
		}
		String sign = null;
		try {
			sign = RSAUtils.signByPrivateKey(buffer.toString(), merchantKey);
		} catch (Exception e) {
			
		}
		if(sign == null || sign.length() == 0) {
			return null;
		}
		depositOrder.setPlatAccount(merchantMerId);
		supserDao.update(depositOrder);
		pushParams.put("sign", sign);
		return pushParams;
	}

	private SortedMap<String, Object> produceParamsOfOnlinePay(Map<String, Object> params){
		DecimalFormat numFormat = new DecimalFormat("##0.00");
		DepositApplication depositOrder = (DepositApplication)params.get("depositOrder");
		SortedMap<String, Object> pushParams = new TreeMap<>();
		StringBuffer buffer = new StringBuffer();
		
		pushParams.put("merchant_code", merchantMerId);
		pushParams.put("service_type", (String)params.get("rechargeType"));
		pushParams.put("notify_url", params.get("asynNotifyURL"));
		//pushParams.put("notify_url", "zhmyb.top");
		pushParams.put("interface_version", onlineBankVersionId);
		//pushParams.put("client_ip", params.get("reqIP"));
		//pushParams.put("client_ip", "121.96.59.56");
		//pushParams.put("client_ip_check", 0);
		pushParams.put("sign_type", signType);
		pushParams.put("order_no", String.valueOf(depositOrder.getOrderNum()));
		pushParams.put("order_time", params.get("createTime"));
		pushParams.put("order_amount", numFormat.format(params.get("amount")));
		pushParams.put("product_name", "lottery");
		pushParams.put("redo_flag", 1);
		pushParams.put("return_url", "http://jll2019.com");
		pushParams.put("input_charset", "UTF-8");
		Iterator<String> keys = pushParams.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			Object valObj = pushParams.get(key);
			String v = "";
			if(valObj == null) {
				continue;
			}
			if(valObj.getClass().getName().equals("java.lang.String")) {
				v = (String)valObj;
			}else if(valObj.getClass().getName().equals("java.lang.Integer")) {
				v = String.valueOf(((Integer)valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Float")) {
				v = String.valueOf(((Float) valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Long")) {
				v = String.valueOf(((Long) valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Boolean")) {
				v = ((Boolean) valObj).toString();
			}
			if (StringUtils.isNotEmpty(v) && !"sign".equals(key) && !"sign_type".equals(key)) {
				buffer.append(key + "=" + v + "&");
			}
		}
		
		if(buffer.toString().endsWith("&")) {
			buffer.deleteCharAt(buffer.length()-1);
		}
		String sign = null;
		try {
			sign = RSAUtils.signByPrivateKey(buffer.toString(), merchantKey);
		} catch (Exception e) {
			
		}
		if(sign == null || sign.length() == 0) {
			return null;
		}
		
		logger.debug("the signature items:" + buffer.toString() +"   signature::" + sign + "  length of signature:" + sign.length());
		
		pushParams.put("sign", sign);
		return pushParams;
	}
	
	@Override
	public String processScanPay(Map<String, Object> params) {
		
		Date createTime = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		URI url = null;
		boolean isSuccess = true;
		SortedMap<String, Object> pushParams = new TreeMap<>();
		Map<String, String> reqHeaders = new HashMap<>();
		
		params.put("createTime", createTime);
//		DepositApplication depositOrder = (DepositApplication)params.get("depositOrder");
		reqHeaders.put("Content-Type", "application/x-www-form-urlencoded");
		//reqHeaders.put("Content-Type", "application/json");
		params.put("createTime", format.format(createTime));
		String reqHost = (String)params.get("reqHost");
		String reqContext = (String)params.get("reqContext");
		params.put("asynNotifyURL", scanPayAsynNOtifyURL.replace("{host}", reqHost).replace("{context}", reqContext));
		
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
		 if(isSuccess) {
//			 depositOrderDao.updateDepositOrder(depositOrder);
			 String qrcode = null;
			 String qrCodeKey = "qrcode";
			 String jsonStr = (String)response.get("responseBody");
			 qrcode = readQrcode(qrCodeKey, jsonStr);
			 params.put("qrcode", qrcode);
		 	return String.valueOf(Message.status.SUCCESS.getCode());
		 }else {
			 //failed to push 
//			 depositOrder.setStatus(Constants.DepositOrderState.FAILED_PUSH.getCode());
//			 depositOrderDao.updateDepositOrder(depositOrder);
		 	return Message.Error.ERROR_PAYMENT_TLCLOUD_FAILED_PUSH_ORDER.getCode();
		 }
	}


	private String readQrcode(String qrCodeKey, String xmlSeg) {
		Map<String, String> resMap = new HashMap<>();
						
		ByteArrayInputStream bis = null;
		if(xmlSeg != null && xmlSeg.length() > 0) {
			try {
				SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
				bis = new ByteArrayInputStream(xmlSeg.getBytes("utf-8"));
				saxParser.parse(bis, new DefaultHandler() {
					private String key = null;
					private String val = null;
					public void startElement (String uri, String localName,
                              String qName, Attributes attributes)
				        throws SAXException
				    {
						key = qName;
						val = null;
				    }
					
					public void characters (char ch[], int start, int length)
					        throws SAXException
					{
						if(val != null) {
							val = val + new String(ch, start, length);								
						}else {
							val = new String(ch, start, length);
						}
					}
					
					public void endElement (String uri, String localName, String qName)
					        throws SAXException
					{
						resMap.put(key, val);
					}
				});
				
				if(resMap.size() == 0) {
					logger.debug("can't read the response!!!");
					return null;
				}
				
				String qrCode = resMap.get(qrCodeKey);
				
				return qrCode;
			} catch (IOException | ParserConfigurationException | SAXException e) {
				e.printStackTrace();
				return null;
			}finally {
				if(bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
					}
				}
			}			
		}
		return null;	
		
	}


	@Override
	public String processOnlineBankPay(Map<String, Object> params) {
		Date createTime = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//URI url = null;
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
		
		pushParams = produceParamsOfOnlinePay(params);
		if(pushParams == null || pushParams.size() == 0) {
			return Message.Error.ERROR_PAYMENT_CAIPAY_FAILED_SIGNATURE_PARAMS.getCode();
		}
		
		params.clear();
		Iterator<String> ite = pushParams.keySet().iterator();
		while(ite.hasNext()) {
			String key = ite.next();
			Object val = pushParams.get(key);
			
			params.put(key, val);
		}
		URI url = null;
		 try {
		 	url = new URI(apiOnLineBankPay);
		 } catch (URISyntaxException e) {
		 	return Message.Error.ERROR_PAYMENT_CAIPAY_FAILED_CANCEL_ORDER.getCode();
		 }

		 
		 String res ="";
			try {
				res ="<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
				+"<html><head><title>To Pay</title>"
				+"<style> .tabPages{margin-top:150px;text-align:center;display:block; border:3px solid #d9d9de; padding:30px; font-size:14px;}</style>"
				+"</head>"
				+"<body onLoad=\"document.uncome.submit()\">"
				+"<div id=\"Content\"><div class=\"tabPages\">我们正在为您连接银行，请稍等......</div></div>"
				+"<form name=\"uncome\" action=\""+apiOnLineBankPay+"\" method=\"post\">";
				
				for (String mapkey:pushParams.keySet()) {
					res +="<input type=\"hidden\" name=\""+mapkey+"\"  value=\""+pushParams.get(mapkey)+"\">";
					
				}
				
				res +="</form></body></html>";
				//res = post(jumpUrl,params);
				System.out.println("res:" + res);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		 
		 Map<String, Object> response = HttpRemoteStub.synPost(url, reqHeaders, pushParams);
		 
		 logger.debug("response from server :: " + (response.get("responseBody") == null ?"":(String)response.get("responseBody")));
		 isSuccess = isOnlineBankResponseSuccess(response);
		 
		 String redirect = (String)response.get("responseBody");
		 
		 params.put("redirect", redirect);
		 
		 if(isSuccess) {
//			 depositOrderDao.updateDepositOrder(depositOrder);
		 	return String.valueOf(Message.status.SUCCESS.getCode());
		 }else {
			 //failed to push 
//			 depositOrder.setStatus(Constants.DepositOrderState.FAILED_PUSH.getCode());
//			 depositOrderDao.updateDepositOrder(depositOrder);
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
	public boolean isNoticesValid(ZhihPayNotices notices) {
		String sign = null;
		SortedMap<String, Object> pushParams = new TreeMap<>();
		StringBuffer buffer = new StringBuffer();
		pushParams.put("bank_seq_no", notices.getBank_seq_no());
		pushParams.put("extra_return_param", notices.getExtra_return_param());
		pushParams.put("interface_version", notices.getInterface_version());
		pushParams.put("merchant_code", notices.getMerchant_code());
		pushParams.put("notify_id", notices.getNotify_id());
		pushParams.put("notify_type", notices.getNotify_type());
		pushParams.put("order_amount", notices.getOrder_amount());
		pushParams.put("order_no", notices.getOrder_no());
		pushParams.put("order_time", notices.getOrder_time());
		pushParams.put("orginal_money", notices.getOrginal_money());
		pushParams.put("trade_no", notices.getTrade_no());
		pushParams.put("trade_status", notices.getTrade_status());
		pushParams.put("trade_time", notices.getTrade_time());
		
		DepositApplication depositOrder = depositOrderDao.queryDepositOrderById(notices.getOrder_no());
		if(null == depositOrder
				|| Double.valueOf(notices.getOrder_amount()).floatValue() != depositOrder.getAmount()){
			return false;
		}
		
		Iterator<String> keys = pushParams.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			Object valObj = pushParams.get(key);
			String v = "";
			if(valObj == null) {
				continue;
			}
			if(valObj.getClass().getName().equals("java.lang.String")) {
				v = (String)valObj;
			}else if(valObj.getClass().getName().equals("java.lang.Integer")) {
				v = String.valueOf(((Integer)valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Float")) {
				v = String.valueOf(((Float) valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Long")) {
				v = String.valueOf(((Long) valObj));
			}else if(valObj.getClass().getName().equals("java.lang.Boolean")) {
				v = ((Boolean) valObj).toString();
			}
			if (StringUtils.isNotEmpty(v) && !"sign".equals(key) && !"sign_type".equals(key)) {
				buffer.append(key + "=" + v + "&");
			}
		}
		
		if(buffer.toString().endsWith("&")) {
			buffer.deleteCharAt(buffer.length()-1);
		}
		
		logger.debug("the sign parameters :: " + buffer.toString() +"  notices sign :: " + notices.getSign());
		logger.debug("public key :: " + zhihfPayPubKey);
		try {
			return RSAUtils.validateSignByPublicKey(buffer.toString(), zhihfPayPubKey, notices.getSign());
		} catch (Exception e) {
			return false;
		}	
		
	}
	
}

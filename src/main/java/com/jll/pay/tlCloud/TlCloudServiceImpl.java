package com.jll.pay.tlCloud;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.http.HttpRemoteStub;
import com.jll.entity.DepositApplication;
import com.jll.pay.PaymentDao;
import com.jll.pay.order.DepositOrderDao;

@Configuration
@PropertySource("classpath:tonglueCloud.properties")
@Service
@Transactional
public class TlCloudServiceImpl implements TlCloudService
{
	@Resource
	TlCloudDao tlCloudDao;
	
	@Resource
	PaymentDao payDao;
	
	@Resource
	DepositOrderDao depositOrderDao;
	
	
	
	@Value("${auth.api-key}")
	private String apiKey;
	  
	@Value("${api.server1}")
	private String apiServer;
	  
	@Value("${api.place_order}")
	private String apiPlaceOrder;
	  
	@Value("${api.revoke_order}")
	private String apiRevokeOrder;
	  
	@Value("${api.list_order}")
	private String apiListOrder;
  

	@Override
	public String saveDepositOrder(Map<String, Object> params) {
		Date createTime = new Date();
		String rechargeType = (String)params.get("rechargeType");
		float amount = (Float)params.get("amount");
		String payCardNumber = (String)params.get("payCardNumber");
		String payerName = (String)params.get("payerName");
		
		Map<String, Object> pushParams = new HashMap<>();
		Map<String, String> reqHeaders = new HashMap<>();
		URI url = null;
		boolean isSuccess = true;
		
		DepositApplication depositOrder = (DepositApplication)params.get("depositOrder");
		
		reqHeaders.put("Content-Type", "application/json");
		pushParams.put("pay_card_number", payCardNumber);
		pushParams.put("pay_username", payerName);
		pushParams.put("order_id",depositOrder.getOrderNum());
		pushParams.put("bank_flag", rechargeType);
		pushParams.put("card_number", params.get("payCardNumber").toString());
		pushParams.put("amount", amount);
		pushParams.put("create_time", createTime);
		pushParams.put("comment", depositOrder.getOrderNum());
		pushParams.put("apikey", apiKey);
		 
		 try {
		 	url = new URI(apiServer + apiPlaceOrder);
		 } catch (URISyntaxException e) {
		 	return Message.Error.ERROR_PAYMENT_TLCLOUD_CONFIGURATION.getCode();
		 }

		 Map<String, Object> response = HttpRemoteStub.synPost(url, reqHeaders, pushParams);
		    
		 isSuccess = isResponseSuccess(response);
		 
		 if(isSuccess) {
		 	return String.valueOf(Message.status.SUCCESS.getCode());
		 }else {
		 	return Message.Error.ERROR_PAYMENT_TLCLOUD_FAILED_PUSH_ORDER.getCode();
		 }
	}


	@Override
	public String cancelOrder(String orderId) {
		Map<String,Object> params = new HashMap<>();
		Map<String, String> reqHeaders = new HashMap<>();
		reqHeaders.put("Content-Type", "application/json");
		URI url;
		 try {
		 	url = new URI(apiServer + apiRevokeOrder);
		 } catch (URISyntaxException e) {
		 	return Message.Error.ERROR_PAYMENT_TLCLOUD_CONFIGURATION.getCode();
		 }

		 params.put("apikey", apiKey);
		 params.put("id", orderId);
		 
		 Map<String, Object> response = HttpRemoteStub.synPost(url, reqHeaders, params);
		    
		 boolean isSuccess = isResponseSuccess(response);
		    
		 if(isSuccess) {
			 DepositApplication depositOrder = depositOrderDao.queryDepositOrderById(orderId);
			 depositOrder.setState(Constants.DepositOrderState.CANCEL_ORDER.getCode());
			 depositOrderDao.updateDepositOrder(depositOrder);
		 	return String.valueOf(Message.status.SUCCESS.getCode());
		 }else {
		 	return Message.Error.ERROR_PAYMENT_TLCLOUD_FAILED_PUSH_ORDER.getCode();
		 }
	}
	
	private boolean isResponseSuccess(Map<String, Object> response) {
		int status = (int)response.get("status");
		if(status != HttpStatus.SC_OK) {
			return false;
		}else {
			String body = (String)response.get("responseBody");
			if(body != null && body.length() > 0) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					JsonNode node = mapper.readTree(body.getBytes());
					if(node == null) {
						return false;
					}
					
					node = node.get("success");
					if(node == null) {
						return false;
					}
					
					boolean isSuccess = node.asBoolean();
					return isSuccess;
				} catch (IOException e) {
					return false;
				}
			}
		}
		return false;
	}
}

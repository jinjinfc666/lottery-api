package com.jll.tp;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jll.common.cache.CacheObject;
import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Message;
import com.jll.common.http.HttpRemoteStub;
import com.jll.common.utils.Utils;
import com.jll.entity.UserInfo;

@Configuration
@PropertySource(value = {"classpath:sms-ucpaas.properties","classpath:sys-setting.properties"})
@Service
@Transactional
public class SMSServiceImpl implements SMSService
{
	private Logger logger = Logger.getLogger(SMSServiceImpl.class);

	@Resource
	CacheRedisService cacheService;
	
	@Value("${sms.appid}")
	private String appid;
	
	@Value("${sms.account_sid}")
	private String accountSid;
	
	@Value("${sms.auth_token}")
	private String authToken;
	
	@Value("${sms.rest_url}")
	private String restUrl;
	
	@Value("${sms.template_id}")
	private String templateId;
	
	@Value("${sys_captcha_code_expired_time}")
	private int captchaCodeExpiredTime;
	
	private String keyStatusCode = "code";
	
	private final String VAL_STATUS_SUCESS = "000000";
	
	@Override
	public String sending6digitsNumbers(String phoneNum) {
		Map<String, Object> params = new HashMap<>();
		Map<String, String> headers = new HashMap<>();
		Map<String, Object> ret = null;
		
		String valStatusCode = null;
		
		String captchaCode = Utils.produce6DigitsCaptchaCodeNumber();
		URI uri = null;
		try {
			uri = new URI(restUrl + "/sendsms");
		} catch (URISyntaxException e) {
			logger.error(Message.Error.ERROR_TP_INVALID_SMS_URL.getCode() +":"+Message.Error.ERROR_TP_INVALID_SMS_URL.getErrorMes());
			return Message.Error.ERROR_TP_INVALID_SMS_URL.getCode();
		}
		
		
		headers.put("Content-Type", "application/json");
		params.put("sid", accountSid);
		params.put("token", authToken);
		params.put("appid", appid);
		params.put("templateid", templateId);
		params.put("param", captchaCode);
		params.put("mobile", phoneNum);
		//jsonObject.put("uid", uid);
		ret = HttpRemoteStub.synPost(uri, headers, params);
		String jsonStr = (String)ret.get("responseBody");
		valStatusCode = readJSON(keyStatusCode, jsonStr);
		/*if(ret == null || ret.size() == 0 
				|| valStatusCode == null 
				|| !VAL_STATUS_SUCESS.equals(valStatusCode)) {
			return Integer.toString(Message.status.FAILED.getCode());
		}*/
		
		logger.debug("captchaCode::::::::::::::::" + captchaCode);
		cacheService.setCaptchaCode(captchaCode, captchaCodeExpiredTime);
		return Integer.toString(Message.status.SUCCESS.getCode());
	}

	@Override
	public boolean isSmsValid(UserInfo user, String sms) {
		CacheObject<String> cacheObject = cacheService.getCaptchaCode(sms);
		if(cacheObject == null
				|| StringUtils.isBlank(cacheObject.getContent())) {
			return false;
		}
		
		if(!cacheObject.getContent().equals(sms)) {
			return false;
		}
		return true;
	}
	
	private String readJSON(String Key, String jsonStr) {
		ObjectMapper mapper = new ObjectMapper();
		String value = null;
		 try {
			 JsonNode node = mapper.readTree(jsonStr);
			 value = node.findValue(Key).asText();
			 //
		} catch (JsonProcessingException e) {
		} catch (IOException e) {                    			
		}
		return value;
	}
	
}

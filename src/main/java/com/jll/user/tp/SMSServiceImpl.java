package com.jll.user.tp;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Message;
import com.jll.common.http.HttpRemoteStub;
import com.jll.common.utils.Utils;
import com.jll.entity.UserInfo;

@Configuration
@PropertySource("classpath:sms-ucpaas.properties")
@Service
@Transactional
public class SMSServiceImpl implements SMSService
{
	private Logger logger = Logger.getLogger(SMSServiceImpl.class);

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
	
	private final String KEY_STATUS = "code";
	
	private final String VAL_SUCESS = "0";
	
	@Override
	public String sending6digitsNumbers(String phoneNum) {
		Map<String, Object> params = new HashMap<>();
		Map<String, String> headers = new HashMap<>();
		Map<String, Object> ret = null;
		
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
		
		if(ret == null || ret.size() == 0 
				|| ret.get(KEY_STATUS) == null 
				|| !VAL_SUCESS.equals((String)ret.get(KEY_STATUS))) {
			return Integer.toString(Message.status.FAILED.getCode());
		}
		
		return Integer.toString(Message.status.SUCCESS.getCode());
	}

	@Override
	public boolean isSmsValid(UserInfo user, String sms) {
		// TODO Auto-generated method stub
		return false;
	}
	
}

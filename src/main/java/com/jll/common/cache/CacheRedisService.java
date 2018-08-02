package com.jll.common.cache;

import java.util.Map;

public interface CacheRedisService {

	/**
	 * store the captcha code into cache, and set the expired time is captchaCodeExpiredTime
	 * @param captchaCode
	 * @param captchaCodeExpiredTime
	 */
	void setCaptchaCode(String captchaCode, int captchaCodeExpiredTime);

	CacheObject<String> getCaptchaCode(String sms);
	
	void setSysCode(String codeName);
	
	CacheObject<Map> getSysCode(String sms);
	
}

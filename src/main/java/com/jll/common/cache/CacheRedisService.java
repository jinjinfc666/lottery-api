package com.jll.common.cache;

public interface CacheRedisService {

	/**
	 * store the captcha code into cache, and set the expired time is captchaCodeExpiredTime
	 * @param captchaCode
	 * @param captchaCodeExpiredTime
	 */
	void setCaptchaCode(String captchaCode, int captchaCodeExpiredTime);

	CacheObject<String> getCaptchaCode(String sms);

}

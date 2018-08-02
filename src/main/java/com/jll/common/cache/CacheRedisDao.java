package com.jll.common.cache;

import java.util.Map;

public interface CacheRedisDao {

	/**
	 * store the captcha code into redis
	 * @param cacheObj
	 */
	void setCaptchaCode(CacheObject<String> cacheObj);

	CacheObject<String> getCaptchaCode(String sms);
	
	void setSysCode(CacheObject<Map> cacheObj);
	
	CacheObject<Map> getSysCode(String codeName);
	
}

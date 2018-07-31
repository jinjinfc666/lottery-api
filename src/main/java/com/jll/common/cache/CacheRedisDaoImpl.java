package com.jll.common.cache;

import org.springframework.stereotype.Repository;

@Repository
public class CacheRedisDaoImpl 
	extends AbstractBaseRedisDao
		implements CacheRedisDao{

	@Override
	public void setCaptchaCode(CacheObject<String> cacheObj) {
		this.saveOrUpdate(cacheObj);
	}

	@Override
	public CacheObject<String> getCaptchaCode(String sms) {
		CacheObject<String> cacheObject = get(sms);
		return cacheObject;
	}

}

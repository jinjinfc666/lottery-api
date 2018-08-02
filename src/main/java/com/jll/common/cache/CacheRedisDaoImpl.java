package com.jll.common.cache;

import java.util.Map;

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

	@Override
	public void setSysCode(CacheObject<Map> cacheObj) {
		this.saveOrUpdate(cacheObj);
	}

	@Override
	public CacheObject<Map> getSysCode(String codeName) {
		CacheObject<Map> cacheObject = get(codeName);
		return cacheObject;
	}

}

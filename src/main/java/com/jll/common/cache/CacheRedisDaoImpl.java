package com.jll.common.cache;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jll.entity.Issue;

@Repository
public class CacheRedisDaoImpl  extends AbstractBaseRedisDao implements CacheRedisDao{

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
	public void setPlan(String cacheKey, List<Issue> issues) {
		CacheObject<List<Issue>> cache = new CacheObject<>();
		cache.setContent(issues);
		cache.setKey(cacheKey);
		this.saveOrUpdate(cache);
	}

	@Override
	public List<Issue> getPlan(String cacheKey) {
		CacheObject<List<Issue>> cache = this.get(cacheKey);
		
		return cache.getContent();
	}

}

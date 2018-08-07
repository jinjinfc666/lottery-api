package com.jll.common.cache;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.jll.entity.Issue;
import com.jll.entity.SysCode;
import com.jll.game.BulletinBoard;

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
		if(cache == null) {
			return null;
		}
		return cache.getContent();
	}
	
	@Override
	public void setSysCode(CacheObject<Map<String, Map<String, SysCode>>> cacheObj) {
		this.saveOrUpdate(cacheObj);
	}

	
	@Override
	public CacheObject<Map<String, SysCode>> getSysCode(String codeName) {
		CacheObject<Map<String, SysCode>> cacheObject = get(codeName);
		return cacheObject;
	}

	@Override
	public CacheObject<BulletinBoard> getBulletinBoard(String keyBulletinBoard) {
		return get(keyBulletinBoard);
	}

	@Override
	public void setBulletinBoard(CacheObject<BulletinBoard> cache) {
		this.saveOrUpdate(cache);
	}

	@Override
	public SysCode getSysCode(String codeTypeName, String codeName) {
		CacheObject<Map<String, SysCode>> cacheObject=get(codeTypeName);
		if(cacheObject==null) {
			return null;
		}
		Map<String,SysCode> map=cacheObject.getContent();
		SysCode sysCode=map.get(codeName); 
		return sysCode;
	}

}

package com.jll.common.cache;

import java.util.List;

import com.jll.entity.Issue;

public interface CacheRedisDao {

	/**
	 * store the captcha code into redis
	 * @param cacheObj
	 */
	void setCaptchaCode(CacheObject<String> cacheObj);

	CacheObject<String> getCaptchaCode(String sms);

	/**
	 * 将彩种计划保存到缓存
	 * @param cacheKey   plan_issues_ + 彩种
	 * @param issues     计划列表
	 */
	void setPlan(String cacheKey, List<Issue> issues);

	/**
	 * 
	 * @param cacheKey
	 * @return
	 */
	List<Issue> getPlan(String cacheKey);

}

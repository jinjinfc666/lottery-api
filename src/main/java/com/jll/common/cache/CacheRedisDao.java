package com.jll.common.cache;

import java.util.List;
import java.util.Map;

import com.jll.entity.Issue;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.game.BulletinBoard;

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
	
	void setSysCode(CacheObject<Map<String, SysCode>> cacheObj);
	
	CacheObject<Map<String, SysCode>> getSysCode(String codeTypeName);

	CacheObject<BulletinBoard> getBulletinBoard(String string);

	void setBulletinBoard(CacheObject<BulletinBoard> cache);
	
	SysCode getSysCode(String codeTypeName, String codeName);

	/**保存玩法到缓存
	 * @param cache
	 */
	void setPlayType(CacheObject<List<PlayType>> cache);

	/**从缓存读取玩法
	 * @param cacheKey
	 * @return
	 */
	List<PlayType> getPlayType(String cacheKey);

	CacheObject<Map<String, Integer>> getStatGroupByBettingNum(String cacheKey);

	void setStatGroupByBettingNum(CacheObject<Map<String, Integer>> cacheObj);
}

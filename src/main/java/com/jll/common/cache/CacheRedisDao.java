package com.jll.common.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.jll.entity.IpBlackList;
import com.jll.entity.Issue;
import com.jll.entity.PayChannel;
import com.jll.entity.PayType;
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

	void upatePlan(String cacheKey, Issue issue);
	
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

	CacheObject<Map<String, Object>> getStatGroupByBettingNum(String cacheKey);

	void setStatGroupByBettingNum(CacheObject<Map<String, Object>> cacheObj);

//	List<PayChannel> getPayChannel(String cacheKey);
//	void setPayChannel(CacheObject<List<PayChannel>> cache);
//	
//	List<PayType> getPayType(String cacheKey);
//	
//	void setPayType(CacheObject<List<PayType>> cache);
	
	/**
	 * 想消息队列发布消息
	 * @param channel  接收消息的队列名称 或者 主题
	 * @param mes 需要发布的消息
	 */
	void publishMessage(String channel, Serializable mes);
	//缓存ip
	CacheObject<Map<Integer, IpBlackList>> getIpBlackList(String codeTypeName);
	
	IpBlackList getIpBlackList(String codeTypeName, Integer codeName);
	
	void setIpBlackList(CacheObject<Map<Integer, IpBlackList>> cacheObj);
	
	void deleteIpBlackList(String codeTypeName,Integer codeName);

	void setUserBettingFlag(CacheObject<Integer> cacheObj);

	CacheObject<Integer> getUserBettingFlag(String cacheKey);
	
	//充值方式
	void setPayType(CacheObject<List<PayType>> cache);
	List<PayType> getPayType(String cacheKey);
	//充值渠道
	CacheObject<Map<Integer, PayChannel>> getPayChannel(String codeTypeName);
	
	PayChannel getPayChannel(String codeTypeName, Integer codeName);
	
	void setPayChannel(CacheObject<Map<Integer, PayChannel>> cacheObj);

	CacheObject<Map<String, Object>> getPlatStat(String string);

	void setPlatStat(CacheObject<Map<String, Object>> cacheObj);

	CacheObject<Integer> getMMCIssueCount(String string);

	void setMMCIssueCount(CacheObject<Integer> cacheObj);
	//存放图片验证码
	void setSessionIdCaptcha(CacheObject<String> cacheObj);
	//获取图片验证码
	CacheObject<String> getSessionIdCaptcha(String key);
	//删除缓存中的图片验证码
	void setSessionIdCaptchaExpired(CacheObject<String> cache,Integer expired);
}


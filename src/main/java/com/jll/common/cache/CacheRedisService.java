package com.jll.common.cache;

import java.util.List;
import java.util.Map;

import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.entity.Issue;
import com.jll.entity.SysCode;
import com.jll.game.BulletinBoard;


public interface CacheRedisService {

	/**
	 * store the captcha code into cache, and set the expired time is captchaCodeExpiredTime
	 * @param captchaCode
	 * @param captchaCodeExpiredTime
	 */
	void setCaptchaCode(String captchaCode, int captchaCodeExpiredTime);

	CacheObject<String> getCaptchaCode(String sms);

	/**store the lottery type plan to cache
	 * @param cacheKey plan_issue_ + lotteryType
	 * @param issues   
	 */
	void setPlan(String cacheKey, List<Issue> issues);

	List<Issue> getPlan(String cacheKey);
	
	/**
	 * 彩种计划是否存在
	 * @param lotteryType 彩种
	 * @return
	 */
	boolean isPlanExisting(String lotteryType);

	BulletinBoard getBulletinBoard(String lotteryType);

	/**
	 * @param lotteryTypes
	 * @param lotteryType
	 * @return
	 */
	boolean isCodeExisting(SysCodeTypes lotteryTypes, String lotteryType);
	
	/**
	 * 将代码类型对象和代码值对象保存到缓存
	 * @param codeTypeName
	 * @param sysCode       
	 */
	void setSysCode(String codeTypeName, SysCode sysCode);
	
	/**
	 * 将一个代码值集合保存到缓存
	 * @param codeTypeName
	 * @param sysCodes
	 */
	void setSysCode(String codeTypeName, List<SysCode> sysCodes);
	
	Map<String, SysCode> getSysCode(String codeTypeName);
	
	SysCode getSysCode(String codeTypeName, String codeName);

	void setBulletinBoard(String lottoType, BulletinBoard bulletinBoard);
}

package com.jll.common.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
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

	/**通过彩票类型查询玩法
	 * @param lotteryType
	 * @return
	 */
	List<PlayType> getPlayType(SysCode lotteryType);
	
	
	/**
	 * 将彩票玩法保存到缓存，以彩种作为key:play_type_ + lotteryType
	 * @param lotteryType
	 * @param playTypes
	 */
	void setPlayType(String lotteryType, List<PlayType> playTypes);

	/**
	 * 按照所选彩种的当前期次的所选号码进行投注金额以及投注注数的统计数据
	 * @param order
	 */
	void statGroupByBettingNum(String lotteryType, OrderInfo order);

	/**
	 * 检查指定的期次是否有效
	 * 
	 * @return
	 */
	boolean isIssueBetting(String lotteryType, int issueId);
	
	/**
	 * 想消息队列发布消息
	 * @param channel  接收消息的队列名称 或者 主题
	 * @param mes 需要发布的消息
	 */
	void publishMessage(String channel, Serializable mes);
	
}

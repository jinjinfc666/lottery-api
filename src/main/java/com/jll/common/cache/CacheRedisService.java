package com.jll.common.cache;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.entity.IpBlackList;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PayChannel;
import com.jll.entity.PayType;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserInfo;
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
	List<PlayType> getPlayType(String cacheCodeName);
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
	void statGroupByBettingNum(String lotteryType, OrderInfo order, UserInfo user);

	/**
	 * 獲取当前期次的投注统计数据
	 * @param order
	 */
	Map<String, Object> getStatGroupByBettingNum(String lotteryType, Integer issueId);
	
	/**
	 * 检查指定的期次是否有效
	 * 
	 * @return
	 */
	boolean isIssueBetting(String lotteryType, int issueId);
	
//	List<PayChannel> getPayChannel(int payTypeId);
	//PayChannel getPayChannelInfo(int payId,int pcId);
	PayType getPayTypeInfo(int payId);
//	void setPayChannel(int payTypeId, List<PayChannel> payChannel);
	
//	List<PayType> getPayType();
	
//	void setPayType(List<PayType> payTypes);
	
	/**
	 * 
	 * 想消息队列发布消息
	 * @param channel  接收消息的队列名称 或者 主题
	 * @param mes 需要发布的消息
	 */
	void publishMessage(String channel, Serializable mes);
	
	//ip缓存
	Map<Integer, IpBlackList> getIpBlackList(String codeName);
	
	IpBlackList getIpBlackList(String codeTypeName, Integer codeName);
	
	void setIpBlackList(String codeTypeName, IpBlackList ipBlackList);
	
	void setIpBlackList(String codeTypeName, List<IpBlackList> ipBlackLists);
	
	void deleteIpBlackList (String codeTypeName, Integer codeName);

	boolean isTimesValid(String lottoType, Integer times);

	boolean isMonUnitValid(String lottoType, Float monUnit);

	boolean isPlayTypeValid(String lotteryType, Integer playTypeId);

	/**
	 * 获取投注的倍数
	 * @param lotteryType
	 * @return
	 */
	SysCode getBetTimes(String lotteryType);

	SysCode getMoneyUnit(String lotteryType);
	//充值方式
	List<PayType> getPayType(String codeName);
	
	void setPayType(String codeTypeName, List<PayType> payTypes);
	
	//充值渠道
	Map<Integer, PayChannel> getPayChannel(String codeName);
	
	PayChannel getPayChannel(String codeName, Integer codeName1);
	
	void setPayChannel(String codeName, PayChannel payChannel);
	
	void setPayChannel(String codeName, List<PayChannel> payChannelLists);

	/**
	 * 系统运行参数
	 * @param keySysRuntimeArg
	 * @return
	 */
	Map<String, SysCode> getSysRuntimeArg(String keySysRuntimeArg);

	/** 
	 * 获取平台统计信息  --->针对彩种，日期
	 * 目前包含:
	 * 1,平台盈利统计
	 *  
	 * @param lotteryType
	 * @return
	 */
	Map<String, Object> getPlatStat(String lotteryType);
	
	void setPlatStat(String lotteryType, Map<String, Object> items);

	Integer getMMCIssueCount(Date currTime);

	void setMMCIssueCount(Date currTime, int i);

	void updatePlan(String lottoType, Issue issue);
	//存储图片验证码
	void setSessionIdCaptcha(String keyCaptcha, String value);
	//获取图片验证码
	String getSessionIdCaptcha(String keyCaptcha);
	//删除缓存中的图片验证码
	void deleteSessionIdCaptcha(String keyCaptcha);
	
	/**
	 * 申请一个锁
	 * @param key
	 * @param val
	 * @return
	 */
	boolean lock(String key, Object val, Integer expired);
	
	/**
	 * 释放一个锁
	 * @param key
	 */
	void releaseLock(String key);
}

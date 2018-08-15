package com.jll.common.cache;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.bcel.classfile.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.entity.IpBlackList;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.game.BulletinBoard;
import com.jll.sysSettings.syscode.SysCodeService;

@Configuration
@PropertySource("classpath:email-sender.properties")
@Service
@Transactional
public class CacheRedisServiceImpl implements CacheRedisService
{
	private Logger logger = Logger.getLogger(CacheRedisServiceImpl.class);

	@Resource
	CacheRedisDao cacheDao;
	
	@Resource
	SysCodeService sysCodeService;
	
	@Value("${email.qq.server}")
	private String qqServer;
	
	@Value("${email.qq.sender}")
	private String qqSender;
	
	@Value("${email.qq.pwd}")
	private String qqPwd;
	
	@Value("${email.reset.pwd.url}")
	private String resetUrl;
	
	@Value("${sys_captcha_code_expired_time}")
	private int captchaCodeExpiredTime;
	
	
	
	@Override
	public void setCaptchaCode(String captchaCode, int captchaCodeExpiredTime) {
		CacheObject<String> cacheObj = new CacheObject<String>();
		cacheObj.setContent(captchaCode);
		cacheObj.setExpired(captchaCodeExpiredTime);
		cacheObj.setKey(captchaCode);
		cacheDao.setCaptchaCode(cacheObj);
	}
	
	@Override
	public CacheObject<String> getCaptchaCode(String sms) {
		return cacheDao.getCaptchaCode(sms);
	}

	@Override
	public void setPlan(String cacheKey, List<Issue> issues) {
		cacheDao.setPlan(cacheKey, issues);
	}



	@Override
	public List<Issue> getPlan(String cacheKey) {
		return cacheDao.getPlan(cacheKey);
	}



	@Override
	public boolean isPlanExisting(String lotteryType) {
		Date today = new Date();
		String cacheKey = Constants.KEY_PRE_PLAN + lotteryType;
		List<Issue> issues = this.getPlan(cacheKey);
		if(issues == null || issues.size() == 0) {
			return false;
		}
		
		Issue lastIssue = issues.get(issues.size() -1);
		if(lastIssue.getEndTime().getTime() < today.getTime() ) {
			return false;
		}
		
		return true;
	}



	@Override
	public BulletinBoard getBulletinBoard(String lotteryType) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(Constants.KEY_PRE_BULLETINBOARD).append("_").append(lotteryType);
		CacheObject<BulletinBoard> cache = cacheDao.getBulletinBoard(buffer.toString());
		
		if(cache == null) {
			return null;
		}
		
		return cache.getContent();
	}

	@Override
	public void setBulletinBoard(String lottoType, BulletinBoard bulletinBoard) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(Constants.KEY_PRE_BULLETINBOARD).append("_").append(lottoType);
		
		CacheObject<BulletinBoard> cache = new CacheObject<>();
		cache.setContent(bulletinBoard);
		cache.setKey(buffer.toString());
		
		cacheDao.setBulletinBoard(cache);
	}

	@Override
	public boolean isCodeExisting(SysCodeTypes lotteryTypes, String lotteryType) {
		SysCode sysCode = getSysCode(lotteryTypes.getCode(), lotteryType);
		if(sysCode == null) {
			return false;
		}		
				
		return true;

	}
	@Override
	public void setSysCode(String codeTypeName, List<SysCode> sysCodes) {
		CacheObject<Map<String, SysCode>> cacheObj = new CacheObject<>();
		Map<String, SysCode> sysCodesTemp = new HashMap<>();
		for(SysCode sysCode : sysCodes) {
			sysCodesTemp.put(sysCode.getCodeName(), sysCode);
		}
		
		//container.put(codeTypeName, sysCodesTemp);
		cacheObj.setContent(sysCodesTemp);
		cacheObj.setKey(codeTypeName);
		cacheDao.setSysCode(cacheObj);
		
	}

	@Override
	public Map<String, SysCode> getSysCode(String codeName) {
		CacheObject<Map<String, SysCode>>  cache = cacheDao.getSysCode(codeName);
		if(cache == null) {
			return null;
		}
		
		return cache.getContent();
	}

	@Override
	public void setSysCode(String codeTypeName, SysCode sysCode) {
		//CacheObject<Map<String, SysCode>> cacheObj = new CacheObject<>();asdad
		CacheObject<Map<String, SysCode>> cacheObject=cacheDao.getSysCode(codeTypeName);
		Map<String, SysCode> sysCodesTemp=null;
		if(cacheObject==null) {
			sysCodesTemp = new HashMap<>();
			cacheObject= new CacheObject<>();
		}else {
			sysCodesTemp=cacheObject.getContent();
		}
		sysCodesTemp.put(sysCode.getCodeName(), sysCode);
		cacheObject.setContent(sysCodesTemp);
		cacheObject.setKey(codeTypeName);
		cacheDao.setSysCode(cacheObject);
	}

	@Override
	public SysCode getSysCode(String codeTypeName, String codeName) {
		return cacheDao.getSysCode(codeTypeName, codeName);
	}

	@Override
	public List<PlayType> getPlayType(SysCode lotteryType) {
		String cacheKey = Constants.KEY_PLAY_TYPE + lotteryType.getCodeName();
		List<PlayType> playTypes = cacheDao.getPlayType(cacheKey);
		return playTypes;
	}

	@Override
	public void setPlayType(String lotteryType, List<PlayType> playTypes) {
		String cacheKey = Constants.KEY_PLAY_TYPE + lotteryType;
		CacheObject<List<PlayType>> cache = new CacheObject<>();
		cache.setContent(playTypes);
		cache.setKey(cacheKey);
		
		cacheDao.setPlayType(cache);
	}

	@Override
	public void statGroupByBettingNum(String lotteryType, OrderInfo order) {
		StringBuffer cacheKey = new StringBuffer();
		cacheKey.append(Constants.KEY_STAT_ISSUE_BETTING)
		.append(lotteryType).append(order.getIssueId());
		Map<String, Integer> statInfo = null;
		CacheObject<Map<String, Integer>> cacheObj = null;
		
		cacheObj = cacheDao.getStatGroupByBettingNum(cacheKey.toString());
		
		if(cacheObj == null) {
			cacheObj = new CacheObject<>();
			statInfo = new HashMap<>();
			cacheObj.setKey(cacheKey.toString());
			cacheObj.setContent(statInfo);
			cacheObj.setExpired(1*60*60);
		}
		
		statInfo = cacheObj.getContent();
		if(statInfo.get(String.valueOf(order.getBetNum())) == null) {
			statInfo.put(String.valueOf(order.getBetNum()), 1);
		}else {
			statInfo.put(String.valueOf(order.getBetNum()), 
					statInfo.get(String.valueOf(order.getBetNum()) + 1));
		}
		
		cacheObj.setContent(statInfo);
		
		cacheDao.setStatGroupByBettingNum(cacheObj);
	}

	@Override
	public boolean isIssueBetting(String lotteryType, int issueId) {
		BulletinBoard bulletinBoard = getBulletinBoard(lotteryType);
		if(bulletinBoard == null) {
			return false;
		}
		
		Issue currIssue = bulletinBoard.getCurrIssue();
		if(currIssue == null 
				|| currIssue.getId().intValue() != issueId
				|| currIssue.getState() != Constants.IssueState.BETTING.getCode()) {
			return false;
		}
		
		return true;
	}

	@Override
	public void publishMessage(String channel, Serializable mes) {
		cacheDao.publishMessage(channel, mes);
	}
	//ip缓存
	@Override
	public Map<Integer, IpBlackList> getIpBlackList(String codeName) {
		CacheObject<Map<Integer, IpBlackList>>  cache = cacheDao.getIpBlackList(codeName);
		if(cache == null) {
			return null;
		}
		return cache.getContent();
	}
	@Override
	public IpBlackList getIpBlackList(String codeTypeName, Integer codeName) {
		return cacheDao.getIpBlackList(codeTypeName, codeName);
	}
	@Override
	public void setIpBlackList(String codeTypeName, IpBlackList ipBlackList) {
		CacheObject<Map<Integer, IpBlackList>> cacheObject=cacheDao.getIpBlackList(codeTypeName);
		Map<Integer, IpBlackList> ipBlackListTemp=null;
		if(cacheObject==null) {
			ipBlackListTemp = new HashMap<>();
			cacheObject= new CacheObject<>();
		}else {
			ipBlackListTemp=cacheObject.getContent();
		}
		ipBlackListTemp.put(ipBlackList.getId(), ipBlackList);
		cacheObject.setContent(ipBlackListTemp);
		cacheObject.setKey(codeTypeName);
		cacheDao.setIpBlackList(cacheObject);
	}
	@Override
	public void setIpBlackList(String codeTypeName, List<IpBlackList> ipBlackLists) {
		CacheObject<Map<Integer, IpBlackList>> cacheObj = new CacheObject<>();
		Map<Integer, IpBlackList> ipBlackListTemp = new HashMap<>();
		for(IpBlackList ipBlackList : ipBlackLists) {
			ipBlackListTemp.put(ipBlackList.getId(), ipBlackList);
		}
		
		//container.put(codeTypeName, sysCodesTemp);
		cacheObj.setContent(ipBlackListTemp);
		cacheObj.setKey(codeTypeName);
		cacheDao.setIpBlackList(cacheObj);
	}

	@Override
	public void deleteIpBlackList(String codeTypeName, Integer codeName) {
		cacheDao.deleteIpBlackList(codeTypeName, codeName);
	}
	
}

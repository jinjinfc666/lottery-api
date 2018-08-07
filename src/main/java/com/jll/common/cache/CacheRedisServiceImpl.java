package com.jll.common.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.entity.Issue;
import com.jll.entity.SysCode;
import com.jll.game.BulletinBoard;
import com.jll.sysSettings.codeManagement.SysCodeService;

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
		CacheObject<Map> cache = getSysCode(lotteryTypes.getCode());
		if(cache == null) {
			return false;
		}
		
		Map<String, SysCode> sysCodes = cache.getContent();
		
		if(sysCodes == null || sysCodes.size() == 0) {
			return false;
		}
		
		SysCode sysCode = sysCodes.get(lotteryType);
		
		if(sysCode == null) {
			return false;
		}
		
		return true;

	}
	
	public void setSysCode(String codeName) {
		CacheObject<Map<String, Object>> cacheObj = new CacheObject<>();
		Map<String,Object> map=new HashMap<String,Object>();
		
		boolean isNull=sysCodeService.isNull(codeName);
		if(isNull) {
			List<SysCode> sysCode=sysCodeService.queryCacheType(codeName);
			Iterator<SysCode> it = sysCode.iterator();
			while (it.hasNext()) {
				SysCode syscode1=it.next();
				map.put(syscode1.getCodeName(), syscode1);
			}
			cacheObj.setContent(map);
			cacheObj.setKey(codeName);
			cacheDao.setSysCode(cacheObj);
		}else {
			List<SysCode> sysCode=sysCodeService.queryCacheTypeOnly(codeName);
			map.put(codeName, sysCode);
			cacheObj.setContent(map);
			cacheObj.setKey(codeName);
			cacheDao.setSysCode(cacheObj);
		}
	}

	@Override
	public CacheObject<Map> getSysCode(String codeName) {
		return cacheDao.getSysCode(codeName);
	}

	
	
}

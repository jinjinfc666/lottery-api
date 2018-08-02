package com.jll.common.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Message;
import com.jll.common.utils.Utils;
import com.jll.entity.SysCode;
import com.jll.entity.UserInfo;
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
	public void setSysCode(String codeName) {
		CacheObject<Map> cacheObj = new CacheObject<Map>();
		Map<String,Object> map=new HashMap<String,Object>();
		
		boolean isNull=sysCodeService.isNull(codeName);
		if(isNull) {
			List<SysCode> sysCode=sysCodeService.queryCacheType(codeName);
			logger.debug(sysCode.toString()+"@@@@@@@@@@@@@@@@@@@@@@@@@@@setSysCode  测试@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			Iterator<SysCode> it = sysCode.iterator();
			while (it.hasNext()) {
				SysCode syscode1=it.next();
				map.put(syscode1.getCodeName(), syscode1);
			}
			logger.debug(map+"@@@@@@@@@@@@@@@@@@@@@@@@@@@setSysCode  测试@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			cacheObj.setContent(map);
			cacheObj.setKey(codeName);
			cacheDao.setSysCode(cacheObj);
		}else {
			List<SysCode> sysCode=sysCodeService.queryCacheTypeOnly(codeName);
//			logger.debug(sysCode.toString()+"@@@@@@@@@@@@@@@@@@@@@@@@@@@setSysCode  测试  如果此大类下面没有节点@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//			Iterator<SysCode> it = sysCode.iterator();
////			while (it.hasNext()) {
			map.put(codeName, sysCode);
//			}
			logger.debug(map+"@@@@@@@@@@@@@@@@@@@@@@@@@@@setSysCode  测试  如果此大类下面没有节点@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
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

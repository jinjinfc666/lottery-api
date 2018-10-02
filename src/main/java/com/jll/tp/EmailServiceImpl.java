package com.jll.tp;

import java.util.Properties;

import javax.annotation.Resource;
import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheObject;
import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.utils.Utils;
import com.jll.entity.UserInfo;

@Configuration
@PropertySource("classpath:email-sender.properties")
@Service
@Transactional
public class EmailServiceImpl implements EmailService
{
	private Logger logger = Logger.getLogger(EmailServiceImpl.class);

	@Resource
	CacheRedisService cacheService;
	
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
	public boolean isEmailValid(UserInfo user, String code) {
		code = Constants.EMAIL+code;

		CacheObject<String> cacheObject = cacheService.getCaptchaCode(code);
		if(cacheObject == null
				|| StringUtils.isBlank(cacheObject.getContent())) {
			return false;
		}
		
		if(!cacheObject.getContent().equals(code)) {
			return false;
		}
		return true;
	}

	@Override
	public String sendingEmail(UserInfo user,String host) throws MessagingException {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		StringBuffer content = new StringBuffer();
		
		String captchaCode = Utils.produce6DigitsCaptchaCodeNumber();
		
		content.append("请点击地址进行验证,有效时间"+captchaCodeExpiredTime+"秒: http://").append(host).append(resetUrl.replace("{userName}", user.getUserName()))
		.append("?verifyCode=").append(captchaCode);
				
		mailSender.setHost(qqServer);  
		mailSender.setUsername(qqSender);  
		mailSender.setPassword(qqPwd);
		
		 //加认证机制        
		Properties javaMailProperties = new Properties();  
		javaMailProperties.put("mail.smtp.auth", true);     
		javaMailProperties.put("mail.smtp.starttls.enable", true);
		javaMailProperties.put("mail.smtp.timeout", 5000);
		mailSender.setJavaMailProperties(javaMailProperties); 
		//创建邮件内容     
		SimpleMailMessage message=new SimpleMailMessage();   
		message.setFrom(qqSender);       
		message.setTo(user.getEmail());      
		message.setSubject("Reset Password");  
		message.setText(content.toString());       
		//发送邮件        
		mailSender.send(message);
		
		cacheService.setCaptchaCode(Constants.EMAIL+captchaCode, captchaCodeExpiredTime);
		return Integer.toString(Message.status.SUCCESS.getCode());
	}
	
}

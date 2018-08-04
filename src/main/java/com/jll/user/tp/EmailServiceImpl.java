package com.jll.user.tp;

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

	@Value("${email.qq.server}")
	private String qqServer;
	
	@Value("${email.qq.sender}")
	private String qqSender;
	
	@Value("${email.qq.pwd}")
	private String qqPwd;
	
	@Value("${email.reset.pwd.url}")
	private String resetUrl;
	

	@Override
	public boolean isSmsValid(UserInfo user, String sms) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String sendingEmail(UserInfo user,String host) throws MessagingException {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		StringBuffer content = new StringBuffer();
		
		content.append(host).append(resetUrl.replace("{userName}", user.getUserName()))
		.append("?verifyCode=").append(Utils.produce6DigitsCaptchaCodeNumber());
				
		mailSender.setHost(qqServer);  
		mailSender.setUsername(qqSender);  
		mailSender.setPassword(qqPwd);
		
		SimpleMailMessage smm = new SimpleMailMessage(); 
		smm.setFrom(mailSender.getUsername());  
		smm.setTo(qqSender);  
		smm.setSubject("Reset Password");

		MimeMessage msg = mailSender.createMimeMessage();  
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);  
		//使用辅助类MimeMessage设定参数  
		helper.setFrom(mailSender.getUsername());  
		helper.setTo(qqSender);  
		helper.setSubject("Reset password");  
		helper.setText(content.toString(), true);
		// 发送邮件  
		mailSender.send(smm);
		
		//TODO save the verify code 
		
		return Integer.toString(Message.status.SUCCESS.getCode());
	}
	
}

package com.jll.sys.security;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.jll.sys.log.LoginService;

@Component
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

	private Logger logger = Logger.getLogger(LoginFailureListener.class);
	@Resource
	LoginService loginService;
	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		logger.debug("login Success....");
		UsernamePasswordAuthenticationToken UPAT=(UsernamePasswordAuthenticationToken) event.getSource();
		User user=(User) UPAT.getPrincipal();
		String userName = user.getUsername();
		if(!userName.equals("lottery-client")&&!userName.equals("lottery-admin")) {
			loginService.updateSuccessLogin(userName);
		}
	}

}

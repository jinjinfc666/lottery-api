package com.jll.sys.security;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	private Logger logger = Logger.getLogger(LoginFailureListener.class);
	
	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		// TODO Auto-generated method stub
		logger.debug("login failure!!!");
	}

}

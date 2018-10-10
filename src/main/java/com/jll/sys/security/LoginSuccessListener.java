package com.jll.sys.security;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

	private Logger logger = Logger.getLogger(LoginFailureListener.class);
	
	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		logger.debug("login Success....");
	}

}

package com.jll.sys.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.jll.sys.log.LoginService;

public class LoginFailureHandler implements AuthenticationFailureHandler {

	private Logger logger = Logger.getLogger(LoginFailureHandler.class);
	
	@Resource
	LoginService loginService;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		logger.debug("onAuthenticationFailure");
//		loginService.failLogin();
	}

}

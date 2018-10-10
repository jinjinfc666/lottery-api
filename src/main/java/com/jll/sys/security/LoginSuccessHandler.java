package com.jll.sys.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.jll.sys.log.LoginService;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private Logger logger = Logger.getLogger(LoginSuccessHandler.class);
	@Resource
	LoginService loginService;
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException { 
		logger.debug("onAuthenticationSuccess");
//		loginService.successLogin(userName);
	}

}

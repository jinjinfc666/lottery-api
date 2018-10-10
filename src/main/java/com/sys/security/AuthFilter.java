package com.sys.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;

public class AuthFilter extends ClientCredentialsTokenEndpointFilter {

	private Logger logger = Logger.getLogger(AuthFilter.class);
	
	@Override
	public void afterPropertiesSet() {
		
	}
	
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		//TODO valid the captcha
		String recCaptcha = request.getParameter("captcha");
		String saveCaptcha = null;
		
		if(recCaptcha == null || saveCaptcha == null)
            throw new CusAuthenticationException(this.messages.getMessage("LoginAuthentication.captchaInvalid"));  
        if(!saveCaptcha.equalsIgnoreCase(recCaptcha)){  
            throw new CusAuthenticationException(this.messages.getMessage("LoginAuthentication.captchaNotEquals"));  
        }
        
		return super.attemptAuthentication(request, response);
	}
}

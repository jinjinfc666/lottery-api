package com.jll.sys.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Message;

public class AuthFilter extends ClientCredentialsTokenEndpointFilter {

	private Logger logger = Logger.getLogger(AuthFilter.class);
	@Resource
	CacheRedisService cacheRedisService;
//	@Override
//	public void afterPropertiesSet() {
//		
//	}
	
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
//		HttpSession session = request.getSession(false);
//		if(session==null) {
//			session=request.getSession(true);
//		}
//		String sessionId=session.getId();
		String recCaptcha = request.getParameter("captcha");
		String sessionId = request.getParameter("jsSessionId");
		String key=sessionId;
		String saveCaptcha = cacheRedisService.getSessionIdCaptcha(key);
		cacheRedisService.deleteSessionIdCaptcha(key);
		if(recCaptcha == null || saveCaptcha == null)
            throw new CusAuthenticationException(Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());  
        if(!saveCaptcha.equalsIgnoreCase(recCaptcha)){   
        	
            throw new CusAuthenticationException(Message.Error.ERROR_LOGIN_INVALID_CAPTCHA.getCode());  
        }
        
		return super.attemptAuthentication(request, response);
	}
}

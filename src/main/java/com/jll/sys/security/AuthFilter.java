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
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
import com.jll.entity.UserInfo;
import com.jll.user.UserInfoService;

public class AuthFilter extends ClientCredentialsTokenEndpointFilter {

	private Logger logger = Logger.getLogger(AuthFilter.class);
	@Resource
	CacheRedisService cacheRedisService;
	
	@Resource
	UserInfoService userServ;
	
//	@Override
//	public void afterPropertiesSet() {
//		
//	}
	
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		HttpSession session = request.getSession(true);
		String sessionId = session.getId();
		String clientId = request.getParameter("client_id");
		String username = request.getParameter("username");
		UserInfo user = null;
		
		if(StringUtils.isBlank(clientId)
				|| StringUtils.isBlank(username)) {
			throw new CusAuthenticationException(Message.Error.ERROR_COMMON_NO_PERMISSION.getCode());  
		}
		
		user = userServ.getUserByUserName(username);
		
		if(user == null) {
			throw new CusAuthenticationException(Message.Error.ERROR_COMMON_NO_PERMISSION.getCode());  
		}
		
		if(Constants.KEY_CLIENT_ID_ADMIN.equals(clientId)) {
			Integer userType = user.getUserType();
			if(userType == null 
					|| userType.intValue() != Constants.UserType.SYS_ADMIN.getCode()) {
				throw new CusAuthenticationException(Message.Error.ERROR_COMMON_NO_PERMISSION.getCode());  
			}
		}else if(Constants.KEY_CLIENT_ID_CLIENT.equals(clientId)) {
			Integer userType = user.getUserType();
			if(userType == null 
					|| userType.intValue() == Constants.UserType.SYS_ADMIN.getCode()) {
				throw new CusAuthenticationException(Message.Error.ERROR_COMMON_NO_PERMISSION.getCode());  
			}
		}else {
			throw new CusAuthenticationException(Message.Error.ERROR_COMMON_NO_PERMISSION.getCode());  
		}
		String recCaptcha = request.getParameter("captcha");
		//String sessionId = request.getParameter("jsessionid");
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

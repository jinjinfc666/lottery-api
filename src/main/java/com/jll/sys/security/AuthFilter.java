package com.jll.sys.security;

import java.io.IOException;
import java.util.Date;

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
import com.jll.common.utils.IpUtils;
import com.jll.common.utils.StringUtils;
import com.jll.entity.IpBlackList;
import com.jll.entity.SysLogin;
import com.jll.entity.UserInfo;
import com.jll.sys.blacklist.IpBlackListService;
import com.jll.sys.log.SysLoginService;
import com.jll.user.UserInfoService;

public class AuthFilter extends ClientCredentialsTokenEndpointFilter {

	private Logger logger = Logger.getLogger(AuthFilter.class);
	@Resource
	CacheRedisService cacheRedisService;
	
	@Resource
	UserInfoService userServ;
	
	@Resource
	SysLoginService sysLoginService;
	
	@Resource
	IpBlackListService ipBlackListService;
	
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
		String grantType = request.getParameter("grant_type");
		UserInfo user = null;
		String ip=request.getRemoteHost();
		String ipLong="";
		Integer type=null;
		if(ip.indexOf(":")!=-1) {
			type=1;
			ipLong=IpUtils.ipv6toInt(ip).toString();
		}else {
			type=0;
			ipLong=Long.toString(IpUtils.ipToLong(ip));
		}
		long count=ipBlackListService.queryByIp(ipLong,type);
		if(count>0) {
			throw new CusAuthenticationException(Message.Error.ERROR_LOGIN_ILLEGAL_USER_NO_LOGIN.getCode());
		}
		
		if(StringUtils.isBlank(grantType)) {
			throw new CusAuthenticationException(Message.Error.ERROR_COMMON_NO_PERMISSION.getCode());
		}
		if(grantType.equals("password")) {
			if(StringUtils.isBlank(clientId)
					|| StringUtils.isBlank(username)) {
				throw new CusAuthenticationException(Message.Error.ERROR_COMMON_NO_PERMISSION.getCode());  
			}
			
			user = userServ.getUserByUserName(username);
			
			if(user == null) {
				long countFail=sysLoginService.queryFailLoginCount(ip);
				if(countFail>=4) {
					if(ip.indexOf(":")!=-1) {
						type=1;
						ipLong=IpUtils.ipv6toInt(ip).toString();
					}else {
						type=0;
						ipLong=Long.toString(IpUtils.ipToLong(ip));
					}
					IpBlackList ipBlackList=new IpBlackList();
					ipBlackList.setIp(ip);
					ipBlackList.setIpLong(ipLong);
					ipBlackList.setType(type);
					ipBlackListService.saveIp(ipBlackList);
				}
				//登陆日志
				SysLogin sysLogin=new SysLogin();
				String logOpeType=StringUtils.OPE_LOG_USER_FAILURE;
				String logData="{\"userName\":\""+username+"\",\"ip\":\""+ip+"\"}";
				sysLogin.setLogData(logData);
				sysLogin.setLogOpeType(logOpeType);
				sysLogin.setLogType(logOpeType);
				sysLoginService.saveOrUpdate(sysLogin);
				//登陆日志
				throw new CusAuthenticationException(Message.Error.ERROR_COMMON_NO_PERMISSION.getCode());  
			}
			if(user.getState()==Constants.UserState.LOCKING.getCode()&&user.getUnlockTime()!=null&&new Date().before(user.getUnlockTime())) {
				throw new CusAuthenticationException(Message.Error.ERROR_USER_HAS_BEEN_LOCKED.getCode());  
			}else if(user.getState()==Constants.UserState.LOCKING.getCode()&&user.getUnlockTime()==null){
				throw new CusAuthenticationException(Message.Error.ERROR_USER_HAS_BEEN_LOCKED_SERVICE.getCode());
			}else if(user.getState()==Constants.UserState.REVOKED.getCode()) {
				throw new CusAuthenticationException(Message.Error.ERROR_USER_HAS_BEEN_DESTROY_SERVICE.getCode());
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
			if(user.getUserType()!=Constants.UserType.DEMO_PLAYER.getCode()) {
				String recCaptcha = request.getParameter("captcha");
				//String sessionId = request.getParameter("jsessionid");
				String key=sessionId;
				String saveCaptcha = cacheRedisService.getSessionIdCaptcha(key);
				cacheRedisService.deleteSessionIdCaptcha(key);
				if(recCaptcha == null || saveCaptcha == null)
		            throw new CusAuthenticationException(Message.Error.ERROR_LOGIN_INVALID_CAPTCHA.getCode());  
		        if(!saveCaptcha.equalsIgnoreCase(recCaptcha)){   
		        	
		            throw new CusAuthenticationException(Message.Error.ERROR_LOGIN_INVALID_CAPTCHA.getCode());  
		        }
			}
		}else if(grantType.equals("refresh_token")){
			String refreshToken = request.getParameter("refresh_token");
			String clientSecret = request.getParameter("client_secret");
			if(StringUtils.isBlank(refreshToken)||StringUtils.isBlank(clientId)||StringUtils.isBlank(clientSecret)) {
				throw new CusAuthenticationException(Message.Error.ERROR_COMMON_NO_PERMISSION.getCode());  
			}
			if((!Constants.KEY_CLIENT_ID_ADMIN.equals(clientId)&&!Constants.KEY_CLIENT_ID_CLIENT.equals(clientId))||!clientSecret.equals("secret_1")) {
				throw new CusAuthenticationException(Message.Error.ERROR_COMMON_NO_PERMISSION.getCode());  
			}
		}else {
			throw new CusAuthenticationException(Message.Error.ERROR_COMMON_NO_PERMISSION.getCode());  
		}
		return super.attemptAuthentication(request, response);
	}
}

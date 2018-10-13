package com.jll.sys.security.login;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Message;
import com.jll.user.UserInfoService;

@RestController
@RequestMapping({ "/security" })
public class LoginController {
	private Logger logger = Logger.getLogger(LoginController.class);

	@Autowired
	private TokenStore tokenStore;
	
	
	@Autowired
	private UserInfoService userInfoService;

	/**
	 * revoke the access token
	 * @param request
	 */
	@RequestMapping(value = { "/logout" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public Map<String,Object> logout(HttpServletRequest request) {
		Map<String,Object> map=new HashMap<String,Object>();
		String token = request.getHeader("Authorization");
		if (token != null) {
			token = token.replace("bearer ", "");
			token = token.replace("Bearer ", "");
			OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);

			if (oAuth2AccessToken != null) {
				tokenStore.removeAccessToken(oAuth2AccessToken);
				map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
				userInfoService.updateDemoUserDisableLogin();
				return map;
			}
		}
		map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
		map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_SYSTEM_LOG_OUT.getCode());
		map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_SYSTEM_LOG_OUT.getErrorMes());
		return map;
	}
}

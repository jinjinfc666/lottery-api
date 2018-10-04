package com.jll.sys.security.login;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/security" })
public class LoginController {
	private Logger logger = Logger.getLogger(LoginController.class);

	@Autowired
	private TokenStore tokenStore;

	/**
	 * revoke the access token
	 * @param request
	 */
	@RequestMapping(value = { "/logout" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void logout(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token != null) {
			token = token.replace("bearer ", "");
			OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);

			if (oAuth2AccessToken != null) {
				tokenStore.removeAccessToken(oAuth2AccessToken);
			}
		}
	}
}

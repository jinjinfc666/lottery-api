package com.jll.user;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Constants.UserType;
import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.UserInfo;
import com.jll.tp.EmailService;
import com.jll.tp.SMSService;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "userInfo", name = "user Info")
@ApiComment(seeClass = UserInfo.class)
@RestController
@RequestMapping({ "/users" })
@Configuration
@PropertySource("classpath:sys-setting.properties")
public class UserController {
	
	private Logger logger = Logger.getLogger(UserController.class);
	
	@Value("${sys_reset_pwd_default_pwd}")
	String defaultPwd;
	
	@Resource
	UserInfoService userInfoService;
	
	@Resource
	SMSService smsServ;
	
	@Resource
	EmailService emailServ;
	
	@Resource
	HttpServletRequest request;
	
	@Value("${sys_captcha_code_expired_time}")
	private int captchaCodeExpiredTime;
	
	/**
	 * query the specified user by userName, only the operator with userName or operator with role:role_bus_manager
	 * can obtain the full information, the person without permission can only read part of information.
	 * @param request
	 */
	@RequestMapping(value="/attrs/user-name/{userName}", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryUserByUserName(@PathVariable("userName") String userName) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
	}
	
	/**
	 * query the specified user by userName, only the operator with userName or operator with role:role_bus_manager
	 * can obtain the full information, the person without permission can only read part of information.
	 * @param request
	 */
	@RequestMapping(value="/attrs/superior/{superior}", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryUserBySuperior(@RequestParam("superior") int Superior) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
	}
	
	/**
	 * register the user who can login front-end web application
	 * this will be only called  by the agent
	 * @param request
	 */
	@RequestMapping(value="/players", method = { RequestMethod.POST }, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> regUser(@RequestBody UserInfo user) {
		Map<String, Object> resp = new HashMap<String, Object>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserInfo superior = null;
		boolean isExisting = false;
		
		if(auth == null) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED);
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_LOGIN.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_LOGIN.getErrorMes());
			return resp;
		}
		
		try {
			superior = userInfoService.getUserByUserName(auth.getName());
			
			if(superior == null) {
				throw new Exception("No superior!");
			}
		}catch(Exception ex) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_INVALID_USER_NAME.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_INVALID_USER_NAME.getErrorMes());
			return resp;
		}		
		
		user.setSuperior(superior.getId()+","+superior.getSuperior());
		user.setUserType(UserType.PLAYER.getCode());
		String ret = userInfoService.validUserInfo(user, superior);
		if(StringUtils.isBlank(ret) ) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return resp;
		}
		
		if(!Integer.toString(Message.status.SUCCESS.getCode()).equals(ret)) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, ret);
			resp.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(ret).getErrorMes());
			return resp;
		}
		
		if(StringUtils.isBlank(user.getFundPwd())) {
			user.setFundPwd(user.getLoginPwd());
		}
		
		try {
			isExisting = userInfoService.isUserExisting(user);
		}catch(Exception ex) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return resp;
		}
		
		if(isExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_EXISTING.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_EXISTING.getErrorMes());
			return resp;
		}		
		
		user.setRebate(superior.getPlatRebate().subtract(user.getPlatRebate()));
		try {
			userInfoService.regUser(user);
		}catch(Exception ex) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_FAILED_REGISTER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_FAILED_REGISTER.getErrorMes());
			return resp;
		}
				
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return resp;
	}
	
	/**
	 * register the agent
	 * this will be only called  by the user with role:role_admin
	 * @param request
	 */
	@RequestMapping(value="/agents", method = { RequestMethod.POST }, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> regAgent(@RequestBody UserInfo user) {
		Map<String, Object> resp = new HashMap<String, Object>();
				
		UserInfo generalAgency = userInfoService.getGeneralAgency();
		if(generalAgency == null) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED);
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_GENERAL_AGENCY.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_GENERAL_AGENCY.getErrorMes());
			return resp;
		}
		
		user.setSuperior(Integer.toString(generalAgency.getId()));
		user.setUserType(UserType.AGENCY.getCode());
		String ret = userInfoService.validUserInfo(user, generalAgency);
		if(StringUtils.isBlank(ret) ) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED);
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return resp;
		}
		
		if(!Integer.toString(Message.status.SUCCESS.getCode()).equals(ret)) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, ret);
			resp.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(ret).getErrorMes());
			return resp;
		}
		
		if(StringUtils.isBlank(user.getFundPwd())) {
			user.setFundPwd(user.getLoginPwd());
		}
		
		boolean isExisting = userInfoService.isUserExisting(user);
		if(isExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_EXISTING.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_EXISTING.getErrorMes());
			return resp;
		}		
		
		user.setRebate(generalAgency.getPlatRebate().subtract(user.getPlatRebate()));
		try {
			userInfoService.regUser(user);
		}catch(Exception ex) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_FAILED_REGISTER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_FAILED_REGISTER.getErrorMes());
			return resp;
		}
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return resp;
	}
	
	/**
	 * register the system users
	 * this will be only called  by the user with role:role_admin
	 * @param request
	 */
	@RequestMapping(value="/sys-users", method = { RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> regSysUser(@RequestBody UserInfo user) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		user.setUserType(UserType.SYS_ADMIN.getCode());
		String ret = userInfoService.validUserInfo(user, null);
		if(StringUtils.isBlank(ret) ) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED);
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return resp;
		}
		
		if(!Integer.toString(Message.status.SUCCESS.getCode()).equals(ret)) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, ret);
			resp.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(ret).getErrorMes());
			return resp;
		}
		
		
		boolean isExisting = userInfoService.isUserExisting(user);
		if(isExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_EXISTING.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_EXISTING.getErrorMes());
			return resp;
		}		
		
		try {
			userInfoService.regUser(user);
		}catch(Exception ex) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_FAILED_REGISTER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_FAILED_REGISTER.getErrorMes());
			return resp;
		}
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return resp;
	}
	
	/**
	 * update the basic information of user
	 * @param user
	 * @return
	 */
	@ApiComment("update the basic information of user[real name,wechar,qq,phone,email]")
	@RequestMapping(value="/{userName}", method = { RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> updateUserBasic(@PathVariable("userName") String userName,
			@RequestBody UserInfo user) {
		user.setUserName(userName);
		return userInfoService.updateUserInfoInfo(user);
	}
	
	/**
	 * Update User login password
	 * 
	 * @param userName
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 */
    @ApiComment("Update User Login Password")
	@RequestMapping(value="/{userName}/attrs/login-pwd", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> updateLoginPwd(
			 @PathVariable("userName") String userName,
			 @RequestParam(name = "oldPwd", required = true) String oldPwd,
			 String newPwd) {
		return userInfoService.updateFundPwd(userName, oldPwd, newPwd);
	}
	
	/**
	 * 
	 *  Update User fund password
	 * 
	 * @param userName
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 */
    @ApiComment("Update User Fun Password")
	@RequestMapping(value="/{userName}/attrs/fund-pwd", method = { RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> updateFundPwd( @PathVariable("userName") String userName,
			 @RequestParam(name = "oldPwd", required = true) String oldPwd,
			 @RequestParam(name = "newPwd", required = true) String newPwd) {
		return userInfoService.updateFundPwd(userName, oldPwd, newPwd);
	}
    
    
    /**
     * Get User Info
     * if login user has role[role_user_info] ,show all info
     * @param userName
     * @return
     */
    @ApiComment("Get User Info")
	@RequestMapping(value="/{userName}", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserInfo( @PathVariable("userName") String userName) {
		return userInfoService.getUserInfoByUserName(userName);
	}
	

	/**
	 * apply for sms
	 * this operation will generate a sms to the specified phone number
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userId}/attrs/phone", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> applyVerifyPhone(@PathVariable("userId") int userId,
			@RequestParam("phoneNum") String phoneNum) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
	}
	
	/**
	 * verify the received sms ,
	 * if pass verification, then the phone number will be saved to current user and change the flag to pass verification.
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userId}/attrs/phone", method = { RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> verifyPhone(@PathVariable("userId") int userId,
			@RequestParam("phoneNum") String phoneNum,
			@RequestParam("sms") String sms) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
	}
	
	/**
	 * apply for email
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userId}/attrs/email/pre-verification", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> applyVerifyEmail(@PathVariable("userId") int userId,
			@RequestParam("email") String email) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
	}
	
	/**
	 * verify the email
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userId}/attrs/email/verification", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> verifyEmail(@PathVariable("userId") int userId,
			@RequestParam("verifyCode") String verifyCode) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
	}
	
	/**
	 * verify the email
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userName}/attrs/login-pwd/reset/sms", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> applyResetLoginPwdBySMS(@PathVariable(name="userName", required = true) String userName) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		UserInfo user = new UserInfo();
		user.setUserName(userName);
		
		boolean isExisting = userInfoService.isUserExisting(user);
		if(!isExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return resp;
		}
		
		user = userInfoService.getUserByUserName(userName);
		
		boolean ifPhoneValid = (user.getIsValidPhone() != null && user.getIsValidPhone() == 1)?true:false;
		if(!ifPhoneValid) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_INVALID_PHONE_NUMBER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_INVALID_PHONE_NUMBER.getErrorMes());
			return resp;
		}
		
		String ret = smsServ.sending6digitsNumbers(user.getPhoneNum());
		if(!Integer.toString(Message.status.SUCCESS.getCode()).equals(ret)) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, ret);
			resp.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(ret).getErrorMes());
			return resp;
		}
		
		Map<String,Object> data = new HashMap<>();
		data.put(Message.KEY_EXPIRED_TIME, captchaCodeExpiredTime);
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		resp.put(Message.KEY_DATA, data);
		return resp;
	}
	
	/**
	 * reset the login password by sms
	 * if the sms is valid , and the userName is existing then the login password will be reset
	 *  finally , redirect the user to login page.
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userName}/attrs/login-pwd/reset/sms", method = { RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> resetLoginPwdBySMS(@PathVariable(name="userName", required = true) String userName,
			@RequestBody Map<String, String> params) {
		Map<String, Object> resp = new HashMap<String, Object>();
		Map<String,Object> data = new HashMap<>();
		String sms = params.get("sms");
		UserInfo user = new UserInfo();
		user.setUserName(userName);
		
		boolean isExisting = userInfoService.isUserExisting(user);
		if(!isExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return resp;
		}
		
		user = userInfoService.getUserByUserName(userName);
		
		boolean isSmsValid = smsServ.isSmsValid(user, sms);
		
		if(!isSmsValid) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_TP_INVALID_SMS.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_TP_INVALID_SMS.getErrorMes());
			return resp;
		}
		
		try {
			userInfoService.resetLoginPwd(user);			
		}catch(Exception ex) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_FAILED_RESET_LOGIN_PWD_SMS.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_FAILED_RESET_LOGIN_PWD_SMS.getErrorMes());
			return resp;		
		}
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		
		data.put(Message.KEY_DEFAULT_PASSWORD, defaultPwd);
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		resp.put(Message.KEY_DATA, data);
		return resp;
	}
	
	/**
	 * apply for reset login password by email
	 * sending the [reset login password by emial] URL to specified email
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userName}/attrs/login-pwd/pre-reset/email", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> applyResetLoginPwdByEmail(@PathVariable("userName") String userName) {
		Map<String, Object> resp = new HashMap<String, Object>();
		UserInfo user = new UserInfo();
		user.setUserName(userName);
		
		boolean isExisting = userInfoService.isUserExisting(user);
		if(!isExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return resp;
		}
		
		user = userInfoService.getUserByUserName(userName);
		boolean ifEmailValid = (user.getIsValidEmail() != null && user.getIsValidEmail() == 1)?true:false;
		if(!ifEmailValid) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_INVALID_EMAIL.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_INVALID_EMAIL.getErrorMes());
			return resp;
		}
		
		try {
			String ret = emailServ.sendingEmail(user, request.getServerName());
			if(!Integer.toString(Message.status.SUCCESS.getCode()).equals(ret)) {
				resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				resp.put(Message.KEY_ERROR_CODE, ret);
				resp.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(ret).getErrorMes());
				return resp;
			}
		} catch (Exception e) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_TP_SENDING_EMAIL.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_TP_SENDING_EMAIL.getErrorMes());
			return resp;
		}

		Map<String,Object> data = new HashMap<>();
		data.put(Message.KEY_EXPIRED_TIME, captchaCodeExpiredTime);
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		resp.put(Message.KEY_DATA, data);
		return resp;
	}
	
	/**
	 * valid the verify code , if verify code is valid,then reset the login password
	 * finally , redirect the user to login page.
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userName}/attrs/login-pwd/reset/email", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> resetLoginPwdByEmail(@PathVariable(name = "userName", required = true) String userName,
			@RequestParam(name = "verifyCode", required = true) String verifyCode) {
		Map<String, Object> resp = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		
		UserInfo user = new UserInfo();
		user.setUserName(userName);
		
		boolean isExisting = userInfoService.isUserExisting(user);
		if(!isExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return resp;
		}
		
		user = userInfoService.getUserByUserName(userName);
		
		boolean isEmailValid = smsServ.isSmsValid(user, verifyCode);
		
		if(!isEmailValid) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_TP_INVALID_SMS.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_TP_INVALID_SMS.getErrorMes());
			return resp;
		}
		
		try {
			userInfoService.resetLoginPwd(user);			
		}catch(Exception ex) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_FAILED_RESET_LOGIN_PWD_EMAIL.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_FAILED_RESET_LOGIN_PWD_EMAIL.getErrorMes());
			return resp;		
		}
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		data.put("default_password", defaultPwd);
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		resp.put(Message.KEY_DATA, data);
		return resp;
	}
	
	/**
	 * revoke the specified user by userId
	 * only the operator with role role_admin
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userId}", method = { RequestMethod.DELETE}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> revokeUser(@PathVariable("userId") int userId) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
	}
	
	@ApiComment("Get User notify lists")
	@RequestMapping(value="/{userId}/notify", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserNotifyLists(@PathVariable("userId") int userId) {
		return userInfoService.getUserNotifyLists(userId);
	}
	
	@ApiComment("Get User Site Message lists")
	@RequestMapping(value="/{userId}/site-message/list", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserSiteMessageLists(@PathVariable("userId") int userId) {
		return userInfoService.getUserSiteMessageLists(userId);
	}
	
	@ApiComment("Show Site Message History Feedback")
	@RequestMapping(value="/{userId}/site-message/history-feedback", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> showSiteMessageFeedback(@PathVariable("userId") int userId,
			 @RequestParam(name = "msgId", required = true) int msgId) {
		return userInfoService.showSiteMessageFeedback(userId,msgId);
	}
	
	@ApiComment("Feedback Site Message ")
	@RequestMapping(value="/{userId}/site-message/feedback", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> siteMessageFeedback(@PathVariable("userId") int userId,
			@RequestBody SiteMessFeedback back,
			@RequestParam(name = "msgId", required = true) int msgId) {
		return userInfoService.siteMessageFeedback(userId,msgId,back);
	}
	
	/**
	 * 
	 * Add Site Message 
	 * 
	 * @param userId
	 * @param msg
	 * @param sendIds value is ALL, if user type is agent,then send all agent low user or agent, if user type is system user ,then send all user
	 *  value is 0001,then send user id value is 0001 user,
	 *  value is 0001,002 ,then send  user id value is 0001 or 0002 user
	 *  value is empty ,then send to system
	 *  
	 * @return
	 */
	
	@ApiComment("Add Site Message ")
	@RequestMapping(value="/{userId}/site-message/add", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> addSiteMessage(@PathVariable("userId") int userId,
			@RequestBody SiteMessage msg,
			@RequestParam("sendIds") String sendIds) {
		return userInfoService.addSiteMessage(userId,sendIds,msg);
	}
	
	@ApiComment("User exchange point")
	@RequestMapping(value="/{userId}/exchange-point", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> exchangePoint(
			@PathVariable("userId") int userId,
			@RequestParam("amount") double amount) {
		return userInfoService.exchangePoint(userId,amount);
	}
	
}

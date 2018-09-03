package com.jll.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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

import com.jll.common.constants.Constants.BankCardState;
import com.jll.common.constants.Constants.UserType;
import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
import com.jll.dao.PageBean;
import com.jll.dao.PageQueryDao;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.SysCode;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserBankCard;
import com.jll.entity.UserInfo;
import com.jll.entity.WithdrawApplication;
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
	@RequestMapping(value="/agents/{agent-id}", method = { RequestMethod.POST }, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> regAgent(@PathVariable("agent-id") Integer agentId,
			@RequestBody UserInfo user) {
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
	//重置登录密码
	@RequestMapping(value={"/resetLoginPwd"}, method={RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> resetLoginPwd(@RequestParam(name = "userId", required = true) Integer userId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			UserInfo user = userInfoService.getUserById(userId);
			userInfoService.resetLoginPwd(user);
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//重置支付密码
	@RequestMapping(value={"/resetFundPwd"}, method={RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> resetFundPwd(@RequestParam(name = "userId", required = true) Integer userId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			UserInfo user = userInfoService.getUserById(userId);
			userInfoService.resetFundPwd(user);
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//用户状态修改
	@RequestMapping(value={"/updateUserType"}, method={RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> updateUserType(@RequestParam(name = "userId", required = true) Integer userId,
			  @RequestParam(name = "userType", required = true) Integer userType,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			UserInfo user = userInfoService.getUserById(userId);
			user.setId(userId);
			user.setUserType(userType);
			userInfoService.updateUserType(user);
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询用户详细信息
	@RequestMapping(value={"/queryUserInfo"}, method={RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryUserInfo(@RequestParam(name = "userId", required = true) Integer userId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			UserInfo userInfo=userInfoService.getUserById(userId);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", userInfo);
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询所有用户
	@RequestMapping(value={"/queryAllUserInfo"}, method={RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryAllUserInfo(@RequestParam(name = "id", required = false) Integer id,
			  @RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "proxyName", required = false) String proxyName,//代理的名字
			  @RequestParam(name = "startTime", required = true) String startTime,
			  @RequestParam(name = "endTime", required = true) String endTime,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(!StringUtils.isBlank(proxyName)) {
			if(id!=null||!StringUtils.isBlank(userName)) {
				ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
				ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
		    	return ret;
			}
		}
		ret.put("id", id);
		ret.put("userName", userName);
		ret.put("proxyName", proxyName);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		try {
			List<UserInfo> userInfoList=userInfoService.queryAllUserInfo(ret);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", userInfoList);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	
	@ApiComment("User exchange point")
	@RequestMapping(value="/{userId}/exchange-point", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> exchangePoint(
			@PathVariable("userId") int userId,
			@RequestParam("amount") double amount) {
		return userInfoService.exchangePoint(userId,amount);
	}
	
	
	@ApiComment("User Profit Report")
	@RequestMapping(value="/{userName}/profit-report", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> userProfitReport(
			@PathVariable("userName") String userName,
			@RequestBody PageQueryDao page) {
		return userInfoService.userProfitReport(userName,page);
	}	
	
	/**
	 * 
	 * @param userName 用户名
	 * @param bankId   银行卡ID ,若小于0,则使用默认第一张银行卡，若为为银行卡id，则使用该id对应的银行卡
	 * @param amount   提款金额
	 * @param passoword 提款密码
	 * @return
	 */
	
    @ApiComment("User Withdraw apply")
	@RequestMapping(value="/{userName}/withdraw/apply", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> userWithdrawApply(
			 @PathVariable("userName") String userName,
			 @RequestParam("bankId") int bankId,
			 @RequestParam("amount") double amount,
			 @RequestParam("passoword") String passoword) {
		return userInfoService.userWithdrawApply(userName, bankId, amount,passoword);
	}
    
    
    @ApiComment(value="User Withdraw notices",seeClass=WithdrawApplication.class)
   	@RequestMapping(value="/{userName}/withdraw/notices", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
   	public Map<String, Object> userWithdrawNotices(
   			@PathVariable("userName") String userName,
   			@RequestBody WithdrawApplication wtd) {
   		return userInfoService.userWithdrawNotices(userName,wtd);
   	}
    
    
    /**
     * 
     * @param fromUser  额度转出的用户
     * @param toUser    额度转入的用户
     * @param amount  转入额度，如果小于0，则转出所有额度
     * @return
     * 
     */
    
    @ApiComment(value="User Amount Transfer",seeClass=WithdrawApplication.class)
   	@RequestMapping(value="/amount/transfer", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
   	public Map<String, Object> userAmountTransfer(
   			@RequestParam("fromUser") String fromUser,
   			@RequestParam("toUser") String toUser,
   			@RequestParam("amount") double amount) {
   		return userInfoService.userAmountTransfer(fromUser,toUser,amount);
   	}
	
	@ApiComment("test")
	@RequestMapping(value="/user-page", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryUserByPage(@RequestParam("pageSize") Integer pageSize,
			@RequestParam("pageIndex") Integer pageIndex) {
		Map<String, Object> ret = new HashMap<>();
		PageBean<UserInfo> reqPage = new PageBean<>();
		reqPage.setPageIndex(pageIndex);
		reqPage.setPageSize(pageSize);		
		
		PageBean<UserInfo> page = userInfoService.queryAllUserInfoByPage(reqPage);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA, page);
		return ret;
	}

	/**
	 * 代理开户功能:  
	 * 	1.查询总代下面的所有一级代理
	 *  2.点击代理查询下一级代理
	 * */
	//查询总代下面的所有一级代理
	@RequestMapping(value={"/queryAllAgent"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryAllAgent() {
		Map<String, Object> ret = new HashMap<>();
		try {
			List<UserInfo> userInfoLists=userInfoService.queryAllAgent();
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", userInfoLists);
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//点击代理查询下一级代理
	@RequestMapping(value={"/queryAgentByAgent"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryAgentByAgent(@RequestParam(name = "id", required = true) Integer id,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,Object> map=userInfoService.queryAgentByAgent(id);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	
	@ApiComment(value="User Red Wallet Amount Transfer",seeClass=WithdrawApplication.class)
   	@RequestMapping(value="/red-wallet/amount/transfer", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
   	public Map<String, Object> userRedWalletAmountTransfer(
   			@RequestParam("userName") String userName,
   			@RequestParam("amount") double amount) {
   		return userInfoService.userRedWalletAmountTransfer(userName,amount);
   	}
	
	@ApiComment(value="User Add Bank",seeClass=BankCardState.class)
   	@RequestMapping(value="/{userId}/bank/add", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
   	public Map<String, Object> addUserBank(
   			@PathVariable("userId") int userId,
   			@RequestBody UserBankCard bank) {
   		return userInfoService.addUserBank(userId, bank);
   	}
	
	@ApiComment(value="User Bank Lists",seeClass=BankCardState.class)
   	@RequestMapping(value="/{userId}/bank/lists", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
   	public Map<String, Object> getUserBankLists(
   			@PathVariable("userId") int userId) {
   		return userInfoService.getUserBankLists(userId);
   	}
	
	@ApiComment(value="User Bank Code Lists",seeClass=SysCode.class)
   	@RequestMapping(value="/bank/code", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
   	public Map<String, Object> getBankCodeList() {
   		return userInfoService.getBankCodeList();
   	}
	
	/**
	 * userId 用户ID
	 * 
	 * amount 操纵金额
	 * 
	 * operationType 流水类型
	 * 
	 * @param dtl
	 * @return
	 */
	
	@ApiComment(value="Direct Operation User Amount",seeClass=UserAccountDetails.class)
   	@RequestMapping(value="/operation/amount", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
   	public Map<String, Object> directOperationUserAmount(
   			@RequestBody UserAccountDetails dtl) {
   		return userInfoService.directOperationUserAmount(dtl);
   	}
	
	
	/**
	 * userId 用户ID
	 * 
	 * accType 钱包类型，若为<0,则冻结所有钱包
	 * 
	 * state 状态
	 * 
	 * @param dtl
	 * @return
	 */
	@ApiComment(value="Direct Operation User Amount",seeClass=UserAccount.class)
   	@RequestMapping(value="/wallet/lock", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
   	public Map<String, Object> userWalletLock(
   			@RequestBody UserAccount dtl) {
   		return userInfoService.userWalletLock(dtl);
   	}
	
}

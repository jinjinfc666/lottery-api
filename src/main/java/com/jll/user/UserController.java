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

import com.jll.common.annotation.LogsInfo;
import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.BankCardState;
import com.jll.common.constants.Constants.EmailValidState;
import com.jll.common.constants.Constants.PhoneValidState;
import com.jll.common.constants.Constants.UserType;
import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
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
import com.jll.user.bank.UserBankCardService;
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
	UserBankCardService userBankCardService;
	
	@Resource
	SMSService smsServ;
	
	@Resource
	EmailService emailServ;
	
	@Resource
	HttpServletRequest request;
	
	@Resource
	CacheRedisService cacheRedisService;
	
	@Value("${sys_captcha_code_expired_time}")
	private int captchaCodeExpiredTime;
	
	
	/**
	 * register the user who can login front-end web application
	 * this will be only called  by the agent
	 * @param request   给前台代理注册用户或者下级代理
	 */
	@RequestMapping(value="/players", method = { RequestMethod.POST }, produces=MediaType.APPLICATION_JSON_VALUE)
	@LogsInfo(logType=StringUtils.OPE_LOG_REG_USER)
	public Map<String, Object> regUser(@RequestBody UserInfo user,HttpServletRequest request) {
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
		if(user.getUserType()==null) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return resp;
		}
		String ret = userInfoService.validUserInfo(user, superior);
		if(StringUtils.isBlank(ret)) {
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
			userInfoService.regUser(user,request);
		}catch(Exception ex) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_FAILED_REGISTER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_FAILED_REGISTER.getErrorMes());
			return resp;
		}
				
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return resp;
	}
	
	
	
	@RequestMapping(value="/demo-players", method = { RequestMethod.GET }, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> regDemoUser(HttpServletRequest request) {
		return userInfoService.saveRandomDemoUserInfo(request);
	}
	
	/**
	 * register the agent
	 * this will be only called  by the user with role:role_admin
	 * @param request   后台用来添加代理或者用户
	 */
	@RequestMapping(value="/agents/{agent-id}", method = { RequestMethod.POST }, produces=MediaType.APPLICATION_JSON_VALUE)
	@LogsInfo(logType=StringUtils.OPE_LOG_REG_AGENT)
	public Map<String, Object> regAgent(@PathVariable("agent-id") Integer agentId,
			@RequestBody UserInfo user,
			HttpServletRequest request) {
		Map<String, Object> resp = new HashMap<String, Object>();
				
		
		UserInfo generalAgency = userInfoService.getUserById(agentId);
		if(generalAgency == null) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED);
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_GENERAL_AGENCY.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_GENERAL_AGENCY.getErrorMes());
			return resp;
		}
		if(StringUtils.isBlank(generalAgency.getSuperior())) {
			user.setSuperior(Integer.toString(generalAgency.getId()));
		}else {
			user.setSuperior(Integer.toString(generalAgency.getId()) +","+generalAgency.getSuperior());
		}
		if(user.getUserType()==null) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return resp;
		}
		String ret = userInfoService.validUserInfo(user, generalAgency);
		if(StringUtils.isBlank(ret)) {
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
			userInfoService.regUser(user,request);
		}catch(Exception ex) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_FAILED_REGISTER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_FAILED_REGISTER.getErrorMes());
			return resp;
		}
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return resp;
	}
//	//给前台用的
//	@RequestMapping(value="/nextAgents", method = { RequestMethod.POST }, produces=MediaType.APPLICATION_JSON_VALUE)
//	public Map<String, Object> regNextAgent(@RequestBody UserInfo user) {
//		Map<String, Object> resp = new HashMap<String, Object>();
//				
//		UserInfo generalAgency = userInfoService.getGeneralAgency();
//		if(generalAgency == null) {
//			resp.put(Message.KEY_STATUS, Message.status.FAILED);
//			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_GENERAL_AGENCY.getCode());
//			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_GENERAL_AGENCY.getErrorMes());
//			return resp;
//		}
//		
//		user.setSuperior(Integer.toString(generalAgency.getId()));
//		user.setUserType(UserType.AGENCY.getCode());
//		String ret = userInfoService.validUserInfo(user, generalAgency);
//		if(StringUtils.isBlank(ret) ) {
//			resp.put(Message.KEY_STATUS, Message.status.FAILED);
//			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
//			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
//			return resp;
//		}
//		
//		if(!Integer.toString(Message.status.SUCCESS.getCode()).equals(ret)) {
//			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
//			resp.put(Message.KEY_ERROR_CODE, ret);
//			resp.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(ret).getErrorMes());
//			return resp;
//		}
//		
//		if(StringUtils.isBlank(user.getFundPwd())) {
//			user.setFundPwd(user.getLoginPwd());
//		}
//		
//		boolean isExisting = userInfoService.isUserExisting(user);
//		if(isExisting) {
//			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
//			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_EXISTING.getCode());
//			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_EXISTING.getErrorMes());
//			return resp;
//		}		
//		
//		user.setRebate(generalAgency.getPlatRebate().subtract(user.getPlatRebate()));
//		try {
//			userInfoService.regUser(user);
//		}catch(Exception ex) {
//			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
//			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_FAILED_REGISTER.getCode());
//			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_FAILED_REGISTER.getErrorMes());
//			return resp;
//		}
//		
//		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//		return resp;
//	}
	/**
	 * register the system users
	 * this will be only called  by the user with role:role_admin
	 * @param request   增加系统用户
	 */
	@RequestMapping(value="/sys-users", method = { RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> regSysUser(@RequestBody UserInfo user,HttpServletRequest request) {
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
			userInfoService.regUser(user,request);
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
	 * @return  更新用户的基本信息  前台用的
	 */
	@ApiComment("update the basic information of user[real name,wechar,qq,phone,email]")
	@RequestMapping(value="/update-info", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	@LogsInfo(logType=StringUtils.OPE_LOG_PERFECT_USER_INFO)
	public Map<String, Object> updateUserBasic(
			@RequestBody UserInfo user) {
		return userInfoService.updateUserInfo(user);
	}
	
	/**
	 * Update User login password
	 * 
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 */
    @ApiComment("Update User Login Password")
	@RequestMapping(value="/attrs/login-pwd", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
    @LogsInfo(logType=StringUtils.OPE_LOG_MOD_LOGIN_PWD)
    public Map<String, Object> updateLoginPwd(@RequestBody Map<String, String> params) {
		String oldPwd = Utils.toString(params.get("oldPwd"));
		String newPwd = Utils.toString(params.get("newPwd"));
		String confirmPwd = Utils.toString(params.get("confirmPwd"));
		return userInfoService.updateLoginPwd(oldPwd, newPwd,confirmPwd);
	}
    /**
	 * Update User login password
	 * 给后台管理人员用来修改密码的
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 */
    @ApiComment("Update User Login Admin Password")
	@RequestMapping(value="/attrs/login-pwdAdmin", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
    @LogsInfo(logType=StringUtils.OPE_LOG_MOD_LOGIN_PWD)
    public Map<String, Object> updateLoginPwdAdmin(@RequestBody Map<String, String> params) {
		String oldPwd = Utils.toString(params.get("oldPwd"));
		String newPwd = Utils.toString(params.get("newPwd"));
		String confirmPwd = Utils.toString(params.get("confirmPwd"));
		Integer id=Integer.valueOf(params.get("id"));
		return userInfoService.updateLoginPwdAdmin(oldPwd, newPwd,confirmPwd,id);
	}
	
	/**
	 * 
	 *  Update User fund password
	 * 
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 */
    @ApiComment("Update User Fun Password")
	@RequestMapping(value="/attrs/fund-pwd", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
    @LogsInfo(logType=StringUtils.OPE_LOG_MOD_FUND_PWD)
    public Map<String, Object> updateFundPwd(@RequestBody Map<String, String> params) {
		String oldPwd = Utils.toString(params.get("oldPwd"));
		String newPwd = Utils.toString(params.get("newPwd"));
		String confirmPwd = Utils.toString(params.get("confirmPwd"));
		return userInfoService.updateFundPwd(oldPwd, newPwd,confirmPwd);
	}
    
    
    /**
     * Get User Info
     * if login user has role[role_user_info] ,show all info
     * @param userName
     * @return
     */
    @ApiComment("Get User Info")
	@RequestMapping(value="/info", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserInfo() {
    	
		return userInfoService.getUserInfo();
	}
	

	/**
	 * apply for sms
	 * this operation will generate a sms to the specified phone number
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userName}/verify/phone-apply", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> applyVerifyPhone(@PathVariable("userName") String userName) {
		Map<String, Object> resp = new HashMap<String, Object>();
		UserInfo	user = userInfoService.getUserByUserName(userName);
		
		if(null== user) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return resp;
		}
		
		if(PhoneValidState.UNVERIFIED.getCode() != user.getIsValidPhone()) {
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
	 * verify the received sms ,
	 * if pass verification, then the phone number will be saved to current user and change the flag to pass verification.
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userName}/verify/phone", method = { RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	@LogsInfo(logType=StringUtils.OPE_LOG_VERIFY_PHONE)
	public Map<String, Object> verifyPhone(@PathVariable(name="userName", required = true) String userName,
			@RequestBody Map<String, String> params) {
		Map<String, Object> resp = new HashMap<String, Object>();
		Map<String,Object> data = new HashMap<>();
		String sms = Utils.toString(params.get("sms"));
		UserInfo user = userInfoService.getUserByUserName(userName);
		
		if(null== user) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return resp;
		}
		
		boolean isSmsValid = smsServ.isSmsValid(user, sms);
		
		if(!isSmsValid) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_TP_INVALID_SMS.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_TP_INVALID_SMS.getErrorMes());
			return resp;
		}
		
		try {
			user.setIsValidPhone(PhoneValidState.VERIFIED.getCode());
			userInfoService.updateUser(user);			
		}catch(Exception ex) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_FAILED_RESET_LOGIN_PWD_SMS.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_FAILED_RESET_LOGIN_PWD_SMS.getErrorMes());
			return resp;		
		}
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		resp.put(Message.KEY_DATA, data);
		return resp;
	}
	
	
	@RequestMapping(value="/{userName}/verify/email-apply", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> applyVerifyEmail(@PathVariable("userName") String userName) {
		Map<String, Object> resp = new HashMap<String, Object>();
		UserInfo	user = userInfoService.getUserByUserName(userName);
		
		if(null== user) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return resp;
		}
		
		if(EmailValidState.UNVERIFIED.getCode() != user.getIsValidEmail()) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_INVALID_PHONE_NUMBER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_INVALID_PHONE_NUMBER.getErrorMes());
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
	 * verify the received sms ,
	 * if pass verification, then the phone number will be saved to current user and change the flag to pass verification.
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userName}/verify/email", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	@LogsInfo(logType=StringUtils.OPE_LOG_VERIFY_EMAIL)
	public Map<String, Object> verifyEmail(@PathVariable(name = "userName", required = true) String userName,
			@RequestParam(name = "verifyCode", required = true) String verifyCode) {
		
		Map<String, Object> resp = new HashMap<String, Object>();
		Map<String,Object> data = new HashMap<>();
		
		UserInfo user = userInfoService.getUserByUserName(userName);
		
		if(null== user) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return resp;
		}
		boolean isSmsValid = emailServ.isEmailValid(user, verifyCode);
		if(!isSmsValid) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_TP_INVALID_SMS.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_TP_INVALID_SMS.getErrorMes());
			return resp;
		}
		
		try {
			user.setIsValidEmail(EmailValidState.VERIFIED.getCode());
			userInfoService.updateUser(user);			
		}catch(Exception ex) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_FAILED_RESET_LOGIN_PWD_SMS.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_FAILED_RESET_LOGIN_PWD_SMS.getErrorMes());
			return resp;		
		}
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		resp.put(Message.KEY_DATA, data);
		return resp;
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
	@LogsInfo(logType=StringUtils.OPE_LOG_RESET_PWD)
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
	@LogsInfo(logType=StringUtils.OPE_LOG_RESET_PWD)
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
		
		boolean isEmailValid = emailServ.isEmailValid(user, verifyCode);
		
		if(!isEmailValid) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_TP_INVALID_EMAIL.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_TP_INVALID_EMAIL.getErrorMes());
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
		
		data.put(Message.KEY_DEFAULT_PASSWORD, defaultPwd);
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		resp.put(Message.KEY_DATA, data);
		return resp;
	}
	
	@ApiComment("Get User notify lists")
	@RequestMapping(value="/notify/lists", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserNotifyLists() {
		return userInfoService.getUserNotifyLists();
	}
	
	@ApiComment("Get User Site Message lists")
	@RequestMapping(value="/site-msg/lists", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserSiteMessageLists() {
		return userInfoService.getUserSiteMessageLists();
	}
	
	@ApiComment("Show Site Message History Feedback")
	@RequestMapping(value="/site-msg/{msgId}/history-feedback", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> showSiteMessageFeedback(@PathVariable("msgId") int msgId) {
		return userInfoService.showSiteMessageFeedback(msgId);
	}
	
	@ApiComment("Feedback Site Message ")
	@RequestMapping(value="/site-msg/feedback", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> siteMessageFeedback(@RequestBody SiteMessFeedback back) {
		
		return userInfoService.updateMessageFeedbackStatus(back);
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
	@RequestMapping(value="/site-msg/add", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> addSiteMessage(@RequestBody Map<String, String> params) {
		SiteMessage msg = new SiteMessage();
		msg.setContent( Utils.toString(params.get("content")));
		msg.setTitle( Utils.toString(params.get("title")));
		return userInfoService.saveSiteMessage("",msg);
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
	//后台管理员修改用户状态和类型
	@RequestMapping(value={"/updateUserType"}, method={RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> updateUserType(@RequestBody UserInfo userInfo) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Integer userId=userInfo.getId();
			Integer state=userInfo.getState();
			Integer userType=userInfo.getUserType();
			UserInfo user = userInfoService.getUserById(userId);
			user.setId(userId);
			if(userType!=null) {
				user.setUserType(userType);
			}
			if(state!=null) {
				user.setState(state);
			}
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
			ret.clear();
			ret=userInfoService.getUserNameById(userId);
		}catch(Exception e){
			ret.clear();
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
			  @RequestParam(name = "pageIndex", required = true) Integer pageIndex,//当前请求页
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		Map<String, Object> map = new HashMap<>();
		if(!StringUtils.isBlank(proxyName)) {
			if(id!=null||!StringUtils.isBlank(userName)) {
				ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
				ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
		    	return ret;
			}
		}
		Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
		ret.put("pageSize", pageSize);
		ret.put("pageIndex", pageIndex);
		ret.put("id", id);
		ret.put("userName", userName);
		ret.put("proxyName", proxyName);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		try {
			map=userInfoService.queryAllUserInfo(ret);
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//查询所有代理
	@RequestMapping(value={"/queryAllUserAgent"}, method={RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryAllUserAgent(@RequestParam(name = "userName", required = false) String userName,
			  @RequestParam(name = "pageIndex", required = true) Integer pageIndex,//当前请求页
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		Map<String, Object> map = new HashMap<>();
		Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
		ret.put("pageSize", pageSize);
		ret.put("pageIndex", pageIndex);
		ret.put("userName", userName);
		try {
			map=userInfoService.queryAllAgent(ret);
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	
	@ApiComment("User exchange point")
	@RequestMapping(value="/exchange-point", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> exchangePoint(@RequestBody Map<String, String> params) {
		double amount = Utils.toDouble(params.get("amount"));
		return userInfoService.processExchangePoint(amount);
	}
	
//	//会员盈亏信息
//	@ApiComment("User Profit Report")
//	@RequestMapping(value="/profit-report", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
//	public Map<String, Object> userProfitReport(@RequestBody Map<String, String> params) {
//		PageQueryDao page = new PageQueryDao(Utils.toDate(params.get("startDate")),Utils.toDate(params.get("endDate")),Utils.toInteger(params.get("pageIndex")),
//				Utils.toInteger(params.get("pageSize")));
//		return userInfoService.userProfitReport(page);
//	}	
	
	/**
	 * 
	 * @param bankId   银行卡ID ,若小于0,则使用默认第一张银行卡，若为为银行卡id，则使用该id对应的银行卡
	 * @param amount   提款金额
	 * @param passoword 提款密码
	 * @return
	 */
	
    @ApiComment("User Withdraw apply")
	@RequestMapping(value="/withdraw/apply", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> userWithdrawApply(@RequestBody Map<String, String> params) {
		int bankId = Utils.toInteger(params.get("bankId"));
		String passoword = Utils.toString(params.get("passoword"));
		double amount = Utils.toDouble(params.get("amount"));
		return userInfoService.saveUpdateUserWithdrawApply(bankId, amount,passoword);
	}
    
    
    @ApiComment(value="User Withdraw notices",seeClass=WithdrawApplication.class)
   	@RequestMapping(value="/withdraw/notices", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
    @LogsInfo(logType=StringUtils.OPE_LOG_PROCESS_WITHDRAW)
    public Map<String, Object> userWithdrawNotices(@RequestBody Map<String, String> params) {
		WithdrawApplication wtd = new WithdrawApplication();
		wtd.setOrderNum(Utils.toString(params.get("orderNum")));
		wtd.setRemark(Utils.toString(params.get("remark")));
		wtd.setState(Utils.toInteger(params.get("state")));
   		return userInfoService.saveUpdateUserWithdrawNotices(wtd);
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
   	public Map<String, Object> userAmountTransfer(@RequestBody Map<String, String> params) {
		String fromUser = Utils.toString(params.get("fromUser"));
		String toUser = Utils.toString(params.get("toUser"));
		double amount = Utils.toDouble(params.get("amount"));
		
   		return userInfoService.processUserAmountTransfer(fromUser,toUser,amount);
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
	//前端登录的代理查询下一级代理
	@RequestMapping(value={"/QDAgentNextAgent"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryQDAgentNextAgent(@RequestParam(name = "id", required = false) Integer id,
			  HttpServletRequest request) {
		if(id==null) {
			String userName=SecurityContextHolder.getContext().getAuthentication().getName();//当前登录的用户
			UserInfo userInfo=userInfoService.getUserByUserName(userName);
			id=userInfo.getId();
		}
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
	
	/**
	 * 
	 * @param bankId   银行卡ID ,若小于0,则使用默认第一张银行卡，若为为银行卡id，则使用该id对应的银行卡
	 * @param amount   提款金额
	 * @param passoword 提款密码
	 * @return
	 */
	@ApiComment(value="User Red Wallet Amount Transfer",seeClass=WithdrawApplication.class)
   	@RequestMapping(value="/red-wallet/amount/transfer", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
   	public Map<String, Object> userRedWalletAmountTransfer(@RequestBody Map<String, String> params) {
		
		int bankId = Utils.toInteger(params.get("bankId"));
		String passoword = Utils.toString(params.get("passoword"));
		double amount = Utils.toDouble(params.get("amount"));
		
   		return userInfoService.processUserRedWalletAmountTransfer(bankId, amount,passoword);
   	}
	
	@ApiComment(value="User Add Bank",seeClass=BankCardState.class)
   	@RequestMapping(value="/{userId}/bank/add", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	@LogsInfo(logType=StringUtils.OPE_LOG_ADD_BANK_CARD)
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
	 * walletId 钱包类型
	 * 
	 * operationType 流水类型
	 * 
	 * @param dtl
	 * @return
	 */
	
	@ApiComment(value="Direct Operation User Amount",seeClass=UserAccountDetails.class)
   	@RequestMapping(value="/operation/amount", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	@LogsInfo(logType=StringUtils.OPE_LOG_OPER_USER_AMT)
	public Map<String, Object> directOperationUserAmount(
   			@RequestBody UserAccountDetails dtl) {
   		return userInfoService.saveUpdateDirectOperationUserAmount(dtl);
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
   		return userInfoService.updateUserWalletLockStatus(dtl);
   	}
	
	//通过用户名查询用户详细信息(当前登录用户)
	@RequestMapping(value={"/byUserNameUserInfo"}, method={RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryByUserNameUserInfo() {
		Map<String, Object> ret = new HashMap<>();
		try {
			String userName=SecurityContextHolder.getContext().getAuthentication().getName();//当前登录的用户
			UserInfo userInfo=userInfoService.getUserByUserName(userName);
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
	//通过用ID查询用户银行卡   给后台管理页面用的
	@RequestMapping(value={"/byUIBankList"}, method={RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryByUserIdBankList(@RequestParam(name = "id", required = true) Integer id,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			return userInfoService.queryByUserIdBankList(id);
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//通过用户名查询用户银行卡   给前台用的
	@RequestMapping(value={"/byUNUBankList"}, method={RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryByUserNameBankList() {
		Map<String, Object> ret = new HashMap<>();
		try {
			return userInfoService.queryByUserNameBankList();
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//返回给前台用户是否可以添加银行卡
	@RequestMapping(value={"/isOrAddBank"}, method={RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> isOrAddBank() {
		Map<String, Object> ret = new HashMap<>();
		try {
			return userInfoService.isOrAddBank();
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//前台用户自己添加银行卡
	@RequestMapping(value={"/userAddBank"}, method={RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> addBank(@RequestBody UserBankCard bank) {
		Map<String, Object> ret = new HashMap<>();
		try {
			return userInfoService.addUserBank(bank);
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//前台用户自己删除银行卡
	@RequestMapping(value={"/deleteBank"}, method={RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> deleteBank(@RequestParam(name = "bankId", required = true) Integer bankId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			return userBankCardService.deleteBank(bankId);
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//用户自己修改信息
	@RequestMapping(value={"/updateUserInfo"}, method={RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> updateUserInfo(@RequestBody UserInfo user) {
		return userInfoService.updateUserInfo(user);
	}
	//用户可以拥有的银行卡数量
	@RequestMapping(value={"/getBankNumber"}, method={RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getBankNumber() {
		Map<String, Object> ret = new HashMap<>();
		try {
			String keySysRuntimeArg = Constants.SysCodeTypes.SYS_RUNTIME_ARGUMENT.getCode();
			String numBank = Constants.SysRuntimeArgument.NUMBER_OF_BANK_CARDS.getCode();
			SysCode sysCode=cacheRedisService.getSysCode(keySysRuntimeArg, numBank);
			String codeVal=sysCode.getCodeVal();
			ret.put("data", codeVal);
		}catch(Exception e){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	
	
	/**
	 * orderNum 订单号
	 * @param params
	 * @return
	 */
	//用户撤单  
	@RequestMapping(value={"/cancel/bet-order"}, method={RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> cancelBetOrder(@RequestBody Map<String, String> params) {
		return userInfoService.processCancelBetOrder(Utils.toString(params.get("orderNum")));
	}
}

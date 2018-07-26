package com.jll.user;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
import com.jll.entity.UserInfo;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "userInfo", name = "user Info")
@ApiComment(seeClass = UserInfo.class)
@RestController
@RequestMapping({ "/users" })
public class UserController {
	private Logger logger = Logger.getLogger(UserController.class);

	@Resource
	UserInfoService userServ;
	
	/**
	 * query the specified user by userName, only the operator with userName or operator with role:role_bus_manager
	 * can obtain the full information, the person without permission can only read part of information.
	 * @param request
	 */
	@RequestMapping(value="/attrs/user-name/{userName}", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryUserByUserName(@PathVariable("userName") String userName) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS);
		return resp;
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
	 * this will be only called  by the agent with role:role_agent
	 * @param request
	 */
	@RequestMapping(value="/players", method = { RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
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
			superior = userServ.getUserByUserName(auth.getName());
			
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
		String ret = userServ.validUserInfo(user);
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
			isExisting = userServ.isUserExisting(user);
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
		
		user.setUserType(UserType.PLAYER.getCode());
		
		try {
			userServ.regUser(user);
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
	@RequestMapping(value="/agents", method = { RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> regAgent(@RequestBody UserInfo user) {
		Map<String, Object> resp = new HashMap<String, Object>();
				
		UserInfo generalAgency = userServ.getGeneralAgency();
		if(generalAgency == null) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED);
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_GENERAL_AGENCY.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_GENERAL_AGENCY.getErrorMes());
			return resp;
		}
		
		user.setSuperior(Integer.toString(generalAgency.getId()));
		String ret = userServ.validUserInfo(user);
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
		
		boolean isExisting = userServ.isUserExisting(user);
		if(isExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_EXISTING.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_EXISTING.getErrorMes());
			return resp;
		}		
		
		user.setUserType(UserType.AGENCY.getCode());
		try {
			userServ.regUser(user);
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
	@RequestMapping(value="/sys-users", method = { RequestMethod.POST }, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> regSysUser(@RequestBody UserInfo user) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
	}
	
	/**
	 * update the basic information of user
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userId}", method = { RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> updateUserBasic(@PathVariable("userId") int userId,
			@RequestBody UserInfo user) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
	}
	
	/**
	 * change the 
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userId}/attrs/login-pwd", method = { RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> updateLoginPwd(@PathVariable("userId") int userId,
			@RequestBody UserInfo user) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
	}
	
	/**
	 * update the basic information of user
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userId}/attrs/fund-pwd", method = { RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> updateFundPwd(@PathVariable("userId") int userId,
			@RequestBody UserInfo user) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
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
	public Map<String, Object> applyResetLoginPwdBySMS(@PathVariable("userName") String userName) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
	}
	
	/**
	 * reset the login password by sms
	 * if the sms is valid , and the userName is existing then the login password will be reset
	 *  finally , redirect the user to login page.
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userName}/attrs/login-pwd/reset/sms", method = { RequestMethod.PUT}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> resetLoginPwdBySMS(@PathVariable("userName") String userName,
			@RequestParam("sms") String sms) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
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
		
		
		return null;
	}
	
	/**
	 * valid the verify code , if verify code is valid,then reset the login password
	 * finally , redirect the user to login page.
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userName}/attrs/login-pwd/reset/email", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> resetLoginPwdByEmail(@PathVariable("userName") String userName,
			@RequestParam("verifyCode") String verifyCode) {
		Map<String, Object> resp = new HashMap<String, Object>();
		
		
		return null;
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
}

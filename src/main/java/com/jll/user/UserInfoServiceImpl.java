package com.jll.user;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants.EmailValidState;
import com.jll.common.constants.Constants.PhoneValidState;
import com.jll.common.constants.Constants.UserLevel;
import com.jll.common.constants.Constants.UserState;
import com.jll.common.constants.Constants.UserType;
import com.jll.common.constants.Message;
import com.jll.common.utils.Utils;
import com.jll.entity.UserInfo;
import com.jll.user.wallet.WalletService;


@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService
{
	private Logger logger = Logger.getLogger(UserInfoServiceImpl.class);
	
	@Resource
	UserInfoDao userDao;

	@Resource
	WalletService walletServ;
	
	
	@Override
	public int getUserId(String userName) {
		return userDao.getUserId(userName);
	}

	@Override
	public boolean isUserInfo(String userName) {
		long count=userDao.getCountUser(userName);
		return count == 0 ? false:true;
	}

	@Override
	public UserInfo getUserByUserName(String userName) {
		return userDao.getUserByUserName(userName);
	}

	@Override
	public String validUserInfo(UserInfo user, UserInfo superior) {
		if(user == null) {
			return Message.Error.ERROR_USER_NO_VALID_USER.getCode();
		}
		
		if(StringUtils.isBlank(user.getUserName())
				|| !Utils.validUserName(user.getUserName())) {
			return Message.Error.ERROR_USER_INVALID_USER_NAME.getCode();
		}
		
		if(StringUtils.isBlank(user.getLoginPwd())
				|| !Utils.validPwd(user.getLoginPwd())) {
			return Message.Error.ERROR_USER_INVALID_USER_LOGIN_PWD.getCode();
		}
		
		if(!StringUtils.isBlank(user.getFundPwd())
				&& !Utils.validPwd(user.getFundPwd())) {
			return Message.Error.ERROR_USER_INVALID_USER_FUND_PWD.getCode();
		}
		
		if(!StringUtils.isBlank(user.getEmail())
				&& !Utils.validEmail(user.getEmail())) {
			return Message.Error.ERROR_USER_INVALID_EMAIL.getCode();
		}
		
		if(!StringUtils.isBlank(user.getPhoneNum())
				&& !Utils.validPhone(user.getPhoneNum())) {
			return Message.Error.ERROR_USER_INVALID_PHONE_NUMBER.getCode();
		}
		
		if(!StringUtils.isBlank(user.getRealName())
				&& !Utils.validRealName(user.getRealName())) {
			return Message.Error.ERROR_USER_INVALID_REAL_NAME.getCode();
		}
		
		if(user.getUserType() != null
				&& user.getUserType().intValue() != 0
				&& user.getUserType().intValue() != 1
				&& user.getUserType().intValue() != 2
				&& user.getUserType().intValue() != 3) {
			return Message.Error.ERROR_USER_INVALID_USER_TYPE.getCode();
		}
		
		if(user.getUserType() != UserType.SYS_ADMIN.getCode()) {
			if(user.getPlatRebate() == null
					|| (user.getPlatRebate().compareTo(superior.getPlatRebate())) == 1) {
				return Message.Error.ERROR_USER_INVALID_PLAT_REBATE.getCode();
			}			
		}
		
		
		return Integer.toString(Message.status.SUCCESS.getCode());
	}

	@Override
	public boolean isUserExisting(UserInfo user) {		
		
		return userDao.isUserExisting(user);
	}

	@Override
	public void regUser(UserInfo user) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loginUserName = null;
		
		user.setLoginPwd(encoder.encode(user.getLoginPwd()));
		
		if(user.getUserType() != UserType.SYS_ADMIN.getCode()) {
			user.setFundPwd(encoder.encode(user.getFundPwd()));
		}
		user.setCreateTime(new Date());
		user.setIsValidEmail(EmailValidState.UNVERIFIED.getCode());
		user.setIsValidPhone(PhoneValidState.UNVERIFIED.getCode());
		user.setLoginCount(0);
		user.setState(UserState.NORMAL.getCode());
		user.setLevel(UserLevel.LEVEL_0.getCode());
		
		if(auth == null) {
			logger.error(Message.Error.ERROR_COMMON_ERROR_LOGIN.getErrorMes());
			throw new RuntimeException(Message.Error.ERROR_COMMON_ERROR_LOGIN.getErrorMes());
		}
		
		loginUserName = auth.getName();
		
		UserInfo loginUser = userDao.getUserByUserName(loginUserName);
		
		if(loginUser == null) {
			logger.error(Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			throw new RuntimeException(Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
		}
		
		user.setCreator(loginUser.getId());
		
		userDao.saveUser(user);
		walletServ.createWallet(user);
	}

	@Override
	public UserInfo getGeneralAgency() {
		return userDao.getGeneralAgency();
	}
}

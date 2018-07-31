package com.jll.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.EmailValidState;
import com.jll.common.constants.Constants.PhoneValidState;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.constants.Constants.SysCodeUseLists;
import com.jll.common.constants.Constants.UserLevel;
import com.jll.common.constants.Constants.UserState;
import com.jll.common.constants.Constants.UserType;
import com.jll.common.constants.Message;
import com.jll.common.utils.SecurityUtils;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
import com.jll.dao.SupserDao;
import com.jll.entity.SysCode;
import com.jll.entity.UserBankCard;
import com.jll.entity.UserInfo;
import com.jll.sysSettings.codeManagement.SysCodeService;
import com.jll.user.wallet.WalletService;

@Configuration
@PropertySource("classpath:sys-setting.properties")
@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService
{
	private Logger logger = Logger.getLogger(UserInfoServiceImpl.class);
	
	@Resource
	UserInfoDao userDao;
	
	@Resource
	SupserDao  supserDao; 

	@Resource
	WalletService walletServ;
	
	@Resource
	SysCodeService sysCodeService;
	
	@Value("${sys_reset_pwd_default_pwd}")
	String defaultPwd;
	
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
	public boolean isUserInfoByUid(int userId) {
		return null == supserDao.get(UserInfo.class,userId);
	}

	@Override
	public Map<String, Object> updateFundPwd(String userName, String oldPwd, String newPwd) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = (UserInfo) supserDao.get(UserInfo.class,"userName",userName);
		if(StringUtils.checkFundPwdFmtIsOK(newPwd)
				|| !newPwd.equals(oldPwd)
				|| null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		BCryptPasswordEncoder bcEncoder = new  BCryptPasswordEncoder();
		String chargePwd = bcEncoder.encode(newPwd);
		if(!chargePwd.equals(dbInfo.getFundPwd())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_OLD_FUND_PWD_ERROR);
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_OLD_FUND_PWD_ERROR.getErrorMes());
			return ret;
		}
		dbInfo.setFundPwd(chargePwd);
		supserDao.update(dbInfo);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> updateLoginPwd(String userName, String oldPwd, String newPwd) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = (UserInfo) supserDao.get(UserInfo.class,"userName",userName);
		if(StringUtils.checkLoginPwdFmtIsOK(newPwd)
				|| !newPwd.equals(oldPwd)
				|| null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		BCryptPasswordEncoder bcEncoder = new  BCryptPasswordEncoder();
		String chargePwd = bcEncoder.encode(newPwd);
		if(!chargePwd.equals(dbInfo.getLoginPwd())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_OLD_LOGIN_PWD_ERROR);
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_OLD_LOGIN_PWD_ERROR.getErrorMes());
			return ret;
		}
		dbInfo.setLoginPwd(chargePwd);
		supserDao.update(dbInfo);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> getUserInfoByUserName(String userName) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = (UserInfo) supserDao.get(UserInfo.class,"userName",userName);
		if(null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		if(!SecurityUtils.checkPermissionIsOK(SecurityContextHolder.getContext().getAuthentication(), SecurityUtils.PERMISSION_ROLE_USER_INFO)){
			//真实姓名只显示第一个字，电话号码只显示后面三位，电子邮件只显示头三个字母以及邮箱地址，微信和qq都只显示后面三位字母
			dbInfo.setPhoneNum(StringUtils.abbreviate(dbInfo.getPhoneNum(),3,StringUtils.MORE_ASTERISK));
			if(!StringUtils.isEmpty(dbInfo.getEmail())){
				String[] emails = dbInfo.getEmail().split("@");
				dbInfo.setEmail(StringUtils.abbreviate(emails[0],3,StringUtils.MORE_ASTERISK)+"@"+emails[1]);
			}
			dbInfo.setWechat(StringUtils.abbreviate(dbInfo.getWechat(),3,StringUtils.MORE_ASTERISK));
			dbInfo.setQq(StringUtils.abbreviate(dbInfo.getQq(),3,StringUtils.MORE_ASTERISK));
			
		}
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA, dbInfo);
		return ret;
	}

	@Override
	public Map<String, Object> updateUserInfoInfo(UserInfo userInfo) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = (UserInfo) supserDao.get(UserInfo.class,"userName",userInfo.getUserName());
		
		if(null == dbInfo
				|| !StringUtils.checkEmailFmtIsOK(userInfo.getEmail())
				|| !StringUtils.checkRealNameFmtIsOK(userInfo.getRealName())
				|| !StringUtils.checkQqFmtIsOK(userInfo.getQq())
				|| !StringUtils.checkWercharFmtIsOK(userInfo.getWechat())
				|| !StringUtils.checkPhoneFmtIsOK(userInfo.getPhoneNum())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		boolean isAdmin = SecurityUtils.checkPermissionIsOK(SecurityContextHolder.getContext().getAuthentication(), SecurityUtils.PERMISSION_ROLE_ADMIN);
		
		if(!isAdmin 
				&& !StringUtils.isEmpty(dbInfo.getRealName())
				&& !StringUtils.isEmpty(userInfo.getRealName())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_MORE_UPDATE_REAL_NAME.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_MORE_UPDATE_REAL_NAME.getErrorMes());
			return ret;
		}else if((!isAdmin 
						&& StringUtils.isEmpty(dbInfo.getRealName())
						&& !StringUtils.isEmpty(userInfo.getRealName()))
				||(isAdmin && !StringUtils.isEmpty(userInfo.getRealName()))){
			dbInfo.setRealName(userInfo.getRealName());
		}
		
		
		if(!isAdmin 
				&& !StringUtils.isEmpty(dbInfo.getEmail())
				&& !StringUtils.isEmpty(userInfo.getEmail())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_MORE_UPDATE_EMAIL.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_MORE_UPDATE_EMAIL.getErrorMes());
			return ret;
		}else if((!isAdmin 
						&& StringUtils.isEmpty(dbInfo.getEmail())
						&& !StringUtils.isEmpty(userInfo.getEmail()))
				||(isAdmin && !StringUtils.isEmpty(userInfo.getEmail()))){
			dbInfo.setEmail(userInfo.getEmail());
			dbInfo.setIsValidEmail(Constants.EmailValidState.UNVERIFIED.getCode());
		}
		
		if(!isAdmin 
				&& !StringUtils.isEmpty(dbInfo.getPhoneNum())
				&& !StringUtils.isEmpty(userInfo.getPhoneNum())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_MORE_UPDATE_PHONE_NUM.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_MORE_UPDATE_PHONE_NUM.getErrorMes());
			return ret;
		}else if((!isAdmin 
						&& StringUtils.isEmpty(dbInfo.getEmail())
						&& !StringUtils.isEmpty(userInfo.getPhoneNum()))
				||(isAdmin && !StringUtils.isEmpty(userInfo.getPhoneNum()))){
			dbInfo.setPhoneNum(userInfo.getPhoneNum());
			dbInfo.setIsValidPhone(Constants.PhoneValidState.UNVERIFIED.getCode());
		}
		
		dbInfo.setWechat(userInfo.getWechat());
		dbInfo.setQq(userInfo.getQq());
		supserDao.update(dbInfo);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
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

	@Override
	public Map<String, Object> getUserBankLists(int userId) {
		Map<String, Object> ret = new HashMap<String, Object>();
		DetachedCriteria dc = DetachedCriteria.forClass(UserBankCard.class);
		dc.add(Restrictions.eq("userId",userId));
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,supserDao.findByCriteria(dc));
		return ret;
	}

	@Override
	public Map<String, Object> addUserBank(int userId, UserBankCard bank) {
		Map<String, Object> bankInfo = verifyUserBankInfo(userId, bank);
		if(null == bankInfo.get(Message.KEY_DATA)){
			return bankInfo;
		}
		bank.setBankCode(bankInfo.get(Message.KEY_DATA).toString());
		bank.setCreateTime(new Date());
		bank.setCreator(userId);
		bank.setState(Constants.BankCardState.ENABLED.getCode());
		return null;
	}

	@Override
	public Map<String, Object> getBankCodeList() {
		Map<String, Object> ret = new HashMap<>();
		List<SysCode> types = sysCodeService.queryType(SysCodeTypes.LOTTERY_TYPES.getCode());
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", types);
		return ret;
	}

	@Override
	public Map<String, Object> verifyUserBankInfo(int userId, UserBankCard bank) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> bankInfo =  getUserBankLists(userId);
		List<?> bankList = (List<?>) bankInfo.get(Message.KEY_DATA);
		int maxCardNum = Integer.valueOf(((SysCode)supserDao.get(SysCode.class, "codeName", SysCodeUseLists.MAX_BIND_BANK)).getCodeVal());
		if(bankList.size() == maxCardNum){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_MORE_BIND_BANK_CARD.getCode());
			ret.put(Message.KEY_ERROR_MES, String.format(Message.Error.ERROR_USER_MORE_BIND_BANK_CARD.getErrorMes(),maxCardNum));
			return ret;
		}
		List<?> checkList = supserDao.findByName(UserBankCard.class,"cardNum",bank.getCardNum());
		if(!checkList.isEmpty()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_BANK_CARD_HAS_BIND.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_BANK_CARD_HAS_BIND.getErrorMes());
			return ret;
		}
		ret =  Utils.validBankInfo(bank.getCardNum());
		return ret;
	}
	
	public void resetLoginPwd(UserInfo user) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();		
		user.setLoginPwd(encoder.encode(defaultPwd));
		
		userDao.saveUser(user);
	}
}

package com.jll.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.AccOperationType;
import com.jll.common.constants.Constants.DepositOrderState;
import com.jll.common.constants.Constants.EmailValidState;
import com.jll.common.constants.Constants.MainWallet;
import com.jll.common.constants.Constants.OrderState;
import com.jll.common.constants.Constants.PhoneValidState;
import com.jll.common.constants.Constants.RedWallet;
import com.jll.common.constants.Constants.SiteMessageReadType;
import com.jll.common.constants.Constants.State;
import com.jll.common.constants.Constants.SysCodeState;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.constants.Constants.SysNotifyReceiverType;
import com.jll.common.constants.Constants.SysNotifyType;
import com.jll.common.constants.Constants.SysRuntimeArgument;
import com.jll.common.constants.Constants.UserLevel;
import com.jll.common.constants.Constants.UserState;
import com.jll.common.constants.Constants.UserType;
import com.jll.common.constants.Constants.WalletState;
import com.jll.common.constants.Constants.WalletType;
import com.jll.common.constants.Constants.WithdrawConif;
import com.jll.common.constants.Constants.WithdrawOrderState;
import com.jll.common.constants.Message;
import com.jll.common.utils.BigDecimalUtil;
import com.jll.common.utils.DateUtil;
import com.jll.common.utils.MathUtil;
import com.jll.common.utils.PageQuery;
import com.jll.common.utils.SecurityUtils;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
import com.jll.dao.PageBean;
import com.jll.dao.PageQueryDao;
import com.jll.dao.SupserDao;
import com.jll.entity.DepositApplication;
import com.jll.entity.MemberPlReport;
import com.jll.entity.OrderInfo;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.SysAuthority;
import com.jll.entity.SysCode;
import com.jll.entity.SysNotification;
import com.jll.entity.SysRole;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserBankCard;
import com.jll.entity.UserInfo;
import com.jll.entity.WithdrawApplication;
import com.jll.game.IssueService;
import com.jll.game.order.OrderDao;
import com.jll.game.order.OrderService;
import com.jll.report.WithdrawApplicationService;
import com.jll.sys.security.permission.SysRoleService;
import com.jll.sys.security.user.SysAuthorityDao;
import com.jll.sys.security.user.SysAuthorityService;
import com.jll.sysSettings.syscode.SysCodeService;
import com.jll.user.bank.UserBankCardService;
import com.jll.user.details.UserAccountDetailsService;
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
	IssueService issueService;
	
	@Resource
	OrderService orderService;
	
	@Resource
	OrderDao orderDao;
	
	@Resource
	UserAccountDetailsService userAccountDetailsService;
	
	@Resource
	SysCodeService sysCodeService;
	
	@Resource
	WithdrawApplicationService withdrawService;
	
	@Resource
	CacheRedisService cacheRedisService;
	
	@Resource
	CacheRedisService cacheServ;
	
	@Resource
	UserBankCardService userBankCardService;
	
	@Resource
	SysAuthorityService sysAuthorityService;
	
	@Resource
	SysRoleService sysRoleService;
	
	@Resource
	SysAuthorityDao sysAuthorityDao;
	
	@Resource
	//HttpServletRequest request;
 	
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
	public Map<String, Object> updateFundPwd(String oldPwd, String newPwd, String checkPwd) {
		Map<String, Object> ret = new HashMap<String, Object>();
		
		UserInfo dbInfo = getCurLoginInfo();
		if(!StringUtils.checkFundPwdFmtIsOK(newPwd)
				|| !newPwd.equals(checkPwd)
				|| null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		BCryptPasswordEncoder bcEncoder = new  BCryptPasswordEncoder();
//		String oldDbPwd = bcEncoder.encode(oldPwd);
		if(!bcEncoder.matches(oldPwd, dbInfo.getLoginPwd())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_OLD_LOGIN_PWD_ERROR.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_OLD_LOGIN_PWD_ERROR.getErrorMes());
			return ret;
		}
		dbInfo.setFundPwd(bcEncoder.encode(newPwd));
		supserDao.update(dbInfo);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> updateLoginPwd(String oldPwd, String newPwd, String checkPwd) {

		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = getCurLoginInfo();
		if(!StringUtils.checkLoginPwdFmtIsOK(newPwd)
				|| !newPwd.equals(checkPwd)
				|| null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		BCryptPasswordEncoder bcEncoder = new  BCryptPasswordEncoder();
//		String oldDbPwd = bcEncoder.encode(oldPwd);
		if(!bcEncoder.matches(oldPwd, dbInfo.getLoginPwd())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_OLD_LOGIN_PWD_ERROR.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_OLD_LOGIN_PWD_ERROR.getErrorMes());
			return ret;
		}
		dbInfo.setLoginPwd(bcEncoder.encode(newPwd));
		supserDao.update(dbInfo);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}
	@Override
	public Map<String, Object> updateLoginPwdAdmin(String oldPwd, String newPwd, String checkPwd,Integer id) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = userDao.getUserById(id);
		if(!StringUtils.checkLoginPwdFmtIsOK(newPwd)
				|| !newPwd.equals(checkPwd)
				|| null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		BCryptPasswordEncoder bcEncoder = new  BCryptPasswordEncoder();
//		String oldDbPwd = bcEncoder.encode(oldPwd);
//		if(!oldDbPwd.equals(dbInfo.getLoginPwd())){
		if(!bcEncoder.matches(oldPwd, dbInfo.getLoginPwd())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_OLD_LOGIN_PWD_ERROR.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_OLD_LOGIN_PWD_ERROR.getErrorMes());
			return ret;
		}
		dbInfo.setLoginPwd(bcEncoder.encode(newPwd));
		supserDao.update(dbInfo);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}
	@Override
	public Map<String, Object> getUserInfo() {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = getCurLoginInfo();
		if(null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}

		List<String> list=sysAuthorityService.queryGetByUserId(dbInfo.getId());
		if(!SecurityUtils.checkViewPermissionIsOK(list)){
			//真实姓名只显示第一个字，电话号码只显示后面三位，电子邮件只显示头三个字母以及邮箱地址，微信和qq都只显示后面三位字母
			if(!StringUtils.isEmpty(dbInfo.getRealName())) {
				dbInfo.setRealName(dbInfo.getRealName().substring(0, 1)+StringUtils.MORE_ASTERISK);
			}
			if(!StringUtils.isEmpty(dbInfo.getPhoneNum())) {
				String userNameNew=dbInfo.getPhoneNum();
				Integer length=userNameNew.length();
				dbInfo.setPhoneNum( userNameNew.substring(0, length-4)+StringUtils.MORE_ASTERISK);
			}
			if(!StringUtils.isEmpty(dbInfo.getEmail())){
				String emailStr=dbInfo.getEmail().substring(0, dbInfo.getEmail().indexOf('@'));
				dbInfo.setEmail(emailStr.substring(0,5)+StringUtils.MORE_ASTERISK+dbInfo.getEmail().substring(dbInfo.getEmail().indexOf('@')));
			}
			if(!StringUtils.isEmpty(dbInfo.getWechat())) {
				dbInfo.setWechat(dbInfo.getWechat().substring(0, 5)+StringUtils.MORE_ASTERISK);
			}
			if(!StringUtils.isEmpty(dbInfo.getQq())) {
				dbInfo.setQq(dbInfo.getQq().substring(0, 5)+StringUtils.MORE_ASTERISK);
			}
		}
		dbInfo.setLoginPwd(StringUtils.MORE_ASTERISK);
		dbInfo.setFundPwd(StringUtils.MORE_ASTERISK);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA, dbInfo);
		return ret;
	}

	@Override
	public Map<String, Object> updateUserInfo(UserInfo userInfo) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = getCurLoginInfo();
		if(null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		if(!StringUtils.isBlank(userInfo.getEmail()) 
						&& !StringUtils.checkEmailFmtIsOK(userInfo.getEmail())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		if(!StringUtils.isBlank(userInfo.getRealName())
				&& !StringUtils.checkRealNameFmtIsOK(userInfo.getRealName())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		if(!StringUtils.isBlank(userInfo.getQq())
				&& !StringUtils.checkQqFmtIsOK(userInfo.getQq())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		if(!StringUtils.isBlank(userInfo.getWechat())
				&& !StringUtils.checkWercharFmtIsOK(userInfo.getWechat())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		if(!StringUtils.isBlank(userInfo.getPhoneNum())
				&& !StringUtils.checkPhoneFmtIsOK(userInfo.getPhoneNum())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		List<String> list=sysAuthorityService.queryGetByUserId(dbInfo.getId());
		boolean isAdmin = SecurityUtils.checkPermissionIsOK(list, SecurityUtils.PERMISSION_ROLE_ADMIN);
		
		if(!isAdmin 
				&& !StringUtils.isEmpty(dbInfo.getRealName())
				&& !StringUtils.isEmpty(userInfo.getRealName())
				&& !userInfo.getRealName().equals(dbInfo.getRealName())){
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
				&& !StringUtils.isEmpty(userInfo.getEmail())
				&& !userInfo.getEmail().equals(dbInfo.getEmail())
				&& (dbInfo.getIsValidEmail().intValue() 
					== Constants.EmailValidState.VERIFIED.getCode())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_MORE_UPDATE_EMAIL.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_MORE_UPDATE_EMAIL.getErrorMes());
			return ret;
		}else if((!isAdmin 
						&& !StringUtils.isEmpty(userInfo.getEmail())
						&& !userInfo.getEmail().equals(dbInfo.getEmail())
						&& (dbInfo.getIsValidEmail().intValue() 
								== Constants.EmailValidState.UNVERIFIED.getCode()))
				||(isAdmin && !StringUtils.isEmpty(userInfo.getEmail()))){
			dbInfo.setEmail(userInfo.getEmail());
			dbInfo.setIsValidEmail(Constants.EmailValidState.UNVERIFIED.getCode());
		}
		
		if(!isAdmin 
				&& !StringUtils.isEmpty(userInfo.getPhoneNum())
				&& !userInfo.getPhoneNum().equals(dbInfo.getPhoneNum())
				&& (dbInfo.getIsValidPhone().intValue() 
						== Constants.PhoneValidState.VERIFIED.getCode())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_MORE_UPDATE_PHONE_NUM.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_MORE_UPDATE_PHONE_NUM.getErrorMes());
			return ret;
		}else if((!isAdmin 
						&& !StringUtils.isEmpty(userInfo.getPhoneNum())
						&& !userInfo.getPhoneNum().equals(dbInfo.getPhoneNum())
						&& (dbInfo.getIsValidPhone().intValue() 
								== Constants.PhoneValidState.UNVERIFIED.getCode()))
				||(isAdmin && !StringUtils.isEmpty(userInfo.getPhoneNum()))){
			dbInfo.setPhoneNum(userInfo.getPhoneNum());
			dbInfo.setIsValidPhone(Constants.PhoneValidState.UNVERIFIED.getCode());
		}
		//dbInfo.setUserId(userInfo.getUserId());
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
	public UserInfo getUserById(Integer userId) {
		return userDao.getUserById(userId);
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
	public void regUser(UserInfo user,HttpServletRequest request) {
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
		
		/*if(loginUser == null) {
			logger.error(Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			throw new RuntimeException(Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
		}*/
		
		user.setCreator(1);
		user.setRegIp(request.getRemoteHost());
		
		userDao.saveUser(user);
		Integer userId=user.getId();
		
		if(user.getUserType()!=null) {
			if(user.getUserType().intValue()!=Constants.UserType.SYS_ADMIN.getCode()) {
				String roleName="";
				if(user.getUserType()==Constants.UserType.PLAYER.getCode()||user.getUserType()==Constants.UserType.DEMO_PLAYER.getCode()) {
					roleName=Constants.Permission.ROLE_USER.getCode();
				}else if(user.getUserType()==Constants.UserType.AGENCY.getCode()) {
					roleName=Constants.Permission.ROLE_AGENT.getCode();
				}
				SysRole sysRole=sysRoleService.queryByRoleName(roleName);
				if(sysRole==null) {
					throw new RuntimeException(Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
				}
				SysAuthority sysAuthority=new SysAuthority();
				sysAuthority.setUserId(userId);
				sysAuthority.setRoleId(sysRole.getId());
				sysAuthorityDao.saveOrUpdateSysAuthority(sysAuthority);
			}
		}else {
			throw new RuntimeException(Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
		}
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
		bank.setUserId(userId);
		bank.setBankCode(bankInfo.get(Message.KEY_DATA).toString());
		bank.setCreateTime(new Date());
		bank.setCreator(userId);
		bank.setState(Constants.BankCardState.ENABLED.getCode());
		userBankCardService.saveOrUserBank(bank);
		bankInfo.clear();
		bankInfo.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return bankInfo; 
	}

	@Override
	public Map<String, Object> getBankCodeList() {
		Map<String, Object> ret = new HashMap<>();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data",cacheServ.getSysCode(SysCodeTypes.BANK_CODE_LIST.getCode()));
		return ret;
	}

	@Override
	public Map<String, Object> verifyUserBankInfo(int userId, UserBankCard bank) {
		Map<String, Object> ret = new HashMap<String, Object>();
		if(StringUtils.isEmpty( bank.getCardNum())
				|| StringUtils.isEmpty( bank.getBankBranch())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES,Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		long count=userDao.queryUserBankCount(userId);
		Integer countNew=Integer.valueOf((int)count);
		SysCode sysCode=cacheServ.getSysCode(Constants.SysCodeTypes.SYS_RUNTIME_ARGUMENT.getCode(),Constants.SysRuntimeArgument.NUMBER_OF_BANK_CARDS.getCode());
		int maxCardNum = Integer.valueOf(sysCode.getCodeVal());
		if(countNew >= maxCardNum){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_MORE_BIND_BANK_CARD.getCode());
			ret.put(Message.KEY_ERROR_MES, String.format(Message.Error.ERROR_USER_MORE_BIND_BANK_CARD.getErrorMes(),maxCardNum));
			return ret;
		}
//		List<?> checkList = supserDao.findByName(UserBankCard.class,"cardNum",bank.getCardNum());
//		if(!checkList.isEmpty()){
//			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
//			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_BANK_CARD_HAS_BIND.getCode());
//			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_BANK_CARD_HAS_BIND.getErrorMes());
//			return ret;
//		}
		ret =  Utils.validBankInfo(bank.getCardNum());
		return ret;
	}
	@Override
	public void saveOrUpdateLoginPwd(UserInfo user) {
		Integer loginCount=0;
		user.setLoginCount(loginCount);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();		
		user.setLoginPwd(encoder.encode(defaultPwd));
		userDao.saveUser(user);
	}
	@Override
	public void saveOrUpdateFundPwd(UserInfo user) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();		
		user.setFundPwd(encoder.encode(defaultPwd));
		userDao.saveUser(user);
	}
	@Override
	public Map<String, Object> getUserNotifyLists() {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = getCurLoginInfo();
		if(null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		Object[] upUserId = StringUtils.getUserSupersId(dbInfo.getSuperior());
		
		Object[] query = new Object[2];
		query[0] = SysNotifyType.ALL_USER.getCode();
		query[1] = SysNotifyType.ALL_COM_USER.getCode();
		
		if(UserType.AGENCY.getCode() == dbInfo.getUserType()){
			query[1] = SysNotifyType.ALL_AGENT.getCode();
		}
		DetachedCriteria dc = DetachedCriteria.forClass(SysNotification.class);
		dc.add(Restrictions.ge("expireTime",new Date()));
		dc.add(Restrictions.or(
				Restrictions.and(
		                Restrictions.eq("receiverType", SysNotifyReceiverType.TYPE.getCode()),
		                Restrictions.in("receiver", query)
		            ),
				Restrictions.and(
		                Restrictions.eq("receiverType", SysNotifyReceiverType.LEVEL.getCode()),
		                Restrictions.in("receiver", upUserId)
		            )
        ));
		dc.addOrder(Order.desc("id"));
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,supserDao.findByCriteria(dc));
		return ret;
	}
	
	public static void main(String[] args) {
		List<SiteMessFeedback> retList = new ArrayList<>();
		
		SiteMessFeedback bs1 = new SiteMessFeedback();
		bs1.setId(2);
		
		SiteMessFeedback bs2 = new SiteMessFeedback();
		bs2.setId(4);
		
		SiteMessFeedback bs3 = new SiteMessFeedback();
		bs3.setId(1);
		
		retList.add(bs1);
		retList.add(bs2);
		retList.add(bs3);
		
		Collections.sort(retList, new Comparator<SiteMessFeedback>() {
			@Override
			public int compare(SiteMessFeedback b1, SiteMessFeedback b2) {
				return b2.getId()-b1.getId();
			}
		});
		
		for (SiteMessFeedback siteMessFeedback : retList) {
			System.out.println(siteMessFeedback.getId());
		}
		
	}
	
	private void getAllSiteMessageFeedback(List<SiteMessFeedback> backList , int userId, int msgId){
		DetachedCriteria dc = DetachedCriteria.forClass(SiteMessFeedback.class);
		dc.add(Restrictions.eq("mesId",msgId));
		dc.add(Restrictions.eq("fbUserId",userId));
		dc.addOrder(Order.desc("id"));
		backList = supserDao.findByCriteria(dc);
		
	}

	@Override
	public Map<String, Object> updateMessageFeedbackStatus(SiteMessFeedback back) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = getCurLoginInfo();
		SiteMessage dbMsg = (SiteMessage) supserDao.get(SiteMessage.class,back.getMesId());
		
		if(null == dbInfo
				|| null == dbMsg){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		if(StringUtils.isEmpty(back.getContent()) ){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_MESSAGE_CONTENT_IS_EMPTY.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_MESSAGE_CONTENT_IS_EMPTY.getErrorMes());
			return ret;
		}
		
		back.setMesId(dbMsg.getId());
		back.setFbUserId(dbInfo.getId());
		
		int validDay = Integer.valueOf(cacheRedisService.getSysCode(SysCodeTypes.SYS_RUNTIME_ARGUMENT.getCode(),SysRuntimeArgument.SITE_MSG_VALID_DAY.getCode()).getCodeVal());
		dbMsg.setExpireTime(DateUtils.addDays(new Date(), validDay));
		dbMsg.setIsRead(SiteMessageReadType.UN_READING.getCode());
		
		back.setFbTime(new Date());
		back.setIsRead(SiteMessageReadType.UN_READING.getCode());
		
		supserDao.save(back);
		supserDao.update(dbMsg);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> saveSiteMessage(String sendIds, SiteMessage msg) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo =getCurLoginInfo();
		if(null == dbInfo
				||(UserType.SYS_ADMIN.getCode() == dbInfo.getUserType()
						&& sendIds.equals(StringUtils.ALL))
				||(UserType.SYS_ADMIN.getCode() != dbInfo.getUserType()
				&& !StringUtils.isEmpty(sendIds))
				){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		if(StringUtils.isEmpty(msg.getTitle()) ){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_MESSAGE_TITLE_IS_EMPTY.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_MESSAGE_TITLE_IS_EMPTY.getErrorMes());
			return ret;
		}
		
		if(StringUtils.isEmpty(msg.getContent()) ){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_MESSAGE_CONTENT_IS_EMPTY.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_MESSAGE_CONTENT_IS_EMPTY.getErrorMes());
			return ret;
		}
		
		msg.setCreateTime(new Date());
		msg.setCreator(dbInfo.getId());
		msg.setIsRead(Constants.SiteMessageReadType.UN_READING.getCode());
		int validDay = Integer.valueOf(cacheRedisService.getSysCode(SysCodeTypes.SYS_RUNTIME_ARGUMENT.getCode(),SysRuntimeArgument.SITE_MSG_VALID_DAY.getCode()).getCodeVal());
		msg.setExpireTime(DateUtils.addDays(new Date(), validDay));
		
		//system to user
		if(!StringUtils.isEmpty(sendIds)){
			if(sendIds.equals(StringUtils.ALL)){
				sendIds = userDao.queryUnSystemUsers();
			}else{
				if(!userDao.checkUserIds(sendIds)){
					ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
					ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
					ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
					return ret;
				}
			}
			List<SiteMessage> addList = new ArrayList<>();
			for(String id:sendIds.split(StringUtils.COMMA)){
				SiteMessage addMsg = new SiteMessage();
				BeanUtils.copyProperties(msg, addMsg);
				addMsg.setUserId(Integer.valueOf(id));
				addMsg.setReceiver(Integer.valueOf(id));
				addList.add(addMsg);
			}
			supserDao.saveList(addList);
		}else{
			//user to system
			msg.setUserId(dbInfo.getId());
			msg.setReceiver(0);
			supserDao.save(msg);
		}
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public double getUserTotalDepostAmt(Date startDate,Date endDate,UserInfo user) {
		DetachedCriteria criteria = DetachedCriteria.forClass(DepositApplication.class);
		criteria.add(Restrictions.ge("createTime",startDate));
		criteria.add(Restrictions.le("createTime",endDate));
		
		criteria.add(Restrictions.eq("userId",user.getId()));
		criteria.add(Restrictions.eq("state",DepositOrderState.END_ORDER.getCode()));
		criteria.setProjection(Projections.sum("amount"));
		List<?> finds =  supserDao.findByCriteria(criteria);
		if(null != finds && !finds.isEmpty()&&finds.size()>0){
//			Object[] totalObj = (Object[]) finds.get(0);
//			return BigDecimalUtil.toDouble(totalObj[0]);
			Object s=finds.get(0);
			if(s!=null) {
				String ss=s.toString();
				return new Double(ss);
			}
		}
		return 0;
	}
	
	@Override
	public double getUserTotalWithdrawAmt(Date startDate,Date endDate,UserInfo user) {
		DetachedCriteria criteria = DetachedCriteria.forClass(WithdrawApplication.class);
		criteria.add(Restrictions.ge("createTime",startDate));
		criteria.add(Restrictions.le("createTime",endDate));
		
		criteria.add(Restrictions.eq("userId",user.getId()));
		criteria.add(Restrictions.eq("state",WithdrawOrderState.ORDER_END.getCode()));
		criteria.setProjection(Projections.sum("amount"));
		List<?> finds =  supserDao.findByCriteria(criteria);
		if(null != finds && !finds.isEmpty()&&finds.size()>0){
//			Object[] totalObj = (Object[]) finds.get(0);
//			return BigDecimalUtil.toDouble(totalObj[0]);
			Object s=finds.get(0);
			if(s!=null) {
				String ss=s.toString();
				return new Double(ss);
			}
		}
		return 0;
	}
	
	
	@Override
	public double getUserTotalBetAmt(Date startDate,Date endDate,UserInfo user) {
		DetachedCriteria criteria = DetachedCriteria.forClass(OrderInfo.class);
		criteria.add(Restrictions.ge("createTime",startDate));
		criteria.add(Restrictions.le("createTime",endDate));
		
		Object[] query = {Integer.parseInt(State.HAS_WON.getCode()),Integer.parseInt(State.NOT_WON.getCode())};
		criteria.add(Restrictions.eq("userId",user.getId()));
		criteria.add(Restrictions.in("state",query));
		criteria.setProjection(Projections.sum("betAmount"));
		List<?> finds =  supserDao.findByCriteria(criteria);
		if(null != finds && !finds.isEmpty()){
//			Object[] totalObj = (Object[]) finds.get(0);
//			return BigDecimalUtil.toDouble(totalObj[0]);
			if(finds.get(0)!=null) {
				return new Double(finds.get(0).toString());
			}
		}
		return 0;
	}
	//修改用户
	@Override
	public void updateUserType(UserInfo user) {
		Integer state=user.getState();
		Calendar calendar = new GregorianCalendar();
		Date date = new Date();
		if(state==0) {
			user.setFailLoginCount(0);
			user.setUnlockTime(date);
		}else if(state==1){
//			calendar.setTime(date);
//			calendar.add(calendar.YEAR, 10);//把日期往后增加一年.整数往后推,负数往前移动
//			date=calendar.getTime();
			user.setUnlockTime(null);
		}else if(state==2) {
			calendar.setTime(date);
			calendar.add(calendar.YEAR, 10);//把日期往后增加一年.整数往后推,负数往前移动
			date=calendar.getTime();
			user.setUnlockTime(date);
		}
		userDao.saveUser(user);
	}
	//查询所有的用户
	@Override
	public Map<String,Object> queryAllUserInfo(Map<String, Object> map) {
		Integer id=(Integer) map.get("id");
		String userName=(String) map.get("userName");
		String proxyName=(String) map.get("proxyName");
		String startTime=(String) map.get("startTime");
		String endTime=(String) map.get("endTime");
		Integer pageIndex=(Integer) map.get("pageIndex");
		Integer pageSize=(Integer) map.get("pageSize");
		UserInfo userInfo=userDao.getUserByUserName(proxyName);
		Integer proxyId=null;
		if(userInfo!=null) {
			if(userInfo.getUserType()==Constants.UserType.GENERAL_AGENCY.getCode()) {
				proxyId=0;
			}else {
				proxyId=userInfo.getId();
			}
		}
		Map<String,Object> userInfoList=userDao.queryAllUserInfo(id, userName, proxyId, startTime, endTime,pageIndex,pageSize);
		return userInfoList;
	}
	//查询所有的代理
	@Override
	public Map<String,Object> queryAllAgent(Map<String, Object> map) {
		String userName=(String) map.get("userName");
		String startTime=(String) map.get("startTime");
		String endTime=(String) map.get("endTime");
		Integer pageIndex=(Integer) map.get("pageIndex");
		Integer pageSize=(Integer) map.get("pageSize");
		Map<String,Object> userInfoList=userDao.queryAllAgent( userName,startTime,endTime,pageIndex,pageSize);
		return userInfoList;
	}
	

	@Override
	public Map<String, Object> processExchangePoint(double amount) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = getCurLoginInfo();
		if(amount < 0
				|| null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		UserAccount dbAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId", dbInfo.getId(), "accType", WalletType.MAIN_WALLET.getCode()).get(0);
		if(dbAcc.getRewardPoints() < amount){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_POINT_NOT_ENOUGH.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_POINT_NOT_ENOUGH.getErrorMes());
			return ret;
		}
		
		SysCode dbCode = cacheServ.getSysCode(SysCodeTypes.SYS_RUNTIME_ARGUMENT.getCode(),SysRuntimeArgument.POINT_EXCHANGE_SCALE.getCode());
		if(dbCode.getState() ==  SysCodeState.INVALID_STATE.getCode()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PROMS_INVALID.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_PROMS_INVALID.getErrorMes());
			return ret;
		}
		
		UserAccount redAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId", dbInfo.getId(), "accType", WalletType.RED_PACKET_WALLET.getCode()).get(0);
		
		//主钱包积分减少
		UserAccountDetails addDtl = new UserAccountDetails();
		addDtl.setUserId(dbInfo.getId());
		addDtl.setCreateTime(new Date());
		addDtl.setAmount(Double.valueOf(amount).floatValue());
		addDtl.setPreAmount(dbAcc.getRewardPoints().doubleValue());
		addDtl.setPostAmount(Double.valueOf(BigDecimalUtil.sub(addDtl.getPreAmount(),addDtl.getAmount())).doubleValue());
		addDtl.setWalletId(dbAcc.getId());
		addDtl.setOperationType(AccOperationType.POINTS_EXCHANGE.getCode());
		supserDao.save(addDtl);
		dbAcc.setRewardPoints(addDtl.getPostAmount().longValue());
		supserDao.update(dbAcc);
		
		
		
		double redAddAmt = BigDecimalUtil.mul(amount, Double.valueOf(dbCode.getCodeVal()));
		//红包钱包金额增加
		UserAccountDetails addRedDtl = new UserAccountDetails();
		addRedDtl.setUserId(dbInfo.getId());
		addRedDtl.setCreateTime(new Date());
		addRedDtl.setAmount(Double.valueOf(redAddAmt).floatValue());
		addRedDtl.setPreAmount(redAcc.getBalance().doubleValue());
		addRedDtl.setPostAmount(Double.valueOf(BigDecimalUtil.add(addRedDtl.getPreAmount(),addRedDtl.getAmount())).doubleValue());
		addRedDtl.setWalletId(redAcc.getId());
		addRedDtl.setOperationType(AccOperationType.POINTS_EXCHANGE.getCode());
		supserDao.save(addRedDtl);
		redAcc.setRewardPoints(addRedDtl.getPostAmount().longValue());
		supserDao.update(redAcc);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public UserInfo getCurLoginInfo() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth == null) {
			return null;
		}
		//return getUserByUserName("zhaowei");
		return getUserByUserName(auth.getName());
	}

	@Override
	public Map<String, Object> userProfitReport(PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>();
		
		DetachedCriteria dc = DetachedCriteria.forClass(MemberPlReport.class);
		dc.add(Restrictions.eq("userName",getCurLoginInfo().getUserName()));
		dc.add(Restrictions.ge("createTime",page.getStartDate()));
		dc.add(Restrictions.le("createTime",page.getEndDate()));
		dc.addOrder(Order.desc("id"));
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenation(supserDao.getHibernateTemplate(), dc, page.getPageIndex(), page.getPageSize()));
		return ret;
	}
	

	@Override
	public Map<String, Object> saveUpdateUserWithdrawApply(int bankId, double amount, String passoword) {
		
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = getCurLoginInfo();
		if(null == dbInfo
				|| amount < 0
				|| StringUtils.isEmpty(passoword)){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		if(UserType.DEMO_PLAYER.getCode() == dbInfo.getUserType()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_DEMO_USER_DISABLE_FUN.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_DEMO_USER_DISABLE_FUN.getErrorMes());
			return ret;
		}
		double minAmt = Double.valueOf(cacheRedisService.getSysCode(SysCodeTypes.WITHDRAWAL_CFG.getCode(), WithdrawConif.MIN_WITHDRAWAL_AMT.getCode()).getCodeVal());
		double maxAmt = Double.valueOf(cacheRedisService.getSysCode(SysCodeTypes.WITHDRAWAL_CFG.getCode(), WithdrawConif.MAX_WITHDRAWAL_AMT.getCode()).getCodeVal());
		if(amount < minAmt
				|| amount > maxAmt){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_WTD_AMT_ERROR.getCode());
			ret.put(Message.KEY_ERROR_MES, String.format(Message.Error.ERROR_WTD_AMT_ERROR.getErrorMes(), minAmt,maxAmt));
			return ret;
		}
		UserAccount mainAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId", dbInfo.getId(), "accType", WalletType.MAIN_WALLET.getCode()).get(0);
		if(mainAcc.getBalance().doubleValue() < amount){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_BALANCE_NOT_ENOUGH.getCode());
			ret.put(Message.KEY_ERROR_MES,Message.Error.ERROR_USER_BALANCE_NOT_ENOUGH.getErrorMes());
			return ret;
		}
		
		if(mainAcc.getState() == WalletState.FROZEN.getCode()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_WALLET_IS_FREEZE.getCode());
			ret.put(Message.KEY_ERROR_MES,Message.Error.ERROR_WALLET_IS_FREEZE.getErrorMes());
			return ret;
		}
		
		//资金密码
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//		if(!encoder.encode(passoword).equals(dbInfo.getFundPwd())){
		if(!encoder.matches(passoword, dbInfo.getFundPwd())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_WTD_PWD_ERROR.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_WTD_PWD_ERROR.getErrorMes());
			return ret;
		}
		
		//检查银行卡
		UserBankCard userCard = null;
		if(bankId < 0 ){
			List<?> rts = supserDao.findByName(UserBankCard.class,"userId",dbInfo.getId());
			if(null == rts || rts.isEmpty()){
				ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_WTD_BIND_BANK_CARD.getCode());
				ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_WTD_BIND_BANK_CARD.getErrorMes());
				return ret;
			}
			userCard = (UserBankCard) rts.get(0);
		}else{
			userCard = (UserBankCard) supserDao.get(UserBankCard.class, bankId);
		}
		if(null == userCard){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_WTD_BANK_CARD_ERROR.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_WTD_BANK_CARD_ERROR.getErrorMes());
			return ret;
		}
		
		//获取当日提款次数
		long curDayCount = withdrawService.getUserWithdrawCount(dbInfo.getId(), DateUtil.getDateDayStart(new Date()),  DateUtil.getDateDayEnd(new Date()));
		long maxDayCount = Long.valueOf(cacheRedisService.getSysCode(SysCodeTypes.WITHDRAWAL_CFG.getCode(), WithdrawConif.DAY_COUNT.getCode()).getCodeVal());
		
		if(curDayCount >= maxDayCount){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_WTD_TIMES_ERROR.getCode());
			ret.put(Message.KEY_ERROR_MES, String.format(Message.Error.ERROR_WTD_TIMES_ERROR.getErrorMes(),maxDayCount,curDayCount));
			return ret;
		}
		
		
		WithdrawApplication wtd = new WithdrawApplication();
		wtd.setAmount(Double.valueOf(amount).floatValue());
		wtd.setState(WithdrawOrderState.ORDER_INIT.getCode());
		wtd.setUserId(dbInfo.getId());
		wtd.setOrderNum(DateUtil.fmtYmdHisEmp(new Date())+StringUtils.getRandomString(6));
		wtd.setWalletId(mainAcc.getId());
		wtd.setBankCardId(userCard.getId());
		wtd.setCreateTime(new Date());
		wtd.setOperator(dbInfo.getId());
		
		supserDao.save(wtd);
		
		UserAccountDetails accDtal1 = userAccountDetailsService.initCreidrRecord(dbInfo.getId(), mainAcc, mainAcc.getBalance().doubleValue(), -amount, AccOperationType.WD_FREEZE.getCode(),wtd.getId(),"");
		mainAcc.setBalance(accDtal1.getPostAmount());
		accDtal1.setOrderId(wtd.getId());
		supserDao.save(accDtal1);
		
		UserAccountDetails accDtal2 = userAccountDetailsService.initCreidrRecord(dbInfo.getId(), mainAcc, mainAcc.getFreeze().doubleValue(), amount, AccOperationType.WD_FREEZE.getCode(),wtd.getId(),"");
		accDtal2.setOrderId(wtd.getId());
		mainAcc.setFreeze(accDtal2.getPostAmount());
		supserDao.save(accDtal2);
		
		supserDao.update(mainAcc);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> saveUpdateUserWithdrawNotices(WithdrawApplication wtd) {
		Map<String, Object> ret = new HashMap<String, Object>();
		if(StringUtils.isEmpty(wtd.getOrderNum())
				|| null == WithdrawOrderState.getValueByCode(wtd.getState())
				|| WithdrawOrderState.ORDER_INIT.getCode() == wtd.getState()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		WithdrawApplication dbWtd = null;
		List<?> finds =  supserDao.findByName(WithdrawApplication.class, "orderNum",wtd.getOrderNum());
		if(!finds.isEmpty()){
			dbWtd = (WithdrawApplication) finds.get(0);
		}
		if(null == dbWtd
				|| WithdrawOrderState.ORDER_INIT.getCode() != dbWtd.getState()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_ORDER_ERROR.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_ORDER_ERROR.getErrorMes());
			return ret;
		}
		
		UserAccount mainAcc = (UserAccount) supserDao.get(UserAccount.class, dbWtd.getWalletId());
		UserAccountDetails addDtail1 = userAccountDetailsService.initCreidrRecord(dbWtd.getUserId(),mainAcc, mainAcc.getFreeze().doubleValue(), -dbWtd.getAmount().doubleValue(), AccOperationType.WD_UNFREEZE.getCode(),dbWtd.getId(),"");
		supserDao.save(addDtail1);
		mainAcc.setFreeze(addDtail1.getPostAmount());
		
		//修改订单状态
		dbWtd.setState(wtd.getState());
		dbWtd.setUpdateTime(new Date());
		dbWtd.setRemark(wtd.getRemark());
		dbWtd.setOperator(getCurLoginInfo().getId());
		
		//审核不通过，退还金额，
		if(WithdrawOrderState.ORDER_END.getCode() != wtd.getState()){
			UserAccountDetails addDtail2 = userAccountDetailsService.initCreidrRecord(dbWtd.getUserId(),mainAcc, mainAcc.getBalance().doubleValue(), dbWtd.getAmount().doubleValue(), AccOperationType.WD_UNFREEZE.getCode(),dbWtd.getId(),"");
			supserDao.save(addDtail2);
			mainAcc.setBalance(addDtail2.getPostAmount());
		}else{
			UserAccountDetails addDtail2 = userAccountDetailsService.initCreidrRecord(dbWtd.getUserId(),mainAcc, mainAcc.getBalance().doubleValue(), dbWtd.getAmount().doubleValue(), AccOperationType.WITHDRAW.getCode(),dbWtd.getId(),"");
			supserDao.save(addDtail2);
		}
		supserDao.update(dbWtd);
		supserDao.update(mainAcc);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}
	
	

	@Override
	public Map<String, Object> processUserAmountTransfer(String fromUser, String toUser, double amount) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo fromUserInfo = getUserByUserName(fromUser);
		UserInfo toUserInfo = getUserByUserName(toUser);
		if(null == fromUserInfo
				|| null == toUserInfo
				|| fromUserInfo.equals(toUserInfo)){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		UserAccount mainAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId", fromUserInfo.getId(), "accType", WalletType.MAIN_WALLET.getCode()).get(0);
		if(amount < 0){
			amount = mainAcc.getBalance().doubleValue();
		}
		
		if(mainAcc.getBalance().doubleValue() < amount){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_BALANCE_NOT_ENOUGH.getCode());
			ret.put(Message.KEY_ERROR_MES,Message.Error.ERROR_USER_BALANCE_NOT_ENOUGH.getErrorMes());
			return ret;
		}
		
		UserAccount subAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId", toUserInfo.getId(), "accType", WalletType.MAIN_WALLET.getCode()).get(0);
		
		UserAccountDetails mainDtl = userAccountDetailsService.initCreidrRecord(fromUserInfo.getId(), mainAcc, mainAcc.getBalance().doubleValue(), -amount, AccOperationType.TRANSFER.getCode(),0,Constants.Transfer.OUT.getCode());
		mainAcc.setBalance(mainDtl.getPostAmount());
		
		UserAccountDetails subDtl = userAccountDetailsService.initCreidrRecord(toUserInfo.getId(), subAcc, subAcc.getBalance().doubleValue(), amount, AccOperationType.TRANSFER.getCode(),0,Constants.Transfer.IN.getCode());
		subAcc.setBalance(subDtl.getPostAmount());
		
		supserDao.save(mainDtl);
		supserDao.save(subDtl);
		Integer id=mainDtl.getId();
		mainDtl.setOrderId(id);
		subDtl.setOrderId(id);
		supserDao.saveOrUpdate(mainDtl);
		supserDao.saveOrUpdate(subDtl);
		supserDao.update(subAcc);
		supserDao.update(mainAcc);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}
	//查询总代下面的所有一级代理
	@Override
	public List<UserInfo> queryAllAgent() {
		UserInfo userInfo=userDao.querySumAgent();
		List<UserInfo> list=userDao.queryAllAgent(userInfo.getId());
		if(list!=null&&list.size()>0) {
			return list;
		}
		return null;
	}
	//点击代理查询下一级代理
	@Override
	public Map<String,Object> queryAgentByAgent(Integer id,String startTime,String endTime,Integer pageIndex) {
		Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
		boolean isOrNo=this.isOrNoUserInfo(id);
		Map<String,Object> map=new HashMap<String,Object>();
		if(isOrNo) {
			PageBean list=userDao.queryAgentByAgent(id,startTime,endTime,pageSize,pageIndex);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			map.put("data", list);
			return map;
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return map;
		}
	}
	//通过id查看这个用户是否存在
	@Override
	public boolean isOrNoUserInfo(Integer id) {
		UserInfo userInfo=userDao.getUserById(id);
		if(userInfo!=null) {
			return true;
		}
		return false;
	}
	@Override
	public Float calPrizePattern(UserInfo user, String lottoType) {
		String keyRunTimeArg = Constants.SysCodeTypes.SYS_RUNTIME_ARGUMENT.getCode();
		String keyPrizeRange = Constants.SysRuntimeArgument.LOTTO_PRIZE_RATE.getCode();
		String keyMaxPlatRebate = Constants.SysRuntimeArgument.MAX_PLAT_REBATE.getCode();
		SysCode prizeRange = cacheServ.getSysCode(keyRunTimeArg, keyPrizeRange);
		SysCode maxPlatRebate = cacheServ.getSysCode(keyRunTimeArg, keyMaxPlatRebate);
		String[] prizeRanges = prizeRange.getCodeVal().split(",");
		Double valRebatePrizeRate = null;
		Float minPrize = Float.valueOf(prizeRanges[0]);
		Float maxPrize = Float.valueOf(prizeRanges[1]);
		Float prizePattern = null;
		
		valRebatePrizeRate = MathUtil.subtract(maxPrize, minPrize, Double.class);
		valRebatePrizeRate = MathUtil.divide(valRebatePrizeRate, 
				Float.valueOf(maxPlatRebate.getCodeVal()).floatValue(), 
				2);
		valRebatePrizeRate = MathUtil.multiply(valRebatePrizeRate.floatValue(), 
				user.getPlatRebate().floatValue(), 
				Double.class);
		
		prizePattern = MathUtil.subtract(Float.valueOf(prizeRanges[1]), 
				valRebatePrizeRate.floatValue(), 
				Float.class);
		return prizePattern;
	}

	@Override
	public Map<String, Object> processUserRedWalletAmountTransfer(int bankId, double amount, String passoword) {
		
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = getCurLoginInfo();
		if(null == dbInfo
				|| amount < 0
				|| StringUtils.isEmpty(passoword)){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		if(UserType.DEMO_PLAYER.getCode() == dbInfo.getUserType()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_DEMO_USER_DISABLE_FUN.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_DEMO_USER_DISABLE_FUN.getErrorMes());
			return ret;
		}
		
		double minAmt = Double.valueOf(cacheRedisService.getSysCode(SysCodeTypes.WITHDRAWAL_CFG.getCode(), WithdrawConif.MIN_WITHDRAWAL_AMT.getCode()).getCodeVal());
		double maxAmt = Double.valueOf(cacheRedisService.getSysCode(SysCodeTypes.WITHDRAWAL_CFG.getCode(), WithdrawConif.MAX_WITHDRAWAL_AMT.getCode()).getCodeVal());
		if(amount < minAmt
				|| amount > maxAmt){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_WTD_AMT_ERROR.getCode());
			ret.put(Message.KEY_ERROR_MES, String.format(Message.Error.ERROR_WTD_AMT_ERROR.getErrorMes(), minAmt,maxAmt));
			return ret;
		}
		UserAccount mainAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId", dbInfo.getId(), "accType", WalletType.RED_PACKET_WALLET.getCode()).get(0);
		if(mainAcc.getBalance().doubleValue() < amount){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_BALANCE_NOT_ENOUGH.getCode());
			ret.put(Message.KEY_ERROR_MES,Message.Error.ERROR_USER_BALANCE_NOT_ENOUGH.getErrorMes());
			return ret;
		}
		
		if(mainAcc.getState() == WalletState.FROZEN.getCode()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_WALLET_IS_FREEZE.getCode());
			ret.put(Message.KEY_ERROR_MES,Message.Error.ERROR_WALLET_IS_FREEZE.getErrorMes());
			return ret;
		}
		
		//资金密码
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//		if(!encoder.encode(passoword).equals(dbInfo.getFundPwd())){
		if(!encoder.matches(passoword, dbInfo.getFundPwd())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_WTD_PWD_ERROR.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_WTD_PWD_ERROR.getErrorMes());
			return ret;
		}
		
		//检查银行卡
		UserBankCard userCard = null;
		if(bankId < 0 ){
			List<?> rts = supserDao.findByName(UserBankCard.class,"userId",dbInfo.getId());
			if(null == rts || rts.isEmpty()){
				ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_WTD_BIND_BANK_CARD.getCode());
				ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_WTD_BIND_BANK_CARD.getErrorMes());
				return ret;
			}
			userCard = (UserBankCard) rts.get(0);
		}else{
			userCard = (UserBankCard) supserDao.get(UserBankCard.class, bankId);
		}
		if(null == userCard){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_WTD_BANK_CARD_ERROR.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_WTD_BANK_CARD_ERROR.getErrorMes());
			return ret;
		}
		
		//获取当日提款次数
		long curDayCount = withdrawService.getUserWithdrawCount(dbInfo.getId(), DateUtil.getDateDayStart(new Date()),  DateUtil.getDateDayEnd(new Date()));
		long maxDayCount = Long.valueOf(cacheRedisService.getSysCode(SysCodeTypes.WITHDRAWAL_CFG.getCode(), WithdrawConif.DAY_COUNT.getCode()).getCodeVal());
		
		if(curDayCount >= maxDayCount){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_WTD_TIMES_ERROR.getCode());
			ret.put(Message.KEY_ERROR_MES, String.format(Message.Error.ERROR_WTD_TIMES_ERROR.getErrorMes(),maxDayCount,curDayCount));
			return ret;
		}
		
		double userSumBet = orderService.getUserBetTotalByDate(mainAcc.getId(),mainAcc.getUserId(), dbInfo.getCreateTime(), new Date());
		
		double wtdRate = Double.valueOf(cacheRedisService.getSysCode(SysCodeTypes.WITHDRAWAL_CFG.getCode(), WithdrawConif.RED_PACKET_WALLET_RATE.getCode()).getCodeVal());
		
		double userSumWtd = withdrawService.getUserWithdrawAmountTotal(mainAcc.getUserId(),mainAcc.getId(),dbInfo.getCreateTime(), new Date());
		
		//总投注 - (提取金额 +以提取金额)*流水倍数  < 0 ,不可提取
		double curCheckAMt = BigDecimalUtil.sub(userSumBet,  BigDecimalUtil.mul(BigDecimalUtil.add(userSumWtd,amount),wtdRate));
		if(curCheckAMt < 0){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_TRANS_RED_WALLET_FAIL.getCode());
			ret.put(Message.KEY_ERROR_MES,String.format(Message.Error.ERROR_USER_TRANS_RED_WALLET_FAIL.getErrorMes(), userSumBet,BigDecimalUtil.mul(amount,wtdRate)));
			return ret;
		}
		
		WithdrawApplication wtd = new WithdrawApplication();
		wtd.setAmount(Double.valueOf(amount).floatValue());
		wtd.setState(WithdrawOrderState.ORDER_INIT.getCode());
		wtd.setUserId(dbInfo.getId());
		wtd.setOrderNum(DateUtil.fmtYmdHisEmp(new Date())+StringUtils.getRandomString(6));
		wtd.setWalletId(mainAcc.getId());
		wtd.setBankCardId(userCard.getId());
		wtd.setCreateTime(new Date());
		wtd.setOperator(dbInfo.getId());
		supserDao.save(wtd);
		
		UserAccountDetails accDtal1 = userAccountDetailsService.initCreidrRecord(dbInfo.getId(), mainAcc, mainAcc.getBalance().doubleValue(), -amount, AccOperationType.WD_FREEZE.getCode(),wtd.getId(),"");
		mainAcc.setBalance(accDtal1.getPostAmount());
		supserDao.save(accDtal1);
		
		UserAccountDetails accDtal2 = userAccountDetailsService.initCreidrRecord(dbInfo.getId(), mainAcc, mainAcc.getFreeze().doubleValue(), amount, AccOperationType.WD_FREEZE.getCode(),wtd.getId(),"");
		mainAcc.setFreeze(accDtal2.getPostAmount());
		
		supserDao.save(accDtal2);
		supserDao.update(mainAcc);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}
	

	@Override
	public Map<String, Object> saveUpdateDirectOperationUserAmount(UserAccountDetails dtl) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo userInfo = userDao.getUserById(dtl.getUserId());
		if(null== getCurLoginInfo()
				|| null == WalletType.getWalletTypeByCode(dtl.getWalletId())
				|| null == userInfo
				|| Utils.toDouble(dtl.getAmount()) <= 0.00
				|| (WalletType.MAIN_WALLET.getCode()== dtl.getWalletId() && null == MainWallet.getValueByCode(dtl.getOperationType()))
				|| (WalletType.RED_PACKET_WALLET.getCode()== dtl.getWalletId() && null == RedWallet.getValueByCode(dtl.getOperationType()))){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		UserAccount userAcc  = (UserAccount) supserDao.findByName(UserAccount.class, "userId",userInfo.getId(), "accType", dtl.getWalletId()).get(0);
		
		int userId=userAcc.getUserId();
		double addAmt=dtl.getAmount()* Constants.AccOperationType.getValueByCode(dtl.getOperationType()).getNumber();
		
		//账号
		if((addAmt < 0 && Utils.toDouble(userAcc.getBalance()) < Math.abs(addAmt))
				|| (AccOperationType.ACC_UNFREEZE.getCode().equals(dtl.getOperationType()) && Utils.toDouble(userAcc.getFreeze()) < Math.abs(addAmt))){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_BALANCE_NOT_ENOUGH.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_BALANCE_NOT_ENOUGH.getErrorMes());
			return ret;
		}
		
		//只对余额进行操作
		if(AccOperationType.CUSTOMER_CLAIMS.getCode().equals(dtl.getOperationType())
				|| AccOperationType.DEPOSIT_CASH.getCode().equals(dtl.getOperationType())
				|| AccOperationType.REG_CASH.getCode().equals(dtl.getOperationType())
				|| AccOperationType.BANK_FEES.getCode().equals(dtl.getOperationType())
				|| AccOperationType.PROMO_CASH.getCode().equals(dtl.getOperationType())
				|| AccOperationType.PROMO_CASH.getCode().equals(dtl.getOperationType())
				|| AccOperationType.SYS_DEDUCTION.getCode().equals(dtl.getOperationType())
				|| AccOperationType.SYS_ADD.getCode().equals(dtl.getOperationType())){
			UserAccountDetails accDtal1 = userAccountDetailsService.initCreidrRecord(userId,userAcc,userAcc.getBalance().doubleValue(),addAmt,dtl.getOperationType(),0,"");
			supserDao.save(accDtal1);
			userAcc.setBalance(accDtal1.getPostAmount());
		}else if(AccOperationType.PLAT_REWARD.getCode().equals(dtl.getOperationType())
				|| AccOperationType.PROMO_POINTS.getCode().equals(dtl.getOperationType())){
			//只对积分进行操作
			UserAccountDetails accDtal1 = userAccountDetailsService.initCreidrRecord(userId,userAcc,userAcc.getRewardPoints().doubleValue(),addAmt,dtl.getOperationType(),0,"");
			supserDao.save(accDtal1);
			userAcc.setRewardPoints(accDtal1.getPostAmount().longValue());
		
		}else if(AccOperationType.ACC_FREEZE.getCode().equals(dtl.getOperationType())
				|| AccOperationType.ACC_UNFREEZE.getCode().equals(dtl.getOperationType())){
			//对余额和冻结金额进行操作
			UserAccountDetails accDtal1 = userAccountDetailsService.initCreidrRecord(userId,userAcc,userAcc.getBalance().doubleValue(),addAmt,dtl.getOperationType(),0,"");
			supserDao.save(accDtal1);
			userAcc.setBalance(accDtal1.getPostAmount());
			
			UserAccountDetails accDtal2 = userAccountDetailsService.initCreidrRecord(userId,userAcc,userAcc.getFreeze().doubleValue(),-addAmt,dtl.getOperationType(),0,"");
			supserDao.save(accDtal2);
			userAcc.setFreeze(accDtal2.getPostAmount());
		}
		supserDao.update(userAcc);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> updateUserWalletLockStatus(UserAccount dtl) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo userInfo = userDao.getUserById(dtl.getUserId());
		
		if((dtl.getAccType() > 0 && null == WalletType.getWalletTypeByCode(dtl.getAccType()))
				|| null == userInfo
				|| null == WalletState.geByCode(dtl.getState())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		
		
		if(dtl.getAccType() < 0
				|| dtl.getAccType()  == WalletType.RED_PACKET_WALLET.getCode()){
			UserAccount mainAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId",userInfo.getId(), "accType", WalletType.RED_PACKET_WALLET.getCode()).get(0);
			mainAcc.setState(dtl.getState());
			supserDao.update(mainAcc);
		}
		
		if(dtl.getAccType() < 0
				|| dtl.getAccType()  == WalletType.MAIN_WALLET.getCode()){
			UserAccount mainAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId",userInfo.getId(), "accType", WalletType.MAIN_WALLET.getCode()).get(0);
			mainAcc.setState(dtl.getState());
			supserDao.update(mainAcc);
		}
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}
	//用户登录后查询用户银行卡信息
	@Override
	public Map<String, Object> queryByUserNameBankList() {
		String userName=SecurityContextHolder.getContext().getAuthentication().getName();//当前登录的用户
		UserInfo userInfo=userDao.getUserByUserName(userName);
		Map<String, Object> ret = new HashMap<String, Object>();
		DetachedCriteria dc = DetachedCriteria.forClass(UserBankCard.class);
		dc.add(Restrictions.eq("userId",userInfo.getId()));
		dc.add(Restrictions.eq("state",Constants.BankCardState.ENABLED.getCode()));
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,supserDao.findByCriteria(dc));
		return ret;
	}
	//判断用户是否可以添加银行卡
	@Override
	public Map<String, Object> isOrAddBank() {
		Map<String, Object> ret = new HashMap<String, Object>();
		String userName=SecurityContextHolder.getContext().getAuthentication().getName();//当前登录的用户
		UserInfo userInfo=userDao.getUserByUserName(userName);
		if(userInfo==null) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return ret;
		}
		if(userInfo.getRealName()==null||StringUtils.isBlank(userInfo.getRealName())) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_BANK_CARD_REAL_NAME.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_BANK_CARD_REAL_NAME.getErrorMes());
			return ret;
		}
		long count=userDao.queryUserBankCount(userInfo.getId());
		String keySysRuntimeArg = Constants.SysCodeTypes.SYS_RUNTIME_ARGUMENT.getCode();
		String numBank = Constants.SysRuntimeArgument.NUMBER_OF_BANK_CARDS.getCode();
		SysCode sysCode=cacheServ.getSysCode(keySysRuntimeArg, numBank);
		String codeVal=sysCode.getCodeVal();
		Integer codeValNew = Integer.valueOf(codeVal);
		Integer countNew=Integer.valueOf((int)count);
		Integer t=null;
		if(countNew>=codeValNew) {
			t=1;
		}else {
			t=0;
		}
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", t);
		return ret;
	}
	//前台用户自己添加银行卡
	@Override
	public Map<String, Object> saveOrUserBank(UserBankCard bank) {
		Map<String,Object> map=new HashMap<String,Object>();
		if(bank.getCardNum()==null||bank.getBankBranch()==null) {
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return map;
		}
		
		String userName=SecurityContextHolder.getContext().getAuthentication().getName();//当前登录的用户
		UserInfo userInfo=userDao.getUserByUserName(userName);
		Map<String, Object> bankInfo = verifyUserBankInfo(userInfo.getId(), bank);
		if(null == bankInfo.get(Message.KEY_DATA)){
			return bankInfo;
		}
		UserBankCard userBankCard=userBankCardService.queryBankCard(bank.getCardNum());
		if(userBankCard==null) {
			bank.setUserId(userInfo.getId());
			bank.setBankCode(bankInfo.get(Message.KEY_DATA).toString());
			bank.setCreateTime(new Date());
			bank.setCreator(userInfo.getId());
			bank.setState(Constants.BankCardState.ENABLED.getCode());
			userBankCardService.saveOrUserBank(bank);
		}else {
			if(userBankCard.getState()==Constants.BankCardState.ENABLED.getCode()) {
				map.clear();
				map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_BANK_CARD_HAS_BIND.getCode());
				map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_BANK_CARD_HAS_BIND.getErrorMes());
				return map;
			}
			userBankCard.setBankBranch(bank.getBankBranch());
			userBankCard.setRemark(bank.getRemark());
			userBankCard.setState(Constants.BankCardState.ENABLED.getCode());
			userBankCardService.saveOrUserBank(userBankCard);
		}
		bankInfo.clear();
		bankInfo.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return bankInfo; 
	}
	//通过用ID查询用户银行卡   给后台管理页面用的
	@Override
	public Map<String, Object> queryByUserIdBankList(Integer id) {
		Map<String, Object> ret = new HashMap<String, Object>();
		boolean haveOrNot=userBankCardService.haveOrNot(id);
		if(!haveOrNot) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_BANKCARD.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_BANKCARD.getErrorMes());
			return ret;
		}
		String codeName=Constants.SysCodeTypes.BANK_CODE_LIST.getCode();
		Integer sysCodeId=sysCodeService.queryByCodeName(codeName);
		List<?> list=userBankCardService.queryByUserId(id, sysCodeId);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,list);
		return ret;
	}

	@Override
	public Map<String, Object> getUserNameById(Integer userId) {
		Map<String,Object> ret=new HashMap<String,Object>();
		UserInfo userInfo=userDao.getUserById(userId);
		if(userInfo==null) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return ret;
		}
		//需要加上权限设置
		UserInfo dbInfo = getCurLoginInfo();
		List<String> list=sysAuthorityService.queryGetByUserId(dbInfo.getId());
		if(!SecurityUtils.checkViewPermissionIsOK(list)){
			//真实姓名只显示第一个字，电话号码只显示后面三位，电子邮件只显示头三个字母以及邮箱地址，微信和qq都只显示后面三位字母
			if(!StringUtils.isEmpty(userInfo.getRealName())) {
				userInfo.setRealName(userInfo.getRealName().substring(0, 1)+StringUtils.MORE_ASTERISK);
			}
			if(!StringUtils.isEmpty(userInfo.getPhoneNum())) {
				String userNameNew=userInfo.getPhoneNum();
				Integer length=userNameNew.length();
				userInfo.setPhoneNum(userNameNew.substring(0, length-4)+StringUtils.MORE_ASTERISK);
			}
			if(!StringUtils.isEmpty(userInfo.getEmail())){
				String emailStr=userInfo.getEmail().substring(0, userInfo.getEmail().indexOf('@'));
				userInfo.setEmail(emailStr.substring(0,5)+StringUtils.MORE_ASTERISK+userInfo.getEmail().substring(userInfo.getEmail().indexOf('@')));
			}
			if(!StringUtils.isEmpty(userInfo.getWechat())) {
				userInfo.setWechat(userInfo.getWechat().substring(0, 5)+StringUtils.MORE_ASTERISK);
			}
			if(!StringUtils.isEmpty(userInfo.getQq())) {
				userInfo.setQq(userInfo.getQq().substring(0, 5)+StringUtils.MORE_ASTERISK);
			}
		}
		String superior=userInfo.getSuperior();
		if(!StringUtils.isBlank(superior)) {
			String[] stringArr=superior.split(",");
			String[] stringSuperior = new String[stringArr.length];
			for(int a=0;a<stringArr.length;a++) {
				Integer userIdNew=Integer.valueOf(stringArr[a]);
				if(userIdNew==Constants.VAL_SUPERIOR) {
					userIdNew=userDao.getGeneralAgency().getId();
				}
				UserInfo userInfoNew=userDao.getUserById(userIdNew);
				if(userInfoNew!=null) {
					stringSuperior[a]=userInfoNew.getUserName();
				}
			}
			String str2 = StringUtils.join(stringSuperior, ",");
			userInfo.setSuperior(str2);
			
			
			userInfo.setLoginPwd(StringUtils.MORE_ASTERISK);
			userInfo.setFundPwd(StringUtils.MORE_ASTERISK);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put(Message.KEY_DATA, userInfo);
		}else {
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put(Message.KEY_DATA, userInfo);
		}
		return ret;
	}

	@Override
	public void updateUser(UserInfo userInfo) {
		supserDao.update(userInfo);
	}

	@Override
	public Map<String, Object> saveRandomDemoUserInfo(HttpServletRequest request) {
		Map<String,Object> ret=new HashMap<String,Object>();
		
		Map<String,SysCode> maps =  cacheRedisService.getSysCode(SysCodeTypes.DEMO_USER_CFG.getCode());
		
		UserInfo user = new UserInfo();
		user.setUserType(UserType.DEMO_PLAYER.getCode());
		user.setRegIp(request.getRemoteHost());
		user.setCreateTime(new Date());
		
		int maxIpReg = Utils.toInteger(maps.get("ip_max_reg_size").getCodeVal());
		int curIpRegSize = Utils.toInteger(userDao.getCountUser(user));
		if(curIpRegSize >= maxIpReg){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_SAME_IP_LIMIT_MAX_REG.getCode());
			ret.put(Message.KEY_ERROR_MES, String.format(Message.Error.ERROR_SAME_IP_LIMIT_MAX_REG.getErrorMes(), user.getRegIp(),curIpRegSize));
			return ret;
		}
		user.setRegIp("");
		user.setCreateTime(null);
		String demoName = String.format(maps.get("demo_user_name_format").getCodeVal(),userDao.getCountUser(user)+1);
		String demoPwd = StringUtils.getRandomString(Utils.toInteger(maps.get("demo_user_pwd_format").getCodeVal()));
		double demoBal = Utils.toDouble(maps.get("demo_user_bal").getCodeVal());
		
		
		//查找总代理
		UserInfo dbAgent = (UserInfo) supserDao.findByName(UserInfo.class, "userType", UserType.GENERAL_AGENCY.getCode()).get(0);
		
		user.setSuperior(dbAgent.getId().toString());
		user.setUserName(demoName);
		user.setLoginPwd(demoPwd);
		user.setFundPwd(demoPwd);
		regUser(user,request);
		
		UserAccount dbAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId", user.getId(), "accType", WalletType.MAIN_WALLET.getCode()).get(0);
		dbAcc.setBalance(new Double(demoBal));
		supserDao.update(dbAcc);
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("userName", demoName);
		map.put(Message.KEY_DEFAULT_PASSWORD, demoPwd);
		map.put("balance", demoBal);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,map);
		return ret;
	}

	@Override
	public Map<String, Object> updateDemoUserDisableLogin() {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = getCurLoginInfo();
		if(null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		if(UserType.DEMO_PLAYER.getCode() == dbInfo.getUserType()){
			dbInfo.setState(UserState.REVOKED.getCode());
			supserDao.update(dbInfo);
		}
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> processCancelBetOrder(String orderNum) {
		Map<String, Object> ret = new HashMap<String, Object>();
		OrderInfo queryOder = orderDao.getOrderInfo(orderNum);
		if(null == queryOder
				|| null == getCurLoginInfo()
				|| !getCurLoginInfo().getId().equals(queryOder.getUserId())
				|| OrderState.WAITTING_PAYOUT.getCode() != queryOder.getState().intValue()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PAYMENT_TLCLOUD_FAILED_CANCEL_ORDER.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_PAYMENT_TLCLOUD_FAILED_CANCEL_ORDER.getErrorMes());
			return ret;
		}
		List<OrderInfo> winLists = new ArrayList<>();
		winLists.add(queryOder);
		issueService.processCalcelOrderWinAmtAndAccRecord(winLists,true,false,false,OrderState.USER_CANCEL);
		supserDao.updateList(winLists);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public boolean verifRebate(UserInfo userInfo) {
		String superior=userInfo.getSuperior();
		String first=StringUtils.substringBefore(superior,",");
		Integer lastAgentId=Integer.valueOf(first);
		if(lastAgentId==Constants.VAL_SUPERIOR) {
			lastAgentId=userDao.getGeneralAgency().getId();
		}
		BigDecimal lastAgentPlatRebate=this.getUserById(lastAgentId).getPlatRebate();
		BigDecimal selfPlatRebate=userInfo.getRebate().add(userInfo.getPlatRebate());
		int a=lastAgentPlatRebate.compareTo(selfPlatRebate);
		if(a==0) {
			return true;
		}
		return false;
	}

	@Override
	public Map<String, Object> queryAgentByAgentHou(Integer id, String startTime, String endTime) {
		boolean isOrNo=this.isOrNoUserInfo(id);
		Map<String,Object> map=new HashMap<String,Object>();
		if(isOrNo) {
			UserInfo userInfo=userDao.getUserById(id);
			if(userInfo.getUserType()==Constants.UserType.GENERAL_AGENCY.getCode()) {
				id=0;
			}
			List<UserInfo> list=userDao.queryAgentByAgentHou(id,startTime,endTime);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			if(list!=null&&list.size()>0) { 
				map.put("data", list);
			}else { 
				map.put("data", null);
			}
			return map;
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return map;
		}
	}

	@Override
	public PageBean<UserInfo> querySiteMsgRec(Integer pageIndex, 
			Integer pageSize, 
			UserInfo sender, 
			Map<String, Object> params) {
		
 		PageBean<UserInfo> page = new PageBean<>();
		StringBuffer sql = new StringBuffer();
		List<Object> params_ = new ArrayList<>();
		
		String receiverName = params.get(Constants.KEY_SITE_MSG_RECEIVER) == null?
				null:(String)params.get(Constants.KEY_SITE_MSG_RECEIVER);
		
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		page.setParams(params_);
		
		if(sender.getUserType().intValue() == Constants.UserType.AGENCY.getCode()
				|| sender.getUserType().intValue() == Constants.UserType.PLAYER.getCode()) {
			sql.append("from UserInfo t where t.userType =?");
			
			params_.add(Constants.UserType.SYS_ADMIN.getCode());
		}else if(sender.getUserType().intValue() == Constants.UserType.SYS_ADMIN.getCode()) {
			sql.append("from UserInfo t where t.userType =? or t.userType =?");
			params_.add(Constants.UserType.AGENCY.getCode());
			params_.add(Constants.UserType.PLAYER.getCode());			
		}
		
		if(!StringUtils.isBlank(receiverName)) {
			sql.append(" and t.userName =?");
			params_.add(receiverName);			
		}
		
		return userDao.querySiteMsgRec(sql.toString(), page);
	}
}

package com.jll.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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
import com.jll.common.constants.Constants.CreditRecordType;
import com.jll.common.constants.Constants.DepositOrderState;
import com.jll.common.constants.Constants.EmailValidState;
import com.jll.common.constants.Constants.PhoneValidState;
import com.jll.common.constants.Constants.SiteMessageReadType;
import com.jll.common.constants.Constants.State;
import com.jll.common.constants.Constants.SysCodeState;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.constants.Constants.SysCodeTypesFlag;
import com.jll.common.constants.Constants.SysNotifyReceiverType;
import com.jll.common.constants.Constants.SysNotifyType;
import com.jll.common.constants.Constants.UserLevel;
import com.jll.common.constants.Constants.UserState;
import com.jll.common.constants.Constants.UserType;
import com.jll.common.constants.Constants.WalletType;
import com.jll.common.constants.Message;
import com.jll.common.utils.BigDecimalUtil;
import com.jll.common.utils.SecurityUtils;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
import com.jll.dao.SupserDao;
import com.jll.entity.DepositApplication;
import com.jll.entity.MemberPlReport;
import com.jll.entity.OrderInfo;
import com.jll.entity.Promo;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.SysCode;
import com.jll.entity.SysNotification;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserBankCard;
import com.jll.entity.UserInfo;
import com.jll.sysSettings.syscode.SysCodeService;
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
	
	@Resource
	CacheRedisService cacheRedisService;
	
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
		List<SysCode> types = sysCodeService.queryAllSmallType(SysCodeTypes.LOTTERY_TYPES.getCode());
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", types);
		return ret;
	}

	@Override
	public Map<String, Object> verifyUserBankInfo(int userId, UserBankCard bank) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> bankInfo =  getUserBankLists(userId);
		List<?> bankList = (List<?>) bankInfo.get(Message.KEY_DATA);
		
		int maxCardNum = Integer.valueOf(((SysCode)cacheRedisService.getSysCode(SysCodeTypes.BANK_LIST.getCode()).get(SysCodeTypes.BANK_LIST.getCode())).getCodeVal());
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
	@Override
	public void resetLoginPwd(UserInfo user) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();		
		user.setLoginPwd(encoder.encode(defaultPwd));
		
		userDao.saveUser(user);
	}
	@Override
	public void resetFundPwd(UserInfo user) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();		
		user.setFundPwd(encoder.encode(defaultPwd));
		
		userDao.saveUser(user);
	}
	@Override
	public Map<String, Object> getUserNotifyLists(int userId) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = (UserInfo) supserDao.get(UserInfo.class,userId);
		if(null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		Object[] upUserId = StringUtils.getUserSupersId(dbInfo.getSuperior());
		
		Object[] query = {SysNotifyType.ALL_USER.getCode()};
		query[1] = SysNotifyType.ALL_COM_USER.getCode();
		if(UserType.AGENCY.getCode() == dbInfo.getUserType()){
			query[1] = SysNotifyType.ALL_AGENT.getCode();
		}
		DetachedCriteria dc = DetachedCriteria.forClass(SysNotification.class);
		dc.add(Restrictions.le("expireTime",new Date()));
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
		
		System.out.println();
	}

	@Override
	public Map<String, Object> getUserSiteMessageLists(int userId) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = (UserInfo) supserDao.get(UserInfo.class,userId);
		if(null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		DetachedCriteria dc = DetachedCriteria.forClass(SiteMessage.class);
		dc.add(Restrictions.eq("userId",userId));
		dc.add(Restrictions.le("expireTime",new Date()));
		dc.addOrder(Order.desc("expireTime"));
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,supserDao.findByCriteria(dc));
		return ret;
		
	}
	
	private void getAllSiteMessageFeedback(List<SiteMessFeedback> backList , int userId, int msgId){
		DetachedCriteria dc = DetachedCriteria.forClass(SiteMessFeedback.class);
		dc.add(Restrictions.eq("mesId",msgId));
		dc.add(Restrictions.eq("fbUserId",userId));
		List<SiteMessFeedback> dbBacks = supserDao.findByCriteria(dc);
		if(null!= dbBacks && !dbBacks.isEmpty()){
			backList.add(dbBacks.get(0));
			getAllSiteMessageFeedback(backList, dbBacks.get(0).getFbUserId(), dbBacks.get(0).getId());
		}
		
	}

	@Override
	public Map<String, Object> showSiteMessageFeedback(int userId, int msgId) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = (UserInfo) supserDao.get(UserInfo.class,userId);
		SiteMessage dbMsg = (SiteMessage) supserDao.get(SiteMessage.class,msgId);
		if(null == dbInfo
				|| null == dbMsg){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		dbMsg.setIsRead(SiteMessageReadType.READING.getCode());
		
		DetachedCriteria dc = DetachedCriteria.forClass(SiteMessFeedback.class);
		dc.add(Restrictions.eq("mesId",msgId));
		dc.add(Restrictions.eq("fbUserId",userId));
		
		List<SiteMessFeedback> retList = new ArrayList<>();
		
		getAllSiteMessageFeedback(retList, userId, msgId);
		Collections.sort(retList, new Comparator<SiteMessFeedback>() {
			@Override
			public int compare(SiteMessFeedback b1, SiteMessFeedback b2) {
				return b2.getId()-b1.getId();
			}
		});
		
		if(!retList.isEmpty()){
			SiteMessFeedback lastBack = retList.get(retList.size()-1);
			if(lastBack.getIsRead() == SiteMessageReadType.UN_READING.getCode()
					&& dbMsg.getReceiver()>0){
				lastBack.setIsRead(SiteMessageReadType.READING.getCode());
				supserDao.update(lastBack);
				dbMsg.setIsRead(SiteMessageReadType.READING.getCode());
				supserDao.update(dbMsg);
			}
		}
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,dbMsg);
		ret.put(Message.KEY_REMAKE,retList);
		return ret;
	
	}

	@Override
	public Map<String, Object> siteMessageFeedback(int userId, int msgId, SiteMessFeedback back) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = (UserInfo) supserDao.get(UserInfo.class,userId);
		SiteMessage dbMsg = (SiteMessage) supserDao.get(SiteMessage.class,msgId);
		SiteMessFeedback dbBack = new SiteMessFeedback();
		
		if(back.getMesId() > 0){
			dbBack = (SiteMessFeedback) supserDao.get(SiteMessFeedback.class,back.getMesId());;
		}
		if(null == dbInfo
				|| null == dbMsg
				|| null == dbBack){
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
		
		// 不是首次回复...
		if(dbBack.getMesId() > 0){
			back.setMesId(dbMsg.getId());
			back.setFbUserId(dbBack.getFbUserId());
		}else{
			//该信息是的类型是 system => user, 回复： user => system
			back.setMesId(msgId);
			back.setFbUserId(dbMsg.getUserId());
		}
		
		int validDay = Integer.valueOf(((SysCode)cacheRedisService.getSysCode(SysCodeTypes.SITE_MSG_VALID_DAY.getCode()).get(SysCodeTypes.SITE_MSG_VALID_DAY.getCode())).getCodeVal());
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
	public Map<String, Object> addSiteMessage(int userId, String sendIds, SiteMessage msg) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = (UserInfo) supserDao.get(UserInfo.class,userId);
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
		msg.setCreator(userId);
		msg.setIsRead(Constants.SiteMessageReadType.UN_READING.getCode());
		//system to user
		if(StringUtils.isEmpty(sendIds)){
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
			int validDay = Integer.valueOf(((SysCode)cacheRedisService.getSysCode(SysCodeTypes.SITE_MSG_VALID_DAY.getCode()).get(SysCodeTypes.SITE_MSG_VALID_DAY.getCode())).getCodeVal());
			msg.setExpireTime(DateUtils.addDays(new Date(), validDay));
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
			msg.setUserId(userId);
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
		if(null != finds && !finds.isEmpty()){
			Object[] totalObj = (Object[]) finds.get(0);
			return BigDecimalUtil.toDouble(totalObj[0]);
		}
		return 0;
	}

	@Override
	public double getUserTotalBetAmt(Date startDate,Date endDate,UserInfo user) {
		DetachedCriteria criteria = DetachedCriteria.forClass(OrderInfo.class);
		criteria.add(Restrictions.ge("createTime",startDate));
		criteria.add(Restrictions.le("createTime",endDate));
		
		Object[] query = {State.HAS_WON.getCode(),State.NOT_WON.getCode()};
		criteria.add(Restrictions.eq("userId",user.getId()));
		criteria.add(Restrictions.in("state",query));
		criteria.setProjection(Projections.sum("betAmount"));
		List<?> finds =  supserDao.findByCriteria(criteria);
		if(null != finds && !finds.isEmpty()){
			Object[] totalObj = (Object[]) finds.get(0);
			return BigDecimalUtil.toDouble(totalObj[0]);
		}
		return 0;
	}
	//修改用户状态
	@Override
	public void updateUserType(UserInfo user) {
		userDao.saveUser(user);
	}
	//查询所有的用户
	@Override
	public List<UserInfo> queryAllUserInfo(Map<String, Object> map) {
		Integer id=(Integer) map.get("id");
		String userName=(String) map.get("userName");
		String proxyName=(String) map.get("proxyName");
		String startTime=(String) map.get("startTime");
		String endTime=(String) map.get("endTime");
		UserInfo userInfo=userDao.getUserByUserName(proxyName);
		Integer proxyId=null;
		if(userInfo!=null) {
			proxyId=userInfo.getId();
		}
		List<UserInfo> userInfoList=userDao.queryAllUserInfo(id, userName, proxyId, startTime, endTime);
		return userInfoList;
	}
	

	@Override
	public Map<String, Object> exchangePoint(int userId, double amount) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = (UserInfo) supserDao.get(UserInfo.class,userId);
		
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
		
		
		UserAccount redAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId", dbInfo.getId(), "accType", WalletType.RED_PACKET_WALLET.getCode()).get(0);
		
		//主钱包积分减少
		UserAccountDetails addDtl = new UserAccountDetails();
		addDtl.setUserId(dbInfo.getId());
		addDtl.setCreateTime(new Date());
		addDtl.setAmount(Double.valueOf(amount).floatValue());
		addDtl.setPreAmount(dbAcc.getRewardPoints().floatValue());
		addDtl.setPostAmount(Double.valueOf(BigDecimalUtil.sub(addDtl.getPreAmount(),addDtl.getAmount())).floatValue());
		addDtl.setWalletId(dbAcc.getId());
		addDtl.setOperationType(CreditRecordType.POINT_EXCHANGE.getCode());
		supserDao.save(addDtl);
		dbAcc.setRewardPoints(addDtl.getPostAmount().longValue());
		supserDao.update(dbAcc);
		
		SysCode dbCode = cacheRedisService.getSysCode(SysCodeTypes.POINT_EXCHANGE_SCALE.getCode(), SysCodeTypes.POINT_EXCHANGE_SCALE.getCode());
		if(dbCode.getState() ==  SysCodeState.INVALID_STATE.getCode()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PROMS_INVALID.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_PROMS_INVALID.getErrorMes());
			return ret;
		}
		
		double redAddAmt = BigDecimalUtil.mul(amount, Double.valueOf(dbCode.getCodeVal()));
		//红包钱包金额增加
		UserAccountDetails addRedDtl = new UserAccountDetails();
		addRedDtl.setUserId(dbInfo.getId());
		addRedDtl.setCreateTime(new Date());
		addRedDtl.setAmount(Double.valueOf(redAddAmt).floatValue());
		addRedDtl.setPreAmount(redAcc.getBalance().floatValue());
		addRedDtl.setPostAmount(Double.valueOf(BigDecimalUtil.add(addRedDtl.getPreAmount(),addRedDtl.getAmount())).floatValue());
		addRedDtl.setWalletId(redAcc.getId());
		addRedDtl.setOperationType(CreditRecordType.POINT_EXCHANGE.getCode());
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
		return getUserByUserName(auth.getName());
	}

	@Override
	public Map<String, Object> userProfitReport(String userName) {
		Map<String, Object> ret = new HashMap<String, Object>();
		MemberPlReport redAcc = new MemberPlReport();
		List<?> finds =  supserDao.findByName(MemberPlReport.class, "userName",userName);
		if(!finds.isEmpty()){
			redAcc = (MemberPlReport) finds.get(0);
		}
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,redAcc);
		return null;
	}
	
}

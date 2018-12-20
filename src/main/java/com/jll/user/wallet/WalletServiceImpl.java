package com.jll.user.wallet;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Message;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.WalletType;
import com.jll.entity.UserAccount;
import com.jll.entity.UserInfo;
import com.jll.user.UserInfoService;


@Service
@Transactional
public class WalletServiceImpl implements WalletService
{
	private Logger logger = Logger.getLogger(WalletServiceImpl.class);
	
	@Resource
	WalletDao walletDao;
	@Resource
	UserInfoService userInfoService;
	@Override
	public void createWallet(UserInfo user) {
		if(user == null) {
			logger.error("Invalid user!!!");
			throw new RuntimeException("Invalid user!!!");
		}
		
		createWallet(user, WalletType.MAIN_WALLET);
		createWallet(user, WalletType.RED_PACKET_WALLET);
	}

	private void createWallet(UserInfo user, WalletType wt) {
		
		UserAccount wallet = new UserAccount();
		wallet.setAccName(wt.getDesc());
		wallet.setAccType(wt.getCode());
		wallet.setBalance(0.0D);
		wallet.setFreeze(0.0D);
		wallet.setPrize(0.0D);
		wallet.setRewardPoints(0L);
		wallet.setUserId(user.getId());
		wallet.setState(Constants.WalletState.NORMAL.getCode());
		wallet.setRemark(wt.getDesc());
		walletDao.createWallet(wallet);
	}

	@Override
	public void updateWallet(UserAccount wallet) {
		walletDao.updateWallet(wallet);
	}

	@Override
	public UserAccount queryById(int walletId) {
		return walletDao.queryById(walletId);
	}
	//通过用户名(false)或时间去查询(true)
	@Override
	public Map<String,Object> queryUserAccount(Map<String, Object> ret) {
		String userName=(String) ret.get("userName");
//		String startTime=(String) ret.get("startTime");
//		String endTime=(String) ret.get("endTime");
		Integer pageIndex=(Integer) ret.get("pageIndex");
		Integer pageSize=(Integer) ret.get("pageSize");
//		Map<String,Object> map=new HashMap<String,Object>();
//		if(!StringUtils.isBlank(userName)) {
//			boolean isNull=userInfoService.isUserInfo(userName);
//			if(!isNull) {
//				map.clear();
//				map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
//				map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
//				map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
//				return map;
//			}
//		}
		Map<String,Object> userAccountLists=walletDao.queryUserAccount(userName,pageIndex,pageSize);
		userAccountLists.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return userAccountLists;
	}
	//修改用户的状态
	@Override
	public Map<String, Object> updateState(Integer userId, Integer state) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean isNo=this.isOrNo(userId);
		if(!isNo) {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return map;
		}
		walletDao.updateState(userId, state);
		map.clear();
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
	//判断该用户在userAccount表存不存在
	@Override
	public boolean isOrNo(Integer userId) {
		List<UserAccount> list=walletDao.queryByUserId(userId);
		if(list!=null&&list.size()>0) {
			return true;
		}
		return false;
	}
	//通过id
	@Override
	public Map<String, Object> queryByIdUserAccount(Map<String, Object> ret) {
		Integer id=(Integer) ret.get("id");
		Map<String,Object> map=new HashMap<String,Object>();
		boolean isNull=this.isNo(id);
		if(!isNull) {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return map;
		}
		Map<String,Object> userAccountLists=walletDao.queryByIdUserAccount(id);
		userAccountLists.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return userAccountLists;
	}

	@Override
	public boolean isNo(Integer id) {
		List<UserAccount> list=walletDao.queryById(id);
		if(list!=null&&list.size()>0) {
			return true;
		}
		return false;
	}
	//通过用户ID查询当前用户的钱包
	@Override
	public Map<String, Object> queryByUserIdUserAccount(Map<String, Object> ret) {
		Integer userId=(Integer) ret.get("userId");
		Map<String,Object> map=new HashMap<String,Object>();
		Map<String,Object> userAccountLists=walletDao.queryByUserIdUserAccount(userId);
		return userAccountLists;
	}
	//通过用户名查询用户的主钱包
	@Override
	public Map<String, Object> queryUserAccount() {
		String userName=SecurityContextHolder.getContext().getAuthentication().getName();//当前登录的用户
		UserInfo userInfo=userInfoService.getUserByUserName(userName);
		Map<String,Object> map=new HashMap<String,Object>();
		Map<String,Object> userAccountLists=walletDao.queryUserAccount(userInfo.getId(),Constants.WalletType.MAIN_WALLET.getCode());
		return userAccountLists;
	}

	@Override
	public UserAccount queryUserAccount(Integer id, Integer walletType) {
		UserAccount wallet = walletDao.queryUserAccountByUserAndWalletId(id, walletType);
		return wallet;
	}
}

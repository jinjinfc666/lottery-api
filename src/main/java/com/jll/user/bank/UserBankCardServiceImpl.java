package com.jll.user.bank;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.entity.UserBankCard;

@Service
@Transactional
public class UserBankCardServiceImpl implements UserBankCardService
{
	private Logger logger = Logger.getLogger(UserBankCardServiceImpl.class);
	
	@Resource
	UserBankCardDao userBankCardDao;
	@Override
	public void addUserBankCard(UserBankCard userBankCard) {
		userBankCardDao.save(userBankCard);
	}
	//通过ID删除银行卡
	@Override
	public Map<String,Object> deleteBank(Integer id) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean isOrNull=this.isOrNo(id);
		if(!isOrNull) {
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USERBANK_DOES_NOT_EXIST.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USERBANK_DOES_NOT_EXIST.getErrorMes());
		}
		userBankCardDao.deleteBank(id);
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
	//判断这张银行卡是否存在
	@Override
	public boolean isOrNo(Integer id) {
		UserBankCard userBankCard=userBankCardDao.queryById(id);
		if(userBankCard!=null) {
			return true;
		}
		return false;
	}
	//通过用户ID查询银行卡
	@Override
	public List<?> queryByUserId(Integer userId,Integer sysCodeId) {
		return userBankCardDao.queryByUserId(userId, sysCodeId);
	}
	//判断这个用户是否有银行卡
	@Override
	public boolean haveOrNot(Integer userId) {
		List<UserBankCard> list=userBankCardDao.queryByUserId(userId);
		if(list!=null&&list.size()>0) {
			return true;
		}
		return false;
	}
}

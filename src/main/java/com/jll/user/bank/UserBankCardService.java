package com.jll.user.bank;

import java.util.List;
import java.util.Map;

import com.jll.entity.UserBankCard;

public interface UserBankCardService
{
	void addUserBankCard(UserBankCard userBankCard);
	//通过ID删除银行卡
	Map<String,Object> deleteBank(Integer id);
	//判断这张银行卡是否存在
	boolean isOrNo(Integer id);
	//通过用户ID查询银行卡
	List<?> queryByUserId(Integer userId,Integer sysCodeId);
	//判断这个用户是否有银行卡
	boolean haveOrNot(Integer userId);
}

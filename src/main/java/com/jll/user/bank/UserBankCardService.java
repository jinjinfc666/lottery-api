package com.jll.user.bank;

import java.util.Map;

import com.jll.entity.UserBankCard;

public interface UserBankCardService
{
	void addUserBankCard(UserBankCard userBankCard);
	//通过ID删除银行卡
	Map<String,Object> deleteBank(Integer id);
	//判断这张银行卡是否存在
	boolean isOrNo(Integer id);
}

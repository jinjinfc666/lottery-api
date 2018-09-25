package com.jll.user.bank;

import java.util.List;

import com.jll.entity.UserBankCard;

public interface UserBankCardDao
{
	void save(UserBankCard userBankCard);
	//通过ID删除银行卡
	void deleteBank(Integer id);
	//通过ID查询银行卡
	UserBankCard queryById(Integer id);
	//通过用户ID查询银行卡
	List<?> queryByUserId(Integer userId,Integer sysCodeId);
	//通过用户id查询用户银行卡
	List<UserBankCard> queryByUserId(Integer userId);
}

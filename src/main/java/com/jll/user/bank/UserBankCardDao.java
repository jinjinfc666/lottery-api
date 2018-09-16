package com.jll.user.bank;

import com.jll.entity.UserBankCard;

public interface UserBankCardDao
{
	void save(UserBankCard userBankCard);
	//通过ID删除银行卡
	void deleteBank(Integer id);
	//通过ID查询银行卡
	UserBankCard queryById(Integer id);
}

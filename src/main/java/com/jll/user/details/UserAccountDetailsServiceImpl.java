package com.jll.user.details;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants;
import com.jll.common.utils.BigDecimalUtil;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;

@Configuration
@PropertySource("classpath:sys-setting.properties")
@Service
@Transactional
public class UserAccountDetailsServiceImpl implements UserAccountDetailsService
{
	private Logger logger = Logger.getLogger(UserAccountDetailsServiceImpl.class);

	@Resource
	UserAccountDetailsDao accDetailsDao;
	
	@Override
	public void saveAccDetails(UserAccountDetails userDetails) {
		accDetailsDao.saveAccDetails(userDetails);
	}

	@Override
	public double getUserOperAmountTotal(int userId, int walletId,String operationType, Date start, Date end) {
		return accDetailsDao.getUserOperAmountTotal(userId,walletId, operationType, start, end);
	}
	
	@Override
	public UserAccountDetails initCreidrRecord(int userId,UserAccount userAcc,double beforAmt,double addAmt,String operType){
		UserAccountDetails addRedDtl = new UserAccountDetails();
		addRedDtl.setUserId(userId);
		addRedDtl.setCreateTime(new Date());
		addRedDtl.setAmount(Math.abs(Double.valueOf(addAmt).floatValue()));
		addRedDtl.setPreAmount(Double.valueOf(beforAmt).floatValue());
		addRedDtl.setPostAmount(Double.valueOf(BigDecimalUtil.add(addRedDtl.getPreAmount(),addAmt)).floatValue());
		addRedDtl.setWalletId(userAcc.getId());
		addRedDtl.setOperationType(operType);
		return addRedDtl;
	}
	
}

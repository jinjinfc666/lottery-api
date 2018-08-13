package com.jll.user.details;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.entity.UserAccountDetails;

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
	
	
}

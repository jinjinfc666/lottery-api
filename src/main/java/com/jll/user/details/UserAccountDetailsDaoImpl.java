package com.jll.user.details;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.UserAccountDetails;

@Repository
public class UserAccountDetailsDaoImpl extends DefaultGenericDaoImpl<UserAccountDetails> implements UserAccountDetailsDao
{
	private Logger logger = Logger.getLogger(UserAccountDetailsDaoImpl.class);

	@Override
	public void saveAccDetails(UserAccountDetails userDetails) {
		this.saveOrUpdate(userDetails);
	}


	
}

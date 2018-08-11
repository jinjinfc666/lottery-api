package com.jll.user.details;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants.UserType;
import com.jll.common.utils.StringUtils;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.UserInfo;

@Repository
public class UserAccountDetailsDaoImpl extends DefaultGenericDaoImpl<UserInfo> implements UserAccountDetailsDao
{
	private Logger logger = Logger.getLogger(UserAccountDetailsDaoImpl.class);


	
}

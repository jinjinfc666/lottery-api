package com.jll.user.tp;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class SMSServiceImpl implements SMSService
{
	private Logger logger = Logger.getLogger(SMSServiceImpl.class);

	@Override
	public String sending6digitsNumbers(String phoneNum) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

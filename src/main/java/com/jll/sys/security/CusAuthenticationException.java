package com.jll.sys.security;

import org.springframework.security.core.AuthenticationException;

public class CusAuthenticationException extends AuthenticationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3512555382560201019L;

	public CusAuthenticationException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}

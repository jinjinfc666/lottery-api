package com.jll.sys.sig;

import com.jll.entity.SignupRec;

public interface SignupRecService
{
	void saveSignupRec(SignupRec signupRec);
	Integer getCount(Integer userId);
}

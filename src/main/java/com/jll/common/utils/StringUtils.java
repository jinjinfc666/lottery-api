package com.jll.common.utils;

import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
	public  final static String MORE_ASTERISK = "****";
	
	/**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
 
    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
 
    /**
     * 正则表达式：验证汉字
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5]{2,6}$";
    
    
    public static final String REGEX_FUND_PWD = "^[0-9A-Za-z]{6,16}$";
    
    public static final String REGEX_LOGIN_PWD = "^[0-9A-Za-z]{6,16}$";
    
    public static final String REGEX_QQ = "^[0-9]{6,10}$";
    
    public static final String REGEX_WECHAR = "^[0-9A-Za-z]{6,16}$";
	
	public static boolean checkFundPwdFmtIsOK(String str){
		return Pattern.matches(REGEX_LOGIN_PWD, str);
	}
	
	public static boolean checkLoginPwdFmtIsOK(String str){
		return Pattern.matches(REGEX_LOGIN_PWD, str);
	}
	
	public static boolean checkPhoneFmtIsOK(String str){
		return Pattern.matches(REGEX_MOBILE, str);
	}
	
	public static boolean checkQqFmtIsOK(String str){
		return Pattern.matches(REGEX_QQ, str);
	}
	
	public static boolean checkEmailFmtIsOK(String str){
		return Pattern.matches(REGEX_EMAIL, str);
	}
	
	public static boolean checkRealNameFmtIsOK(String str){
		return Pattern.matches(REGEX_CHINESE, str);
	}
	
	public static boolean checkWercharFmtIsOK(String str){
		return Pattern.matches(REGEX_WECHAR, str);
	}

	public static String abbreviate(final String str, final int length,final String abrevMarker) {
        if (isEmpty(str)) {
            return "";
        }
        if (str.length() <=length ) {
            return str + abrevMarker;
        }
        return str.substring(str.length()-length,str.length())+abrevMarker;
    }
	
}

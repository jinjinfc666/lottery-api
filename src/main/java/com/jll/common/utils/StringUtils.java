package com.jll.common.utils;

import java.util.Random;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
	public  final static String MORE_ASTERISK = "****";
	
	public  final static String ALL = "ALL";
	
	public  final static String COMMA = ",";
	public  final static String EMPTY = "";
	
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
	
	public static String[] getUserSupersId(String userPaath){
		if(isEmpty(userPaath)){
			return new String[]{};
		}
		return userPaath.split(",");
	}
	
	public static String generateStingByLength(String str1,int len1,String str2,int len2){
		StringBuffer strs = new StringBuffer();
		char[] chr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        Random random = new Random();
        int index = 0;
        for (int i = 0; i < len1+len2; i++) {
        	if("M".equals(chr[random.nextInt(chr.length)])
        			|| "s".equals(chr[random.nextInt(chr.length)])){
        		if(index <= len2){
        			index++;
	        		strs.append(str2);
        		}else{
        			strs.append(str1);
        		}
        	}else{
        		strs.append(str1);
        	}
        }
        
        String checkStr = strs.toString().replaceAll(str1, EMPTY);
        if(checkStr.length() < len2){
        	char[] retChar =  strs.toString().toCharArray();
        	 for (int i = 0; i < len2 - checkStr.length(); i++) {
        		 int retInt = random.nextInt(retChar.length);
        		 if(retInt%2 ==0){
        			 retInt ++;
        		 }
        		 retChar[retInt] = str2.toCharArray()[0];
        	 }
        	 return String.valueOf(retChar);
        }
		return strs.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(generateStingByLength("A", 80,"B",20));
		
	}
	
}

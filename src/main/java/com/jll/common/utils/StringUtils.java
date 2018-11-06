package com.jll.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.regex.Pattern;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
	public  final static String MORE_ASTERISK = "****";
	
	public  final static String ALL = "ALL";
	
	public  final static String COMMA = ",";
	public  final static String EMPTY = "";
	
	public  final static String OPE_LOG_PROCESS_DEPOSIT = "ope_log_process_deposit";
	public  final static String OPE_LOG_RESET_PWD = "ope_log_reset_pwd";
	public  final static String OPE_LOG_VERIFY_PHONE = "ope_log_verify_phone";
	public  final static String OPE_LOG_VERIFY_EMAIL = "ope_log_verify_email";
	public  final static String OPE_LOG_ADD_BANK_CARD = "ope_log_add_bank_card";
	public  final static String OPE_LOG_PERFECT_USER_INFO = "ope_log_perfect_user_info";
	public  final static String OPE_LOG_MOD_LOGIN_PWD = "ope_log_mod_login_pwd";
	public  final static String OPE_LOG_MOD_FUND_PWD = "ope_log_mod_fund_pwd";
	public  final static String OPE_LOG_REG_USER = "ope_log_reg_user";
	public  final static String OPE_LOG_REG_AGENT = "ope_log_reg_agent";
	public  final static String OPE_LOG_PROCESS_WITHDRAW = "ope_log_process_withdraw";
	public  final static String OPE_LOG_MOD_PERMISSION = "ope_log_mod_permission";
	public  final static String OPE_LOG_SPEC_WINNING_NUM = "ope_log_spec_winning_num";
	public  final static String OPE_LOG_ISSUE_MANUAL_PAYOUT = "ope_log_issue_manual_payout";
	public  final static String OPE_LOG_REVOKE_PAYOUT = "ope_log_revoke_payout";
	public  final static String OPE_LOG_RE_PAYOUT = "ope_log_re_payout";
	public  final static String OPE_LOG_CANCEL_ISSUE = "ope_log_cancel_issue";
	public  final static String OPE_LOG_ORDER_MANUAL_PAYOUT = "ope_log_order_manual_payout";
	public  final static String OPE_LOG_CANCEL_ORDER = "ope_log_cancel_order";
	public  final static String OPE_LOG_OPER_USER_AMT = "ope_log_oper_user_amt";
	
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
	
	public static Integer[] getUserSupersId(String userPath){
		if(isEmpty(userPath)){
			return new Integer[]{};
		}
		String[] ids = userPath.split(",");
		Integer[] rets = new Integer[ids.length];
		for (int index = 0; index < ids.length; index++) {
			rets[index] = Integer.valueOf(ids[index]);
		}
		return rets;
	}
	
	public static String getStringValue(Object str){
		try{
			return str.toString();
		}catch (Exception e) {
			// TODO: handle exception
		}
		return EMPTY;
	}
	
	public static String getRandomString(int length) {
		StringBuffer buffer = new StringBuffer("0123456789");
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		int range = buffer.length();
		for (int i = 0; i < length; i++) {
			sb.append(buffer.charAt(r.nextInt(range)));
		}
		return sb.toString();
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
	
	/**
     * Description: 复制输入流</br>
     *
     * @param inputStream
     * @return</br>
     */
    public static InputStream cloneInputStream(ServletInputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len=0;
        try {
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return byteArrayInputStream;
    }

	public static String getBodyString(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = cloneInputStream(request.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
	
}

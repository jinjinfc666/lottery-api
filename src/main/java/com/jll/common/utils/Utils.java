package com.jll.common.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.jll.common.constants.Message;
import com.jll.common.utils.sequence.GenSequenceService;
import com.jll.entity.GenSequence;

public class Utils {
	public static final char[] alphabet = {'a','b','c','d','e','f','g','h','i',
											'j','k','l','m','n','o','p','q','r',
											's','t','u','v','w','x','y','z','0',
											'1','2','3','4','5','6','7','8','9'};
	
	@Resource
	GenSequenceService genSeqServ;
	
	public static String produce6DigitsCaptchaCode() {
		String ret = "";
		Random random = new Random();
		for(int i = 0; i < 6; i++) {
			int currIndex = random.nextInt(alphabet.length);
			ret += alphabet[currIndex];
		}
		return ret;
				
	}
	
	public synchronized static String gen16DigitsSeq(Long seq) {
		Date nowTime = new Date();
		StringBuffer ret = new StringBuffer();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		DecimalFormat numFormat = new DecimalFormat("000000");
		Random random = new Random();		
		
		ret.append(dateFormat.format(nowTime));
		ret.append(numFormat.format(seq.intValue()));
		for(int i = 0; i < 4; i++) {
			int currIndex = random.nextInt(10);
			ret.append(Integer.toString(currIndex));
		}
		return ret.toString();
				
	}
	
	public static String produce6DigitsCaptchaCodeNumber() {
		StringBuffer ret = new StringBuffer();
		Random random = new Random();
		for(int i = 0; i < 6; i++) {
			int currIndex = random.nextInt(10);
			ret.append(Integer.toString(currIndex));
		}
		return ret.toString();
				
	}
	
	public static boolean validUserName(String userName) {		
		String regex = "[a-zA-Z0-9_]{6,20}";
		return Pattern.matches(regex, userName);
	}

	public static boolean validPwd(String pwd) {
		String regex = "[a-zA-Z0-9_]{6,15}";
		return Pattern.matches(regex, pwd);
	}

	public static boolean validEmail(String email) {
		String regex = "[a-zA-Z0-9]+[@]{1}[a-zA-Z0-9]+[.]+[a-z]+";
		return Pattern.matches(regex, email);
	}

	public static boolean validPhone(String phoneNum) {
		 if (phoneNum.length() != 11)
	        {
	            return false;
	        }else{
	            /**
	             * 移动号段正则表达式
	             */
	            String pat1 = "^((13[4-9])|(147)|(15[0-2,7-9])|(178)|(18[2-4,7-8]))\\d{8}|(1705)\\d{7}$";
	            /**
	             * 联通号段正则表达式
	             */
	            String pat2  = "^((13[0-2])|(145)|(15[5-6])|(176)|(18[5,6]))\\d{8}|(1709)\\d{7}$";
	            /**
	             * 电信号段正则表达式
	             */
	            String pat3  = "^((133)|(153)|(177)|(18[0,1,9])|(149)|(199))\\d{8}$";
	            /**
	             * 虚拟运营商正则表达式
	             */
	            String pat4 = "^((170))\\d{8}|(1718)|(1719)\\d{7}$";

	            
	            boolean isMatch1 = Pattern.matches(pat1, phoneNum);
	            if(isMatch1){
	                return true;
	            }	            
	            
	            boolean isMatch2 = Pattern.matches(pat2, phoneNum);
	            if(isMatch2){
	                return true;
	            }
	            
	            boolean isMatch3 = Pattern.matches(pat3, phoneNum);
	            if(isMatch3){
	                return true;
	            }
	            
	            boolean isMatch4 = Pattern.matches(pat4, phoneNum);
	            if(isMatch4){
	                return true;
	            }
	            return false; 
	        }
	}

	public static boolean validRealName(String realName) {
		if(StringUtils.isBlank(realName)) {
			return false;
		}
		
		if(realName.length() <= 1 || realName.length() > 20) {
			return false;
		}
		
		
		return isChinese(realName);
	}
	
	private static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		
		if('·' == c) {
			return true;
		}
		return false;
	}
 
	// 完整的判断中文汉字和符号
	public static boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!isChinese(c)) {
				return false;
			}
		}
		return true;
	}
	
	public static Map<String,Object> validBankInfo(String bank){
		Map<String, Object> ret = new HashMap<String, Object>();
		Gson json = new Gson();
		Map<String, Object> checkResut = json.fromJson(HttpUtils.sendGetRepuest("https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo="+bank+"&cardBinCheck=true"), HashMap.class);
		if(!Boolean.valueOf(checkResut.get("validated").toString())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_INVALID_BANK_CARD.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_INVALID_BANK_CARD.getErrorMes());
			return ret;
		}
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,checkResut.get("bank").toString().toUpperCase());
		return ret;
	}

	public static Integer toInteger(Object val){
		try{
			return Integer.valueOf(val.toString());
		}catch (Exception e) {
		}
		return null;
	}
	
	public static String toString(Object val){
		try{
			return String.valueOf(val.toString());
		}catch (Exception e) {
		}
		return null;
	}
	
	public static Date toDate(Object val){
		try{
			return DateUtil.fmtYmdHisToDate(val.toString());
		}catch (Exception e) {
		}
		return null;
	}
}

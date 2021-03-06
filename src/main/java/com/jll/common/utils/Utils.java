package com.jll.common.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.jll.common.constants.Constants;
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
	
	public static  String produce5Digits0to9Number() {
		StringBuffer ret = new StringBuffer();
		Random random = new Random();
		for(int i = 0; i < 5; i++) {
			int currIndex = random.nextInt(10);
			ret.append(Integer.toString(currIndex)).append(",");
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				
			}
		}
		
		ret.delete(ret.length() - 1, ret.length() + 1);
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
		return 0;
	}
	
	public static Double toDouble(Object val){
		try{
			return Double.valueOf(val.toString());
		}catch (Exception e) {
		}
		return 0.00;
	}
	
	public static String toString(Object val){
		try{
			return String.valueOf(val.toString());
		}catch (Exception e) {
		}
		return "";
	}
	
	public static Date toDate(Object val){
		try{
			return DateUtil.fmtYmdHisToDate(val.toString());
		}catch (Exception e) {
		}
		return null;
	}

	public static boolean validateNum(String betNumTemp) {
		String regex = "[0-9]+";
		return Pattern.matches(regex, betNumTemp);
	}

	public static boolean validateEleIn5Num(String betNumTemp) {
		String regex = "((0[1-9])|(10)|(11)){1,11}";
		return Pattern.matches(regex, betNumTemp);
	}
	
	public static List<Map<String,String>> parseQszuxZsBetNumber(String betNum){
		List<Map<String,String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		List<String[]> arrangements = new ArrayList<>();
		List<String[]> finalRet = new ArrayList<>();
		
		for(String singleBetNumArray : betNumArray) {
			String[] betNumBits = new String[singleBetNumArray.length()];
			for(int i = 0; i < singleBetNumArray.length(); i++) {
				betNumBits[i] = singleBetNumArray.substring(i, i + 1);
			}
			List<String[]> combinations = new ArrayList<>();
			MathUtil.combinationSelect(betNumBits, 2, combinations);
			
			
			for(String[] combination : combinations) {
				MathUtil.arrangementSelect(combination, 2, arrangements);
				for(String[] arrangement : arrangements) {
					String repeatBit = arrangement[0];
					String[] extendArrangement = new String[3];
					extendArrangement[0] = repeatBit;
					extendArrangement[1] = arrangement[0];
					extendArrangement[2] = arrangement[1];
					finalRet.add(extendArrangement);
					
					String[] extendArrangement1 = new String[3];
					extendArrangement1[0] = arrangement[0];
					extendArrangement1[1] = arrangement[1];
					extendArrangement1[2] = repeatBit;
					finalRet.add(extendArrangement1);
					
					String repeatBit2 = arrangement[1];
					String[] extendArrangement2 = new String[3];
					extendArrangement2[0] = repeatBit2;
					extendArrangement2[1] = arrangement[0];
					extendArrangement2[2] = arrangement[1];
					finalRet.add(extendArrangement2);
					
					String[] extendArrangement3 = new String[3];
					extendArrangement3[0] = arrangement[0];
					extendArrangement3[1] = arrangement[1];
					extendArrangement3[2] = repeatBit2;
					finalRet.add(extendArrangement3);
					
					
				}
			}
			
			
			for(String[] temp : finalRet) {
				StringBuffer buffer = new StringBuffer();
				for(String tempBit : temp) {
					buffer.append(tempBit);
				}
				String tempStr = buffer.toString();
				if(!isExisting(betNumList, tempStr)) {
					Map<String, String> row = new HashMap<String, String>();
					row.put(Constants.KEY_FACADE_BET_NUM, tempStr);
					row.put(Constants.KEY_FACADE_PATTERN, tempStr + "[0-9]{2}");
					row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, tempStr + "00");
					betNumList.add(row);
					//betNumList.add(tempStr);
				}
			}
		}
		
		return betNumList;
	}
	
	private static boolean isExisting(List<Map<String, String>> betNumList, String tempStr) {
		for(Map<String, String> temp : betNumList) {
			String betNum = temp.get(Constants.KEY_FACADE_BET_NUM);
			if(StringUtils.isBlank(betNum)) {
				return false;
			}
			
			if(betNum.equals(tempStr)) {
				return true;
			}
		}
		
		return false;
	}

	public static List<Map<String, String>> parseQszuxZLBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		List<String[]> arrangements = new ArrayList<>();
		
		for(String singleBetNumArray : betNumArray) {
			String[] betNumBits = new String[singleBetNumArray.length()];
			for(int i = 0; i < singleBetNumArray.length(); i++) {
				betNumBits[i] = singleBetNumArray.substring(i, i + 1);
			}
			List<String[]> combinations = new ArrayList<>();
			MathUtil.combinationSelect(betNumBits, 3, combinations);
			
			
			for(String[] combination : combinations) {
				MathUtil.arrangementSelect(combination, 3, arrangements);				
			}
			
			
			for(String[] temp : arrangements) {
				StringBuffer buffer = new StringBuffer();
				for(String tempBit : temp) {
					buffer.append(tempBit);
				}
				
				//buffer.append("**");
				String tempStr = buffer.toString();
				if(!isExisting(betNumList, tempStr)) {
					Map<String, String> row = new HashMap<String, String>();
					row.put(Constants.KEY_FACADE_BET_NUM, tempStr);
					row.put(Constants.KEY_FACADE_PATTERN, tempStr + "[0-9]{2}");
					row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, tempStr + "00");				
					betNumList.add(row);
					//betNumList.add(tempStr);
				}
			}
		}
		
		return betNumList;
	}
}

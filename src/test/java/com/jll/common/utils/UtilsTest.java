package com.jll.common.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccount;
import com.jll.entity.UserInfo;


public class UtilsTest {

	//@Test
	public void testValidUserName_tooShort() {
		String userName = "t";
		boolean isValid = Utils.validUserName(userName);
		Assert.assertFalse(isValid);
	}

	//@Test
	public void testValidUserName_tooLong() {
		String userName = "test1111111111111111111111111111111111111111111111111111111111111111111111111111111111111test";
		boolean isValid = Utils.validUserName(userName);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidUserName_illegalCharacter() {
		String userName = "test111+";
		boolean isValid = Utils.validUserName(userName);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidUserName_pass() {
		String userName = "test11122_";
		boolean isValid = Utils.validUserName(userName);
		Assert.assertTrue(isValid);
	}
	
	//@Test
	public void testValidPwd() {
		String pwd = "test11122_";
		boolean isValid = Utils.validPwd(pwd);
		Assert.assertTrue(isValid);
	}
	
	//@Test
	public void testValidPwd_tooShort() {
		String pwd = "t2_";
		boolean isValid = Utils.validPwd(pwd);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidPwd_tooLong() {
		String pwd = "t21111111111111111111111111111111111111111111111111111111_";
		boolean isValid = Utils.validPwd(pwd);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidPwd_illegalCharacter() {
		String pwd = "t2222222_+";
		boolean isValid = Utils.validPwd(pwd);
		Assert.assertFalse(isValid);
	}

	//@Test
	public void testValidEmail() {
		String pwd = "test//@Test.com";
		boolean isValid = Utils.validEmail(pwd);
		Assert.assertTrue(isValid);
	}

	//@Test
	public void testValidEmail_noAtSign() {
		String pwd = "testtest.com";
		boolean isValid = Utils.validEmail(pwd);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidEmail_noDot() {
		String pwd = "test//@Testcom";
		boolean isValid = Utils.validEmail(pwd);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidEmail_() {
		String pwd = "//@Testcom";
		boolean isValid = Utils.validEmail(pwd);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidPhone() {
		String phone = "15927228888";
		boolean isValid = Utils.validPhone(phone);
		Assert.assertTrue(isValid);
	}
	
	//@Test
	public void testValidPhone_tooShort() {
		String phone = "111";
		boolean isValid = Utils.validPhone(phone);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidPhone_tooLong() {
		String phone = "111111111111111";
		boolean isValid = Utils.validPhone(phone);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidPhone_wrongNum() {
		String phone = "11111111111";
		boolean isValid = Utils.validPhone(phone);
		Assert.assertFalse(isValid);
	}

	//@Test
	public void testValidRealName() {
		String realName = "张三";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertTrue(isValid);
	}

	//@Test
	public void testValidRealName_dot() {
		String realName = "张三·丰";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertTrue(isValid);
	}
	
	//@Test
	public void testValidRealName_tooShort() {
		String realName = "张";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidRealName_tooLong() {
		String realName = "张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidRealName_alphabet() {
		String realName = "张三丰A";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testSplitBit() {
		String singleSel = "01,,03";
		int step = 1;
		
		List<String> retList = new ArrayList<>();
		StringBuffer buffer = new StringBuffer();
		
		for(int i = 0; i < singleSel.length();) {
			String temp = singleSel.substring(i, i + step);
			if(",".equals(temp)) {
				retList.add(buffer.toString());
				buffer.delete(0, buffer.length());
			}else {
				buffer.append(temp);
			}
			
			i += step;
			
			if(i >= singleSel.length()) {
				retList.add(buffer.toString());
				buffer.delete(0, buffer.length());
			}
		}
		
		Assert.assertTrue(retList.toArray(new String[0]).length == 3);
	}
	
	//@Test
	public void testCalRebate() {
		UserInfo user = new UserInfo();
		OrderInfo order = new OrderInfo();
		BigDecimal preAmount = new BigDecimal(0.93F);
		BigDecimal postAmount = new BigDecimal(0.93F);
		
		user.setRebate(new BigDecimal(0.65F));
		order.setBetAmount(10F);
		
		BigDecimal rebate = null;
		BigDecimal rebateRate = user.getRebate();
		rebateRate = rebateRate.multiply(new BigDecimal(0.01F));
		BigDecimal betAmount = new BigDecimal(order.getBetAmount());
		
		rebate = betAmount.multiply(rebateRate);
		
		postAmount = preAmount.add(rebate);
		
		Assert.assertTrue(rebate.compareTo(new BigDecimal(1)) == 0);
		
	}
	
}

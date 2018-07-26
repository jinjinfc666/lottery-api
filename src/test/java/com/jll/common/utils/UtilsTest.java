package com.jll.common.utils;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;


public class UtilsTest {

	@Test
	public void testValidUserName_tooShort() {
		String userName = "t";
		boolean isValid = Utils.validUserName(userName);
		Assert.assertFalse(isValid);
	}

	@Test
	public void testValidUserName_tooLong() {
		String userName = "test1111111111111111111111111111111111111111111111111111111111111111111111111111111111111test";
		boolean isValid = Utils.validUserName(userName);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void testValidUserName_illegalCharacter() {
		String userName = "test111+";
		boolean isValid = Utils.validUserName(userName);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void testValidUserName_pass() {
		String userName = "test11122_";
		boolean isValid = Utils.validUserName(userName);
		Assert.assertTrue(isValid);
	}
	
	@Test
	public void testValidPwd() {
		String pwd = "test11122_";
		boolean isValid = Utils.validPwd(pwd);
		Assert.assertTrue(isValid);
	}
	
	@Test
	public void testValidPwd_tooShort() {
		String pwd = "t2_";
		boolean isValid = Utils.validPwd(pwd);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void testValidPwd_tooLong() {
		String pwd = "t21111111111111111111111111111111111111111111111111111111_";
		boolean isValid = Utils.validPwd(pwd);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void testValidPwd_illegalCharacter() {
		String pwd = "t2222222_+";
		boolean isValid = Utils.validPwd(pwd);
		Assert.assertFalse(isValid);
	}

	@Test
	public void testValidEmail() {
		String pwd = "test@test.com";
		boolean isValid = Utils.validEmail(pwd);
		Assert.assertTrue(isValid);
	}

	@Test
	public void testValidEmail_noAtSign() {
		String pwd = "testtest.com";
		boolean isValid = Utils.validEmail(pwd);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void testValidEmail_noDot() {
		String pwd = "test@testcom";
		boolean isValid = Utils.validEmail(pwd);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void testValidEmail_() {
		String pwd = "@testcom";
		boolean isValid = Utils.validEmail(pwd);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void testValidPhone() {
		String phone = "15927228888";
		boolean isValid = Utils.validPhone(phone);
		Assert.assertTrue(isValid);
	}
	
	@Test
	public void testValidPhone_tooShort() {
		String phone = "111";
		boolean isValid = Utils.validPhone(phone);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void testValidPhone_tooLong() {
		String phone = "111111111111111";
		boolean isValid = Utils.validPhone(phone);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void testValidPhone_wrongNum() {
		String phone = "11111111111";
		boolean isValid = Utils.validPhone(phone);
		Assert.assertFalse(isValid);
	}

	@Test
	public void testValidRealName() {
		String realName = "张三";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertTrue(isValid);
	}

	@Test
	public void testValidRealName_dot() {
		String realName = "张三·丰";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertTrue(isValid);
	}
	
	@Test
	public void testValidRealName_tooShort() {
		String realName = "张";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void testValidRealName_tooLong() {
		String realName = "张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void testValidRealName_alphabet() {
		String realName = "张三丰A";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertFalse(isValid);
	}
}

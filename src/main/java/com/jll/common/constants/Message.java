package com.jll.common.constants;

public class Message {
	public final static String KEY_ERROR_CODE = "error_code";
	
	public final static String KEY_ERROR_MES = "error_mes";
	
	public final static String KEY_STATUS = "status";
	
	public static enum status{
		SUCCESS(1),
		FAILED(0);
		
		private int value;
		
		private status(int value) {
			this.value = value;
		}
		
		public int getCode() {
			return value;
		}
	}
	
	//code : **-->module name;****-->errorNum
	public static enum Error{
		/****common module****/
		ERROR_COMMON_ERROR_PARAMS("000001", "Error parameter!!"),
		ERROR_COMMON_NO_PERMISSION("000002", "No permission!!"),
		ERROR_COMMON_ERROR_LOGIN("000003", "No pricipal!!"),
		
		ERROR_COMMON_OTHERS("000003", "Others!!"),
		
		/******user module**************/
		ERROR_USER_EXISTING("010001", "The user is existing!!"),
		ERROR_USER_NO_GENERAL_AGENCY("010002", "No general agency!!"),
		ERROR_USER_NO_VALID_USER("010003", "No valid user!!"),
		ERROR_USER_INVALID_USER_NAME("010004", "Invalid user name!!"),
		ERROR_USER_INVALID_USER_LOGIN_PWD("010005", "Invalid user login pwd!!"),
		ERROR_USER_INVALID_USER_FUND_PWD("010006", "Invalid user fund pwd!!"),
		ERROR_USER_INVALID_EMAIL("010007", "Invalid Email!!"),
		ERROR_USER_INVALID_PHONE_NUMBER("010008", "Invalid Phone number!!"),
		ERROR_USER_INVALID_REAL_NAME("010009", "Invalid Real name!!"),
		ERROR_USER_INVALID_USER_TYPE("010010", "Invalid User type!!"),
		ERROR_USER_FAILED_SAVE("010011", "Failed save user!!"),
		ERROR_USER_FAILED_REGISTER("010012", "Failed register user!!"),
		
		ERROR_PAYMENT_TLCLOUD_FAILED_PUSH_ORDER("010002", "Failed to push order!!"),
		ERROR_PAYMENT_TLCLOUD_FAILED_CANCEL_ORDER("010003", "Failed to cancel order!!"),
		ERROR_PAYMENT_DEPOSIT_ERROR_ORDER("010004", "No order existing!!"),
		
		ERROR_PAYMENT_CAIPAY_FAILED_CANCEL_ORDER("010005", "configuration of cai-pay is wrong!!"),
		ERROR_PAYMENT_CAIPAY_FAILED_SIGNATURE_PARAMS("010006", "Can not sign the parameters!!"),
		
		
		ERROR_SYSTEM_CONFIG_NO_RECEIVER_BANK_CARD("020001", "No Receiver bank card is specified!!"),
		
		ERROR_SYSTEM_AUTH_NO_ACCESS_PERMISSION("030001", "No Permission to access resource!!")
		
		;
		
		
		
		private String code;
		
		private String errorMes;
		
		private Error(String code, String errorMes) {
			this.code = code;
			this.errorMes = errorMes;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public String getErrorMes() {
			return this.errorMes;
		}
		
		public static Error getErrorByCode(String code) {
			Error[] errors = Error.values();
			for(Error err: errors) {
				if(err.getCode().equals(code)) {
					return err;
				}
			}
			return null;
		}
	}
}

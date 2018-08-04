package com.jll.common.constants;

public class Message {
	public final static String KEY_ERROR_CODE = "error_code";
	
	public final static String KEY_ERROR_MES = "error_mes";
	
	public final static String KEY_STATUS = "status";
	
	public final static String KEY_DATA = "data";
	
<<<<<<< HEAD
	public final static String KEY_REMAKE = "remake";
=======
	public final static String KEY_EXPIRED_TIME = "expired_time";
	
	public final static String KEY_DEFAULT_PASSWORD = "default_password";
>>>>>>> e44d83bd405c6ca9b81ca264ceb4aa172cf042a6
	
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
		ERROR_COMMON_ERROR_PARAMS("000001", "Error parameter!!"),
		
		ERROR_PAYMENT_TLCLOUD_CONFIGURATION("010001", "configuration of tl-cloud is wrong!!"),
		ERROR_PAYMENT_TLCLOUD_FAILED_PUSH_ORDER("010002", "Failed to push order!!"),
		ERROR_PAYMENT_TLCLOUD_FAILED_CANCEL_ORDER("010003", "Failed to cancel order!!"),
		ERROR_PAYMENT_DEPOSIT_ERROR_ORDER("010004", "No order existing!!"),
		
		ERROR_PAYMENT_CAIPAY_FAILED_CANCEL_ORDER("010005", "configuration of cai-pay is wrong!!"),
		ERROR_PAYMENT_CAIPAY_FAILED_SIGNATURE_PARAMS("010006", "Can not sign the parameters!!"),
		ERROR_SYSTEM_CONFIG_NO_RECEIVER_BANK_CARD("020001", "No Receiver bank card is specified!!"),
		
		ERROR_SYSTEM_AUTH_NO_ACCESS_PERMISSION("030001", "No Permission to access resource!!"),
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
		ERROR_USER_INVALID_PLAT_REBATE("010011", "Invalid Platform rebate!!"),
		ERROR_USER_FAILED_SAVE("010012", "Failed save user!!"),
		ERROR_USER_FAILED_REGISTER("010013", "Failed register user!!"),
		
		ERROR_OLD_FUND_PWD_ERROR("010014", "Old fund password error!!"),
		ERROR_OLD_LOGIN_PWD_ERROR("010015", "Old login password error!!"),
		ERROR_MORE_UPDATE_REAL_NAME("010016", "The real name is binding and cannot be modified. If necessary, please contact customer service.!!"),
		ERROR_MORE_UPDATE_EMAIL("010017", "The email is binding and cannot be modified. If necessary, please contact customer service.!!"),
		ERROR_MORE_UPDATE_PHONE_NUM("010018", "The phone num is binding and cannot be modified. If necessary, please contact customer service.!!"),
		ERROR_BANK_CARD_HAS_BIND("010019", "The bank card  has been bound by other users. Please re-input the bank card.!!"),
		ERROR_USER_INVALID_BANK_CARD("010020", "Invalid bank card!!"),
		ERROR_USER_MORE_BIND_BANK_CARD("010021", "Users bind up to %d bank cards!!"),
<<<<<<< HEAD
		ERROR_MESSAGE_TITLE_IS_EMPTY("010022", "Message title is empty!!"),
		ERROR_MESSAGE_CONTENT_IS_EMPTY("010023", "Message content is empty!!"),
=======
		ERROR_USER_FAILED_RESET_LOGIN_PWD_SMS("010022", "Failed Reset login password by SMS!!"),
		ERROR_USER_FAILED_RESET_LOGIN_PWD_EMAIL("010022", "Failed Reset login password by Email!!"),
>>>>>>> e44d83bd405c6ca9b81ca264ceb4aa172cf042a6
		/**************third party*************************/
		ERROR_TP_INVALID_SMS("020001", "Invalid SMS!!"),
		ERROR_TP_INVALID_SMS_URL("020002", "Invalid SMS URL!!"),
		ERROR_TP_SENDING_EMAIL("020003", "Failed sending Email!!"),
		
		/************************game module*******************************/
		ERROR_GAME_LOTTERY_TYPE_INVALID("030001", "Invalid lottery type!!"),
		ERROR_GAME_END("030002", "The game is over today!!")
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

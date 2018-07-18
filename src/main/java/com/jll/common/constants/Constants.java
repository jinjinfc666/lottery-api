package com.jll.common.constants;

public class Constants {	
	public static enum DepositOrderState{
		INIT_OR_PUSHED(0),
		FAILED_PUSH(10),
		CANCEL_ORDER(11),
		END_ORDER(1);
		
		private int value;
		
		private DepositOrderState(int value) {
			this.value = value;
		}
		
		public int getCode() {
			return value;
		}
	}
	
	public static enum CAI_PAY_MODE{
		ALI_PAY_SCAN_QR("00021", "支付宝扫码"),
		WECHAT_SCAN_QR("00022", "微信扫码"),
		QQ_SCAN_QR("00032", "QQ扫码"),
		UNION_SCAN_QR("00045", "银联扫码"),
		JD_SCAN_QR("00027", "京东扫码"),
		JD_ONLINE_BANK("00023", "快捷"),
		ALIPAY_WAP_ONLINE_BANK("00024", "支付宝Wap"),
		WECHAT_H5_ONLINE_BANK("00025", "微信h5"),
		QQ_H5_ONLINE_BANK("00033", "QQh5"),
		JD_WAP_ONLINE_BANK("00046", "京东wap"),
		ALIPAY_H5_ONLINE_BANK("00026", "支付宝h5"),
		BANK_ACC_ONLINE_BANK("00020", "银行卡");
		
		private String code;
		
		private String des;
		
		private CAI_PAY_MODE(String code, String desc) {
			this.code = code;
			this.des = desc;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getDesc() {
			return this.des;
		}
		
		public static String getDescByCode(String code) {
			CAI_PAY_MODE[] modes = CAI_PAY_MODE.values();
			for(CAI_PAY_MODE mode : modes) {
				if(mode.getCode().equals(code)) {
					return mode.getDesc();
				}
			}
			
			return null;
		}
		
		public static String getCodeByDesc(String desc) {
			CAI_PAY_MODE[] modes = CAI_PAY_MODE.values();
			for(CAI_PAY_MODE mode : modes) {
				if(mode.getDesc().equals(desc)) {
					return mode.getCode();
				}
			}
			
			return null;
		}
	}
	
	public static enum ZHIH_PAY_MODE{
		ALI_PAY_SCAN_QR("alipay_scan", "支付宝扫码"),
		WECHAT_SCAN_QR("weixin_scan", "微信扫码"),
		QQ_SCAN_QR("tenpay_scan", "QQ扫码"),
		UNION_SCAN_QR("ylpay_scan", "银联扫码"),
		DIRECT_ONLINE_BANK("direct_pay", "快捷");
		
		private String code;
		
		private String des;
		
		private ZHIH_PAY_MODE(String code, String desc) {
			this.code = code;
			this.des = desc;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getDesc() {
			return this.des;
		}
		
		public static String getDescByCode(String code) {
			ZHIH_PAY_MODE[] modes = ZHIH_PAY_MODE.values();
			for(ZHIH_PAY_MODE mode : modes) {
				if(mode.getCode().equals(code)) {
					return mode.getDesc();
				}
			}
			
			return null;
		}
		
		public static String getCodeByDesc(String desc) {
			ZHIH_PAY_MODE[] modes = ZHIH_PAY_MODE.values();
			for(ZHIH_PAY_MODE mode : modes) {
				if(mode.getDesc().equals(desc)) {
					return mode.getCode();
				}
			}
			
			return null;
		}
	}
	
	public static enum THIRD_PART_DEPOSIT{
		CAI_PAY("0001", "彩付"),
		ZHIH_PAY("0002", "智慧付");
		
		private String code;
		
		private String des;
		
		private THIRD_PART_DEPOSIT(String code, String desc) {
			this.code = code;
			this.des = desc;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getDesc() {
			return this.des;
		}
		
		public static String getDescByCode(String code) {
			THIRD_PART_DEPOSIT[] modes = THIRD_PART_DEPOSIT.values();
			for(THIRD_PART_DEPOSIT mode : modes) {
				if(mode.getCode().equals(code)) {
					return mode.getDesc();
				}
			}
			
			return null;
		}
	}
}

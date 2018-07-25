package com.jll.common.constants;

import java.util.HashMap;
import java.util.Map;

import com.jll.common.constants.Message.Error;

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
	/**
	 *设置代码所需的状态
	 * @author Silence
	 */
	public static enum SysCodeState{
		VALID_STATE(1),
		INVALID_STATE(0);
		
		private int value;
		
		private SysCodeState(int value) {
			this.value = value;
		}
		
		public int getCode() {
			return value;
		}
	}
	/**
	 *SysCode大类类型
	 * @author Silence
	 */
	public static enum SysCodeTypes{
		LOTTERY_TYPES("caizhongleixing"),
		FLOW_TYPES("acc_ope_type");
		private String value;
		
		private SysCodeTypes(String value) {
			this.value = value;
		}
		
		public String getCode() {
			return value;
		}
	}
	/**
	 *彩票交易明细列表：查询条件：是否追号
	 * @author Silence
	 */
	public static enum IsZh{
		IS_ZH("1", "是"),
		NO_ZH("0", "否");
		private String code;
		
		private String names;
		
		private IsZh(String code, String names) {
			this.code = code;
			this.names = names;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public String getNames() {
			return this.names;
		}
		
		public static Map<String,Object> getIsZhByCode() {
			Map<String,Object> map=new HashMap<String,Object>();
			IsZh[] names = IsZh.values();
			for(IsZh name: names) {
				map.put(name.getCode(), name.getNames());
			}
			return map;
		}
	}
	/**
	 *彩票交易明细列表：查询条件：中奖情况
	 * @author Silence
	 */
	public static enum State{
		WAITING_FOR_PRIZE("0", "等待派奖"),
		HAS_WON("1", "已中奖"),
		NOT_WON("2", "未中奖"),
		USER_CANCEL_ORDER("3", "用户取消订单"),
		SYSTEM_CANCEL_ORDER("4", "系统取消订单");
		private String code;
		
		private String names;
		
		private State(String code, String names) {
			this.code = code;
			this.names = names;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public String getNames() {
			return this.names;
		}
		
		public static Map<String,Object> getStateByCode() {
			Map<String,Object> map=new HashMap<String,Object>();
			State[] names = State.values();
			for(State name: names) {
				map.put(name.getCode(), name.getNames());
			}
			return map;
		}
	}
	/**
	 *彩票交易明细列表：查询条件：订单来源
	 * @author Silence
	 */
	public static enum TerminalType{
		MOBILE_PHONE("1", "手机端"),
		PC("0", "PC端");
		private String code;
		
		private String names;
		
		private TerminalType(String code, String names) {
			this.code = code;
			this.names = names;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public String getNames() {
			return this.names;
		}
		
		public static Map<String,Object> getTerminalTypeByCode() {
			Map<String,Object> map=new HashMap<String,Object>();
			TerminalType[] names = TerminalType.values();
			for(TerminalType name: names) {
				map.put(name.getCode(), name.getNames());
			}
			return map;
		}
	}
}

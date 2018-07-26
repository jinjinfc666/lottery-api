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
	
	/**
	 *The state required to set the code
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
	
	public static enum WalletType{
		MAIN_WALLET(1, "主钱包"),
		RED_PACKET_WALLET(2, "红包钱包");
		
		private int code;
		
		private String desc;
		
		private WalletType(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static WalletType getWalletTypeByCode(int code) {
			WalletType[] walletTypes = WalletType.values();
			for(WalletType walletType: walletTypes) {
				if(walletType.getCode() == code) {
					return walletType;
				}
			}
			return null;
		}
	}
	
	public static enum EmailValidState{
		UNVERIFIED(0, "未验证"),
		VERIFIED(1, "已验证");
		
		private int code;
		
		private String desc;
		
		private EmailValidState(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static EmailValidState getStateByCode(int code) {
			EmailValidState[] states = EmailValidState.values();
			for(EmailValidState state: states) {
				if(state.getCode() == code) {
					return state;
				}
			}
			return null;
		}
	}
	
	public static enum PhoneValidState{
		UNVERIFIED(0, "未验证"),
		VERIFIED(1, "已验证");
		
		private int code;
		
		private String desc;
		
		private PhoneValidState(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static PhoneValidState getStateByCode(int code) {
			PhoneValidState[] states = PhoneValidState.values();
			for(PhoneValidState state: states) {
				if(state.getCode() == code) {
					return state;
				}
			}
			return null;
		}
	}
	
	public static enum UserState{
		NORMAL(0, "正常"),
		LOCKING(1, "锁定"),
		REVOKED(2, "销毁");
		
		private int code;
		
		private String desc;
		
		private UserState(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static UserState getStateByCode(int code) {
			UserState[] states = UserState.values();
			for(UserState state: states) {
				if(state.getCode() == code) {
					return state;
				}
			}
			return null;
		}
	}
	
	public static enum UserType{
		PLAYER(0, "玩家"),
		AGENCY(1, "代理"),
		SYS_ADMIN(2, "系统用户"),
		GENERAL_AGENCY(3, "总代");
		
		private int code;
		
		private String desc;
		
		private UserType(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static UserType getStateByCode(int code) {
			UserType[] states = UserType.values();
			for(UserType state: states) {
				if(state.getCode() == code) {
					return state;
				}
			}
			return null;
		}
	}
	
	public static enum UserLevel{
		LEVEL_0(0, "青铜"),
		LEVEL_1(1, "白银"),
		LEVEL_2(2, "黄金"),
		LEVEL_3(3, "铂金"),
		LEVEL_4(4, "钻石");
		
		private int code;
		
		private String desc;
		
		private UserLevel(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static UserLevel getStateByCode(int code) {
			UserLevel[] states = UserLevel.values();
			for(UserLevel state: states) {
				if(state.getCode() == code) {
					return state;
				}
			}
			return null;
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

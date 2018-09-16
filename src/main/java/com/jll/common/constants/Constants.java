
package com.jll.common.constants;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Constants {	
	public final static String KEY_PRE_PLAN = "plan_issues_";
	
	public final static String KEY_PRE_BULLETINBOARD = "bulletin_board";
	
	public final static String KEY_PLAY_TYPE = "play_type_";
	
	public final static String KEY_PAY_TYPE = "pay_type_";
	
	public final static String KEY_STAT_ISSUE_BETTING = "stat_issuse_betting_";

	public final static String TOPIC_WINNING_NUMBER = "winning_number";
	
	public final static String TOPIC_PAY_OUT = "pay_out";
	
	public final static String KEY_LOTTO_ATTRI_PREFIX = "lottery_config_";
	
	public final static Integer VAL_REBATE_PRIZE_RATE = 26;
	
	
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
	
	public static enum WithdrawOrderState{
		ORDER_INIT(0,"等待付款"),
		ORDER_END(1,"付款成功"),
		ORDER_APPLY_FAILED(2,"审核不通过"),
		ORDER_BANK_ERROR(3,"银行故障"),
		ORDER_USER_INFO_ERROR(4,"账户信息错误"),
		ORDER_OTHER_ERROR(5,"其它错误");
		
		private int code;
		private String value;
		private WithdrawOrderState(int code, String value) {
			this.code = code;
			this.value = value;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
		public static WithdrawOrderState getValueByCode(int code) {
			WithdrawOrderState[] walletTypes = WithdrawOrderState.values();
			for(WithdrawOrderState walletType: walletTypes) {
				if(walletType.getCode() == code) {
					return walletType;
				}
			}
			return null;
		}
		
	}
	
	public static enum WithdrawConif{
		
		MIN_WITHDRAWAL_AMT("min_withdrawal_amt","最低提款金额"),
		MAX_WITHDRAWAL_AMT("max_withdrawal_amt","最高提款金额"),
		DAY_COUNT("day_count","每日可提款次数"),
		RED_PACKET_WALLET_RATE("red_packet_wallet_rate","红包余额转主钱包的流水倍数");
		private String code;
		private String value;
		
		private WithdrawConif(String code, String value) {
			this.code = code;
			this.value = value;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
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
	
	public static enum WalletState{
		NORMAL(0, "正常"),
		FROZEN(1, "冻结");
		
		private int code;
		
		private String desc;
		
		private WalletState(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static WalletState geByCode(int code) {
			WalletState[] walStates = WalletState.values();
			for(WalletState walState: walStates) {
				if(walState.getCode() == code) {
					return walState;
				}
			}
			return null;
		}
	}
	
	public static enum PromoValueType{
		CASH(1, "现金"),
		POINT(2, "积分");
		
		private int code;
		
		private String desc;
		
		private PromoValueType(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static PromoValueType getValueByCode(int code) {
			PromoValueType[] walletTypes = PromoValueType.values();
			for(PromoValueType walletType: walletTypes) {
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
	
	public static enum PromoMultipleType{
		ONCE(0, "单次领取"),
		MANY(1, "多次领取");
		
		private int code;
		
		private String desc;
		
		private PromoMultipleType(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static PromoMultipleType getValueByCode(int code) {
			PromoMultipleType[] states = PromoMultipleType.values();
			for(PromoMultipleType state: states) {
				if(state.getCode() == code) {
					return state;
				}
			}
			return null;
		}
	}
	
	
	public static enum BankCardState{
		DISABLE(0, "无效"),
		ENABLED(1, "有效");
		
		private int code;
		
		private String desc;
		
		private BankCardState(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static BankCardState getStateByCode(int code) {
			BankCardState[] states = BankCardState.values();
			for(BankCardState state: states) {
				if(state.getCode() == code) {
					return state;
				}
			}
			return null;
		}
		public static Map<Integer,String> getIsOrNo() {
			Map<Integer,String> map=new HashMap<Integer,String>();
			BankCardState[] descs = BankCardState.values();
			for(BankCardState desc: descs) {
				map.put(desc.getCode(), desc.getDesc());
			}
			return map;
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
		LOTTERY_TYPES("lottery_type"),
		BANK_LIST("number_of_bank_cards"),
		BANK_CODE_LIST("bank_code_list"),
		FLOW_TYPES("flow_type"),
		PAYMENT_PLATFORM("payment_platform"),
		SITE_MSG_VALID_DAY("site_msg_valid_day"),
		NOTIFY_MSG_VALID_DAY("notify_msg_valid_day"),
		LUCKY_DRAW("lucky_draw"),//幸运抽奖
		LOTTERY_CONFIG_CQSSC("lottery_config_cqssc"),//,"重庆时时彩属性"
		LOTTERY_CONFIG_GD11X5("lottery_config_gd11x5"),//,"广东11选5属性"
		LOTTERY_CONFIG_TXFFC("lottery_config_txffc"),//,"腾讯分分彩属性"
		LOTTERY_CONFIG_5CC("lottery_config_5cc"),//,"5分彩属性"
		LOTTERY_CONFIG_SFC("lottery_config_sfc"),//,"双分彩属性"
		LOTTERY_CONFIG_FFC("lottery_config_ffc"),//,"分分彩属性"
		LOTTERY_CONFIG_MMC("lottery_config_mmc"),//"秒秒彩属性"
		LOTTERY_CONFIG_BJPK10("lottery_config_bjpk10"),//"PK10属性"
		SIGN_IN_DAY("sign_in_day"),
		POINT_EXCHANGE_SCALE("point_exchange_scale"),
		CT_PLAY_TYPE_CLASSICFICATION("ct_play_type_classicfication"),//"玩法类型"
		WITHDRAWAL_CFG("withdrawal_cfg"),
		PAY_TYPE("pay_type");//充值方式
		private String value;
		
		private SysCodeTypes(String value) {
			this.value = value;
		}
		
		public String getCode() {
			return value;
		}
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			SysCodeTypes[] names = SysCodeTypes.values();
			for(SysCodeTypes name: names) {
				String nameConfig=name.getCode();
				if(nameConfig.startsWith("lottery_config_")) {
					map.add(nameConfig);
				}
			}
			return map;
		}
	}
	
	/**
	 * notification type
	 */
	public static enum SiteMessageReadType{
		READING(0, "已阅读"),
		UN_READING(1, "未阅读");
		
		private int code;
		
		private String desc;
		
		private SiteMessageReadType(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static SiteMessageReadType getMessageReadType(int code) {
			SiteMessageReadType[] walletTypes = SiteMessageReadType.values();
			for(SiteMessageReadType walletType: walletTypes) {
				if(walletType.getCode() == code) {
					return walletType;
				}
			}
			return null;
		}
	}
	/**
	 *SysCode大类类型
	 * @author Silence
	 */
	public static enum SysCodeTypesFlag{
		code_type(1),
		code_val(0);
		
		private int value;
		
		private SysCodeTypesFlag(int value) {
			this.value = value;
		}
		
		public int getCode() {
			return value;
		}
	}
	
	/**
	 * notification type
	 */
	public static enum SysNotifyType{
		ALL_USER(0, "全部用户"),
		ALL_AGENT(1, "全部代理用户"),
		ALL_COM_USER(2, "全部普通用户");
		
		private int code;
		
		private String desc;
		
		private SysNotifyType(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static SysNotifyType getSysNotifyTypeByCode(int code) {
			SysNotifyType[] walletTypes = SysNotifyType.values();
			for(SysNotifyType walletType: walletTypes) {
				if(walletType.getCode() == code) {
					return walletType;
				}
			}
			return null;
		}
	}
	
	/**
	 * notification receiver type
	 */
	public static enum SysNotifyReceiverType{
		LEVEL(0, "按层级关系"),
		TYPE(1, "按类型");
		
		private int code;
		
		private String desc;
		
		private SysNotifyReceiverType(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static SysNotifyReceiverType getSysNotifyReceiverTypeByCode(int code) {
			SysNotifyReceiverType[] walletTypes = SysNotifyReceiverType.values();
			for(SysNotifyReceiverType walletType: walletTypes) {
				if(walletType.getCode() == code) {
					return walletType;
				}
			}
			return null;
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

	/**
	 *存取款明细：查询条件：取款状态
	 * @author Silence
	 */
	public static enum WithdrawType{
		WAIT("0", "等待付款"),
		SUCESS("1", "付款成功"),
		AUDIT_NOT_PASSED("2", "审核不通过"),
		BANK_FAILURE("3", "银行故障"),
		ACCOUNT_INFORMATION_ERROR("4", "账户信息错误"),
		OTHER("5", "others");
		private String code;
		
		private String names;
		
		private WithdrawType(String code, String names) {
			this.code = code;
			this.names = names;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public String getNames() {
			return this.names;
		}
		
		public static Map<String,Object> getWithdrawTypeByCode() {
			Map<String,Object> map=new HashMap<String,Object>();
			WithdrawType[] names = WithdrawType.values();
			for(WithdrawType name: names) {
				map.put(name.getCode(), name.getNames());
			}
			return map;
		}
	}
	/**
	 *存取款明细：查询条件：存款状态
	 * @author Silence
	 */
	public static enum DepositType{
		WAIT("0", "等待充值"),
		SUCESS("1", "充值成功"),
		AUDIT_NOT_PASSED("2", "充值失败");
		private String code;
		
		private String names;
		
		private DepositType(String code, String names) {
			this.code = code;
			this.names = names;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public String getNames() {
			return this.names;
		}
		
		public static Map<String,Object> getDepositTypeByCode() {
			Map<String,Object> map=new HashMap<String,Object>();
			DepositType[] names = DepositType.values();
			for(DepositType name: names) {
				map.put(name.getCode(), name.getNames());
			}
			return map;
		}
	}
	/**
	 *存取款明细：类别
	 * @author Silence
	 */
	public static enum DWType{
		WAIT("1", "存款"),
		SUCESS("2", "取款");
		private String code;
		
		private String names;
		
		private DWType(String code, String names) {
			this.code = code;
			this.names = names;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public String getNames() {
			return this.names;
		}
		
		public static Map<String,Object> getDWTypeByCode() {
			Map<String,Object> map=new HashMap<String,Object>();
			DWType[] names = DWType.values();
			for(DWType name: names) {
				map.put(name.getCode(), name.getNames());
			}
			return map;
		}
	}
	
	/**
	 *存取款明细：类别
	 * @author Silence
	 */
	public static enum IssueState{
		INIT(0, "初始状态"),
		BETTING(1, "投注状态"),
		END_BETTING(2, "结束投注"),
		END_ISSUE(3, "期次结束"),
		LOTTO_DARW(4, "已开奖"),
		PAYOUT(5, "已派奖"),
		RE_PAYOUT(6, "重新派奖"),
		DISABLE(7, "期次作废");
		
		private int code;
		
		private String names;
		
		private IssueState(int code, String names) {
			this.code = code;
			this.names = names;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getNames() {
			return this.names;
		}
		
		public static IssueState getStateByCode(int code) {
			IssueState[] names = IssueState.values();
			for(IssueState name: names) {
				if(name.getCode() == code) {
					return name;
				}
			}
			
			return null;
		}
	}
	/**
	 *用户类型
	 * @author Silence
	 */
	public static enum UserTypes{
		PLATFORM_USER(0,"平台客户"),
		PROXY(1,"代理"),
		SYSTEM_USER(2,"系统用户"),
		GENERAL_AGENT(3,"总代");
		
		private Integer code;
		
		private String names;
		
		private UserTypes(Integer code, String names) {
			this.code = code;
			this.names = names;
		}
		
		public Integer getCode() {
			return this.code;
		}
		
		public String getNames() {
			return this.names;
		}
	}
	
	public static enum CreditRecordType{
		INITIATE_PURCHASING("initiate_purchasing","发起代购"),
		SYSTEM_AWARD("system_award","系统派奖"),
		SYSTEM_REBATE("rebate","系统返点"),
		SYSTEM_WITHDRAWAL("system_withdrawal","系统撤单"),
		USER_DEPOSIT("deposit","用户存款"),
		SYS_ADD("sys_add","系统加钱"),
		SYS_DEDUCTION("sys_deduction","系统扣除"),
		THIRD_RECHARGE("third_recharge","三方补单充值"),
		USER_WITHDRAWAL("withdrawal","用户提现"),
		RECHARGE_DEDUCTION("recharge_deduction","充值扣除"),
		ACTIVITY_GIFT_RED("activity_gift_red","活动红包礼金"),
		CUSTOMER_CLAIMS("customer_claims","客户理赔"),
		PLATFORM_REWARD("platform_reward","平台奖励"),
		RECHARGE_GIFT("recharge_gift","充值赠送"),
		REGISTRATION_GIFT("registration_gift","注册礼金"),
		USER_RED_ENVELOPE_WITHDRAWAL_DEDUCTION("user_red_envelope_withdrawal_deduction","用户红包提现扣除"),
		USER_RED_BAG_WITHDRAWAL("user_red_bag_withdrawal","用户红包提现"),
		GOLD_COINS_AGAINST_THE_RENMINBI("gold_coins_against_the_renminbi","金币兑人民币"),
		RMB_AGAINST_GOLD_COINS("RMB_against_gold_coins","人民币兑金币"),
		BANK_FEES("bank_fees","银行手续费"),
		TRANSFER_OF_FUNDS("transfer","资金转移"),
		ACTIVITY_GIFT_CASH("promo_cash","活动现金礼金"),
		ACTIVITY_GIFT_POINT("promo_points","活动积分礼金"),
		ACC_UNFREEZE("acc_unfreeze","账户资金解冻"),
		ACC_FREEZE("acc_freeze","账户资金冻结"),
		WITHDRAWAL_BACK("withdrawal_back","提款退还"),
		POINT_EXCHANGE("point_exchange","积分兑换"),
		RECOVERY_PAYOUT("recovery_payout","派奖回收"),
		ISSUE_DISBALE("issue_disbale","期次作废"),
		CANCEL_REBATE("cancel_rebate","撤单扣除返点"),;

		
		private String code;
		
		private String desc;
		
		private CreditRecordType(String code,String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
		
		public static CreditRecordType getValueByCode(String code) {
			CreditRecordType[] names = CreditRecordType.values();
			for(CreditRecordType name: names) {
				if(name.getCode().equals(code)) {
					return name;
				}
			}
			return null;
		}
	}	
	/**
	 *报表统计 需要的类型：扣除
	 * @author Silence
	 */
	public static enum Deduction{
		RECHARGE_DEDUCTION("recharge_deduction","充值扣除"),//充值扣除
		SYS_DEDUCTION("sys_deduction","系统扣除");//系统扣除
		
		private String code;
		private String name;
		
		private Deduction(String code,String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getCode() {
			return this.code;
		}
		public String getName() {
			return this.name;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			Deduction[] names = Deduction.values();
			for(Deduction name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<String,Object> getMap() {
			Map<String,Object> map=new HashMap<String,Object>();
			Deduction[] names = Deduction.values();
			for(Deduction name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
	}
	/**
	 *报表统计 需要的类型：用户存款
	 * @author Silence
	 */
	public static enum UserDeposit{
		USER_DEPOSIT("user_deposit","用户存款");
		
		private String code;
		private String name;
		
		private UserDeposit(String code,String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getCode() {
			return this.code;
		}
		public String getName() {
			return this.name;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			UserDeposit[] names = UserDeposit.values();
			for(UserDeposit name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<String,Object> getMap() {
			Map<String,Object> map=new HashMap<String,Object>();
			UserDeposit[] names = UserDeposit.values();
			for(UserDeposit name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
	}
	/**
	 *报表统计 需要的类型：系统充值
	 * @author Silence
	 */
	public static enum SystemRecharge{
		SYSTEM_RECHARGE("system_recharge","系统充值"),
		AGENT_RECHARGE("agent_recharge","代理充值");
		
		
		private String code;
		private String name;
		
		private SystemRecharge(String code,String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getCode() {
			return this.code;
		}
		public String getName() {
			return this.name;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			SystemRecharge[] names = SystemRecharge.values();
			for(SystemRecharge name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<String,Object> getMap() {
			Map<String,Object> map=new HashMap<String,Object>();
			SystemRecharge[] names = SystemRecharge.values();
			for(SystemRecharge name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
	}
	/**
	 *报表统计 需要的类型：用户取款
	 * @author Silence
	 */
	public static enum UserWithdrawal{
		USER_WITHDRAWAL("user_withdrawal","用户取款");
		
		private String code;
		private String name;
		
		private UserWithdrawal(String code,String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getCode() {
			return this.code;
		}
		public String getName() {
			return this.name;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			UserWithdrawal[] names = UserWithdrawal.values();
			for(UserWithdrawal name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<String,Object> getMap() {
			Map<String,Object> map=new HashMap<String,Object>();
			UserWithdrawal[] names = UserWithdrawal.values();
			for(UserWithdrawal name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
	}
	/**
	 *报表统计 需要的类型：消费
	 * @author Silence
	 */
	public static enum Consumption{
		INITIATE_PURCHASING("initiate_purchasing","发起代购");
		
		private String code;
		private String name;
		
		private Consumption(String code,String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getCode() {
			return this.code;
		}
		public String getName() {
			return this.name;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			Consumption[] names = Consumption.values();
			for(Consumption name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<String,Object> getMap() {
			Map<String,Object> map=new HashMap<String,Object>();
			Consumption[] names = Consumption.values();
			for(Consumption name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
	}
	/**
	 *报表统计 需要的类型：撤单
	 * @author Silence
	 */
	public static enum Withdrawal{
		SYSTEM_WITHDRAWAL("system_withdrawal","系统撤单");
		
		private String code;
		private String name;
		
		private Withdrawal(String code,String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getCode() {
			return this.code;
		}
		public String getName() {
			return this.name;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			Withdrawal[] names = Withdrawal.values();
			for(Withdrawal name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<String,Object> getMap() {
			Map<String,Object> map=new HashMap<String,Object>();
			Withdrawal[] names = Withdrawal.values();
			for(Withdrawal name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
	}
	/**
	 *报表统计 需要的类型：返奖
	 * @author Silence
	 */
	public static enum ReturnPrize{
		SYSTEM_AWARD("system_award","系统派奖");
		
		private String code;
		private String name;
		
		private ReturnPrize(String code,String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getCode() {
			return this.code;
		}
		public String getName() {
			return this.name;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			ReturnPrize[] names = ReturnPrize.values();
			for(ReturnPrize name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<String,Object> getMap() {
			Map<String,Object> map=new HashMap<String,Object>();
			ReturnPrize[] names = ReturnPrize.values();
			for(ReturnPrize name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
	}
	/**
	 *报表统计 需要的类型：返点
	 * @author Silence
	 */
	public static enum Rebate{
		SYSTEM_REBATE("system_rebate","系统返点");
		
		private String code;
		private String name;
		
		private Rebate(String code,String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getCode() {
			return this.code;
		}
		public String getName() {
			return this.name;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			Rebate[] names = Rebate.values();
			for(Rebate name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<String,Object> getMap() {
			Map<String,Object> map=new HashMap<String,Object>();
			Rebate[] names = Rebate.values();
			for(Rebate name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
	}
	
	public static enum ZhState{
		ZH(0, "追号"),
		NON_ZH(1, "非追号");
		
		private int code;
		private String desc;
		
		private ZhState(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static ZhState getByCode(int code) {
			ZhState[] zhStates = ZhState.values();
			for(ZhState zh : zhStates) {
				if(zh.getCode() == code) {
					return zh;
				}
			}
			
			return null;
		}
	}	
	
	/**
	 *彩种的玩法类型
	 * @author Silence
	 */
	public static enum SysCodePlayType{
		CT_PLAY_TYPE_CLASSICFICATION("ct_play_type_classicfication","玩法类型");
		
		private String code;
		private String name;
		
		private SysCodePlayType(String code,String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getCode() {
			return this.code;
		}
		public String getName() {
			return this.name;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			SysCodePlayType[] names = SysCodePlayType.values();
			for(SysCodePlayType name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<String,Object> getMap() {
			Map<String,Object> map=new HashMap<String,Object>();
			SysCodePlayType[] names = SysCodePlayType.values();
			for(SysCodePlayType name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
	}
	/**
	 *玩法是单式还是复式
	 * @author Silence
	 */
	public static enum SingleOrDouble{
		SINGLE("0","单式"),
		Double("1","复式");
		
		private String code;
		private String name;
		
		private SingleOrDouble(String code,String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getCode() {
			return this.code;
		}
		public String getName() {
			return this.name;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			SingleOrDouble[] names = SingleOrDouble.values();
			for(SingleOrDouble name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<String,Object> getMap() {
			Map<String,Object> map=new HashMap<String,Object>();
			SingleOrDouble[] names = SingleOrDouble.values();
			for(SingleOrDouble name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
	}
	/**
	 *玩法名称是否隐藏
	 * @author Silence
	 */
	public static enum HidePlayName{
		SHOW("0","显示"),
		HIDE("1","隐藏");
		
		private String code;
		private String name;
		
		private HidePlayName(String code,String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getCode() {
			return this.code;
		}
		public String getName() {
			return this.name;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			HidePlayName[] names = HidePlayName.values();
			for(HidePlayName name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<String,Object> getMap() {
			Map<String,Object> map=new HashMap<String,Object>();
			HidePlayName[] names = HidePlayName.values();
			for(HidePlayName name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
	}
	
	/**
	 *账户流水 数据类型
	 */
	public static enum DataItemType{
		BALANCE(0, "balance"),
		PRIZE(1, "prize"),
		REWARD_POINTS(2, "reward_points"),
		FREEZE(3, "freeze");
		
		private String desc;
		private int code;
		
		private DataItemType(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return desc;
		}
	}
	
	/**
	 *账户流水 数据类型
	 */
	public static enum AccOperationType{
		DEPOSIT("deposit"),
		WITHDRAW("withdraw"),
		WD_FREEZE("wd_freeze"),
		WD_UNFREEZE("wd_unfreeze"),
		BETTING("betting"),
		TRANSFER("transfer"),
		PAYOUT("payout"),
		REBATE("rebate"),
		REFUND("refund"),
		CANCEL_REBATE("cancel_rebate"),
		CUSTOMER_CLAIMS("customer_claims"),
		PLAT_REWARD("plat_reward"),
		DEPOSIT_CASH("deposit_cash"),
		REG_CASH("reg_cash"),
		BANK_FEES("bank_fees"),
		PROMO_CASH("promo_cash"),
		PROMO_POINTS("promo_points"),
		ACC_FREEZE("acc_freeze"),
		ACC_UNFREEZE("acc_unfreeze"),
		SYS_DEDUCTION("sys_deduction"),
		SYS_ADD("sys_add"),
		THIRD_RECHARGE("third_recharge"),
		DAILY_SIGN_IN("daily_sign_in"),
		POINTS_EXCHANGE("points_exchange");
		
		private String desc;
		
		private AccOperationType(String desc) {
			this.desc = desc;
		}
		
		
		public String getDesc() {
			return desc;
		}
	}
	/**
	 *彩种的属性
	 * @author Silence
	 */
	public static enum LotteryAttributes{
		MAX_PRIZE_AMOUNT("max_prize_amount"),//,"单个订单最大中奖金额"
		PRIZE_MODE("prize_mode"),//,"开奖模式"
		DT_SETTING("dt_setting"),//,"单挑设置"
		WINING_RATE("wining_rate"),//中奖率
		BETTING_END_TIME("betting_end_time"),//投注截止时间
		URL_WINING_NUMBER_EXTENAL("url_wining_number_extenal"),//外部数据接口
		LOTTO_PRIZE_RATE("lotto_prize_rate"),//中奖赔率
		BET_TIMES("bet_times"),
		MONEY_UNIT("money_unit");
		
		private String code;
		
		private LotteryAttributes(String code) {
			this.code = code;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			LotteryAttributes[] names = LotteryAttributes.values();
			for(LotteryAttributes name: names) {
				map.add(name.getCode());
			}
			return map;
		}
	}
	
	/**
	 *账户流水 数据类型
	 */
	public static enum OrderState{
		WAITTING_PAYOUT(0, "等待派奖"),
		WINNING(1, "赢"),
		LOSTING(2, "输"),
		USER_CANCEL(3, "用户取消订单"),
		SYS_CANCEL(4, "系统取消订单"),
		RE_PAYOUT(5, "重新派奖"),
		DISABLE(6, "订单作废");
		
		private String desc;
		
		private int code;
		
		private OrderState(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		
		public String getDesc() {
			return desc;
		}
		
		public int getCode() {
			return this.code;
		}
	}
	public static enum PayTypeState{
		VALID_STATE(1,"有效"),
		INVALID_STATE(0,"无效");
		
		private int code;
		
		private String desc;

		private PayTypeState(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
		
	}
	
	//系统内部使用的充值渠道
	public static enum PayChannelType{
		ADMIN_SEND_USER("admin_send_user", "管理员的充值申请"),
		AGENT_SEND_USER("agent_send_user", "代理给用户充值"),
		USER_TRANS("user_trans", "会员转账");

		private PayChannelType(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		private String code;
		
		private String desc;
		
		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
		
		
		public static PayChannelType getValueByCode(String code) {
			PayChannelType[] valuse = PayChannelType.values();
			for(PayChannelType val: valuse) {
				if( val.getCode().equals(code)){
					return val;
				}
			}
			return null;
		}
		
	
	}
	
	   //支付方式
		public static enum PayType{
			SELF_PAY("self_pay"),
			WISDOM_PAYMENT("wisdom_payment"),//智慧付
			CAI_PAY("cai_pay"),
			TLY_PAY("tly_pay");
			
			private String code;

			private PayType(String code) {
				this.code = code;
			}

			public String getCode() {
				return code;
			}

			public void setCode(String code) {
				this.code = code;
			}
		}
		
	/**
	 * ip 缓存codeName
	 */
	public static enum IpBlackList{
		IP_BLACK_LIST("ip_black_list");
		private String code;
		
		private IpBlackList(String code) {
			this.code = code;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			IpBlackList[] names = IpBlackList.values();
			for(IpBlackList name: names) {
				map.add(name.getCode());
			}
			return map;
		}
	}
	
	/**
	 * 订单延迟状态
	 */
	public static enum OrderDelayState{
		DEPLAY(0, "delay"),
		NON_DEPLAY(1, "non_deplay");
		
		private int code;
		private String desc;
		
		private OrderDelayState(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public String getDesc() {
			return this.desc;
		}
		
		public static OrderDelayState getStateByCode(int code) {
			OrderDelayState[] states = OrderDelayState.values();
			for(OrderDelayState state : states) {
				if(state.getCode() == code) {
					return state;
				}
			}
			
			return null;
		}
	}
	
	/**
	 * payType配置时需要的属性
	 */
	public static enum PayTypeIs{
		IS(0,"非第三方平台"),
		NO(1,"第三方平台");
		
		private Integer code;
		private String name;
		
		private PayTypeIs(Integer code,String name) {
			this.code = code;
			this.name = name;
		}
		
		public Integer getCode() {
			return this.code;
		}
		public String getName() {
			return this.name;
		}
		
		public static List<Integer> getList() {
			List<Integer> map=new ArrayList<Integer>();
			PayTypeIs[] names = PayTypeIs.values();
			for(PayTypeIs name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<Integer,String> getMap() {
			Map<Integer,String> map=new HashMap<Integer,String>();
			PayTypeIs[] names = PayTypeIs.values();
			for(PayTypeIs name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
	}
	
	/**
	 * payType配置时需要的属性
	 */
	public static enum PayTypes{
		qszx("cqssc","qszx", "前三直选"),		
		zszx("cqssc","zszx", "中三直选"),
		hszx("cqssc","hszx", "后三直选"),
		qszux_zsfs("cqssc","qszux/zsfs", "前三组选/组三复式"),
		qszux_zlfs("cqssc","qszux/zlfs", "前三组选/组六复式"),
		qszux_hhzxds("cqssc","qszux/hhzxds", "前三组选/混合组选(单式)"),
		zszux_zsfs("cqssc","qszux/hhzxds", "中三组选/组三复式"),
		zszux_zlfs("cqssc","zszux/zlfs", "中三组选/组六复式"),
		zszux_hhzxds("cqssc","zszux/hhzxds", "中三组选/混合组选(单式)"),
		hszux_zsfs("cqssc","hszux/zsfs", "后三组选/组三复式"),
		hszux_zlfs("cqssc","hszux/zsfs", "后三组选/组六复式"),
		hszux_hhzxds("cqssc","hszux/hhzxds", "后三组选/混合组选(单式)"),
		wxq2("cqssc","wxq2", "五星前二"),
		wxq2_zxfs("cqssc","wxq2/zxfs", "五星前二/组选(复式)"),
		bdw_qsbdwfs("cqssc","bdw/qsbdwfs", "不定位/前三不定位(复式)"),
		bdw_zsbdwfs("cqssc","bdw/zsbdwfs", "不定位/中三不定位(复式)"),
		bdw_hsbdwfs("cqssc","bdw/hsbdwfs", "不定位/后三不定位(复式)"),
		dwd("cqssc","dwd", "定位胆(复式)"),;
		
		private String lottoType;
		private String name;
		private String remark;
		
		private PayTypes(String lottoType, String name, String remark) {
			this.lottoType = lottoType;
			this.name = name;
			this.remark = remark;
		}
		
		public String getLottoType() {
			return this.lottoType;
		}
	}
	/**
	 * payType配置缓存时需要的key名
	 */
	public static enum PayTypeName{
		PAY_TYPE_CLASS("pay_type_class");
		
		private String code;
		
		private PayTypeName(String code) {
			this.code = code;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			PayTypeName[] names = PayTypeName.values();
			for(PayTypeName name: names) {
				map.add(name.getCode());
			}
			return map;
		}
	}
	/**
	 * PayChannnel添加时需要的enable_max_amount字段值
	 */
	public static enum PayChannnelEMA{
		IS(0,"不激活"),
		NO(1,"激活");
		
		private Integer code;
		private String name;
		
		private PayChannnelEMA(Integer code,String name) {
			this.code = code;
			this.name = name;
		}
		
		public Integer getCode() {
			return this.code;
		}
		public String getName() {
			return this.name;
		}
					
		public static List<Integer> getList() {
			List<Integer> map=new ArrayList<Integer>();
			PayChannnelEMA[] names = PayChannnelEMA.values();
			for(PayChannnelEMA name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<Integer,String> getMap() {
			Map<Integer,String> map=new HashMap<Integer,String>();
			PayChannnelEMA[] names = PayChannnelEMA.values();
			for(PayChannnelEMA name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
	}
	/**
	 * payChannel配置缓存时需要的key名
	 */
	public static enum PayChannel{
		PAY_CHANNEL("pay_channel");
		
		private String code;
		
		private PayChannel(String code) {
			this.code = code;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			PayChannel[] names = PayChannel.values();
			for(PayChannel name: names) {
				map.add(name.getCode());
			}
			return map;
		}
	}
	/**
	 * 分页需要的数据：每页显示多少条数据
	 */
	public static enum Pagination{
		SUM_NUMBER(10);
		
		private Integer code;
		
		private Pagination(Integer code) {
			this.code = code;
		}
		
		public Integer getCode() {
			return this.code;
		}
	}
}
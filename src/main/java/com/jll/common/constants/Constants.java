
package com.jll.common.constants;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jll.common.utils.StringUtils;



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
	
	public final static String KEY_ISSUE_TOTAL_BETTING_AMOUNT = "total";
	
	public final static String KEY_LOTTO_TYPE_PROFIT_LOSS = "profit_loss";
	
	public final static String KEY_LOTTO_TYPE_PLAT_STAT = "plat_stat_";
	
	public final static String MMC_ISSUE_COUNT = "mmc_issue_count_";
	
	public final static String EMAIL = "email_";
	
	public final static String MMC_SERVICE_IMPL = "com.jll.game.lotterytypeservice.MmcServiceImpl";
	
	public final static String KEY_FACADE_PATTERN = "facade_pattern";
	
	public final static String KEY_FACADE_BET_NUM = "bet_num";
	
	public final static String KEY_FACADE_BET_NUM_SAMPLE = "bet_num_sample";
	
	public final static String KEY_CURR_ISSUE = "curr_issue";
	
	public final static String KEY_IS_CHANGED = "is_changed";
	
	public final static String KEY_WINNING_BET_TOTAL = "win_bet_total";
	
	public final static String KEY_WIN_AMOUNT = "win_amount";
	
	public final static String KEY_SINGLE_BETTING_PRIZE = "single_betting_prize";
	
	public final static String KEY_CLIENT_ID_ADMIN = "lottery-admin";
	
	public final static String KEY_CLIENT_ID_CLIENT = "lottery-client";
	
	public final static String KEY_LOCK_BETTING = "lock_betting_{userId}_{issue}";
	
	public final static String KEY_LOCK_STATISTICAL = "lock_betting_statistical";
	
	public final static String KEY_LOCK_CACHE_STATISTIC = "lock_cache_statistic";
	
	public final static String KEY_LOCK_MAKING_PLAN = "lock_making_plan";
	
	public final static String KEY_LOCK_SCHEDULE_ISSUE = "lock_schedule_issue";
	
	public final static String KEY_LOCK_WINNING_NUMBER = "lock_winning_number_{lottoType}_{issue}";
	
	public final static String KEY_LOCK_PAY_OUT = "lock_pay_out_{lottoType}_{issue}";
	
	public final static Integer LOCK_BETTING_EXPIRED = 5;
	
	public final static Integer LOCK_CACHE_STATISTIC_EXPIRED = 300;
	
	public final static Integer LOCK_MAKING_PLAN_EXPIRED = 30;
	
	public final static Integer LOCK_SCHEDULE_ISSUE_EXPIRED = 30;
	
	public final static Integer LOCK_WINNING_NUMBER_EXPIRED = 30;
	
	public final static Integer LOCK_PAY_OUT_EXPIRED = 30;
	
	public final static Integer LOCK_STATISTIC_EXPIRED = 60;
	//再给总代添加下级代理时需要填写的superior
	public final static Integer VAL_SUPERIOR = 0;
	
	
	
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
		GENERAL_AGENCY(3, "总代"),
		DEMO_PLAYER(4, "试玩玩家");;
		
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
	 */
	public static enum SysCodeTypes{
		LOTTERY_TYPES("lottery_type"),
		BANK_CODE_LIST("bank_code_list"),
		FLOW_TYPES("flow_type"),
		PAYMENT_PLATFORM("payment_platform"),
		LUCKY_DRAW("lucky_draw"),//幸运抽奖
		LOTTERY_CONFIG_CQSSC("lottery_config_cqssc"),//,"重庆时时彩属性"
		LOTTERY_CONFIG_GD11X5("lottery_config_gd11x5"),//,"广东11选5属性"
		LOTTERY_CONFIG_TXFFC("lottery_config_txffc"),//,"腾讯分分彩属性"
		LOTTERY_CONFIG_5FC("lottery_config_5fc"),//,"5分彩属性"
		LOTTERY_CONFIG_SFC("lottery_config_sfc"),//,"双分彩属性"
		LOTTERY_CONFIG_FFC("lottery_config_ffc"),//,"分分彩属性"
		LOTTERY_CONFIG_MMC("lottery_config_mmc"),//"秒秒彩属性"
		LOTTERY_CONFIG_BJPK10("lottery_config_bjpk10"),//"PK10属性"
		SIGN_IN_DAY("sign_in_day"),
		CT_PLAY_TYPE_CLASSICFICATION("ct_play_type_classicfication"),//"玩法类型"
		WITHDRAWAL_CFG("withdrawal_cfg"),
		DEMO_USER_CFG("demo_user_cfg"),//试玩用户属性
		PAY_TYPE("pay_type"),//充值方式
		SYS_RUNTIME_ARGUMENT("sys_runtime_argument");
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
		
		public static SysCodeTypes getLottoAttriFromLottoTye(String lottoType) {
			if(StringUtils.isBlank(lottoType)) {
				return null;
			}
			String lottoAttriName = Constants.KEY_LOTTO_ATTRI_PREFIX + lottoType;
			SysCodeTypes[] sysCodeTypes = SysCodeTypes.values();
			for(SysCodeTypes sysCodeType : sysCodeTypes) {
				if(sysCodeType.getCode().equals(lottoAttriName)) {
					return sysCodeType;
				}
			}
			
			return null;
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
	 */
	public static enum ZhState{
		NON_ZH(0, "非追号"),
		ZH(1, "追号");
		
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
		public static Map<Integer,String> getIsZhByCode() {
			Map<Integer,String> map=new HashMap<Integer,String>();
			ZhState[] names = ZhState.values();
			for(ZhState name: names) {
				map.put(name.getCode(), name.getDesc());
			}
			return map;
		}
	}	
//	public static enum IsZh{
//		IS_ZH("0", "是"),
//		NO_ZH("1", "否");
//		private String code;
//		
//		private String names;
//		
//		private IsZh(String code, String names) {
//			this.code = code;
//			this.names = names;
//		}
//		
//		public String getCode() {
//			return this.code;
//		}
//		
//		public String getNames() {
//			return this.names;
//		}
//		
//		public static Map<String,Object> getIsZhByCode() {
//			Map<String,Object> map=new HashMap<String,Object>();
//			IsZh[] names = IsZh.values();
//			for(IsZh name: names) {
//				map.put(name.getCode(), name.getNames());
//			}
//			return map;
//		}
//	}
	/**
	 *彩票交易明细列表：查询条件：中奖情况
	 */
	public static enum State{
		WAITING_FOR_PRIZE("0", "等待派奖"),
		HAS_WON("1", "已中奖"),
		NOT_WON("2", "未中奖"),
		USER_CANCEL_ORDER("3", "用户取消订单"),
		SYSTEM_CANCEL_ORDER("4", "系统取消订单"),
		WAITING_TO_RE_SEND_PRIZE("5", "等待重新派奖");
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
		public static Map<Integer,String> getMap() {
			Map<Integer,String> map=new HashMap<Integer,String>();
			IssueState[] names = IssueState.values();
			for(IssueState name: names) {
				map.put(name.getCode(), name.getNames());
			}
			return map;
		}
	}
	/**
	 *用户类型
	 */
//	public static enum UserTypes{
//		PLATFORM_USER(0,"平台客户"),
//		PROXY(1,"代理"),
//		SYSTEM_USER(2,"系统用户"),
//		GENERAL_AGENT(3,"总代");
//		
//		private Integer code;
//		
//		private String names;
//		
//		private UserTypes(Integer code, String names) {
//			this.code = code;
//			this.names = names;
//		}
//		
//		public Integer getCode() {
//			return this.code;
//		}
//		
//		public String getNames() {
//			return this.names;
//		}
//	}

	/**
	 *报表统计 需要的类型：扣除
	 */
	public static enum Deduction{
		SYS_DEDUCTION("sys_deduction","系统扣款");//系统扣款
		
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
	 */
	public static enum UserDeposit{
		DEPOSIT("deposit","用户存款");
		
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
	 *报表统计 需要的类型：用户取款
	 */
	public static enum UserWithdrawal{
		WITHDRAW("withdraw","用户取款");
		
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
	 */
	public static enum Consumption{
		BETTING("betting","投注");
		
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
	 */
	public static enum Withdrawal{
		REFUND("refund","撤单返回本金");
		
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
	 */
	public static enum ReturnPrize{
		PAYOUT("payout","系统派奖");
		
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
	 */
	public static enum Rebate{
		REBATE("rebate","返点");
		
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

	
	/**
	 *彩种的玩法类型
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
		DEPOSIT("deposit","充值",1),
		WITHDRAW("withdraw","提款",-1),
		WD_FREEZE("wd_freeze","提款冻结",-1),
		WD_UNFREEZE("wd_unfreeze","提款解冻",1),
		BETTING("betting","投注",-1),
		TRANSFER("transfer","转账",-1),
		PAYOUT("payout","派奖",1),
		REBATE("rebate","代理返点",1),
		REFUND("refund","撤单返回本金",1),
		CANCEL_REBATE("cancel_rebate","撤单扣除返点",-1),
		CUSTOMER_CLAIMS("customer_claims","平台奖励",1),
		PLAT_REWARD("plat_reward","平台积分",1),
		DEPOSIT_CASH("deposit_cash","充值礼金",1),
		REG_CASH("reg_cash","注册礼金",1),
		BANK_FEES("bank_fees","银行手续费",-1),
		PROMO_CASH("promo_cash","活动现金",1),
		PROMO_POINTS("promo_points","活动积分",1),
		ACC_FREEZE("acc_freeze","账户资金冻结",-1),
		ACC_UNFREEZE("acc_unfreeze","账户资金解冻",1),
		SYS_DEDUCTION("sys_deduction","系统扣款",-1),
		SYS_ADD("sys_add","系统加钱",1),
		DAILY_SIGN_IN("daily_sign_in","签到积分",1),
		POINTS_EXCHANGE("points_exchange","积分兑换",-1),
//		WITHDRAWAL_BACK("withdrawal_back","提款退还",-1),
		ACTIVITY_GIFT_RED("activity_gift_red","活动红包礼金",1),
		RECOVERY_PAYOUT("recovery_payout","派奖回收",-1);
//		USER_RED_BAG_WITHDRAWAL("user_red_bag_withdrawal","用户红包提现",-1);
//		USER_RED_ENVELOPE_WITHDRAWAL_DEDUCTION("user_red_envelope_withdrawal_deduction","用户红包提现扣除",-1);
		
		private String code;
		
		private String desc;

		private Integer number;
		
		private AccOperationType(String code,String desc,Integer number) {
			this.code = code;
			this.desc = desc;
			this.number = number;
		}
		
		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
		
		public Integer getNumber() {
			return number;
		}
		
		public static AccOperationType getValueByCode(String code) {
			AccOperationType[] names = AccOperationType.values();
			for(AccOperationType name: names) {
				if(name.getCode().equals(code)) {
					return name;
				}
			}
			return null;
		}
		public static Map<String,Integer> getNumberMap() {
			Map<String,Integer> map=new HashMap<String,Integer>();
			AccOperationType[] names = AccOperationType.values();
			for(AccOperationType code: names) {
				map.put(code.getCode(), code.getNumber());
			}
			return map;
		}
	}
	/**
	 *彩种的属性
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
		MONEY_UNIT("money_unit"),
		UP_LIMIT_PROFIT_LOSS("up_limit_profit_loss");
		
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
	
	
	public static enum PayChannelShowType{
		REDIRECT(0,"重定向"),//重定向
		IMG_PATH(1,"图片地址"),//图片地址
		JUMP_URL(2,"跳转地址"),//跳转地址
		QR_CODE(3,"二维码地址"),//二维码地址
		MESSAGE(4,"只显示消息"),//只显示消息
		BANK_ACC(5,"接收银行账户");
		
		private int code;
		private String desc;

		private PayChannelShowType(int code,String desc) {
			this.code = code;
			this.desc=desc;
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
		public static Map<Integer,String> getMap() {
			Map<Integer,String> map=new HashMap<Integer,String>();
			PayChannelShowType[] names = PayChannelShowType.values();
			for(PayChannelShowType name: names) {
				map.put(name.getCode(), name.getDesc());
			}
			return map;
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
		DEPLAY(1, "delay"),
		NON_DEPLAY(0, "non_deplay");
		
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
	/**
	 * 后台管理人员操作主钱包时，需要操作的类型
	 */
	public static enum MainWallet{
		CUSTOMER_CLAIMS("customer_claims","平台奖励",1),
		DEPOSIT_CASH("deposit_cash","充值礼金",1),
		REG_CASH("reg_cash","注册礼金",1),
		BANK_FEES("bank_fees","银行手续费",1),
		PROMO_CASH("promo_cash","活动现金",1),
		ACC_FREEZE("acc_freeze","账户资金冻结",-1),
		ACC_UNFREEZE("acc_unfreeze","账户资金解冻",1),
		SYS_DEDUCTION("sys_deduction","系统扣款",-1),
		SYS_ADD("sys_add","系统加钱",1);

		
		private String code;
		
		private String desc;
		
		private Integer number;
		
		private MainWallet(String code,String desc,Integer number) {
			this.code = code;
			this.desc = desc;
			this.number = number;
		}
		
		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
		
		public Integer getNumber() {
			return number;
		}
		
		public static MainWallet getValueByCode(String code) {
			MainWallet[] names = MainWallet.values();
			for(MainWallet name: names) {
				if(name.getCode().equals(code)) {
					return name;
				}
			}
			return null;
		}
		public static Map<String,String> getMap() {
			Map<String,String> map=new HashMap<String,String>();
			MainWallet[] names = MainWallet.values();
			for(MainWallet code: names) {
				map.put(code.getCode(), code.getDesc());
			}
			return map;
		}
	}
	/**
	 * 后台管理人员操作红包钱包时，需要操作的类型
	 */
	public static enum RedWallet{
		CUSTOMER_CLAIMS("customer_claims","平台奖励",1),
		PLAT_REWARD("plat_reward","平台积分",1),
		DEPOSIT_CASH("deposit_cash","充值礼金",1),
		REG_CASH("reg_cash","注册礼金",1),
		PROMO_CASH("promo_cash","活动现金",1),
		PROMO_POINTS("promo_points","活动积分",1),
		ACC_FREEZE("acc_freeze","账户资金冻结",-1),
		ACC_UNFREEZE("acc_unfreeze","账户资金解冻",1),
		SYS_DEDUCTION("sys_deduction","系统扣款",-1),
		SYS_ADD("sys_add","系统加钱",1);
		
		private String code;
		
		private String desc;
		
		private Integer number;
		
		private RedWallet(String code,String desc,Integer number) {
			this.code = code;
			this.desc = desc;
			this.number = number;
		}
		
		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
		
		public Integer getNumber() {
			return number;
		}
		
		public static RedWallet getValueByCode(String code) {
			RedWallet[] names = RedWallet.values();
			for(RedWallet name: names) {
				if(name.getCode().equals(code)) {
					return name;
				}
			}
			return null;
		}
		public static Map<String,String> getMap() {
			Map<String,String> map=new HashMap<String,String>();
			RedWallet[] names = RedWallet.values();
			for(RedWallet code: names) {
				map.put(code.getCode(), code.getDesc());
			}
			return map;
		}
	}
	/**
	 * 系统运行时参数
	 */
	public static enum SysRuntimeArgument{
		LOTTO_PRIZE_RATE("lotto_prize_rate"),
		MAX_PLAT_REBATE("max_plat_rebate"),
		NUMBER_OF_BANK_CARDS("number_of_bank_cards"),
		SITE_MSG_VALID_DAY("site_msg_valid_day"),
		NOTIFY_MSG_VALID_DAY("notify_msg_valid_day"),
		POINT_EXCHANGE_SCALE("point_exchange_scale"),
		LOCKING_TIME("locking_time"),
		FAIL_LOGIN_COUNT("fail_login_count");
		
		private String code;
		
		private SysRuntimeArgument(String code) {
			this.code = code;
		}
		
		public String getCode() {
			return this.code;
		}
		
	}
	
	/**
	 * 系统运行模式
	 */
	public static enum PrizeMode{
		MANUAL("0", "人工开奖模式"),
		NON_INTERVENTIONAL("1", "非干预模式"),
		INTERVENTIONAL("2", "系统干预模式"),
		DAEMO("3", "系统自动值守模式");
		
		private String code;
		
		private String desc;
		
		
		private PrizeMode(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public static PrizeMode getByCode(String code) {
			PrizeMode[] modes = PrizeMode.values();
			for(PrizeMode prizeMode : modes) {
				if(prizeMode.getCode().equals(code)) {
					return prizeMode;
				}
			}
			
			return null;
		}
		
	}
	
	
	/**
	 * 彩种类型
	 */
	public static enum LottoType{
		CQSSC("cqssc"),
		GD11X5("gd11x5"),
		FIVEFC("5fc"),
		SFC("sfc"),
		FFC("ffc"),
		MMC("mmc"),
		BJPK10("bjpk10");
		
		private String code;
		
		private LottoType(String code) {
			this.code = code;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public static LottoType getFromCode(String code) {
			LottoType[] lottoTypes = LottoType.values();
			for(LottoType type : lottoTypes) {
				if(type.getCode().equals(code)) {
					return type;
				}
			}
			
			return null;
		}
	}
	/**
	 * 提现备注
	 * */
	public static enum Remark{
		DATA_IS_NOT_LOCKED(0,"资料未锁定"),
		NO_NAME(1,"名字未填写"),
		BANK_NOT_SELECTED(2,"银行未选择"),
		SPECIAL_REASON(3,"特殊原因"),
		BANK_MAINTENANCE(4,"银行维护"),
		CONSUMPTION_HAS_NOT_BEEN_REACHED(5,"消费未达到"),
		CARD_NUMBER_NOT_FILLED_IN(6,"卡号未填写"),
		DATA_ERROR(7,"资料错误"),
		BANK_PUNCH(8,"银行冲正");
		
		private Integer code;
		private String name;
		
		private Remark(Integer code,String name) {
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
			Remark[] names = Remark.values();
			for(Remark name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		public static Map<Integer,String> getMap() {
			Map<Integer,String> map=new HashMap<Integer,String>();
			Remark[] names = Remark.values();
			for(Remark name: names) {
				map.put(name.getCode(), name.getName());
			}
			return map;
		}
		public static String getValueByCode(Integer code) {
			Remark[] states = Remark.values();
			for(Remark state: states) {
				if(state.getCode() == code) {
					return state.getName();
				}
			}
			return null;
		}
	}
	/**
	 * 日志类型
	 * */
	public static enum LogType{
		MEMBER_LOGIN("member_login"),
		SYS_ADMINISTRATOR_LOGIN("sys_administrator_login"),
		OPE_LOG_PROCESS_DEPOSIT(StringUtils.OPE_LOG_PROCESS_DEPOSIT),
		OPE_LOG_RESET_PWD(StringUtils.OPE_LOG_RESET_PWD),
		OPE_LOG_VERIFY_PHONE(StringUtils.OPE_LOG_VERIFY_PHONE),
		OPE_LOG_VERIFY_EMAIL(StringUtils.OPE_LOG_VERIFY_EMAIL),
		OPE_LOG_ADD_BANK_CARD(StringUtils.OPE_LOG_ADD_BANK_CARD),
		OPE_LOG_PERFECT_USER_INFO(StringUtils.OPE_LOG_PERFECT_USER_INFO),
		OPE_LOG_MOD_LOGIN_PWD(StringUtils.OPE_LOG_MOD_LOGIN_PWD),
		OPE_LOG_MOD_FUND_PWD(StringUtils.OPE_LOG_MOD_FUND_PWD),
		OPE_LOG_REG_USER(StringUtils.OPE_LOG_REG_USER),
		OPE_LOG_REG_AGENT(StringUtils.OPE_LOG_REG_AGENT),
		OPE_LOG_PROCESS_WITHDRAW(StringUtils.OPE_LOG_PROCESS_WITHDRAW),
		OPE_LOG_MOD_PERMISSION(StringUtils.OPE_LOG_MOD_PERMISSION),
		OPE_LOG_SPEC_WINNING_NUM(StringUtils.OPE_LOG_SPEC_WINNING_NUM),
		OPE_LOG_ISSUE_MANUAL_PAYOUT(StringUtils.OPE_LOG_ISSUE_MANUAL_PAYOUT),
		OPE_LOG_REVOKE_PAYOUT(StringUtils.OPE_LOG_REVOKE_PAYOUT),
		OPE_LOG_RE_PAYOUT(StringUtils.OPE_LOG_RE_PAYOUT),
		OPE_LOG_CANCEL_ISSUE(StringUtils.OPE_LOG_CANCEL_ISSUE),
		OPE_LOG_ORDER_MANUAL_PAYOUT(StringUtils.OPE_LOG_ORDER_MANUAL_PAYOUT),
		OPE_LOG_CANCEL_ORDER(StringUtils.OPE_LOG_CANCEL_ORDER),
		OPE_LOG_OPER_USER_AMT(StringUtils.OPE_LOG_OPER_USER_AMT);
		
		String code;
		
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		
		private LogType(String code) {
			this.code = code;
		}
		public static List<String> getList() {
			List<String> map=new ArrayList<String>();
			LogType[] names = LogType.values();
			for(LogType name: names) {
				map.add(name.getCode());
			}
			return map;
		}
		
		public static String getValueByCode(String code) {
			LogType[] states = LogType.values();
			for(LogType state: states) {
				if(state.getCode().equals(code)) {
					return state.getCode();
				}
			}
			return null;
		}
		
	}
	/**
	 * 图片验证码key
	 */
	public static enum Captcha{
		CAPTCHA("Captcha");
		
		private String code;
		
		private Captcha(String code) {
			this.code = code;
		}
		
		public String getCode() {
			return this.code;
		}
		
	}
	
	
	public static enum ZhBlockState{
		BLOCK(0),
		NON_BLOCK(1);
		
		private int code;
		
		private ZhBlockState(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
	}
	
	//支付方式
	public static enum PayTypeClass {
		WECHAT_PAY_SCAN(0, "wechat_pay_scan"), 
		ALI_PAY_SCAN(1, "ali_pay_scan"),
		ONLINE_BANKING_PAY(2, "online_banking_pay");

		private int code;

		private String desc;
		
		private PayTypeClass(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return this.desc;
		}
		
	}
	//每次签到领取多少积分
	public static enum SignupRecClass {
		SIGN_IN_INTEGRATION(10);

		private int code;
		
		private SignupRecClass(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
		
	}
}

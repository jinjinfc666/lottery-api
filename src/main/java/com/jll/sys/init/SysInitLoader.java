package com.jll.sys.init;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.entity.IpBlackList;
import com.jll.entity.PayChannel;
import com.jll.entity.PayType;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.game.LotteryCenterServiceImpl;
import com.jll.game.playtype.PlayTypeService;
import com.jll.sys.blacklist.IpBlackListService;
import com.jll.sys.deposit.PayChannelService;
import com.jll.sys.deposit.PayTypeService;
import com.jll.sysSettings.syscode.SysCodeService;

public class SysInitLoader {
	
	private Logger logger = Logger.getLogger(LotteryCenterServiceImpl.class);
	
	@Resource
	CacheRedisService cacheServ;
	
	@Resource
	SysCodeService sysCodeServ;
	
	@Resource
	PlayTypeService playTypeServ;
	
	@Resource
	IpBlackListService ipBlackListService;
	
	@Resource
	PayTypeService payTypeService;
	
	@Resource
	PayChannelService payChannelService;
	
	public void init() {
		initSysCode();
		initPlayType();
		initIpBlackList();
		initPayType();
		initPayChannel();
	}
	

	private void initSysCode() {
		initLotteryType();
		initLotteryAttributes();
		initSysCodePlayType();
		initAccOpeType();
		initPaymentPlatform();
		initLuckyDraw();
		initSignInDay();
		initSysCodePayType();
	}
	//加载签到活动
	private void initSignInDay() {
		String codeTypeName = Constants.SysCodeTypes.SIGN_IN_DAY.getCode();
		Map<String, SysCode> signInDay = cacheServ.getSysCode(codeTypeName);
		List<SysCode> sysCodes = null;
		
		if(signInDay == null || signInDay.size() == 0) {
			sysCodes = sysCodeServ.queryAllSmallType(codeTypeName);
			List<SysCode> sysCodeTypes = sysCodeServ.queryByCodeNameBigType(codeTypeName);
			
			if(sysCodes == null || sysCodes.size() == 0
					|| sysCodeTypes == null
					|| sysCodeTypes.size() == 0) {
				return ;
			}
			
			sysCodes.add(sysCodeTypes.get(0));
			
			cacheServ.setSysCode(codeTypeName, sysCodes);
		}
	}
	//加载流水类型
	private void initAccOpeType() {
		String codeTypeName = Constants.SysCodeTypes.FLOW_TYPES.getCode();
		Map<String, SysCode> accOpeType = cacheServ.getSysCode(codeTypeName);
		List<SysCode> sysCodes = null;
		
		if(accOpeType == null || accOpeType.size() == 0) {
			sysCodes = sysCodeServ.queryAllSmallType(codeTypeName);
			List<SysCode> sysCodeTypes = sysCodeServ.queryByCodeNameBigType(codeTypeName);
			
			if(sysCodes == null || sysCodes.size() == 0
					|| sysCodeTypes == null
					|| sysCodeTypes.size() == 0) {
				return ;
			}
			
			sysCodes.add(sysCodeTypes.get(0));
			
			cacheServ.setSysCode(codeTypeName, sysCodes);
		}
	}
	//加载支付平台
	private void initPaymentPlatform() {
		String codeTypeName = Constants.SysCodeTypes.PAYMENT_PLATFORM.getCode();
		Map<String, SysCode> paymentPlatform = cacheServ.getSysCode(codeTypeName);
		List<SysCode> sysCodes = null;
		
		if(paymentPlatform == null || paymentPlatform.size() == 0) {
			sysCodes = sysCodeServ.queryAllSmallType(codeTypeName);
			List<SysCode> sysCodeTypes = sysCodeServ.queryByCodeNameBigType(codeTypeName);
			
			if(sysCodes == null || sysCodes.size() == 0
					|| sysCodeTypes == null
					|| sysCodeTypes.size() == 0) {
				return ;
			}
			
			sysCodes.add(sysCodeTypes.get(0));
			
			cacheServ.setSysCode(codeTypeName, sysCodes);
		}
	}
	//加载幸运抽奖
	private void initLuckyDraw() {
		String codeTypeName = Constants.SysCodeTypes.LUCKY_DRAW.getCode();
		Map<String, SysCode> luckyDraw = cacheServ.getSysCode(codeTypeName);
		List<SysCode> sysCodes = null;
		
		if(luckyDraw == null || luckyDraw.size() == 0) {
			sysCodes = sysCodeServ.queryAllSmallType(codeTypeName);
			List<SysCode> sysCodeTypes = sysCodeServ.queryByCodeNameBigType(codeTypeName);
			
			if(sysCodes == null || sysCodes.size() == 0
					|| sysCodeTypes == null
					|| sysCodeTypes.size() == 0) {
				return ;
			}
			
			sysCodes.add(sysCodeTypes.get(0));
			
			cacheServ.setSysCode(codeTypeName, sysCodes);
		}
	}

	//加载彩种类型
	private void initLotteryType() {
		String codeTypeName = Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
		Map<String, SysCode> lottoTypes = cacheServ.getSysCode(codeTypeName);
		List<SysCode> sysCodes = null;
		
		if(lottoTypes == null || lottoTypes.size() == 0) {
			sysCodes = sysCodeServ.queryAllSmallType(codeTypeName);
			List<SysCode> sysCodeTypes = sysCodeServ.queryByCodeNameBigType(codeTypeName);
			
			if(sysCodes == null || sysCodes.size() == 0
					|| sysCodeTypes == null
					|| sysCodeTypes.size() == 0) {
				return ;
			}
			
			sysCodes.add(sysCodeTypes.get(0));
			
			cacheServ.setSysCode(codeTypeName, sysCodes);
		}
	}
	//加载充值方式类型   这是SysCode表里的Pay_Type(充值方式)的缓存
	private void initSysCodePayType() {
		String codeTypeName = Constants.SysCodeTypes.PAY_TYPE.getCode();
		Map<String, SysCode> sysCodePayType = cacheServ.getSysCode(codeTypeName);
		List<SysCode> sysCodes = null;
		
		if(sysCodePayType == null || sysCodePayType.size() == 0) {
			sysCodes = sysCodeServ.queryAllSmallType(codeTypeName);
			List<SysCode> sysCodeTypes = sysCodeServ.queryByCodeNameBigType(codeTypeName);
			
			if(sysCodes == null || sysCodes.size() == 0
					|| sysCodeTypes == null
					|| sysCodeTypes.size() == 0) {
				return ;
			}
			
			sysCodes.add(sysCodeTypes.get(0));
			
			cacheServ.setSysCode(codeTypeName, sysCodes);
		}
	}
	//加载玩法类型
	private void initSysCodePlayType() {
		String codeTypeName = Constants.SysCodePlayType.CT_PLAY_TYPE_CLASSICFICATION.getCode();
		Map<String, SysCode> playTypes = cacheServ.getSysCode(codeTypeName);
		List<SysCode> sysCodes = null;
		
		if(playTypes == null || playTypes.size() == 0) {
			sysCodes = sysCodeServ.queryAllSmallType(codeTypeName);
			List<SysCode> sysCodeTypes = sysCodeServ.queryByCodeNameBigType(codeTypeName);
			
			if(sysCodes == null || sysCodes.size() == 0
					|| sysCodeTypes == null
					|| sysCodeTypes.size() == 0) {
				return ;
			}
			
			sysCodes.add(sysCodeTypes.get(0));
			
			cacheServ.setSysCode(codeTypeName, sysCodes);
		}
	}
	//加载ip限制
	private void initIpBlackList() {
		String codeTypeName = Constants.IpBlackList.IP_BLACK_LIST.getCode();
		Map<Integer, IpBlackList> ipBlackListMaps = cacheServ.getIpBlackList(codeTypeName);
		List<IpBlackList> ipBlackLists = null;
		
		if(ipBlackListMaps == null || ipBlackListMaps.size() == 0) {
			ipBlackLists = ipBlackListService.query();
			
			if(ipBlackLists == null || ipBlackLists.size() == 0) {
				return ;
			}
			cacheServ.setIpBlackList(codeTypeName, ipBlackLists);
		}
	}
	//加载彩种的属性
	private void initLotteryAttributes() {
		List<String> lotteryAttributesList=Constants.SysCodeTypes.getList();
		if(lotteryAttributesList!=null&&lotteryAttributesList.size()>0) {
			for(int a=0;a<lotteryAttributesList.size();a++) {
				String codeTypeName = lotteryAttributesList.get(a);
				Map<String, SysCode> lottoTypes = cacheServ.getSysCode(codeTypeName);
				List<SysCode> sysCodes = null;
				
				if(lottoTypes == null || lottoTypes.size() == 0) {
					sysCodes = sysCodeServ.queryAllSmallType(codeTypeName);
					List<SysCode> sysCodeTypes = sysCodeServ.queryByCodeNameBigType(codeTypeName);
					
					if(sysCodes == null || sysCodes.size() == 0
							|| sysCodeTypes == null
							|| sysCodeTypes.size() == 0) {
						return ;
					}
					
					sysCodes.add(sysCodeTypes.get(0));
					
					cacheServ.setSysCode(codeTypeName, sysCodes);
				}
			}
		}
	}
	//加载玩法
	private void initPlayType() {
		String lotteryTypeName = Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
		Map<String, SysCode> lotteryTypes = cacheServ.getSysCode(lotteryTypeName);
		if(lotteryTypes == null || lotteryTypes.size() == 0) {
			return ;
		}
		
		Iterator<String> keys = lotteryTypes.keySet().iterator(); 
		while(keys.hasNext()) {
			String key = keys.next();
			SysCode lotteryType = lotteryTypes.get(key);
			if(lotteryType.getIsCodeType() == Constants.SysCodeTypesFlag.code_type.getCode()) {
				continue;
			}
			
			List<PlayType> playTypes = cacheServ.getPlayType(lotteryType);
			
			logger.debug((playTypes == null || playTypes.size() == 0)?
					"no play type cache for lottery type:" + lotteryType.getCodeName()
					:lotteryType.getCodeName() + " cached play types :" + playTypes.size());
			
			if(playTypes == null || playTypes.size() == 0) {
				playTypes = playTypeServ.queryPlayType(String.valueOf(lotteryType.getCodeName()));
				if(playTypes == null || playTypes.size() == 0) {
					continue;
				}
				
				logger.debug(String.format("Loading %s play types into cache!", playTypes.size()));
				
				cacheServ.setPlayType(lotteryType.getCodeName(), playTypes);
			}
		}
	}
	//加载充值方式  这是pay_type表的缓存
	private void initPayType() {
		String payTypeName=Constants.PayTypeName.PAY_TYPE_CLASS.getCode();
		List<PayType> payTypeLists = cacheServ.getPayType(payTypeName);
		
		if(payTypeLists == null || payTypeLists.size() == 0) {
			payTypeLists = payTypeService.queryAllPayType();
			if(payTypeLists == null || payTypeLists.size() == 0) {
				return ;
			}
			cacheServ.setPayType(payTypeName, payTypeLists);
		}
	}
	//加载充值渠道
	private void initPayChannel() {
		String payChannelName=Constants.PayChannel.PAY_CHANNEL.getCode();
		Map<Integer, PayChannel> payChannelMaps = cacheServ.getPayChannel(payChannelName);
		List<PayChannel> payChannelLists = null;
		
		if(payChannelMaps == null || payChannelMaps.size() == 0) {
			payChannelLists = payChannelService.queryAll();
			
			if(payChannelLists == null || payChannelLists.size() == 0) {
				return ;
			}
			cacheServ.setPayChannel(payChannelName, payChannelLists);
		}
	}
}

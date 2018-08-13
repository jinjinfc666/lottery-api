package com.jll.sys.init;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.game.LotteryCenterServiceImpl;
import com.jll.game.playtype.PlayTypeService;
import com.jll.sysSettings.syscode.SysCodeService;

public class SysInitLoader {
	
	private Logger logger = Logger.getLogger(LotteryCenterServiceImpl.class);
	
	@Resource
	CacheRedisService cacheServ;
	
	@Resource
	SysCodeService sysCodeServ;
	
	@Resource
	PlayTypeService playTypeServ;
	
	public void init() {
		initSysCode();
		initPlayType();
	}
	

	private void initSysCode() {
		initLotteryType();
		initLotteryAttributes();
		initSysCodePlayType();
		initAccOpeType();
	}
	
	private void initAccOpeType() {
		String codeTypeName = Constants.SysCodeTypes.FLOW_TYPES.getCode();
		Map<String, SysCode> accOpeType = cacheServ.getSysCode(codeTypeName);
		List<SysCode> sysCodes = null;
		
		if(accOpeType == null || accOpeType.size() == 0) {
			sysCodes = sysCodeServ.queryCacheType(codeTypeName);
			List<SysCode> sysCodeTypes = sysCodeServ.queryCacheTypeOnly(codeTypeName);
			
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
			sysCodes = sysCodeServ.queryCacheType(codeTypeName);
			List<SysCode> sysCodeTypes = sysCodeServ.queryCacheTypeOnly(codeTypeName);
			
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
			sysCodes = sysCodeServ.queryCacheType(codeTypeName);
			List<SysCode> sysCodeTypes = sysCodeServ.queryCacheTypeOnly(codeTypeName);
			
			if(sysCodes == null || sysCodes.size() == 0
					|| sysCodeTypes == null
					|| sysCodeTypes.size() == 0) {
				return ;
			}
			
			sysCodes.add(sysCodeTypes.get(0));
			
			cacheServ.setSysCode(codeTypeName, sysCodes);
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
					sysCodes = sysCodeServ.queryCacheType(codeTypeName);
					List<SysCode> sysCodeTypes = sysCodeServ.queryCacheTypeOnly(codeTypeName);
					
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
}

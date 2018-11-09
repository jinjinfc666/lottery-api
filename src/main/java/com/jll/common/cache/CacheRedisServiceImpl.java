package com.jll.common.cache;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.utils.DateUtil;
import com.jll.common.utils.MathUtil;
import com.jll.entity.IpBlackList;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PayChannel;
import com.jll.entity.PayType;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserInfo;
import com.jll.game.BulletinBoard;
import com.jll.game.playtype.PlayTypeFacade;
import com.jll.game.playtype.PlayTypeService;
import com.jll.game.playtypefacade.PlayTypeFactory;
import com.jll.sysSettings.syscode.SysCodeService;

@Configuration
@PropertySource("classpath:email-sender.properties")
@Service
@Transactional
public class CacheRedisServiceImpl implements CacheRedisService
{
	private Logger logger = Logger.getLogger(CacheRedisServiceImpl.class);

	private Object locker = new Object();
	
	@Resource
	CacheRedisDao cacheDao;
	
	@Resource
	SysCodeService sysCodeService;
	
	@Value("${email.qq.server}")
	private String qqServer;
	
	@Value("${email.qq.sender}")
	private String qqSender;
	
	@Value("${email.qq.pwd}")
	private String qqPwd;
	
	@Value("${email.reset.pwd.url}")
	private String resetUrl;
	
	@Value("${sys_captcha_code_expired_time}")
	private int captchaCodeExpiredTime;
	
	@Resource
	PlayTypeService playTypeServ;
	
	@Override
	public void setCaptchaCode(String captchaCode, int captchaCodeExpiredTime) {
		CacheObject<String> cacheObj = new CacheObject<String>();
		cacheObj.setContent(captchaCode);
		cacheObj.setExpired(captchaCodeExpiredTime);
		cacheObj.setKey(captchaCode);
		cacheDao.setCaptchaCode(cacheObj);
	}
	
	@Override
	public CacheObject<String> getCaptchaCode(String sms) {
		return cacheDao.getCaptchaCode(sms);
	}

	@Override
	public void setPlan(String lotteryType, List<Issue> issues) {
		String cacheKey = Constants.KEY_PRE_PLAN + lotteryType;
		
		cacheDao.setPlan(cacheKey, issues);
	}



	@Override
	public List<Issue> getPlan(String lotteryType) {
		String cacheKey = Constants.KEY_PRE_PLAN + lotteryType;
		return cacheDao.getPlan(cacheKey);
	}



	@Override
	public boolean isPlanExisting(String lotteryType) {
		Date today = new Date();
		String cacheKey = lotteryType;
		
		today = DateUtil.addMinutes(today, 10);
		//logger.debug(String.format("plan key %s", cacheKey));
		List<Issue> issues = this.getPlan(cacheKey);
		if(issues == null || issues.size() == 0) {
			//logger.debug(String.format("No plan existing..."));
			return false;
		}
		
		Issue lastIssue = issues.get(issues.size() -1);
		if(lastIssue.getEndTime().getTime() < today.getTime() ) {
			//logger.debug(String.format("Last issue is over..."));
			return false;
		}
		
		return true;
	}



	@Override
	public BulletinBoard getBulletinBoard(String lotteryType) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(Constants.KEY_PRE_BULLETINBOARD).append("_").append(lotteryType);
		CacheObject<BulletinBoard> cache = cacheDao.getBulletinBoard(buffer.toString());
		
		if(cache == null) {
			return null;
		}
		
		return cache.getContent();
	}

	@Override
	public synchronized void setBulletinBoard(String lottoType, BulletinBoard bulletinBoard) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(Constants.KEY_PRE_BULLETINBOARD).append("_").append(lottoType);
		
		CacheObject<BulletinBoard> cache = new CacheObject<>();
		cache.setContent(bulletinBoard);
		cache.setKey(buffer.toString());
		
		cacheDao.setBulletinBoard(cache);
	}

	@Override
	public boolean isCodeExisting(SysCodeTypes lotteryTypes, String lotteryType) {
		SysCode sysCode = getSysCode(lotteryTypes.getCode(), lotteryType);
		if(sysCode == null) {
			return false;
		}		
				
		return true;

	}
	@Override
	public void setSysCode(String codeTypeName, List<SysCode> sysCodes) {
		CacheObject<Map<String, SysCode>> cacheObj = new CacheObject<>();
		Map<String, SysCode> sysCodesTemp = new HashMap<>();
		for(SysCode sysCode : sysCodes) {
			sysCodesTemp.put(sysCode.getCodeName(), sysCode);
		}
		
		//container.put(codeTypeName, sysCodesTemp);
		cacheObj.setContent(sysCodesTemp);
		cacheObj.setKey(codeTypeName);
		cacheDao.setSysCode(cacheObj);
		
	}

	@Override
	public Map<String, SysCode> getSysCode(String codeName) {
		CacheObject<Map<String, SysCode>>  cache = cacheDao.getSysCode(codeName);
		if(cache == null) {
			return null;
		}
		
		return cache.getContent();
	}

	@Override
	public void setSysCode(String codeTypeName, SysCode sysCode) {
		//CacheObject<Map<String, SysCode>> cacheObj = new CacheObject<>();asdad
		CacheObject<Map<String, SysCode>> cacheObject=cacheDao.getSysCode(codeTypeName);
		Map<String, SysCode> sysCodesTemp=null;
		if(cacheObject==null) {
			sysCodesTemp = new HashMap<>();
			cacheObject= new CacheObject<>();
		}else {
			sysCodesTemp=cacheObject.getContent();
		}
		sysCodesTemp.put(sysCode.getCodeName(), sysCode);
		cacheObject.setContent(sysCodesTemp);
		cacheObject.setKey(codeTypeName);
		cacheDao.setSysCode(cacheObject);
	}

	@Override
	public SysCode getSysCode(String codeTypeName, String codeName) {
		return cacheDao.getSysCode(codeTypeName, codeName);
	}
	//playType 玩法--------------------Start----------------------------------
	@Override
	public List<PlayType> getPlayType(SysCode lotteryType) {
		String cacheKey = Constants.KEY_PLAY_TYPE + lotteryType.getCodeName();
		List<PlayType> playTypes = cacheDao.getPlayType(cacheKey);
		return playTypes;
	}
	//通过lotteryType查找
	@Override
	public List<PlayType> getPlayType(String cacheCodeName) {
		String cacheKey = Constants.KEY_PLAY_TYPE + cacheCodeName;
		List<PlayType> playTypes = cacheDao.getPlayType(cacheKey);
		return playTypes;
	}
	@Override
	public void setPlayType(String lotteryType, List<PlayType> playTypes) {
		String cacheKey = Constants.KEY_PLAY_TYPE + lotteryType;
		CacheObject<List<PlayType>> cache = new CacheObject<>();
		cache.setContent(playTypes);
		cache.setKey(cacheKey);
		
		cacheDao.setPlayType(cache);
	}
	//playType 玩法--------------------End----------------------------------
	
	@Override
	public synchronized void statGroupByBettingNum(String lotteryType, OrderInfo order, UserInfo user) {
		StringBuffer cacheKey = new StringBuffer();
		cacheKey.append(Constants.KEY_STAT_ISSUE_BETTING)
		.append(lotteryType).append(order.getIssueId());
		Map<String, Object> statInfo = null;
		CacheObject<Map<String, Object>> cacheObj = null;
		PlayTypeFacade playTypeFacade = null;
		Integer playTypeId = null;
		PlayType playType = null;
		String playTypeName = null;
		Map<String, Object> params = new HashMap<>();		
		Map<String, Object> preBetResult = null;
		String betNums = order.getBetNum();
		//String[] betNumSet = null;
		List<Map<String, String>> betNumMapping = null;
		
		cacheObj = cacheDao.getStatGroupByBettingNum(cacheKey.toString());
		
		if(cacheObj == null) {
			cacheObj = new CacheObject<>();
			statInfo = new HashMap<>();
			cacheObj.setKey(cacheKey.toString());
			cacheObj.setContent(statInfo);
			cacheObj.setExpired(1*60*60);
		}
		
		if(playTypeFacade == null) {
			playTypeId = order.getPlayType();
			if(playTypeId == null) {
				return ;
			}
			playType = playTypeServ.queryById(playTypeId);
			if(playType == null) {
				return ;
			}
			
			if(playType.getPtName().equals("fs")) {
				playTypeName = playType.getClassification() + "/fs";
			}else if(playType.getPtName().equals("ds")){
				playTypeName = playType.getClassification() + "/ds";
			}else {
				playTypeName = playType.getClassification() + "/" + playType.getPtName();
			}
			playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(playTypeName);
		}		
		
		betNumMapping = playTypeFacade.parseBetNumber(betNums);
		statInfo = cacheObj.getContent();
		
		Date startDate = new Date();
		//TODO
		for(Map<String, String> temp : betNumMapping) {
			String betNumTemp = temp.get(Constants.KEY_FACADE_BET_NUM);
			String pattern = temp.get(Constants.KEY_FACADE_PATTERN);
			String sample = temp.get(Constants.KEY_FACADE_BET_NUM_SAMPLE);
			boolean isExisting = false;			
						
			params.put("betNum", betNumTemp);
			params.put("times", order.getTimes());
			params.put("monUnit", order.getPattern().floatValue());
			params.put("lottoType", lotteryType);
			if(preBetResult == null) {
				preBetResult = playTypeFacade.preProcessNumber(params, user);
			}
			
			if(statInfo.get(pattern) != null) {
				isExisting = true;
			}
			
			if(isExisting) {
				isExisting = false;				
				statInfo.put(pattern, 
						MathUtil.add((Float)statInfo.get(pattern), 
								(Float)preBetResult.get("maxWinAmount"), 
								Float.class));
			}else {
				statInfo.put(pattern, preBetResult.get("maxWinAmount"));
			}
			
			if(statInfo.get(Constants.KEY_ISSUE_TOTAL_BETTING_AMOUNT) == null) {
				statInfo.put(Constants.KEY_ISSUE_TOTAL_BETTING_AMOUNT, 
						preBetResult.get("betAmount"));
			}else {
				Float total = (Float)statInfo.get(Constants.KEY_ISSUE_TOTAL_BETTING_AMOUNT);
				total = MathUtil.add(total, 
						(Float)preBetResult.get("betAmount"), 
						Float.class);
				statInfo.put(Constants.KEY_ISSUE_TOTAL_BETTING_AMOUNT, 
						total);
			}		
			
		}
		
		Date endDate = new Date();
		
		logger.debug(String.format("totaly take over  %s,   try to add %s  records  ,  existing %s records", 
				endDate.getTime() - startDate.getTime(),
				betNumMapping.size(),
				statInfo.size()));
		
		/*for(Map<String, String> temp : betNumMapping) {
			String betNumTemp = temp.get(Constants.KEY_FACADE_BET_NUM);
			String pattern = temp.get(Constants.KEY_FACADE_PATTERN);
			String sample = temp.get(Constants.KEY_FACADE_BET_NUM_SAMPLE);
			boolean isExisting = false;			
			
			Iterator<String> ite = statInfo.keySet().iterator();
			while(ite.hasNext()) {
				String key = ite.next();
				boolean isMatch = Pattern.matches(key, sample);
				if(isMatch) {
					isExisting = true;
					pattern = key;
					logger.debug(String.format("key  %s    pattern   %s     sample  %s", key, pattern, sample));
					break;
				}
			}
			
			params.put("betNum", betNumTemp);
			params.put("times", order.getTimes());
			params.put("monUnit", order.getPattern().floatValue());
			params.put("lottoType", lotteryType);
			preBetResult = playTypeFacade.preProcessNumber(params, user);
			
			if(isExisting) {
				isExisting = false;				
				statInfo.put(pattern, 
						MathUtil.add((Float)statInfo.get(pattern), 
								(Float)preBetResult.get("maxWinAmount"), 
								Float.class));
			}else {
				statInfo.put(pattern, preBetResult.get("maxWinAmount"));
			}
			
			if(statInfo.get(Constants.KEY_ISSUE_TOTAL_BETTING_AMOUNT) == null) {
				statInfo.put(Constants.KEY_ISSUE_TOTAL_BETTING_AMOUNT, 
						preBetResult.get("betAmount"));
			}else {
				Float total = (Float)statInfo.get(Constants.KEY_ISSUE_TOTAL_BETTING_AMOUNT);
				total = MathUtil.add(total, 
						(Float)preBetResult.get("betAmount"), 
						Float.class);
				statInfo.put(Constants.KEY_ISSUE_TOTAL_BETTING_AMOUNT, 
						total);
			}		
			
		}*/
		
		cacheObj.setContent(statInfo);
		
		cacheDao.setStatGroupByBettingNum(cacheObj);
	}

	@Override
	public boolean isIssueBetting(String lotteryType, int issueId) {
		BulletinBoard bulletinBoard = getBulletinBoard(lotteryType);
		if(bulletinBoard == null) {
			return false;
		}
		
		Issue currIssue = bulletinBoard.getCurrIssue();
		if(currIssue == null 
				|| currIssue.getId().intValue() != issueId
				|| currIssue.getState() != Constants.IssueState.BETTING.getCode()) {
			return false;
		}
		
		return true;
	}

//	@Override
//	public List<PayChannel> getPayChannel(int payTypeId) {
//		String cacheKey = Constants.KEY_PLAY_TYPE + payTypeId;
//		List<PayChannel> playTypes = cacheDao.getPayChannel(cacheKey);
//		return playTypes;
//	}

//	@Override
//	public void setPayChannel(int payTypeId, List<PayChannel> payChannel) {
//		String cacheKey = Constants.KEY_PAY_TYPE+payTypeId;
//		CacheObject<List<PayChannel>> cache = new CacheObject<>();
//		cache.setContent(payChannel);
//		cache.setKey(cacheKey);
//		cacheDao.setPayChannel(cache);
//	}

//	@Override
//	public List<PayType> getPayType() {
//		String cacheKey = Constants.KEY_PLAY_TYPE ;
//		List<PayType> playTypes = cacheDao.getPayType(cacheKey);
//		return playTypes;
//	}

//	@Override
//	public void setPayType(List<PayType> payTypes) {
//		String cacheKey = Constants.KEY_PAY_TYPE;
//		CacheObject<List<PayType>> cache = new CacheObject<>();
//		cache.setContent(payTypes);
//		cache.setKey(cacheKey);
//		cacheDao.setPayType(cache);
//	}

	
	@Override
	public PayType getPayTypeInfo(int payId){
		List<PayType> pcLists =  getPayType(Constants.PayTypeName.PAY_TYPE_CLASS.getCode());
		for (PayType pt : pcLists) {
			if(pt.getId() ==payId){
				return pt;
			}
		}
		return null;
	}
	public void publishMessage(String channel, Serializable mes) {
		logger.debug(String.format("publish message %s to %s", mes, channel));//"publish message to notify the module to obtain the winning number!!!" + mes);
		cacheDao.publishMessage(channel, mes);
	}
	//ip缓存
	@Override
	public Map<Integer, IpBlackList> getIpBlackList(String codeName) {
		CacheObject<Map<Integer, IpBlackList>>  cache = cacheDao.getIpBlackList(codeName);
		if(cache == null) {
			return null;
		}
		return cache.getContent();
	}
	@Override
	public IpBlackList getIpBlackList(String codeTypeName, Integer codeName) {
		return cacheDao.getIpBlackList(codeTypeName, codeName);
	}
	@Override
	public void setIpBlackList(String codeTypeName, IpBlackList ipBlackList) {
		CacheObject<Map<Integer, IpBlackList>> cacheObject=cacheDao.getIpBlackList(codeTypeName);
		Map<Integer, IpBlackList> ipBlackListTemp=null;
		if(cacheObject==null) {
			ipBlackListTemp = new HashMap<>();
			cacheObject= new CacheObject<>();
		}else {
			ipBlackListTemp=cacheObject.getContent();
		}
		ipBlackListTemp.put(ipBlackList.getId(), ipBlackList);
		cacheObject.setContent(ipBlackListTemp);
		cacheObject.setKey(codeTypeName);
		cacheDao.setIpBlackList(cacheObject);
	}
	@Override
	public void setIpBlackList(String codeTypeName, List<IpBlackList> ipBlackLists) {
		CacheObject<Map<Integer, IpBlackList>> cacheObj = new CacheObject<>();
		Map<Integer, IpBlackList> ipBlackListTemp = new HashMap<>();
		for(IpBlackList ipBlackList : ipBlackLists) {
			ipBlackListTemp.put(ipBlackList.getId(), ipBlackList);
		}
		
		//container.put(codeTypeName, sysCodesTemp);
		cacheObj.setContent(ipBlackListTemp);
		cacheObj.setKey(codeTypeName);
		cacheDao.setIpBlackList(cacheObj);
	}

	@Override
	public void deleteIpBlackList(String codeTypeName, Integer codeName) {
		cacheDao.deleteIpBlackList(codeTypeName, codeName);
	}

	@Override
	public boolean isUserBetting(UserInfo user, OrderInfo order) {
		StringBuffer cacheKey = new StringBuffer();
		cacheKey.append("betting_flag_").append(order.getIssueId()).append("_").append(user.getUserId());
		
		CacheObject<Integer> cacheObj = getUserBettingFlag(cacheKey.toString());
		
		if(cacheObj == null) {
			return false;
		}
		
		return cacheObj.getContent().intValue() == 0?false:true;
	}

	@Override
	public synchronized void setUserBettingFlag(UserInfo user, OrderInfo order) {
		synchronized(locker) {
			StringBuffer cacheKey = new StringBuffer();
			CacheObject<Integer> cacheObj = null;
			
			cacheKey.append("betting_flag_").append(order.getIssueId()).append("_").append(user.getUserId());
			cacheObj = getUserBettingFlag(cacheKey.toString());
			
			if(cacheObj == null) {
				cacheObj = new CacheObject<>();
				cacheObj.setExpired(24*60*60);
				cacheObj.setKey(cacheKey.toString());
			}
			
			cacheObj.setContent(1);
			
			cacheDao.setUserBettingFlag(cacheObj);
		}
	}

	private CacheObject<Integer> getUserBettingFlag(String cacheKey) {
		CacheObject<Integer> cacheObj = cacheDao.getUserBettingFlag(cacheKey);
		
		return cacheObj;
	}

	@Override
	public void releaseUserBettingFlag(UserInfo user, OrderInfo order) {
		StringBuffer cacheKey = new StringBuffer();
		CacheObject<Integer> cacheObj = null;
		
		cacheKey.append("betting_flag_").append(order.getIssueId()).append("_").append(user.getUserId());
		cacheObj = getUserBettingFlag(cacheKey.toString());
		
		if(cacheObj == null) {
			cacheObj = new CacheObject<>();
			cacheObj.setExpired(24*60*60);
			cacheObj.setKey(cacheKey.toString());
		}
		
		cacheObj.setContent(0);
		
		cacheDao.setUserBettingFlag(cacheObj);		
	}

	@Override
	public boolean isTimesValid(String lottoType, Integer times) {
		String timesStr = null;
		String[] timesArray = null;
		SysCode sysCode = cacheDao.getSysCode(Constants.KEY_LOTTO_ATTRI_PREFIX+lottoType, 
				Constants.LotteryAttributes.BET_TIMES.getCode());
		if(sysCode == null) {
			return false;
		}
		
		timesStr = sysCode.getCodeVal();
		timesArray = timesStr.split(",");
		
		Integer startNum=Integer.valueOf(timesArray[0]);
		Integer endNum=Integer.valueOf(timesArray[1]);
		if(startNum<=times&&endNum>=times) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isMonUnitValid(String lottoType, Float monUnit) {
		String monUnitStr = null;
		String[] monUnitArray = null;
		SysCode sysCode = cacheDao.getSysCode(Constants.KEY_LOTTO_ATTRI_PREFIX+lottoType, 
				Constants.LotteryAttributes.MONEY_UNIT.getCode());
		if(sysCode == null) {
			return false;
		}
		
		monUnitStr = sysCode.getCodeVal();
		monUnitArray = monUnitStr.split(",");
		
		for(String temp : monUnitArray) {
			BigDecimal tempDecimal = new BigDecimal(Float.valueOf(temp));
			BigDecimal moneyUnitDecimal = new BigDecimal(monUnit);
			if(tempDecimal.compareTo(moneyUnitDecimal) == 0) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean isPlayTypeValid(String lotteryType, Integer playTypeId) {
		List<PlayType> playTypes = cacheDao.getPlayType(Constants.KEY_PLAY_TYPE + lotteryType);
		if(playTypes == null || playTypes.size() == 0) {
			return false;
		}
				
		for(PlayType temp : playTypes) {
			if(temp.getId().intValue() == playTypeId) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public SysCode getBetTimes(String lotteryType) {
		SysCode sysCode = cacheDao.getSysCode(Constants.KEY_LOTTO_ATTRI_PREFIX+lotteryType, 
				Constants.LotteryAttributes.BET_TIMES.getCode());
		
		return sysCode;
	}

	@Override
	public SysCode getMoneyUnit(String lotteryType) {
		SysCode sysCode = cacheDao.getSysCode(Constants.KEY_LOTTO_ATTRI_PREFIX+lotteryType, 
				Constants.LotteryAttributes.MONEY_UNIT.getCode());
		return sysCode;
	}
	
	//充值方式

	@Override
	public List<PayType> getPayType(String codeName) {
		List<PayType> playTypes = cacheDao.getPayType(codeName);
		if(playTypes!=null&&playTypes.size()>0) {
			return playTypes;
		}
		return null;
	}

	@Override
	public void setPayType(String codeTypeName, List<PayType> payTypes) {
		CacheObject<List<PayType>> cache = new CacheObject<>();
		cache.setContent(payTypes);
		cache.setKey(codeTypeName);
		
		cacheDao.setPayType(cache);
		
	}
	//充值渠道
	@Override
	public Map<Integer, PayChannel> getPayChannel(String codeName) {
		CacheObject<Map<Integer, PayChannel>>  cache = cacheDao.getPayChannel(codeName);
		if(cache == null) {
			return null;
		}
		return cache.getContent();
	}

	@Override
	public PayChannel getPayChannel(String codeName, Integer codeName1) {
		return cacheDao.getPayChannel(codeName, codeName1);
	}

	@Override
	public void setPayChannel(String codeName, PayChannel payChannel) {
		CacheObject<Map<Integer, PayChannel>> cacheObject=cacheDao.getPayChannel(codeName);
		Map<Integer, PayChannel> payChannelTemp=null;
		if(cacheObject==null) {
			payChannelTemp = new HashMap<>();
			cacheObject= new CacheObject<>();
		}else {
			payChannelTemp=cacheObject.getContent();
		}
		payChannelTemp.put(payChannel.getId(), payChannel);
		cacheObject.setContent(payChannelTemp);
		cacheObject.setKey(codeName);
		cacheDao.setPayChannel(cacheObject);
	}

	@Override
	public void setPayChannel(String codeName, List<PayChannel> payChannelLists) {
		CacheObject<Map<Integer, PayChannel>> cacheObj = new CacheObject<>();
		Map<Integer, PayChannel> payChannelTemp = new HashMap<>();
		for(PayChannel payChannel : payChannelLists) {
			payChannelTemp.put(payChannel.getId(), payChannel);
		}
		//container.put(codeTypeName, sysCodesTemp);
		cacheObj.setContent(payChannelTemp);
		cacheObj.setKey(codeName);
		cacheDao.setPayChannel(cacheObj);
	}

	@Override
	public Map<String, SysCode> getSysRuntimeArg(String keySysRuntimeArg) {
		CacheObject<Map<String, SysCode>>  cache = cacheDao.getSysCode(keySysRuntimeArg);
		if(cache == null) {
			return null;
		}
		return cache.getContent();
	}

	@Override
	public Map<String, Object> getStatGroupByBettingNum(String lotteryType, Integer issueId) {
		StringBuffer cacheKey = new StringBuffer();
		cacheKey.append(Constants.KEY_STAT_ISSUE_BETTING)
		.append(lotteryType).append(issueId);
		
		CacheObject<Map<String, Object>> cacheObj = null;
		
		cacheObj = cacheDao.getStatGroupByBettingNum(cacheKey.toString());
		
		if(cacheObj == null) {
			return null;
		}
		
		return cacheObj.getContent();
	}

	@Override
	public Map<String, Object> getPlatStat(String lotteryType) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date currDay = new Date();
		String currDayStr = format.format(currDay);
		StringBuffer cacheKey = new StringBuffer();
		Map<String, Object> ret = null;
		CacheObject<Map<String, Object>> cacheObj = null;
		
		cacheKey.append(Constants.KEY_LOTTO_TYPE_PLAT_STAT)
		.append(lotteryType).append("_")
		.append(currDayStr);
		
		cacheObj = cacheDao.getPlatStat(cacheKey.toString());
		
		if(cacheObj == null) {
			return null;
		}
		return cacheObj.getContent();
	}

	@Override
	public void setPlatStat(String lotteryType, Map<String, Object> items) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date currDay = new Date();
		String currDayStr = format.format(currDay);
		StringBuffer cacheKey = new StringBuffer();
		CacheObject<Map<String, Object>> cacheObj = new CacheObject<>();
		Map<String, Object> cacheContent = null;
		
		cacheKey.append(Constants.KEY_LOTTO_TYPE_PLAT_STAT)
		.append(lotteryType).append("_")
		.append(currDayStr);
		
		cacheContent = getPlatStat(lotteryType);
		if(cacheContent == null) {
			cacheContent = new HashMap<>();
		}
		
		Iterator<String> keys = items.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			Object val = items.get(key);
			
			cacheContent.put(key, val);
		}
		cacheObj.setContent(cacheContent);
		cacheObj.setKey(cacheKey.toString());
		
		cacheDao.setPlatStat(cacheObj);
	}

	@Override
	public Integer getMMCIssueCount(Date currTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date currDay = new Date();
		String currDayStr = format.format(currDay);
		StringBuffer cacheKey = new StringBuffer();
		Map<String, Object> ret = null;
		CacheObject<Integer> cacheObj = null;
		
		cacheKey.append(Constants.MMC_ISSUE_COUNT)
		.append(currDayStr);
		
		cacheObj = cacheDao.getMMCIssueCount(cacheKey.toString());
		
		if(cacheObj == null) {
			return null;
		}
		return cacheObj.getContent();
	}

	@Override
	public void setMMCIssueCount(Date currTime, int i) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date currDay = new Date();
		String currDayStr = format.format(currDay);
		StringBuffer cacheKey = new StringBuffer();
		CacheObject<Integer> cacheObj = new CacheObject<>();
		Integer cacheContent = null;
		
		cacheKey.append(Constants.MMC_ISSUE_COUNT)
		.append(currDayStr);
		
		cacheContent = new Integer(i);
		
		cacheObj.setContent(cacheContent);
		cacheObj.setKey(cacheKey.toString());
		
		cacheDao.setMMCIssueCount(cacheObj);
	}

	@Override
	public void updatePlan(String lottoType, Issue issue) {
		String cacheKey = Constants.KEY_PRE_PLAN + lottoType;
		cacheDao.upatePlan(cacheKey, issue);
	}
	//存储图片验证码
	@Override
	public void setSessionIdCaptcha(String keyCaptcha, String value) {
		String key=Constants.Captcha.CAPTCHA.getCode();
		CacheObject<Map<String,String>> cacheObject=cacheDao.getSessionIdCaptcha(key);
		Map<String,String> map=null;
		if(cacheObject==null) {
			map = new HashMap<>();
			cacheObject= new CacheObject<>();
			cacheObject.setContent(map);
		}
		
		map = cacheObject.getContent();
		map.put(keyCaptcha, value);
		cacheObject.setContent(map);
		cacheObject.setKey(key);
		cacheDao.setSessionIdCaptcha(cacheObject);
		
	}
	//获取图片验证码
	@Override
	public String getSessionIdCaptcha(String keyCaptcha) {
		String key=Constants.Captcha.CAPTCHA.getCode();
		CacheObject<Map<String,String>> valueap=cacheDao.getSessionIdCaptcha(key);
		Map<String,String> map=valueap.getContent();
		if(map==null) {
			return null;
		}
		String value=map.get(keyCaptcha);
		return value;
	}
	//删除缓存中的图片验证码
	@Override
	public void deleteSessionIdCaptcha(String keyCaptcha) {
		String key=Constants.Captcha.CAPTCHA.getCode();
		cacheDao.deleteSessionIdCaptcha(key, keyCaptcha);
	}
}

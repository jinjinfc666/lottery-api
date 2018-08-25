package com.jll.game.cqssc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.utils.MathUtil;
import com.jll.common.utils.StringUtils;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.SysCode;
import com.jll.entity.UserInfo;
import com.jll.game.playtype.PlayTypeFacade;
import com.jll.spring.extend.SpringContextUtil;
import com.jll.user.UserInfoService;

/**
 * bet number like : 1[;2],2,3---->万 千 百位
 * @author Administrator
 *
 */
public class QszxPlayTypeFacadeImpl implements PlayTypeFacade {
	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	private String playTypeDesc = "cqssc/qszx|前三直选";
	
	CacheRedisService cacheServ = (CacheRedisService)SpringContextUtil.getBean("cacheRedisServiceImpl");
	
	UserInfoService userServ = (UserInfoService)SpringContextUtil.getBean("userInfoServiceImpl");
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		//开奖号码的每一位
		String[] winNumSet = null;
		//投注号码的每个位的号码，可能多个号码
		String[] betNumSet = new String[3];
		//每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul= null;
		String betNum = null;
		String winNum = null;
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(0,3);
		winNumSet = winNum.split(",");
		//betNumSet = betNum.split(",");
		betNumMul = betNum.split(";");
		
		for(String temp : betNumMul) {
			String[] tempSet = temp.split(",");
			if(StringUtils.isBlank(betNumSet[0])) {
				betNumSet[0] = tempSet[0];
			}else {
				betNumSet[0] = betNumSet[0] + tempSet[0];
			}
			
			if(StringUtils.isBlank(betNumSet[1])) {
				betNumSet[1] = tempSet[1];
			}else {
				betNumSet[1] = betNumSet[1] + tempSet[1];
			}
			
			if(StringUtils.isBlank(betNumSet[2])) {
				betNumSet[2] = tempSet[2];
			}else {
				betNumSet[2] = betNumSet[2] + tempSet[2];
			}			
			
		}
		
		logger.debug("proced bet number is :: " + Arrays.asList(betNumSet));
		
		for(int i = 0; i< winNumSet.length; i++) {
			String betNumDigit = betNumSet[i];
			if(!betNumDigit.contains(winNumSet[i])) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public Map<String, Object> preProcessNumber(Map<String, Object> params, UserInfo user) {		
		Map<String, Object> ret = new HashMap<>();
		String betNum = (String)params.get("betNum");
		Integer times = (Integer)params.get("times");
		Float monUnit = (Float)params.get("monUnit");
		Integer playType = (Integer)params.get("playType");
		String lottoType = (String)params.get("lottoType");
		Float prizeRate = userServ.calPrizeRate(user, lottoType);
		String[] betNumSet = null;
		int betTotal = 1;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		
		betNumSet = betNum.split(",");
		for(String subBetNum : betNumSet) {
			int len = subBetNum.length();
			betTotal *= MathUtil.combination(1, len);
		}
		
		betAmount = MathUtil.multiply(betTotal, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit, Float.class);
		maxWinAmount = MathUtil.multiply(betAmount, prizeRate, Float.class);
		
		ret.put("playType", playType);
		ret.put("betAmount", betAmount);
		ret.put("maxWinAmount", maxWinAmount);
		ret.put("betTotal", betTotal);
		ret.put("prizeRate", prizeRate);
		return ret;
	}

	

	@Override
	public String getPlayTypeDesc() {
		return playTypeDesc;
	}

	@Override
	public boolean validBetNum(OrderInfo order) {
		String betNum = null;
		String[] betNumSet = null;
		
		betNum = order.getBetNum();
		if(StringUtils.isBlank(betNum)) {
			return false;
		}
		
		betNumSet = betNum.split(",");
		if(betNumSet == null || betNumSet.length != 3) {
			return false;
		}
		
		return true;
	}

	@Override
	public BigDecimal calPrize(Issue issue, OrderInfo order, UserInfo user) {
		// 开奖号码的每一位
		String[] winNumSet = null;
		// 投注号码的每个位的号码，可能多个号码
		String[] betNumSet = new String[3];
		// 每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul = null;
		String betNum = null;
		String winNum = null;
		int winningBetAmount = 0;
		/*BigDecimal prize = null;
		int betTotal = 1;*/
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		Integer times = order.getTimes();
		BigDecimal monUnit = order.getPattern();
		Float prizeRate = userServ.calPrizeRate(user, issue.getLotteryType());
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(0, 3);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		

		for(String singleSel : betNumMul) {
			betNumSet = singleSel.split(",");
			if(betNumSet[0].contains(winNumSet[0])
					&& betNumSet[1].contains(winNumSet[1])
					&& betNumSet[2].contains(winNumSet[2])) {
				winningBetAmount++;
			}
		}
		
		betAmount = MathUtil.multiply(winningBetAmount, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit, Float.class);
		maxWinAmount = MathUtil.multiply(betAmount, prizeRate, Float.class);
		
		
		
		return new BigDecimal(maxWinAmount);
	}
}

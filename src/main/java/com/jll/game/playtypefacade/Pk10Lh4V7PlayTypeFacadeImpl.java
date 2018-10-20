package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jll.common.utils.MathUtil;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;

/**
 * 龙      1 
 * 虎       0
 * @author Administrator
 *
 */
public class Pk10Lh4V7PlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(Pk10Lh4V7PlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "lh|龙虎/4v7|4v7/fs-ds";
	
	private final int DRAGON = 1;
	
	private final int TIGER = 0;
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		//开奖号码的每一位
		String[] winNumSet = null;
		//投注号码的每个位的号码，可能多个号码
		//String[] betNumSet = new String[3];
		//每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul= null;
		String betNum = null;
		String winNum = null;
		int winNumFinal = -1;
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		//winNum = winNum.substring(0,3);
		winNumSet = winNum.split(",");
		//betNumSet = betNum.split(",");
		betNumMul = betNum.split(";");
		
		if(Integer.parseInt(winNumSet[3]) > Integer.parseInt(winNumSet[6])) {
			winNumFinal = DRAGON;
		}else {
			winNumFinal = TIGER;
		}
		
		for(String temp : betNumMul) {
			if(temp.contains(String.valueOf(winNumFinal))) {
				return true;
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
		Float prizePattern = userServ.calPrizePattern(user, lottoType);
		BigDecimal winningRate = calWinningRate();
		BigDecimal singleBettingPrize = calSingleBettingPrize(prizePattern, winningRate);
		String[] betNumSet = null;
		int betTotal = 0;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		
		betNumSet = betNum.split(";");
		for(String subBetNum : betNumSet) {						
			int len = subBetNum.length();
			betTotal += MathUtil.combination(1, len);
		}
		
		betAmount = MathUtil.multiply(betTotal, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit, Float.class);
		maxWinAmount = MathUtil.multiply(betAmount, singleBettingPrize.floatValue(), Float.class);
		
		ret.put("playType", playType);
		ret.put("betAmount", betAmount);
		ret.put("maxWinAmount", maxWinAmount);
		ret.put("betTotal", betTotal);
		ret.put("singleBettingPrize", singleBettingPrize);
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
		
		betNumSet = betNum.split(";");
				
		for(String temp : betNumSet) {
			if(StringUtils.isBlank(temp)) {
				return false;
			}			
						
			if(temp.length() > 2
					|| !Utils.validateNum(temp)) {
				return false;
			}	
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
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		Integer times = order.getTimes();
		BigDecimal monUnit = order.getPattern();
		BigDecimal singleBettingPrize = null;
		int winNumFinal = -1;
		
		//1700 --- 1960
		Float prizePattern = userServ.calPrizePattern(user, issue.getLotteryType());
		BigDecimal winningRate = calWinningRate();
		singleBettingPrize =  calSingleBettingPrize(prizePattern, winningRate);
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(0, 5);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		if(Integer.parseInt(winNumSet[3]) > Integer.parseInt(winNumSet[6])) {
			winNumFinal = DRAGON;
		}else {
			winNumFinal = TIGER;
		}
		
		for(String singleSel : betNumMul) {
			betNumSet = singleSel.split(",");
			if(singleSel.contains(String.valueOf(winNumFinal))) {
				winningBetAmount++;
			}
		}
		
		betAmount = MathUtil.multiply(winningBetAmount, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit.floatValue(), Float.class);
		maxWinAmount = MathUtil.multiply(betAmount, singleBettingPrize.floatValue(), Float.class);
		
		return new BigDecimal(maxWinAmount);
	}

	/* (non-Javadoc)
	 * @see com.jll.game.playtype.PlayTypeFacade#calWinningRate()
	 * 
	 * 1/1000
	 */
	@Override
	public BigDecimal calWinningRate() {
		BigDecimal winningRate = null;
		BigDecimal winCount = null;
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
		Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.combination(1, 9)));
		Double tempVal2 = 0D;
		
		for(int i = 1; i <= 9; i++) {
			tempVal2 += Double.parseDouble(Long.toString(MathUtil.combination(1, i)));
		}
		
		tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
		
		totalCount = new BigDecimal(tempVal);
		winCount = new BigDecimal(tempVal2);
		winningRate = winCount.divide(totalCount, 5, BigDecimal.ROUND_HALF_UP);
		return winningRate;
	}
}

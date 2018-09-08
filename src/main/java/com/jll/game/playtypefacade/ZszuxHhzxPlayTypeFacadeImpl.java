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

public class ZszuxHhzxPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl  {

	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	private String playTypeDesc = "cqssc/zszx|中三组选/hhzxds";
	
	@Override
	public String getPlayTypeDesc() {
		return playTypeDesc;
	}
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		// 开奖号码的每一位
		String[] winNumSet = null;
		// 投注号码的每个位的号码，可能多个号码
		String[] betNumSet = new String[3];
		// 每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul = null;
		String betNum = null;
		String winNum = null;

		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(2, 7);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");

		logger.debug("proceed bet number is :: " + Arrays.asList(betNumSet));

		for (String temp : betNumMul) {
			if (temp.contains(winNumSet[0]) 
					&& temp.contains(winNumSet[1]) 
					&& temp.contains(winNumSet[2])) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Map<String, Object> preProcessNumber(Map<String, Object> params, UserInfo user) {		
		Map<String, Object> ret = new HashMap<>();
		
		String betNum = (String)params.get("betNum");
		Integer times = (Integer)params.get("times");
		Float monUnit = (Float)params.get("monUnit");
		Integer playType = (Integer)params.get("playType");
		String[] betNumMul = betNum.split(";");
		//组三单注奖金
		BigDecimal singleBettingPrizeZs = null;
		//组六单注奖金
		BigDecimal singleBettingPrizeZl = null;
		
		int betTotal = 1;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		String lottoType = (String)params.get("lottoType");
		Float prizePattern = userServ.calPrizePattern(user, lottoType);
		BigDecimal winningRateZs = calWinningRateZs();
		BigDecimal winningRateZl = calWinningRateZl();
		singleBettingPrizeZs =  calSingleBettingPrize(prizePattern, winningRateZs);
		singleBettingPrizeZl = calSingleBettingPrize(prizePattern, winningRateZl);
		
		
		betTotal = betNumMul.length;
		Float singleBetAmount = MathUtil.multiply(1, times, Float.class);
		singleBetAmount = MathUtil.multiply(singleBetAmount, monUnit, Float.class);
		
		for(String tempBettingNum : betNumMul) {
			Float singleWinAmount = 0F;
			boolean isZsBettingNum = isZsBettingNum(tempBettingNum);
			if(isZsBettingNum) {				
				singleWinAmount = MathUtil.multiply(singleBetAmount, 
						singleBettingPrizeZs.floatValue(), 
						Float.class);
			}else {
				singleWinAmount = MathUtil.multiply(singleBetAmount, 
						singleBettingPrizeZl.floatValue(), 
						Float.class);
			}
			
			maxWinAmount = MathUtil.add(maxWinAmount, singleWinAmount, Float.class);
		}
		
		betAmount = MathUtil.multiply(singleBetAmount, betTotal, Float.class);
		
		
		ret.put("playType", playType);
		ret.put("betAmount", betAmount);
		ret.put("maxWinAmount", maxWinAmount);
		ret.put("betTotal", betTotal);
		ret.put("singleBettingPrize", singleBettingPrizeZs);
		ret.put("singleBettingPrizeZl", singleBettingPrizeZl);
		return ret;
	}
	
	private boolean isZsBettingNum(String tempBettingNum) {
		Map<String, String> bettingNumBit = new HashMap<>();
		for(int i = 0;i < tempBettingNum.length(); i++) {
			String numBit = tempBettingNum.substring(i, i+1);
			bettingNumBit.put(numBit, numBit);
		}
		return (bettingNumBit.size() == 2)?true:false;
	}

	@Override
	public boolean validBetNum(OrderInfo order) {
		String betNum = null;
		String[] betNumMul = null;
		
		betNum = order.getBetNum();
		if(StringUtils.isBlank(betNum)) {
			return false;
		}
		
		betNumMul = betNum.split(";");
		for(String betNumTemp : betNumMul) {
			if(StringUtils.isBlank(betNumTemp)) {
				return false;
			}
			
			if(betNumTemp.length() != 3
					|| !Utils.validateNum(betNumTemp)) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public BigDecimal calPrize(Issue issue, OrderInfo order, UserInfo user) {
		// 开奖号码的每一位
		String[] winNumSet = null;
		// 每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul = null;
		String betNum = null;
		String winNum = null;
		Float singleBetAmount = 0F;
		Float maxWinAmount = 0F;
		Integer times = order.getTimes();
		BigDecimal monUnit = order.getPattern();
		//组三单注奖金
		BigDecimal singleBettingPrizeZs = null;
		//组六单注奖金
		BigDecimal singleBettingPrizeZl = null;
		
		//1700 --- 1960
		Float prizePattern = userServ.calPrizePattern(user, issue.getLotteryType());
		BigDecimal winningRateZs = calWinningRateZs();
		BigDecimal winningRateZl = calWinningRateZl();
		singleBettingPrizeZs =  calSingleBettingPrize(prizePattern, winningRateZs);
		singleBettingPrizeZl = calSingleBettingPrize(prizePattern, winningRateZl);
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(2, 7);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");	
		singleBetAmount = MathUtil.multiply(1, times, Float.class);
		singleBetAmount = MathUtil.multiply(singleBetAmount, monUnit, Float.class);
		
		for(String temp : betNumMul) {
			if(temp.contains(winNumSet[0]) 
					&& temp.contains(winNumSet[1])
					&& temp.contains(winNumSet[2])) {
				Float singleWinAmount = 0F;
				boolean isZsBettingNum = isZsBettingNum(temp);
				if(isZsBettingNum) {
					singleWinAmount = MathUtil.multiply(singleBetAmount, singleBettingPrizeZs, Float.class);
				}else {
					singleWinAmount = MathUtil.multiply(singleBetAmount, singleBettingPrizeZl, Float.class);
				}				
				
				maxWinAmount = MathUtil.add(maxWinAmount, singleWinAmount, Float.class);		
			}
		}		
		
		return new BigDecimal(maxWinAmount);
	}

	/* (non-Javadoc)
	 * @see com.jll.game.playtype.PlayTypeFacade#calWinningRate()
	 * 
	 * 1/1000
	 */
	@Override
	public BigDecimal calWinningRate() {
		return null;
	}
	
	public BigDecimal calWinningRateZs() {
		BigDecimal winningRate = null;
		BigDecimal winCount = new BigDecimal(3);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
		
		tempVal = Math.pow(tempVal, 3);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount);
		return winningRate;
	}
	
	public BigDecimal calWinningRateZl() {
		BigDecimal winningRate = null;
		BigDecimal winCount = new BigDecimal(6);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
		
		tempVal = Math.pow(tempVal, 3);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount);
		return winningRate;
	}
}

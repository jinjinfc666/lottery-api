package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jll.common.utils.MathUtil;
import com.jll.common.utils.StringUtils;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;

public class EleIn5Rx3PlayTypeFacadeImpl  extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(EleIn5Rx3PlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "rx|任选/rxszs|任选三中三/fs-ds";
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		//开奖号码的每一位
		String[] winNumSet = null;
		//投注号码的每个位的号码，可能多个号码
		String[] betNumSet = new String[2];
		//每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul= null;
		String betNum = null;
		String winNum = null;
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		//winNum = winNum.substring(0,3);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		logger.debug("proced bet number is :: " + Arrays.asList(betNumSet));
		int matchCount = 0;
		
		for(String temp : betNumMul) {
			if(temp.contains(winNumSet[0])){
				matchCount++;
			}
			
			if(temp.contains(winNumSet[1])){
				matchCount++;
			}
			
			if(temp.contains(winNumSet[2])){
				matchCount++;
			}
			
			if(temp.contains(winNumSet[3])){
				matchCount++;
			}
			
			if(temp.contains(winNumSet[4])){
				matchCount++;
			}
			
			if(matchCount >= 3) {
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
		String lottoType = (String)params.get("lottoType");
		Float prizePattern = userServ.calPrizePattern(user, lottoType);
		BigDecimal winningRate = calWinningRate();
		BigDecimal singleBettingPrize = calSingleBettingPrize(prizePattern, winningRate);
		String[] betNumSet = null;
		int betTotal = 1;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		
		betNumSet = betNum.split(";");
		for(String subBetNum : betNumSet) {
			int len = subBetNum.length() / 2;
			betTotal *= MathUtil.combination(3, len);
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
		String[] betNumMul = null;
		betNum = order.getBetNum();
		if(StringUtils.isBlank(betNum)) {
			return false;
		}
		
		betNumMul = betNum.split(";");
		for(String betNumTemp : betNumMul) {
			if(StringUtils.isBlank(betNumTemp) 
					|| betNumTemp.length() / 2 < 3
					|| betNumTemp.length() / 2 > 11) {
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
		
		//1700 --- 1960
		Float prizePattern = userServ.calPrizePattern(user, issue.getLotteryType());
		BigDecimal winningRate = calWinningRate();
		singleBettingPrize =  calSingleBettingPrize(prizePattern, winningRate);
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		//winNum = winNum.substring(0, 3);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");		
		//int betNumBitCount = 0;
		
		for(String singleSel : betNumMul) {			
			for(int i = 0; i < singleSel.length();) {
				String singleSelBit = singleSel.substring(i, i + 2);
				if(winNum.contains(singleSelBit)) {
					winningBetAmount++;
				}
				
				i += 2;
				
			}			
		}
		
		if(winningBetAmount < 3) {
			winningBetAmount = 0;
		}else {
			winningBetAmount = ((Long)MathUtil.combination(3, winningBetAmount)).intValue();
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
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.arrangement(3, 5)));
		Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.combination(3, 11)));
		Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(3, 3)));
		
		tempVal1 = MathUtil.multiply(tempVal1, tempVal2, Double.class);
		tempVal = MathUtil.divide(tempVal, tempVal1, 4);
		winningRate = new BigDecimal(tempVal);
		return winningRate;
	}
}

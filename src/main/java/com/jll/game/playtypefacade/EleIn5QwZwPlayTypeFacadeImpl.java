package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jll.common.utils.MathUtil;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;

public class EleIn5QwZwPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(EleIn5QwZwPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "qwx|趣味型/qwczw|趣味猜中位/fs-ds";
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		Integer[] betNumSetList = new Integer[5];
		String midBit = null;
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		//winNum = winNum.substring(0, 5);
		winNumSet = winNum.split(",");
		//betNumSet = betNum.split(",");
		betNumMul = betNum.split(";");
		
		for(int i = 0; i < winNumSet.length; i++) {
			String winNumBit = winNumSet[i];
			Integer val = Integer.valueOf(winNumBit);
			betNumSetList[i] = val;
		}
		
		Arrays.sort(betNumSetList, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				Integer val1 = (Integer)o1;
				Integer val2 = (Integer)o2;
				return val1.intValue() - val2.intValue();
			}
			
		});
		
		midBit = String.valueOf(betNumSetList[2]);
		
		logger.debug("proced bet number is :: " + Arrays.asList(betNumSet));
		for(String temp : betNumMul) {
			if(StringUtils.isBlank(temp)) {
				continue;
			}
			
			if(temp.contains(String.valueOf(midBit))) {
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
		BigDecimal winningRate = calWinningRate(3);
		BigDecimal singleBettingPrize = calSingleBettingPrize(prizePattern, winningRate);
		String[] betNumSet = null;
		int betTotal = 1;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		
		betNumSet = betNum.split(";");
		for(String subBetNum : betNumSet) {
			int len = subBetNum.length();
			betTotal *= MathUtil.combination(1, len);
		}
		
		betAmount = MathUtil.multiply(betTotal, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit.floatValue(), Float.class);
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
		String[] betNumMul= null;		
		
		betNum = order.getBetNum();
		
		if(StringUtils.isBlank(betNum)) {
			return false;
		}
		
		betNumMul = betNum.split(";");
		
		for(String betNumTemp : betNumMul) {
			if(StringUtils.isBlank(betNumTemp)) {
				return false;
			}
			
			if(betNumTemp.length() > 7
					|| !Utils.validateNum(betNumTemp)) {
				return false;
			}
		}
				
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
		Integer[] betNumSetList = new Integer[5];
		String midBit = null;
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		//winNum = winNum.substring(0, 5);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		for(int i = 0; i < winNumSet.length; i++) {
			String winNumBit = winNumSet[i];
			Integer val = Integer.valueOf(winNumBit);
			betNumSetList[i] = val;
		}
		
		Arrays.sort(betNumSetList, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				Integer val1 = (Integer)o1;
				Integer val2 = (Integer)o2;
				return val1.intValue() - val2.intValue();
			}
			
		});
		
		midBit = String.valueOf(betNumSetList[2]);
		
		//1700 --- 1960
		Float prizePattern = userServ.calPrizePattern(user, issue.getLotteryType());
		BigDecimal winningRate = calWinningRate(3);
		singleBettingPrize =  calSingleBettingPrize(prizePattern, winningRate);
		
		
		for(String singleSel : betNumMul) {
			if(singleSel.contains(String.valueOf(midBit))) {
				winningBetAmount++;
			}
		}
		
		betAmount = MathUtil.multiply(winningBetAmount, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit, Float.class);
		maxWinAmount = MathUtil.multiply(betAmount, singleBettingPrize, Float.class);
		
		return new BigDecimal(maxWinAmount);
	}

	private BigDecimal calWinningRate(int oddCounter) {
		switch(oddCounter) {
			case 3:{
				BigDecimal winningRate = null;
				Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(2, 8)));
				Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 5)));
				Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 11)));
				
				tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
				tempVal = MathUtil.divide(tempVal, tempVal2, 4);
								
				winningRate = new BigDecimal(tempVal);
				return winningRate;
			}
			case 4:{
				BigDecimal winningRate = null;
				//BigDecimal winCount = new BigDecimal(3);
				//BigDecimal totalCount = null;
				Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(2, 3)));
				Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 5)));
				Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 11)));
				Double tempVal3 = Double.parseDouble(Long.toString(MathUtil.combination(2, 7)));
				
				tempVal = MathUtil.multiply(tempVal, tempVal3, Double.class);
				tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
				tempVal = MathUtil.divide(tempVal, tempVal2, 4);
								
				winningRate = new BigDecimal(tempVal);
				return winningRate;
			}
			
			case 5:{
				BigDecimal winningRate = null;
				//BigDecimal winCount = new BigDecimal(3);
				//BigDecimal totalCount = null;
				Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(2, 4)));
				Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 5)));
				Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 11)));
				Double tempVal3 = Double.parseDouble(Long.toString(MathUtil.combination(2, 6)));
				
				tempVal = MathUtil.multiply(tempVal, tempVal3, Double.class);
				tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
				tempVal = MathUtil.divide(tempVal, tempVal2, 4);
								
				winningRate = new BigDecimal(tempVal);
				return winningRate;
			}
			
			case 6:{
				BigDecimal winningRate = null;
				//BigDecimal winCount = new BigDecimal(3);
				//BigDecimal totalCount = null;
				Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(2, 5)));
				Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 5)));
				Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 11)));
				Double tempVal3 = Double.parseDouble(Long.toString(MathUtil.combination(2, 5)));
				
				tempVal = MathUtil.multiply(tempVal, tempVal3, Double.class);
				tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
				tempVal = MathUtil.divide(tempVal, tempVal2, 4);
								
				winningRate = new BigDecimal(tempVal);
				return winningRate;
			}
			
			case 7:{
				BigDecimal winningRate = null;
				//BigDecimal winCount = new BigDecimal(3);
				//BigDecimal totalCount = null;
				Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(2, 6)));
				Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 5)));
				Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 11)));
				Double tempVal3 = Double.parseDouble(Long.toString(MathUtil.combination(2, 4)));
				
				tempVal = MathUtil.multiply(tempVal, tempVal3, Double.class);
				tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
				tempVal = MathUtil.divide(tempVal, tempVal2, 4);
								
				winningRate = new BigDecimal(tempVal);
				return winningRate;
			}
			
			case 8:{
				BigDecimal winningRate = null;
				//BigDecimal winCount = new BigDecimal(3);
				//BigDecimal totalCount = null;
				Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(2, 7)));
				Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 5)));
				Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 11)));
				Double tempVal3 = Double.parseDouble(Long.toString(MathUtil.combination(2, 3)));
				
				tempVal = MathUtil.multiply(tempVal, tempVal3, Double.class);
				tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
				tempVal = MathUtil.divide(tempVal, tempVal2, 4);
								
				winningRate = new BigDecimal(tempVal);
				return winningRate;
			}
			case 9:{
				BigDecimal winningRate = null;
				Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(2, 8)));
				Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 5)));
				Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 11)));
				
				tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
				tempVal = MathUtil.divide(tempVal, tempVal2, 4);
								
				winningRate = new BigDecimal(tempVal);
				return winningRate;
			}
			default:{
				return null;
			}
		}
		
		
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
}

package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.jll.common.constants.Constants;
import com.jll.common.utils.MathUtil;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;

public class HszuxHhzxPlayTypeFacadeImpl  extends DefaultPlayTypeFacadeImpl  {

	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	private String playTypeDesc = "hszux|后三组选/hhzxds";
	
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
		winNum = winNum.substring(4, 9);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");

		logger.debug("proceed bet number is :: " + Arrays.asList(betNumSet));

		for (String temp : betNumMul) {
			if (temp.contains(winNumSet[0]) 
					&& temp.contains(winNumSet[1]) 
					&& temp.contains(winNumSet[2])) {
				if(isZxZs(temp)) {
					if(isPatternMath(temp, winNum.replace(",", ""))) {						
						return true;
					}
				}else if(isZxZl(temp)){
					if(isPatternMath(temp, winNum.replace(",", ""))) {
						return true;		
					}
				}
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
			boolean isZsBettingNum = isZxZs(tempBettingNum);
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
			
			if(!isZxZs(betNumTemp) && !isZxZl(betNumTemp)) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public Map<String, Object> calPrize(Issue issue, OrderInfo order, UserInfo user) {
		Map<String, Object> ret = new HashMap<String, Object>();
		// 开奖号码的每一位
		String[] winNumSet = null;
		// 每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul = null;
		String betNum = null;
		String winNum = null;
		Float singleBetAmountZs = 0F;
		Float singleBetAmountZl = 0F;
		Float maxWinAmount = 0F;
		Float maxWinAmountZs = 0F;
		Float maxWinAmountZl = 0F;
		int winningBetAmount = 0;
		int winningBetAmountZs = 0;
		int winningBetAmountZl = 0;
		Float betAmount = 0F;
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
		winNum = winNum.substring(4, 9);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");	
		singleBetAmountZs = MathUtil.multiply(1, times, Float.class);
		singleBetAmountZs = MathUtil.multiply(singleBetAmountZs, monUnit, Float.class);
		singleBetAmountZs = MathUtil.multiply(singleBetAmountZs, singleBettingPrizeZs.floatValue(), Float.class);
		
		singleBetAmountZl = MathUtil.multiply(1, times, Float.class);
		singleBetAmountZl = MathUtil.multiply(singleBetAmountZl, monUnit, Float.class);
		singleBetAmountZl = MathUtil.multiply(singleBetAmountZl, singleBettingPrizeZl.floatValue(), Float.class);
		
		
		for(String temp : betNumMul) {
			if(temp.contains(winNumSet[0]) 
					&& temp.contains(winNumSet[1])
					&& temp.contains(winNumSet[2])) {				
				if(isZxZs(temp)) {
					if(isPatternMath(temp, winNum.replace(",", ""))) {
						winningBetAmountZs++;
					}
					
				}else if(isZxZl(temp)){
					if(isPatternMath(temp, winNum.replace(",", ""))) {
						winningBetAmountZl++;
					}
				}
			}
		}		
		
		winningBetAmount = winningBetAmountZl + winningBetAmountZs;
		maxWinAmountZs = MathUtil.multiply(singleBetAmountZs, winningBetAmountZs, Float.class);
		maxWinAmountZl = MathUtil.multiply(singleBetAmountZl, winningBetAmountZl, Float.class);
		
		maxWinAmount = MathUtil.add(maxWinAmountZs, maxWinAmountZl, Float.class);
		
		ret.put(Constants.KEY_WINNING_BET_TOTAL, winningBetAmount);
		ret.put(Constants.KEY_WIN_AMOUNT, maxWinAmount);
		ret.put(Constants.KEY_SINGLE_BETTING_PRIZE, 
				winningBetAmountZs > 0?
						singleBettingPrizeZs : singleBettingPrizeZl);
		
		return ret;
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
	
	@Override
	public List<Map<String, String>> parseBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		StringBuffer buffer = new StringBuffer();
		Map<String, Integer> threeBits = new HashMap<>();
		StringBuffer matchBuffer = new StringBuffer();
		
		for(String singleBetNumArray : betNumArray) {
			for(int i = 0; i < 10; i++) {
				for(int ii = 0; ii < 10;ii++){
					for(int iii = 0; iii < 10;iii++){
						
						for(int iiii = 0; iiii < 10;iiii++){
							for(int iiiii = 0; iiiii < 10;iiiii++){
								buffer.delete(0, buffer.length());
								threeBits.clear();
								matchBuffer.delete(0, matchBuffer.length());
								
								matchBuffer.append(iii).append(iiii).append(iiiii);
								if(threeBits.get(String.valueOf(iiii)) == null) {
									threeBits.put(String.valueOf(iiii), 1);
								}else {
									Integer val = threeBits.get(String.valueOf(iiii));
									threeBits.put(String.valueOf(iiii), val.intValue() + 1);
								}
								
								if(threeBits.get(String.valueOf(iiiii)) == null) {
									threeBits.put(String.valueOf(iiiii), 1);
								}else {
									Integer val = threeBits.get(String.valueOf(iiiii));
									threeBits.put(String.valueOf(iiiii), val.intValue() + 1);
								}
								
								if(threeBits.get(String.valueOf(iii)) == null) {
									threeBits.put(String.valueOf(iii), 1);
								}else {
									Integer val = threeBits.get(String.valueOf(iii));
									threeBits.put(String.valueOf(iii), val.intValue() + 1);
								}
								
								if(threeBits.size() == 1
										|| !isPatternMath(matchBuffer.toString(), singleBetNumArray)) {
									continue;
								}
								
								buffer.append(i).append(ii).append(iii).append(iiii).append(iiiii);
								
								
								Map<String, String> row = new HashMap<String, String>();
								row.put(Constants.KEY_FACADE_BET_NUM, buffer.toString());
								row.put(Constants.KEY_FACADE_PATTERN, buffer.toString());
								row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, buffer.toString());
								betNumList.add(row);
								
							}
							
						}
					}
				}
			}
		}
		
		return betNumList;
	}

	
	private List<Map<String, String>> parseHszuxZLBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		List<String[]> arrangements = new ArrayList<>();
		
		for(String singleBetNumArray : betNumArray) {
			String[] betNumBits = new String[singleBetNumArray.length()];
			for(int i = 0; i < singleBetNumArray.length(); i++) {
				betNumBits[i] = singleBetNumArray.substring(i, i + 1);
			}
			List<String[]> combinations = new ArrayList<>();
			MathUtil.combinationSelect(betNumBits, 3, combinations);
			
			
			for(String[] combination : combinations) {
				MathUtil.arrangementSelect(combination, 3, arrangements);				
			}
			
			
			for(String[] temp : arrangements) {
				StringBuffer buffer = new StringBuffer();
				for(String tempBit : temp) {
					buffer.append(tempBit);
				}
				
				//buffer.append("**");
				String tempStr = buffer.toString();
				if(!isExisting(betNumList, tempStr)) {
					Map<String, String> row = new HashMap<String, String>();
					row.put(Constants.KEY_FACADE_BET_NUM, tempStr);
					row.put(Constants.KEY_FACADE_PATTERN, "[0-9]{2}" + tempStr);
					row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, "00" + tempStr);				
					betNumList.add(row);
					//betNumList.add(tempStr);
				}
			}
		}
		
		return betNumList;
	}
	
	private boolean isExisting(List<Map<String, String>> betNumList, String tempStr) {
		for(Map<String, String> temp : betNumList) {
			String betNum = temp.get(Constants.KEY_FACADE_BET_NUM);
			if(StringUtils.isBlank(betNum)) {
				return false;
			}
			
			if(betNum.equals(tempStr)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String obtainSampleBetNumber(){
		Random random = new Random();
		StringBuffer betNum = new StringBuffer();
		
		int bit = random.nextInt(10);
		int bit2 = -1;
		//拼装组三号码
		betNum.append(Integer.toString(bit));
		betNum.append(Integer.toString(bit));
		while(true) {
			bit2 = random.nextInt(10);
			if(bit != bit2) {
				betNum.append(Integer.toString(bit2));
				break;
			}
		}
				
		return betNum.toString();
	}
	
	/**
	 * 是否组选组三
	 * @param singleBetNumArray
	 * @return
	 */
	private boolean isZxZs(String singleBetNumArray) {
		Map<String,String> betNumBits = new HashMap<>();
		for(int i = 0; i < singleBetNumArray.length(); i++) {
			String bit = singleBetNumArray.substring(i, i + 1);
			betNumBits.put(bit, bit);
		}
		
		if(betNumBits.size() == 2) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 是否组选组三
	 * @param singleBetNumArray
	 * @return
	 */
	private boolean isZxZl(String singleBetNumArray) {
		Map<String,String> betNumBits = new HashMap<>();
		for(int i = 0; i < singleBetNumArray.length(); i++) {
			String bit = singleBetNumArray.substring(i, i + 1);
			betNumBits.put(bit, bit);
		}
		
		if(betNumBits.size() == 3) {
			return true;
		}
		
		return false;
	}
	
	private boolean isPatternMath(String matchBetNum, 
			String betNum) {
		Map<String,Integer> betNumBits = new HashMap<>();
		Map<String,Integer> matchBits = new HashMap<>();
		
		if(matchBetNum.length() != betNum.length()) {
			return false;
		}
		
		for(int i = 0; i < matchBetNum.length(); i++) {
			String bit = betNum.substring(i, i + 1);
			String matchBit = matchBetNum.substring(i, i + 1);
			if(betNumBits.get(bit) == null) {
				betNumBits.put(bit, 1);
			}else {
				Integer val = betNumBits.get(bit);
				betNumBits.put(bit, val.intValue() + 1);
			}
			
			if(matchBits.get(matchBit) == null) {
				matchBits.put(matchBit, 1);
			}else {
				Integer val = matchBits.get(matchBit);
				matchBits.put(matchBit, val.intValue() + 1);
			}
		}
		
		if(betNumBits.size() != matchBits.size()) {
			return false;
		}
		
		Iterator<String> keys = matchBits.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			Integer val = matchBits.get(key);
			if(betNumBits.get(key) == null 
					|| betNumBits.get(key).intValue() != val.intValue()) {
				return false;
			}
		}
		
		return true;
	}
}

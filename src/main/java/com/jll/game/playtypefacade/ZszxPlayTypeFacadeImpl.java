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
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;

public class ZszxPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "zszx|中三直选/fs";
	
	private String betNumOptions = "0,1,2,3,4,5,6,7,8,9";
	
	private String[] optionsArray = {"0","1","2","3","4","5","6","7","8","9"};
	
	@Override
	public String getPlayTypeDesc() {
		return playTypeDesc;
	}
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		//开奖号码的每一位
		String[] winNumSet = null;
		//每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul= null;
		String betNum = null;
		String winNum = null;
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(2,7);
		winNumSet = winNum.split(",");
		//betNumSet = betNum.split(",");
		betNumMul = betNum.split(";");
		
		for(String temp : betNumMul) {
			String[] tempSet = temp.split(",");
			if(tempSet[0].contains(winNumSet[0])
					&& tempSet[1].contains(winNumSet[1])
					&& tempSet[2].contains(winNumSet[2])) {
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
		int betTotal = 0;
		int singleBetTotal = 1;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		int winBetTotal = 0;
		
		betNumSet = betNum.split(";");
		for(String singleBetNum : betNumSet) {
			String[] betNumBits = singleBetNum.split(",");
			
			singleBetTotal = 1;
			for(String betNumBit : betNumBits) {
				int len = betNumBit.length();
				singleBetTotal *= MathUtil.combination(1, len);
			}
			
			betTotal += singleBetTotal;
			winBetTotal++;
		}
		
		betAmount = MathUtil.multiply(betTotal, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit, Float.class);
		
		maxWinAmount = MathUtil.multiply(winBetTotal, 
				times, 
				Float.class);
		maxWinAmount = MathUtil.multiply(maxWinAmount, 
				monUnit, 
				Float.class);
		maxWinAmount = MathUtil.multiply(maxWinAmount, 
				singleBettingPrize.floatValue(), 
				Float.class);
		
		ret.put("playType", playType);
		ret.put("betAmount", betAmount);
		ret.put("maxWinAmount", maxWinAmount);
		ret.put("betTotal", betTotal);
		ret.put("singleBettingPrize", singleBettingPrize);
		return ret;
	}

	@Override
	public boolean validBetNum(OrderInfo order) {
		String betNum = null;
		String[] betNumSet = null;
		Map<String, String> allBetNumBit = new HashMap<>();
		
		betNum = order.getBetNum();
		if(StringUtils.isBlank(betNum)) {
			return false;
		}
		
		betNumSet = betNum.split(";");
		for(String singleBetNum : betNumSet) {
			
			String[] betNumMulTempSet = singleBetNum.split(",");
			if(betNumMulTempSet == null 
					|| betNumMulTempSet.length != 3) {
				return false;
			}
			
			for(String betNumMulTempBit : betNumMulTempSet) {
								
				allBetNumBit.clear();
				
				Map<String, String> tempBits = splitBetNum(betNumMulTempBit);
				if(tempBits.size() == 0
						|| tempBits.size() > 10
						|| tempBits.size() != betNumMulTempBit.length()) {
					return false;
				}
				
				Iterator<String> ite = tempBits.keySet().iterator();
				while(ite.hasNext()) {
					String key = ite.next();
					if(!betNumOptions.contains(key)) {
						return false;
					}
					
					if(allBetNumBit.containsKey(key)) {
						return false;
					}
					
					allBetNumBit.put(key, key);
				}
			}
		}
		
		return true;
	}

	@Override
	public Map<String, Object> calPrize(Issue issue, OrderInfo order, UserInfo user) {
		Map<String, Object> ret = new HashMap<String, Object>();
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
		winNum = winNum.substring(2, 7);
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
		betAmount = MathUtil.multiply(betAmount, monUnit.floatValue(), Float.class);
		maxWinAmount = MathUtil.multiply(betAmount, singleBettingPrize.floatValue(), Float.class);
		
		ret.put(Constants.KEY_WINNING_BET_TOTAL, winningBetAmount);
		ret.put(Constants.KEY_WIN_AMOUNT, maxWinAmount);
		ret.put(Constants.KEY_SINGLE_BETTING_PRIZE, singleBettingPrize);
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.jll.game.playtype.PlayTypeFacade#calWinningRate()
	 * 
	 * 1/1000
	 */
	@Override
	public BigDecimal calWinningRate() {
		BigDecimal winningRate = null;
		BigDecimal winCount = new BigDecimal(1);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
		
		tempVal = Math.pow(tempVal, 3);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount, 4, BigDecimal.ROUND_HALF_UP);
		return winningRate;
	}

	@Override
	public List<Map<String, String>> parseBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		StringBuffer buffer = new StringBuffer();
		//boolean isMatch1 = false;
		boolean isMatch2 = false;
		boolean isMatch3 = false;
		boolean isMatch4 = false;
		/*boolean isMatch5 = false;*/
		
		for(String singleBetNumArray : betNumArray) {
			String[] betNumBits = splitBit(singleBetNumArray, 1);
			
			for(int i = 0; i < 10; i++) {				
				/*if(betNumBits[0].contains(String.valueOf(i))) {
					isMatch1 = true;
				}*/
				
				for(int ii = 0; ii < 10;ii++){
					if(betNumBits[0].contains(String.valueOf(ii))) {
						isMatch2 = true;
					}
					
					if(!isMatch2) {
						isMatch2 = false;
						continue;
					}
					
					for(int iii = 0; iii < 10;iii++){
						if(betNumBits[1].contains(String.valueOf(iii))) {
							isMatch3 = true;
						}
						
						if(!isMatch3) {
							isMatch3 = false;
							continue;
						}
						
						for(int iiii = 0; iiii < 10;iiii++){
							if(betNumBits[2].contains(String.valueOf(iiii))) {
								isMatch4 = true;
							}
							
							if(!isMatch4) {
								isMatch4 = false;
								continue;
							}
							
							
							for(int iiiii = 0; iiiii < 10;iiiii++){
								/*if(betNumBits[4].contains(String.valueOf(iiiii))) {
									isMatch5 = true;
								}*/
								
								buffer.delete(0, buffer.length());
								
								
								buffer.append(i).append(ii).append(iii).append(iiii).append(iiiii);
								
								
								Map<String, String> row = new HashMap<String, String>();
								row.put(Constants.KEY_FACADE_BET_NUM, buffer.toString());
								row.put(Constants.KEY_FACADE_PATTERN, buffer.toString());
								row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, buffer.toString());
								betNumList.add(row);
								
								//isMatch5 = false;
							}
							
							isMatch4 = false;
						}
						
						isMatch3 = false;
					}
					isMatch2 = false;
				}
				//isMatch1 = false;
			}
		}
		
		return betNumList;
	}
	
	@Override
	public String obtainSampleBetNumber(){
		Random random = new Random();
		StringBuffer betNum = new StringBuffer();
		StringBuffer betNums = new StringBuffer();
		StringBuffer bitNum = new StringBuffer();
		
		int betNumLen = random.nextInt(5) + 1;
		for(int a = 0; a < betNumLen; a++) {
			for(int i = 0 ; i < 3; i++) {				
				int bitLen = random.nextInt(6) + 1;
				
				for(int ii = 0; ii < bitLen;) {
					int bit = random.nextInt(10);
					if(bitNum.toString().contains(optionsArray[bit])) {
						continue;
					}
					
					bitNum.append(optionsArray[bit]);
					ii++;
				}
				
				betNum.append(bitNum.toString()).append(",");
				
				bitNum.delete(0, bitNum.length());
			}
			
			betNum.delete(betNum.length()-1, betNum.length());
			
			betNums.append(betNum.toString()).append(";");
			
			betNum.delete(0, betNum.length());
		}
		
		betNums.delete(betNums.length()-1, betNums.length());
		
		return betNums.toString();
	}
	
	private String[] splitBit(String singleSel, int step) {
		List<String> retList = new ArrayList<>();
		StringBuffer buffer = new StringBuffer();
		
		for(int i = 0; i < singleSel.length();) {
			String temp = singleSel.substring(i, i + step);
			if(",".equals(temp)) {
				retList.add(buffer.toString());
				buffer.delete(0, buffer.length());
			}else {
				buffer.append(temp);
			}
			
			i += step;
			
			if(i >= singleSel.length()) {
				retList.add(buffer.toString());
				buffer.delete(0, buffer.length());
			}
		}
		
		return retList.toArray(new String[0]);
	}
	
	private Map<String, String> splitBetNum(String temp) {
		Map<String, String> bits = new HashMap<String, String>();
				
		for(int i = 0; i < temp.length();) {
			String bit = temp.substring(i, i + 1);
			bits.put(bit, bit);
			i += 1;
		}
		
		return bits;
	}
}

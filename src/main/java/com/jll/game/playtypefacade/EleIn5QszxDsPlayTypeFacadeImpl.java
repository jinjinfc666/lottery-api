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

/**
 * 广东11 选 5
 * @author Administrator
 *
 */
public class EleIn5QszxDsPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(EleIn5QszxDsPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "sm|三码/qszx|前三直选/ds";
	
	private String betNumOptions = "01,02,03,04,05,06,07,08,09,10,11";
	
	String[] optionsArray = {"01","02","03","04","05","06","07","08","09","10","11"};
	
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
		winNum = winNum.substring(0,8);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		//logger.debug("proced bet number is :: " + Arrays.asList(betNumSet));
		
		for(String temp : betNumMul) {
			String[] tempSet = temp.replace(" ", ",").split(",");
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
		//String[] betNumSet = null;
		int betTotal = 1;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		
		/*betNumSet = betNum.split(",");*/
		/*for(String subBetNum : betNumSet) {
			int len = subBetNum.length() / 2;
			betTotal *= MathUtil.combination(1, len);  
			
			List<Map<String, String>> totalNum = parseBetNumber(subBetNum);
		}*/
		
		betNum = betNum.replaceAll(" ", ",");
		betTotal = calBetTotal(betNum);
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
		String[] betNumMul= null;
		Map<String, String> allBetNumBit = new HashMap<>();
		int betTotal = 0;
		
		betNum = order.getBetNum();
		if(StringUtils.isBlank(betNum)) {
			return false;
		}
		
		betNum = betNum.replaceAll(" ", ",");
		betNumMul = betNum.split(";");
		for(String betNumMulTemp : betNumMul) {
			if(StringUtils.isBlank(betNumMulTemp)) {
				return false;
			}
			
			String[] betNumMulTempSet = betNumMulTemp.split(",");
			if(betNumMulTempSet == null 
					|| betNumMulTempSet.length != 3) {
				return false;
			}
			
			for(String betNumMulTempBit : betNumMulTempSet) {
				if(StringUtils.isBlank(betNumMulTempBit)) {
					return false;
				}
				
				allBetNumBit.clear();
				
				Map<String, String> tempBits = splitBetNum(betNumMulTempBit);
				if(tempBits.size() == 0
						|| tempBits.size() > 11
						|| !Utils.validateEleIn5Num(betNumMulTempBit)
						|| tempBits.size() != (betNumMulTempBit.length() / 2)) {
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
			
			betTotal = calBetTotal(betNumMulTemp);
			if(betTotal == 0) {
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
		winNum = winNum.substring(0, 8);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		
		for(String singleSel : betNumMul) {
			singleSel = singleSel.replace(" ", ",");
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
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 11)));
		Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
		Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.combination(1, 9)));
		
		tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
		tempVal = MathUtil.multiply(tempVal, tempVal2, Double.class);
		
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount,3, BigDecimal.ROUND_HALF_UP);
		return winningRate;
	}
	
	@Override
	public List<Map<String, String>> parseBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		StringBuffer buffer = new StringBuffer();
		boolean isMatch1 = false;
		boolean isMatch2 = false;
		boolean isMatch3 = false;
		/*boolean isMatch4 = false;
		boolean isMatch5 = false;*/
		Map<String, String> threeBits = new HashMap<>();
		
		for(String singleBetNumArray : betNumArray) {
			singleBetNumArray = singleBetNumArray.replaceAll(" ", ",");
			String[] betNumBits = splitBit(singleBetNumArray, 1);
			
			for(int i = 0; i < 11; i++) {
				if(betNumBits[0].contains(optionsArray[i])) {
					isMatch1 = true;
				}
				
				if(!isMatch1) {
					isMatch1 = false;
					continue;
				}
				
				for(int ii = 0; ii < 11;ii++){
					if(betNumBits[1].contains(optionsArray[ii])) {
						isMatch2 = true;
					}
					
					if(!isMatch2) {
						isMatch2 = false;
						continue;
					}
					
					for(int iii = 0; iii < 11;iii++){
						if(betNumBits[2].contains(optionsArray[iii])) {
							isMatch3 = true;
						}
						
						if(!isMatch3) {
							isMatch3 = false;
							continue;
						}
						
						for(int iiii = 0; iiii < 11;iiii++){
							/*if(betNumBits[3].contains(String.valueOf(iiii))) {
								isMatch4 = true;
							}*/
							for(int iiiii = 0; iiiii < 11;iiiii++){
								/*if(betNumBits[4].contains(String.valueOf(iiiii))) {
									isMatch5 = true;
								}*/
								
								threeBits.clear();
								
								threeBits.put(optionsArray[i], optionsArray[i]);
								threeBits.put(optionsArray[ii], optionsArray[ii]);
								threeBits.put(optionsArray[iii], optionsArray[iii]);
								threeBits.put(optionsArray[iiii], optionsArray[iiii]);
								threeBits.put(optionsArray[iiiii], optionsArray[iiiii]);
								
								if(threeBits.size() != 5) {
									continue;
								}
								
								/*if(!isPatternMatch(threeBits, singleBetNumArray.replaceAll(",", ""))) {
									continue;
								}*/
								
								buffer.delete(0, buffer.length());
								
								
								buffer.append(optionsArray[i])
								.append(optionsArray[ii])
								.append(optionsArray[iii])
								.append(optionsArray[iiii])
								.append(optionsArray[iiiii]);
								
								
								Map<String, String> row = new HashMap<String, String>();
								row.put(Constants.KEY_FACADE_BET_NUM, buffer.toString());
								row.put(Constants.KEY_FACADE_PATTERN, buffer.toString());
								row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, buffer.toString());
								betNumList.add(row);
								
								//isMatch5 = false;
							}
							
							//isMatch4 = false;
						}
						
						isMatch3 = false;
					}
					isMatch2 = false;
				}
				isMatch1 = false;
			}
		}
		
		return betNumList;
	}
	
	
	private int calBetTotal(String betNum){
		/*List<Map<String, String>> betNumList = new ArrayList<>();*/
		String[] betNumArray = betNum.split(";");
		/*List<String[]> excludingResults = null;
		int excludingCounter = 2;*/
		/*List<Map<String, String>> betNumCombinations = new ArrayList<>();
		Map<String, String> betNumCombination = new HashMap<>();*/
		Map<String, String> first3Bits = null;
		int result = 0;
		for(String singleBetNumArray : betNumArray) {
			singleBetNumArray = singleBetNumArray.replaceAll(" ", ",");
			String[] betNumBits = singleBetNumArray.split(",");
			
			for(int i = 0 ; i < betNumBits[0].length();) {
				String a = betNumBits[0].substring(i, i + 2);
				for(int ii = 0; ii < betNumBits[1].length();) {
					String aa = betNumBits[1].substring(ii, ii + 2);
					
					ii += 2;
					
					if(aa.equals(a)) {
						continue;
					}
					
					for(int iii = 0; iii < betNumBits[2].length();) {
						String aaa = betNumBits[2].substring(iii, iii + 2);
						StringBuffer buffer = new StringBuffer();
						buffer.append(a).append(aa).append(aaa);
						
						first3Bits = new HashMap<>();
						first3Bits.put(a, a);
						first3Bits.put(aaa, aaa);
						first3Bits.put(aa, aa);
						//忽略包含相同数字的号码
						iii += 2;
						if(first3Bits.size() != 3) {
							continue;
						}
						
						result++;
											
					}
					
					 
				}
				
				i += 2;
			}
		}
		
		return result;
	}
	
	
	@Override
	public String obtainSampleBetNumber() {
		Random random = new Random();
		StringBuffer betNum = new StringBuffer();
		StringBuffer bitBetBum = new StringBuffer();
		int betNums = random.nextInt(5) + 1;

		for (int a = 0; a < betNums; a++) {
			for (int i = 0; i < 3; i++) {
				while (true) {
					int bit = random.nextInt(11);
					String bitStr = optionsArray[bit];
					if (bitBetBum.toString().contains(bitStr)) {
						continue;
					}

					bitBetBum.append(bitStr).append(" ");
					break;
				}

				
				//bitBetBum.delete(0, bitBetBum.length());
			}
			bitBetBum.delete(bitBetBum.length() - 1, bitBetBum.length());
			
			betNum.append(bitBetBum).append(";");
			
			//betNum.delete(betNum.length() - 1, betNum.length());
			
			bitBetBum.delete(0, bitBetBum.length());
		}

		betNum.delete(betNum.length() - 1, betNum.length());

		return betNum.toString();
	}
	
	private Map<String, String> splitBetNum(String temp) {
		Map<String, String> bits = new HashMap<String, String>();
		int len = temp.length();
		
		if(len % 2 != 0) {
			return bits;
		}
		
		for(int i = 0; i < temp.length();) {
			String bit = temp.substring(i, i + 2);
			bits.put(bit, bit);
			i += 2;
		}
		
		return bits;
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
}

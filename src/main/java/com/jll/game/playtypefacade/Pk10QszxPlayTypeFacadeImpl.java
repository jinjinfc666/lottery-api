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

public class Pk10QszxPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(Pk10QszxPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "qsfs|前三复式/fs";
	
	private String betNumOptions = "01,02,03,04,05,06,07,08,09,10";
	
	String[] optionsArray = {"01","02","03","04","05","06","07","08","09","10"};
	
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
		//winNum = winNum.substring(0,3);
		winNumSet = winNum.split(",");
		//betNumSet = betNum.split(",");
		betNumMul = betNum.split(";");
		
		for(String temp : betNumMul) {
			String[] singleBits = temp.split(",");
			int winCounter = 0;
			
			for(int i = 0; i < singleBits.length; i++) {
				
				Map<String, String> tempBits = splitBetNum(singleBits[i]);
				try {
					if(tempBits.get(winNumSet[i]) != null) {
						winCounter++;
					}
					
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			
			if(winCounter == singleBits.length) {
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
		int betTotal = 0;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		
		betTotal = calBetTotal(betNum);
		
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
		String[] betNumSet = null;
		String[] betNumMul = null;
		Map<String, String> allBetNumBit = new HashMap<>();
		int betTotal = 0;
		
		betNum = order.getBetNum();
		if(StringUtils.isBlank(betNum)) {
			return false;
		}
		
		betNumMul = betNum.split(";");
		for(String betNumTemp : betNumMul) {
			betNumSet = betNumTemp.split(",");
			if(betNumSet == null || betNumSet.length != 3) {
				return false;
			}
			
			for(String betNumBit : betNumSet) {
				Map<String, String> tempBits = splitBetNum(betNumBit);
				allBetNumBit.clear();
				
				if(tempBits.size() < 1
						|| tempBits.size() > 10
						|| tempBits.size() != (betNumBit.length() / 2)) {
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
		
		betTotal = calBetTotal(betNum);
		if(betTotal == 0) {
			return false;
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
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		
		for(String singleSel : betNumMul) {
			String[] singleBits = singleSel.split(",");
			int winCounter = 0;
			for(int i = 0; i < singleBits.length; i++) {
				
				Map<String, String> tempBits = splitBetNum(singleBits[i]);
				
				if(tempBits.get(winNumSet[i]) != null) {
					winCounter++;
				}
			}
			
			if(winCounter == singleBits.length) {
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
		Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.combination(1, 9)));
		Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.combination(1, 8)));
		
		tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
		tempVal = MathUtil.multiply(tempVal, tempVal2, Double.class);
		
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount, 5, BigDecimal.ROUND_HALF_UP);
		return winningRate;
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
	
	@Override
	public List<Map<String, String>> parseBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		List<String[]> excludingResults = null;
		int excludingCounter = 7;
		List<Map<String, String>> betNumCombinations = new ArrayList<>();
		Map<String, String> betNumCombination = new HashMap<>();
		Map<String, String> first3Bits = null;
		StringBuffer buffer = null;
		StringBuffer betNumBuffer = null;
		
		for(String singleBetNumArray : betNumArray) {
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
						//StringBuffer buffer = new StringBuffer();
						
						first3Bits = new HashMap<>();
						first3Bits.put(a, a);
						first3Bits.put(aaa, aaa);
						first3Bits.put(aa, aa);
						
						iii += 2;
						//忽略包含相同数字的号码
						if(first3Bits.size() != 3) {
							continue;
						}
						
						String[] excludingArray = obtainExcludingArray(new String[] {a,aa,aaa});
						excludingResults = new ArrayList<String[]>();
						try {					
							MathUtil.combinationSelect(excludingArray, excludingCounter, excludingResults);
							
							
							for(String[] excludingResult : excludingResults) {
								buffer = new StringBuffer();
								betNumBuffer = new StringBuffer();
								
								buffer.append(a).append(aa).append(aaa);
								betNumBuffer.append(a).append(",").append(aa).append(",").append(aaa).append(",");
								
								betNumCombination = new HashMap<>();
								betNumCombination.put(a, a);
								betNumCombination.put(aaa, aaa);
								betNumCombination.put(aa, aa);
								for(String bit : excludingResult) {
									buffer.append(bit);
									betNumBuffer.append(bit).append(",");
									
									betNumCombination.put(bit, bit);
								}
								
								betNumBuffer.delete(betNumBuffer.length() - 1, betNumBuffer.length());
								
								if(isBetNumCombinationExisting(betNumCombinations, betNumCombination)) {
									continue;
								}
								
								betNumCombinations.add(betNumCombination);
								Map<String, String> row = new HashMap<String, String>();
								row.put(Constants.KEY_FACADE_BET_NUM, betNumBuffer.toString());
								row.put(Constants.KEY_FACADE_PATTERN, buffer.toString());
								row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, buffer.toString());
								betNumList.add(row);
							}
						}catch(Exception ex) {
							return betNumList;
						}
					}
				}
				
				i += 2;
			}
		}
		
		return betNumList;
	}
	
	@Override
	public String obtainSampleBetNumber(){
		Random random = new Random();
		StringBuffer betNum = new StringBuffer();
		StringBuffer bitBetBum = new StringBuffer();
		
		for(int i = 0 ; i < 3; i++) {
			int bitLen = random.nextInt(5) + 1;
			int counter = 0;
			bitBetBum = new StringBuffer();
			while(counter < bitLen) {
				int bit = random.nextInt(10) + 1;
				while(true) {
					if(bit < 10) {
						if(bitBetBum.toString().contains("0" + Integer.toString(bit))) {
							bit = random.nextInt(10) + 1;
							continue;
						}
						bitBetBum.append("0").append(Integer.toString(bit));
						break;
					}else {
						if(bitBetBum.toString().contains(Integer.toString(bit))) {
							bit = random.nextInt(10) + 1;
							continue;
						}
						bitBetBum.append(Integer.toString(bit));
						break;
					}
				}
				
				counter++;
			}
			
			betNum.append(bitBetBum).append(",");
		}
		
		betNum.delete(betNum.length()-1, betNum.length());
		
		return betNum.toString();
	}
	
	private String[] obtainExcludingArray(String[] key) {
		String[] ret = new String[10 - key.length];
		int indx = 0 ;
		for(String temp : optionsArray) {
			boolean isSame = false;
			for(String keyTemp : key) {
				if(temp.equals(keyTemp)) {
					isSame = true;
					break;
				}
			}
			
			if(!isSame) {
				ret[indx++] = temp;
			}
		}
		return ret;
	}
	
	private boolean isBetNumCombinationExisting(List<Map<String, String>> betNumCombinations,
			Map<String, String> betNumCombination) {
		for(Map<String, String> temp : betNumCombinations) {
			int existingCounter = 0;
			Iterator<String> ite = temp.keySet().iterator();
			while(ite.hasNext()) {
				String key = ite.next();
				if(betNumCombination.get(key) != null) {
					existingCounter++;
				}
			}
			
			if(existingCounter == temp.size()) {
				return true;
			}
		}
		return false;
	}
	
	private List<String> arrangementSelect(String betNum) {
		List<String> ret = new ArrayList<>();
		List<String[]> results = new ArrayList<>();
		String[] selArray = new String[betNum.length() / 2];
		for(int i = 0, j = 0; i< betNum.length();j++) {
			String result = betNum.substring(i, i + 2);
			selArray[j] = result;
			
			i += 2;
		}
		MathUtil.arrangementSelect(selArray, selArray.length, results);
		
		for(String[] result : results) {
			StringBuffer buffer = new StringBuffer();
			for(String bit : result) {
				buffer.append(bit);
			}
			
			ret.add(buffer.toString());
		}
		return ret;
	}
	
	private int calBetTotal(String betNum){
		String[] betNumArray = betNum.split(";");
		Map<String, String> first3Bits = null;
		int result = 0;
		for(String singleBetNumArray : betNumArray) {
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
	
}

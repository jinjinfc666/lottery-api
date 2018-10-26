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

public class EleIn5Q2ZuxPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl  {

	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	private String playTypeDesc = "em|二码/qezux|前二组选/fs-ds";

	private String betNumOptions = "01,02,03,04,05,06,07,08,09,10,11";
	
	String[] optionsArray = {"01","02","03","04","05","06","07","08","09","10","11"};
	
	@Override
	public String getPlayTypeDesc() {
		return playTypeDesc;
	}
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		//开奖号码的每一位
		String[] winNumSet = null;
		//每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul = null;
		String betNum = null;
		String winNum = null;
				
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(0, 5);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		//logger.debug("proceed bet number is :: " + Arrays.asList(betNumSet));
		
		for(String temp : betNumMul) {
			if(temp.contains(winNumSet[0]) 
					&& temp.contains(winNumSet[1])) {
				if(!winNumSet[0].equals(winNumSet[1])) {
					return true;
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
		String lottoType = (String)params.get("lottoType");
		Float prizePattern = userServ.calPrizePattern(user, lottoType);
		BigDecimal winningRate = calWinningRate();
		BigDecimal singleBettingPrize = calSingleBettingPrize(prizePattern, winningRate);
		int betTotal = 1;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		
		
		int len = betNum.length() / 2;
		betTotal = (int)(MathUtil.combination(2, len));
		
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
			
			Map<String, String> tempBits = splitBetNum(betNumTemp);
			if(tempBits.size() < 2
					|| tempBits.size() > 11
					|| !Utils.validateEleIn5Num(betNumTemp)
					|| tempBits.size() != (betNumTemp.length() / 2)) {
				return false;
			}
			
			Iterator<String> ite = tempBits.keySet().iterator();
			while(ite.hasNext()) {
				String key = ite.next();
				if(!betNumOptions.contains(key)) {
					return false;
				}				
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
		winNum = winNum.substring(0, 5);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		
		for(String temp : betNumMul) {
			if(temp.contains(winNumSet[0]) 
					&& temp.contains(winNumSet[1])) {
				if(!winNumSet[0].equals(winNumSet[1])) {
					winningBetAmount++;
				}				
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
		BigDecimal winCount = new BigDecimal(2);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 11)));
		Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
		
		tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount, 4, BigDecimal.ROUND_HALF_UP);
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
		List<String[]> excludingResults = null;
		List<String[]> bitBetNum = null;
		int excludingCounter = 3;
		Map<String, String> betNumCombination = new HashMap<>();
		List<Map<String, String>> betNumCombinations = new ArrayList<>();		
		String[] betNumArray = betNum.split(";");
		
		for(String singleBetNumArray : betNumArray) {
			bitBetNum = combinationBetNum(singleBetNumArray, 2);
			for(String[] tempBitBetBum : bitBetNum) {
				String[] excludingArray = obtainExcludingArray(tempBitBetBum);
				excludingResults = new ArrayList<String[]>();
				try {					
					MathUtil.combinationSelect(excludingArray, excludingCounter, excludingResults);
					
					for(String[] excludingResult : excludingResults) {
						betNumCombination = new HashMap<>();
						StringBuffer buffer = new StringBuffer();
						
						for(String bit : tempBitBetBum) {
							buffer.append(bit);
							betNumCombination.put(bit, bit);
						}
						
						for(String bit : excludingResult) {
							buffer.append(bit);
							betNumCombination.put(bit, bit);
						}
						
						if(isBetNumCombinationExisting(betNumCombinations, betNumCombination)) {
							continue;
						}
						
						betNumCombinations.add(betNumCombination);
						List<String> arrangementSel = arrangementSelect(buffer.toString());
						for(String sel : arrangementSel) {
							Map<String, String> row = new HashMap<String, String>();
							row.put(Constants.KEY_FACADE_BET_NUM, sel);
							row.put(Constants.KEY_FACADE_PATTERN, sel);
							row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, sel);
							betNumList.add(row);
						}
					}
				}catch(Exception ex) {
					return betNumList;
				}
			}			
			
		}
		
		
		return betNumList;
	}
	
	@Override
	public String obtainSampleBetNumber(){
		
		Random random = new Random();
		StringBuffer betNum = new StringBuffer();
		
		int bitLen = random.nextInt(6) + 2;
		
		for(int i = 0 ; i < bitLen; i++) {
			int bitIndx = random.nextInt(11);
			
			while(true) {
				if(betNum.toString().contains(optionsArray[bitIndx])) {
					bitIndx = random.nextInt(11);
					continue;
				}
				betNum.append(optionsArray[bitIndx]);
				break;
			}
		}
		
		return betNum.toString();
	}
	
	private String[] obtainExcludingArray(String[] key) {
		String[] ret = new String[11 - key.length];
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
	
	private List<String[]> combinationBetNum(String betNum, int selCount) {
		String[] betNumArray = new String[betNum.length() / 2];
		List<String[]> ret = new ArrayList<>();
		
		if(StringUtils.isBlank(betNum)) {
			return ret;
		}
		
		for(int i = 0,j = 0; i < betNum.length();j++) {
			betNumArray[j] = betNum.substring(i, i + 2);
			
			i += 2;
		}
		
		MathUtil.combinationSelect(betNumArray, selCount, ret);
		
		return ret;
	}
}

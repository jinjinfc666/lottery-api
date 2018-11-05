package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
 * 龙      1 
 * 虎       0
 * @author Administrator
 *
 */
public class Pk10Dx1PlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(Pk10Dx1PlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "dx|大小/dym|第一名/fs-ds";
	
	private final int BIGGER = 1;
	
	private final int SMALLER = 0;
	
	private String betNumOptions = "00,01";
	
	private String bigStr = "0607080910";
	
	private String smallStr = "01020304";
	
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
		
		if(Integer.parseInt(winNumSet[0]) > 5) {
			winNumFinal = BIGGER;
		}else {
			winNumFinal = SMALLER;
		}
		
		for(String temp : betNumMul) {
			if(temp.contains("0" + String.valueOf(winNumFinal))) {
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
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		
		betNumSet = betNum.split(";");
		for(String subBetNum : betNumSet) {						
			int len = subBetNum.length() / 2;
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
							
			Map<String, String> tempBits = splitBetNum(temp);
			if(tempBits.size() < 1
					|| tempBits.size() > 2
					|| tempBits.size() != (temp.length() / 2)) {
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
		int winNumFinal = -1;
		
		//1700 --- 1960
		Float prizePattern = userServ.calPrizePattern(user, issue.getLotteryType());
		BigDecimal winningRate = calWinningRate();
		singleBettingPrize =  calSingleBettingPrize(prizePattern, winningRate);
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		//winNum = winNum.substring(0, 5);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		if(Integer.parseInt(winNumSet[0]) > 5) {
			winNumFinal = BIGGER;
		}else {
			winNumFinal = SMALLER;
		}
		
		for(String singleSel : betNumMul) {
			//betNumSet = singleSel.split(",");
			if(singleSel.contains("0" + String.valueOf(winNumFinal))) {
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
		BigDecimal winCount = null;
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 5)));
		Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
						
		winCount = new BigDecimal(tempVal);
		totalCount = new BigDecimal(tempVal1);
		winningRate = winCount.divide(totalCount, 5, BigDecimal.ROUND_HALF_UP);
		return winningRate;
	}
	
	@Override
	public String obtainSampleBetNumber(){
		String[] optionsArray = {"00","01"};
		Random random = new Random();
		StringBuffer betNum = new StringBuffer();
		
		int bitLen = random.nextInt(2) + 1;
		
		for(int i = 0 ; i < bitLen; i++) {
			int bitIndx = random.nextInt(2);
			
			while(true) {
				if(betNum.toString().contains(optionsArray[bitIndx])) {
					bitIndx = random.nextInt(2);
					continue;
				}
				betNum.append(optionsArray[bitIndx]);
				break;
			}
		}
		
		return betNum.toString();
	}
	
	
	private Map<String, String> splitBetNum(String temp) {
		Map<String, String> bits = new HashMap<String, String>();
		int len = temp.length();
						
		if(len % 2 != 0) {
			return bits;
		}
		
		for(int i = 0; i < len;) {
			String bit = temp.substring(i, i + 2);
			bits.put(bit, bit);
			
			i += 2;
		}
		
		return bits;
	}
	
	@Override
	public List<Map<String, String>> parseBetNumber(String betNum){
		Date currDate = new Date();
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		List<String[]> excludingResults = null;
		List<String[]> bitBetNum = null;
		int excludingCounter = 9;
		List<Map<String, String>> betNumCombinations = new ArrayList<>();
		Map<String, String> betNumCombination = new HashMap<>();
		Map<String, String> bitBetNumMap = null;
		
		for(String singleBetNumArray : betNumArray) {
			bitBetNumMap = splitBetNum(singleBetNumArray);
			Iterator<String> ite = bitBetNumMap.keySet().iterator();
			while(ite.hasNext()) {
				String key = ite.next();
				if(Integer.parseInt(key) == BIGGER) {
					bitBetNum = combinationBetNum(bigStr, 1);
				}else {
					bitBetNum = combinationBetNum(smallStr, 1);
				}
				
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
							Map<String, String> row = new HashMap<String, String>();
							row.put(Constants.KEY_FACADE_BET_NUM, buffer.toString());
							row.put(Constants.KEY_FACADE_PATTERN, buffer.toString());
							row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, buffer.toString());
							betNumList.add(row);
						}
					}catch(Exception ex) {
						return betNumList;
					}
				}
			}			
		}
		
		Date lastDate = new Date();
		logger.debug(String.format("totally take over  %s  MS, total records %s", 
				(lastDate.getTime() - currDate.getTime()),betNumList.size()));
		return betNumList;
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
	
	private String[] obtainExcludingArray(String[] key) {
		String[] optionsArray = {"01","02","03","04","05","06","07","08","09","10"};
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
				try {
				ret[indx++] = temp;
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	private List<String> arrangementSelect(String betNum) {
		List<String> ret = new LinkedList<>();
		List<String[]> results = new LinkedList<>();
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
}

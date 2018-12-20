package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
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

public class EleIn5QwZwPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(EleIn5QwZwPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "qwx|趣味型/qwczw|趣味猜中位/fs";
	
	private String betNumOptions = "03,04,05,06,07,08,09";
	
	String[] betNumOptionsArray = {"03","04","05","06","07","08","09"};
	
	String[] optionsArray = {"01","02","03","04","05","06","07","08","09","10","11"};
	
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
		
		//logger.debug("proced bet number is :: " + Arrays.asList(betNumSet));
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
			int len = subBetNum.length() / 2;
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
			
			Map<String, String> tempBits = splitBetNum(betNumTemp);
			if(tempBits.size() == 0
					|| tempBits.size() > 7
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
		BigDecimal winningRate = calWinningRate(betNumSetList[2]);
		singleBettingPrize =  calSingleBettingPrize(prizePattern, winningRate);
		
		
		for(String singleSel : betNumMul) {
			if(singleSel.contains(String.valueOf(midBit))) {
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

	private BigDecimal calWinningRate(int midBit) {
		switch(midBit) {
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
	
	@Override
	public String obtainSampleBetNumber(){
		Random random = new Random();
		StringBuffer betNum = new StringBuffer();
		
		int bitLen = random.nextInt(4) + 1;
		
		for(int i = 0 ; i < bitLen; i++) {
			int bitIndx = random.nextInt(7);
			
			while(true) {
				if(betNum.toString().contains(betNumOptionsArray[bitIndx])) {
					bitIndx = random.nextInt(7);
					continue;
				}
				betNum.append(betNumOptionsArray[bitIndx]);
				break;
			}
		}
		
		return betNum.toString();
	}
	
	@Override
	public List<Map<String, String>> parseBetNumber(String betNum){
		Date currDate = new Date();
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		Map<String, String> bitBetNum = null;
		Map<String, String> combineBitBetNum = new HashMap<String, String>();
		List<String[]> lowerResults = null;
		List<String[]> higherResults = null;
		
		for(String singleBetNumArray : betNumArray) {
			bitBetNum = splitBetNum(singleBetNumArray);
			Iterator<String> ite = bitBetNum.keySet().iterator();
			while(ite.hasNext()) {
				String key = ite.next();
				String[] lowerArray = obtainLowerArray(key);
				String[] higherArray = obtainHigherArray(key);
				
				try {
					int lowerCounter = 2;
					int higherCounter = 2;
					lowerResults = new ArrayList<String[]>();
					higherResults = new ArrayList<String[]>();
					MathUtil.combinationSelect(lowerArray, lowerCounter, lowerResults);
					MathUtil.combinationSelect(higherArray, higherCounter, higherResults);
					
					for(String[] lowerResult : lowerResults) {
						StringBuffer buffer = new StringBuffer();
						buffer.append(key);
						for(String temp : lowerResult) {
							buffer.append(temp);
						}
						
						for(String[] higherResult : higherResults) {
							StringBuffer buffer2 = new StringBuffer();
							buffer2.append(buffer.toString());
							for(String temp : higherResult) {
								buffer2.append(temp);
							}
							
							List<String> arrangementSel = arrangementSelect(buffer2.toString());
							for(String sel : arrangementSel) {
								combineBitBetNum.put(sel, sel);
							}
						}
					}
				}catch(Exception ex) {
					return betNumList;
				}
			}
			
			ite = combineBitBetNum.keySet().iterator();
			while(ite.hasNext()) {
				String key = ite.next();
				if(!isExisting(betNumList, key)) {
					Map<String, String> row = new HashMap<String, String>();
					row.put(Constants.KEY_FACADE_BET_NUM, key);
					row.put(Constants.KEY_FACADE_PATTERN, key);
					row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, key);
					betNumList.add(row);
				}
			}
		}
		
		Date lastDate = new Date();
		logger.debug(String.format("totally consume time  %s", (lastDate.getTime() - currDate.getTime())/1000));
		return betNumList;
	}
	
	private String[] obtainHigherArray(String key) {
		String[] ret = null;
		int index = 0;
		Map<String, String> lowers = new HashMap<>();
		Iterator<String> ite = null;
		
		for(String betNum : optionsArray) {
			if(Integer.parseInt(betNum) > Integer.parseInt(key)) {
				lowers.put(betNum, betNum);
			}
		}
		
		ret = new String[lowers.size()];
		ite = lowers.keySet().iterator();
		
		while(ite.hasNext()) {
			String betNum = ite.next();
			ret[index++] = betNum;
		}
		return ret;
	}

	private String[] obtainLowerArray(String key) {
		String[] ret = null;
		int index = 0;
		Map<String, String> lowers = new HashMap<>();
		Iterator<String> ite = null;
		
		for(String betNum : optionsArray) {
			if(Integer.parseInt(betNum) < Integer.parseInt(key)) {
				lowers.put(betNum, betNum);
			}
		}
		
		ret = new String[lowers.size()];
		ite = lowers.keySet().iterator();
		
		while(ite.hasNext()) {
			String betNum = ite.next();
			ret[index++] = betNum;
		}
		return ret;
	}

	private static boolean isExisting(List<Map<String, String>> betNumList, String tempStr) {
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
	public Map<String, Object> querySingleBettingPrizeRange(Float prizePattern){
		BigDecimal singleBettingPrize = null;
		BigDecimal singleBettingPrizeNext = null;
		BigDecimal winningRate = calWinningRate(6);
		Map<String, Object> ret = new HashMap<>();
		
		singleBettingPrize =  calSingleBettingPrize(prizePattern, winningRate);
		
		/*winningRate = calWinningRate(4);
		singleBettingPrizeNext =  calSingleBettingPrize(prizePattern, winningRate);
		
		winningRate = calWinningRate(5);
		singleBettingPrizeNext =  calSingleBettingPrize(prizePattern, winningRate);*/
		
		winningRate = calWinningRate(3);
		singleBettingPrizeNext =  calSingleBettingPrize(prizePattern, winningRate);
		
		/*winningRate = calWinningRate(7);
		singleBettingPrizeNext =  calSingleBettingPrize(prizePattern, winningRate);
		
		winningRate = calWinningRate(8);
		singleBettingPrizeNext =  calSingleBettingPrize(prizePattern, winningRate);
		
		winningRate = calWinningRate(9);
		singleBettingPrizeNext =  calSingleBettingPrize(prizePattern, winningRate);*/
		
		ret.put(Constants.KEY_SINGLE_BETTING_PRIZE, 
				String.valueOf(singleBettingPrize) + 
				"---" + 
				String.valueOf(singleBettingPrizeNext));
		//ret.put(Constants.KEY_SINGLE_BETTING_PRIZE_NEXT, singleBettingPrizeNext);
		return ret;
	}
}

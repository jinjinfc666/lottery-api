package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

public class Pk10DwdPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "dwd|定位胆/qs|前十/fs-ds";
	
	private String betNumOptions = "01,02,03,04,05,06,07,08,09,10";
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		//开奖号码的每一位
		String[] winNumSet = null;
		//投注号码的每个位的号码，可能多个号码
		String[] betNumSet = new String[5];
		//每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul= null;
		String betNum = null;
		String winNum = null;
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		//winNum = winNum.substring(4, 9);
		winNumSet = winNum.split(",");
		//betNumSet = betNum.split(",");
		betNumMul = betNum.split(";");
		
		//logger.debug("proced bet number is :: " + Arrays.asList(betNumSet));
		for(String temp : betNumMul) {
			if(StringUtils.isBlank(temp)) {
				continue;
			}
			
			
			String[] tempSet = splitBit(temp, 1);
			for(int i = 0; i < tempSet.length; i++) {
				if(StringUtils.isBlank(tempSet[i])) {
					continue;
				}
				Map<String, String> tempBits = splitBetNum(tempSet[i]);
				
				if(tempBits.get(winNumSet[i]) != null) {
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
		int betTotal = 0;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		String[] betNumSet = null;
		
		betNumSet = betNum.split(";");
		for(String subBetNum : betNumSet) {
			String[] singleBits = subBetNum.split(",");
			int singleAmount = 0;
			for(String singleBit : singleBits) {
				int len = singleBit.length() / 2;
				singleAmount += MathUtil.combination(1, len);
			}
			
			betTotal += singleAmount;
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
			
			String[] betNumTempSet = splitBit(betNumTemp, 1);
			
			if(betNumTempSet.length > 10) {
				return false;
			}
			
			for(String betNumTempBit : betNumTempSet) {
				if(StringUtils.isBlank(betNumTempBit)) {
					continue;
				}
				
				Map<String, String> tempBits = splitBetNum(betNumTempBit);
				if(tempBits.size() == 0
						|| tempBits.size() > 10
						|| tempBits.size() != (betNumTempBit.length() / 2)) {
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
		}
				
		return true;
	}

	@Override
	public Map<String, Object> calPrize(Issue issue, OrderInfo order, UserInfo user) {
		Map<String, Object> ret = new HashMap<String, Object>();
		// 开奖号码的每一位
		String[] winNumSet = null;
		// 投注号码的每个位的号码，可能多个号码
		//String[] betNumSet = new String[5];
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
		//winNum = winNum.substring(4, 9);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");		
		
		for(String singleSel : betNumMul) {			
			String[] singleSelSet = splitBit(singleSel, 1);
			for(int i = 0; i< singleSelSet.length; i++) {
				if(StringUtils.isBlank(singleSel)) {
					continue;
				}
				
				Map<String, String> tempBits = splitBetNum(singleSelSet[i]);
				
				if(tempBits.get(winNumSet[i]) != null) {
					winningBetAmount++;
				}
				
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
		
		//tempVal = Math.pow(tempVal, 3);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount, 5, BigDecimal.ROUND_HALF_UP);
		return winningRate;
	}
	
	
	@Override
	public String obtainSampleBetNumber(){
		Random random = new Random();
		StringBuffer betNumBuffer = new StringBuffer();
		StringBuffer finalBetNumBuffer = new StringBuffer();
		int len = random.nextInt(10) + 1;
		int bitLen = random.nextInt(10) + 1;
		Map<String, String> betNums = new HashMap<>();
		Iterator<String> ite = null;
		
		for(int j = 0;j < bitLen; j++) {
			for(int i = 0; i < len; i++) {			
				while(true) {
					betNumBuffer.delete(0, betNumBuffer.length());
					int bit = random.nextInt(10) + 1;
					//String betNum = null;
					if(bit < 10) {
						betNumBuffer.append("0").append(bit);
					}else {
						betNumBuffer.append(bit);
					}
					
					if(betNums.containsKey(betNumBuffer.toString())) {
						continue;
					}else {
						betNums.put(betNumBuffer.toString(), betNumBuffer.toString());
						break;
					}
				}
			}
			
			betNumBuffer.delete(0, betNumBuffer.length());
			ite = betNums.keySet().iterator();
			while(ite.hasNext()) {
				String key = ite.next();
				
				betNumBuffer.append(key);
			}
			
			finalBetNumBuffer.append(betNumBuffer.toString()).append(",");
			betNums.clear();
		}
		finalBetNumBuffer.delete(finalBetNumBuffer.length() - 1 , finalBetNumBuffer.length());
		
		for(int i = 0; i < (10 - bitLen); i++) {
			finalBetNumBuffer.append(",");
		}
		return finalBetNumBuffer.toString();
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
		
		for(String singleBetNumArray : betNumArray) {
			String[] tempSet = splitBit(singleBetNumArray, 1);
			for(int i = 0;i < tempSet.length; i++) {
				String bitBetNumBit = tempSet[i];
				if(StringUtils.isBlank(bitBetNumBit)) {
					continue;
				}
				
				bitBetNum = combinationBetNum(bitBetNumBit, 1);
				for(String[] tempBitBetBum : bitBetNum) {
					String[] excludingArray = obtainExcludingArray(tempBitBetBum);
					excludingResults = new ArrayList<String[]>();
					try {					
						MathUtil.combinationSelect(excludingArray, excludingCounter, excludingResults);
						
						for(String[] excludingResult : excludingResults) {
							betNumCombination = new HashMap<>();
							StringBuffer buffer = new StringBuffer();														
							int indx  = 0;
							
							for(String bit : excludingResult) {
								if(indx == i) {
									for(String bitI : tempBitBetBum) {
										buffer.append(bitI);
										betNumCombination.put(bitI, bitI);
									}
								}
								
								buffer.append(bit);
								betNumCombination.put(bit, bit);
								
								indx++;
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
}

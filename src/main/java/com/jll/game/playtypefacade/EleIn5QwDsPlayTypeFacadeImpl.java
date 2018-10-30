package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.util.ArrayList;
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

public class EleIn5QwDsPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "qwx|趣味型/qwdds|趣味定单双/fs-ds";
	
	private String[] oddsArray = {"01","03","05","07","09","11"};
	
	private String[] evenArray = {"02","04","06","08","10"};
	
	private String betNumOptions = "01,02,03,04,05,06";
	
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
		int oddCounter = 0;
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		//winNum = winNum.substring(0, 5);
		winNumSet = winNum.split(",");
		//betNumSet = betNum.split(",");
		betNumMul = betNum.split(";");
		
		for(String winNumBit : winNumSet) {
			Integer val = Integer.valueOf(winNumBit);
			if(val % 2 != 0) {
				oddCounter++;
			}
		}
		
		//logger.debug("proced bet number is :: " + Arrays.asList(betNumSet));
		for(String temp : betNumMul) {
			if(StringUtils.isBlank(temp)) {
				continue;
			}
			
			if(temp.contains(String.valueOf("0" + (oddCounter + 1)))) {
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
		BigDecimal winningRate = calWinningRate(0);
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
					|| tempBits.size() > 6
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
		int oddCounter = 0;
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		//winNum = winNum.substring(0, 5);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		for(String winNumBit : winNumSet) {
			Integer val = Integer.valueOf(winNumBit);
			if(val % 2 != 0) {
				oddCounter++;
			}
		}
		
		//1700 --- 1960
		Float prizePattern = userServ.calPrizePattern(user, issue.getLotteryType());
		BigDecimal winningRate = calWinningRate(oddCounter);
		singleBettingPrize =  calSingleBettingPrize(prizePattern, winningRate);
		
		
		for(String singleSel : betNumMul) {
			if(singleSel.contains(String.valueOf("0" + (oddCounter + 1)))) {
				winningBetAmount++;
			}
		}
		
		betAmount = MathUtil.multiply(winningBetAmount, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit.floatValue(), Float.class);
		maxWinAmount = MathUtil.multiply(betAmount, singleBettingPrize.floatValue(), Float.class);
		
		return new BigDecimal(maxWinAmount);
	}

	private BigDecimal calWinningRate(int oddCounter) {
		switch(oddCounter) {
			case 0:{
				BigDecimal winningRate = null;
				//BigDecimal winCount = new BigDecimal(3);
				//BigDecimal totalCount = null;
				Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(5, 5)));
				Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 5)));
				Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 11)));
				
				tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
				tempVal = MathUtil.divide(tempVal, tempVal2, 4);
								
				winningRate = new BigDecimal(tempVal);
				return winningRate;
			}
			case 1:{
				BigDecimal winningRate = null;
				//BigDecimal winCount = new BigDecimal(3);
				//BigDecimal totalCount = null;
				Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 6)));
				Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 5)));
				Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 11)));
				Double tempVal3 = Double.parseDouble(Long.toString(MathUtil.combination(4, 5)));
				
				tempVal = MathUtil.multiply(tempVal, tempVal3, Double.class);
				tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
				tempVal = MathUtil.divide(tempVal, tempVal2, 4);
								
				winningRate = new BigDecimal(tempVal);
				return winningRate;
			}
			
			case 2:{
				BigDecimal winningRate = null;
				//BigDecimal winCount = new BigDecimal(3);
				//BigDecimal totalCount = null;
				Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(2, 6)));
				Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 5)));
				Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 11)));
				Double tempVal3 = Double.parseDouble(Long.toString(MathUtil.combination(3, 5)));
				
				tempVal = MathUtil.multiply(tempVal, tempVal3, Double.class);
				tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
				tempVal = MathUtil.divide(tempVal, tempVal2, 4);
								
				winningRate = new BigDecimal(tempVal);
				return winningRate;
			}
			
			case 3:{
				BigDecimal winningRate = null;
				//BigDecimal winCount = new BigDecimal(3);
				//BigDecimal totalCount = null;
				Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(3, 6)));
				Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 5)));
				Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 11)));
				Double tempVal3 = Double.parseDouble(Long.toString(MathUtil.combination(2, 5)));
				
				tempVal = MathUtil.multiply(tempVal, tempVal3, Double.class);
				tempVal = MathUtil.multiply(tempVal, tempVal1, Double.class);
				tempVal = MathUtil.divide(tempVal, tempVal2, 4);
								
				winningRate = new BigDecimal(tempVal);
				return winningRate;
			}
			
			case 4:{
				BigDecimal winningRate = null;
				//BigDecimal winCount = new BigDecimal(3);
				//BigDecimal totalCount = null;
				Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(4, 6)));
				Double tempVal1 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 5)));
				Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 11)));
				Double tempVal3 = Double.parseDouble(Long.toString(MathUtil.combination(1, 5)));
				
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
				Double tempVal = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 6)));
				Double tempVal2 = Double.parseDouble(Long.toString(MathUtil.arrangement(5, 11)));
				
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
	public List<Map<String, String>> parseBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		Map<String, String> bitBetNum = null;
		Map<String, String> combineBitBetNum = new HashMap<String, String>();
		List<String[]> oddsResults = null;
		List<String[]> eventResults = null;
		
		for(String singleBetNumArray : betNumArray) {
			bitBetNum = splitBetNum(singleBetNumArray);
			Iterator<String> ite = bitBetNum.keySet().iterator();
			while(ite.hasNext()) {
				String key = ite.next();
				try {
					int oddsCounter = Integer.parseInt(key) - 1;
					int eventCounter = 5 - oddsCounter;
					oddsResults = new ArrayList<String[]>();
					eventResults = new ArrayList<String[]>();
					MathUtil.combinationSelect(oddsArray, oddsCounter, oddsResults);
					MathUtil.combinationSelect(evenArray, eventCounter, eventResults);
					
					for(String[] oddsResult : oddsResults) {
						StringBuffer buffer = new StringBuffer();
						for(String temp : oddsResult) {
							buffer.append(temp);
						}
						
						for(String[] eventResult : eventResults) {
							StringBuffer buffer2 = new StringBuffer();
							buffer2.append(buffer.toString());
							for(String temp : eventResult) {
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
		
		return betNumList;
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

	@Override
	public String obtainSampleBetNumber(){
		Random random = new Random();
		StringBuffer betNum = new StringBuffer();
		String[] betNumOptions = {"06","05","04","03","02","01"};
		int bitLen = random.nextInt(4) + 1;
		
		for(int i = 0 ; i < bitLen; i++) {
			int bitIndx = random.nextInt(6);
			
			while(true) {
				if(betNum.toString().contains(betNumOptions[bitIndx])) {
					bitIndx = random.nextInt(6);
					continue;
				}
				betNum.append(betNumOptions[bitIndx]);
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
		
		for(int i = 0; i < temp.length();) {
			String bit = temp.substring(i, i + 2);
			bits.put(bit, bit);
			i += 2;
		}
		
		return bits;
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
}

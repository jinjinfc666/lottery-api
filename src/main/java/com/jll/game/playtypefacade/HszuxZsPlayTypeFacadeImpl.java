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

public class HszuxZsPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {

	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	private String playTypeDesc = "hszux|后三组选/zsfs";

	private String betNumOptions = "0,1,2,3,4,5,6,7,8,9";
	
	String[] optionsArray = {"0","1","2","3","4","5","6","7","8","9"};
	
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
		Map<String, String> winNumMap = new HashMap<>();
		
				
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(4, 9);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		for(String winNumBit : winNumSet) {
			winNumMap.put(winNumBit, winNumBit);
		}
		
		if(winNumMap.size() != 2) {
			return false;
		}
		
		for(String singleBetNum : betNumMul) {
			if(isPatternMatch(winNumMap, singleBetNum)) {
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
		int betTotal = 0;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		int winBetTotal = 0;
		String[] betNumSet = null;
		
		
		betNumSet = betNum.split(";");
		for(String singleBetNum : betNumSet) {
			int len = singleBetNum.length();
			betTotal += (int)(MathUtil.combination(2, len) 
					* MathUtil.combination(1, 2));
			
			winBetTotal++;
		}
		
		
		betAmount = MathUtil.multiply(betTotal, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit, Float.class);
		
		maxWinAmount = MathUtil.multiply(winBetTotal, times, Float.class);
		maxWinAmount = MathUtil.multiply(maxWinAmount, monUnit.floatValue(), Float.class);
		maxWinAmount = MathUtil.multiply(maxWinAmount, singleBettingPrize.floatValue(), Float.class);
		
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
		//String[] betNumSet = null;
		String[] betNumMul = null;
		
		betNum = order.getBetNum();
		if(StringUtils.isBlank(betNum)) {
			return false;
		}
		
		betNumMul = betNum.split(";");
		for(String betNumTemp : betNumMul) {						
			Map<String, String> tempBits = splitBetNum(betNumTemp);
			if(tempBits.size() < 2
					|| tempBits.size() > 10
					|| tempBits.size() != betNumTemp.length()) {
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
		Map<String, String> winNumMap = new HashMap<>();
		
		//1700 --- 1960
		Float prizePattern = userServ.calPrizePattern(user, issue.getLotteryType());
		BigDecimal winningRate = calWinningRate();
		singleBettingPrize =  calSingleBettingPrize(prizePattern, winningRate);
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(4, 9);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		for(String winNumBit : winNumSet) {
			winNumMap.put(winNumBit, winNumBit);
		}
				
		for(String temp : betNumMul) {
			if(isPatternMatch(winNumMap, temp)) {
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
		BigDecimal winCount = new BigDecimal(3);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
		
		tempVal = Math.pow(tempVal, 3);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount);
		return winningRate;
	}
	
	@Override
	public List<Map<String,String>> parseBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		StringBuffer buffer = new StringBuffer();
		Map<String, String> threeBits = new HashMap<>();
		
		for(String singleBetNumArray : betNumArray) {
			for(int i = 0; i < 10; i++) {
				for(int ii = 0; ii < 10;ii++){
					for(int iii = 0; iii < 10;iii++){
						for(int iiii = 0; iiii < 10;iiii++){
							for(int iiiii = 0; iiiii < 10;iiiii++){
								threeBits.clear();
								
								threeBits.put(String.valueOf(iiii), String.valueOf(iiii));
								threeBits.put(String.valueOf(iiiii), String.valueOf(iiiii));
								threeBits.put(String.valueOf(iii), String.valueOf(iii));
								
								if(threeBits.size() != 2) {
									continue;
								}
								
								Iterator<String> ite = threeBits.keySet().iterator();
								
								if(!singleBetNumArray.contains(ite.next())
										|| !singleBetNumArray.contains(ite.next())) {
									continue;
								}
								
								buffer.delete(0, buffer.length());
								
								
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
	
	@Override
	public String obtainSampleBetNumber(){
		Random random = new Random();
		StringBuffer betNum = new StringBuffer();
		StringBuffer betNums = new StringBuffer();
		int betNumCounter = random.nextInt(5) + 1;
		
		for(int i = 0 ;i < betNumCounter; i++) {
			int betNumLen = random.nextInt(6) + 2;
			for(int ii = 0; ii < betNumLen; ) {
				int bit = random.nextInt(10);
				if(betNum.toString().contains(optionsArray[bit])) {
					continue;
				}
				
				betNum.append(optionsArray[bit]);
				ii++;
			}
			
			betNums.append(betNum.toString()).append(";");
			
			betNum.delete(0, betNum.length());
		}
		
		betNums.delete(betNums.length() - 1, betNums.length());		
				
		return betNums.toString();
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
	
	private boolean isPatternMatch(Map<String, String> pattern, String singleBetNum) {
		Iterator<String> ite = pattern.keySet().iterator();
		while(ite.hasNext()) {
			String key = ite.next();
			if(!singleBetNum.contains(key)) {
				return false;
			}
		}
		
		return true;
	}
}

package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

public class EleIn5QszuxZlPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl  {

	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	private String playTypeDesc = "sm|三码/qszux|前三组选/fs-ds";

	@Override
	public String getPlayTypeDesc() {
		return playTypeDesc;
	}
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		//开奖号码的每一位
		String[] winNumSet = null;
		//投注号码的每个位的号码，可能多个号码
		String[] betNumSet = new String[3];
		//每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul = null;
		String betNum = null;
		String winNum = null;
				
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(0, 8);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		//logger.debug("proceed bet number is :: " + Arrays.asList(betNumSet));
		
		
		for(String temp : betNumMul) {
			if(temp.contains(winNumSet[0]) 
					&& temp.contains(winNumSet[1])
					&& temp.contains(winNumSet[2])) {
				if(isZxZl(winNum.replaceAll(",", ""))) {
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
		betTotal = (int)(MathUtil.combination(3, len));
		
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
		//String[] betNumSet = null;
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
			if(tempBits.size() < 3
					|| tempBits.size() > 11
					|| !Utils.validateEleIn5Num(betNumTemp)
					|| tempBits.size() != (betNumTemp.length() / 2)) {
				return false;
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
		winNum = winNum.substring(0, 8);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		
		for(String temp : betNumMul) {
			if(temp.contains(winNumSet[0]) 
					&& temp.contains(winNumSet[1])
					&& temp.contains(winNumSet[2])) {
				if(isZxZl(winNum.replaceAll(",", ""))) {
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
		BigDecimal winCount = new BigDecimal(6);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 11)));
		
		tempVal = Math.pow(tempVal, 3);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount, 4, BigDecimal.ROUND_HALF_UP);
		return winningRate;
	}
	
	/**
	 * 是否组选组三
	 * @param singleBetNumArray
	 * @return
	 */
	private boolean isZxZl(String singleBetNumArray) {
		Map<String,String> betNumBits = new HashMap<>();
		for(int i = 0; i < singleBetNumArray.length();) {
			String bit = singleBetNumArray.substring(i, i + 2);
			betNumBits.put(bit, bit);
			
			i += 2;
		}
		
		if(betNumBits.size() == 3) {
			return true;
		}
		
		return false;
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
	public List<Map<String,String>> parseBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		List<String[]> arrangements = new ArrayList<>();
		
		for(String singleBetNumArray : betNumArray) {
			String[] betNumBits = new String[singleBetNumArray.length()/2];
			for(int i = 0,j = 0; i < singleBetNumArray.length();j++) {
				betNumBits[j] = singleBetNumArray.substring(i, i + 2);
				
				i += 2;
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
					row.put(Constants.KEY_FACADE_PATTERN, tempStr + "((0[1-9])|(10)|(11)){2}");
					row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, tempStr + "0101");				
					betNumList.add(row);
					//betNumList.add(tempStr);
				}
			}
		}
		
		return betNumList;
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
	
	@Override
	public String obtainSampleBetNumber(){
		Random random = new Random();
		StringBuffer betNum = new StringBuffer();
		
		int bit = random.nextInt(11) + 1;
		int bit2 = -1;
		int bit3 = -1;
		if(bit < 10) {
			betNum.append("0").append(Integer.toString(bit));
		}else {
			betNum.append(Integer.toString(bit));			
		}
		while(true) {
			bit2 = random.nextInt(11) + 1;
			if(bit != bit2) {
				if(bit2 < 10) {
					betNum.append("0").append(Integer.toString(bit2));
				}else {
					betNum.append(Integer.toString(bit2));
				}
				
				break;
			}
		}
		
		while(true) {
			bit3 = random.nextInt(11) + 1;
			if(bit3 != bit2
					&& bit3 != bit) {
				if(bit3 < 10) {
					betNum.append("0").append(Integer.toString(bit3));
				}else {
					betNum.append(Integer.toString(bit3));					
				}
				break;
			}
		}		
		
		return betNum.toString();
	}
}

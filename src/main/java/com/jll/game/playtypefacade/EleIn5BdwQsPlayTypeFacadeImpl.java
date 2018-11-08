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

public class EleIn5BdwQsPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "bdw|不定位/fs";
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		//每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul= null;
		String betNum = null;
		String winNum = null;
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(0, 8);
		betNumMul = betNum.split(";");
		
		//logger.debug("proced bet number is :: " + Arrays.asList(betNumSet));
		for(String temp : betNumMul) {
			if(StringUtils.isBlank(temp)) {
				continue;
			}
			
			Map<String, String> tempBits = splitBetNum(temp);
			Iterator<String> ite = tempBits.keySet().iterator();
			while(ite.hasNext()) {
				String key = ite.next();
				if(winNum.contains(key)) {
					return true;
				}
			}
		}
				
		return false;
	}

	@Override
	public List<Map<String, String>> parseBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		for(String singleBetNumArray : betNumArray) {			
			for(int i = 0; i < singleBetNumArray.length();) {
				String aaa = singleBetNumArray.substring(i, i + 2);
				StringBuffer buffer = new StringBuffer();
				buffer.append(aaa);
				Map<String, String> row = new HashMap<String, String>();
				row.put(Constants.KEY_FACADE_BET_NUM, buffer.toString());
				row.put(Constants.KEY_FACADE_PATTERN, buffer.toString() + "((0[1-9])|(10)|(11)){4}");
				row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, buffer.toString() + "01010101");
				betNumList.add(row);
				
				row = new HashMap<String, String>();
				row.put(Constants.KEY_FACADE_BET_NUM, buffer.toString());
				row.put(Constants.KEY_FACADE_PATTERN, "((0[1-9])|(10)|(11)){1}" + buffer.toString() + "((0[1-9])|(10)|(11)){3}");
				row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, "01" + buffer.toString() + "010101");
				betNumList.add(row);
				
				row = new HashMap<String, String>();
				row.put(Constants.KEY_FACADE_BET_NUM, buffer.toString());
				row.put(Constants.KEY_FACADE_PATTERN, "((0[1-9])|(10)|(11)){2}" + buffer.toString() + "((0[1-9])|(10)|(11)){2}");
				row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, "0101" + buffer.toString() + "0101");
				betNumList.add(row);
				
				i += 2;
			}
		}
		
		
		return betNumList;
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
					|| tempBits.size() > 11
					|| !Utils.validateEleIn5Num(betNumTemp)
					|| tempBits.size() != (betNumTemp.length() / 2)) {
				return false;
			}			
		}
				
		return true;
	}

	@Override
	public Map<String, Object> calPrize(Issue issue, OrderInfo order, UserInfo user) {	
		Map<String, Object> ret = new HashMap<String, Object>();
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
		betNumMul = betNum.split(";");		
		
		for(String singleSel : betNumMul) {
			Map<String, String> tempBits = splitBetNum(singleSel);
			Iterator<String> ite = tempBits.keySet().iterator();
			while(ite.hasNext()) {
				String key = ite.next();
				if(winNum.contains(key)) {
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
		BigDecimal winCount = new BigDecimal(3);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 11)));
		
		//tempVal = Math.pow(tempVal, 3);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount, 4, BigDecimal.ROUND_HALF_UP);
		return winningRate;
	}
	
	@Override
	public String obtainSampleBetNumber(){
		Random random = new Random();
		StringBuffer betNumBuffer = new StringBuffer();
		int len = random.nextInt(11) + 1;
		Map<String, String> betNums = new HashMap<>();
		Iterator<String> ite = null;
		
		for(int i = 0; i < len; i++) {			
			while(true) {
				betNumBuffer.delete(0, betNumBuffer.length());
				int bit = random.nextInt(11) + 1;
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
		
		return betNumBuffer.toString();
	}
}

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

public class EleIn5DwdPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "dwd|定位胆/fs-ds";
	
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
		winNumSet = splitBit(winNum, 1);
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
				Iterator<String> ite = tempBits.keySet().iterator();
				while(ite.hasNext()) {
					String key = ite.next();
					if(winNumSet[i].contains(key)) {
						return true;
					}
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
		
		betNum = betNum.replaceAll(",", "");
		int len = betNum.length() / 2;
		betTotal = (int)MathUtil.combination(1, len);
		
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
			
			if(betNumTempSet.length > 3) {
				return false;
			}
			
			for(String betNumTempBit : betNumTempSet) {
				if(StringUtils.isBlank(betNumTempBit)) {
					continue;
				}
				
				Map<String, String> tempBits = splitBetNum(betNumTempBit);
				if(tempBits.size() == 0
						|| tempBits.size() > 11
						|| !Utils.validateEleIn5Num(betNumTempBit)
						|| tempBits.size() != (betNumTempBit.length() / 2)) {
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
		winNumSet = splitBit(winNum, 1);
		betNumMul = betNum.split(";");		
		
		for(String singleSel : betNumMul) {			
			if(StringUtils.isBlank(singleSel)) {
					continue;
			}
			String[] singleSelSet = splitBit(singleSel, 1);
			for(int i = 0; i < singleSelSet.length;i++) {
				if(StringUtils.isBlank(singleSelSet[i])) {
					continue;
				}
				
				Map<String, String> tempBits = splitBetNum(singleSelSet[i]);
				Iterator<String> ite = tempBits.keySet().iterator();
				while(ite.hasNext()) {
					String key = ite.next();
					if(winNumSet[i].contains(key)) {
						winningBetAmount++;
					}
				}
			}			
		}
		
		betAmount = MathUtil.multiply(winningBetAmount, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit.floatValue(), Float.class);
		maxWinAmount = MathUtil.multiply(betAmount, singleBettingPrize.floatValue(), Float.class);
		
		return new BigDecimal(maxWinAmount);
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
		
		//tempVal = Math.pow(tempVal, 3);
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
	public String obtainSampleBetNumber(){
		Random random = new Random();
		StringBuffer betNumBuffer = new StringBuffer();
		StringBuffer finalBetNumBuffer = new StringBuffer();
		int len = random.nextInt(11) + 1;
		int bitLen = random.nextInt(3) + 1;
		Map<String, String> betNums = new HashMap<>();
		Iterator<String> ite = null;
		
		for(int j = 0;j < bitLen; j++) {
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
			
			finalBetNumBuffer.append(betNumBuffer.toString()).append(",");
			betNums.clear();
		}
		finalBetNumBuffer.delete(finalBetNumBuffer.length() - 1 , finalBetNumBuffer.length());
		
		for(int i = 0; i < (3 - bitLen); i++) {
			finalBetNumBuffer.append(",");
		}
		return finalBetNumBuffer.toString();
	}
	
	@Override
	public List<Map<String, String>> parseBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		for(String singleBetNumArray : betNumArray) {
			StringBuffer buffer = new StringBuffer();
			for(int i = 0; i < singleBetNumArray.length(); i++) {
				String temp = singleBetNumArray.substring(i, i + 1);
				String temp2 = null;
				if(i + 1 < singleBetNumArray.length()) {
					temp2 = singleBetNumArray.substring(i + 1, i + 2);
				}
				if(temp.equals(",") && ",".equals(temp2)) {
					buffer.append(temp).append(" ");
				}else {
					buffer.append(temp);
				}
			}
			String[] betNumBits = buffer.toString().split(",");
			
			String betNumBit = null;
			
			if(betNumBits.length > 0) {
				betNumBit = betNumBits[0];
				if(!StringUtils.isBlank(betNumBit)) {
					for(int i = 0; i < betNumBit.length();) {
						String a = betNumBit.substring(i, i + 2);
						Map<String, String> row = new HashMap<String, String>();
						row.put(Constants.KEY_FACADE_BET_NUM, a);
						row.put(Constants.KEY_FACADE_PATTERN, a + "((0[1-9])|(10)|(11)){4}");
						row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, a + "01010101");				
						betNumList.add(row);
						
						i += 2;
					}
				}				
			}
			
			if(betNumBits.length > 1) {
				betNumBit = betNumBits[1];
				if(!StringUtils.isBlank(betNumBit)) {
					for(int i = 0; i < betNumBit.length();) {
						String a = betNumBit.substring(i, i + 2);
						Map<String, String> row = new HashMap<String, String>();
						row.put(Constants.KEY_FACADE_BET_NUM, a);
						row.put(Constants.KEY_FACADE_PATTERN, "((0[1-9])|(10)|(11)){1}" + a + "((0[1-9])|(10)|(11)){3}");
						row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, "01" + a + "010101");				
						betNumList.add(row);
						
						i += 2;
					}
				}
			}
			
			if(betNumBits.length > 2) {
				betNumBit = betNumBits[2];
				if(!StringUtils.isBlank(betNumBit)) {
					for(int i = 0; i < betNumBit.length();) {
						String a = betNumBit.substring(i, i + 2);
						Map<String, String> row = new HashMap<String, String>();
						row.put(Constants.KEY_FACADE_BET_NUM, a);
						row.put(Constants.KEY_FACADE_PATTERN, "((0[1-9])|(10)|(11)){2}" + a + "((0[1-9])|(10)|(11)){2}");
						row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, "0101" + a + "0101");				
						betNumList.add(row);
						
						i += 2;
					}
				}
			}
		}
		
		
		return betNumList;
	}
}

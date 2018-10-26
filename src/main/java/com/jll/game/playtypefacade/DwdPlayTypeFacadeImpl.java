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

public class DwdPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "dwd|定位胆/dwdfs";
	
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
		
		logger.debug("proced bet number is :: " + Arrays.asList(betNumSet));
		for(String temp : betNumMul) {
			if(StringUtils.isBlank(temp)) {
				continue;
			}
			
			String[] tempSet = splitBit(temp, 1);
			for(int i = 0; i < tempSet.length; i++) {
				String tempBit = tempSet[i];
				String winNumBit = winNumSet[i];
				if(StringUtils.isBlank(tempBit)) {
					continue;
				}
				
				if(tempBit.contains(winNumBit)) {
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
		
		betNum = betNum.replaceAll(",", "");
		int len = betNum.length();
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
			
			String[] betNumTempSet = betNumTemp.split(",");
			
			if(betNumTempSet.length > 5) {
				return false;
			}
			
			for(String betNumTempBit : betNumTempSet) {
				if(StringUtils.isBlank(betNumTempBit)) {
					continue;
				}
				
				if(betNumTempBit.length() > 10) {
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
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");		
		
		for(String singleSel : betNumMul) {			
			if(StringUtils.isBlank(singleSel)) {
				continue;
			}
			String[] singleSelSet = splitBit(singleSel, 1);
			for(int i = 0; i< singleSelSet.length; i++) {
				if(StringUtils.isBlank(singleSelSet[i])) {
					continue;
				}
				if(singleSelSet[i].contains(winNumSet[i])) {
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
		BigDecimal winCount = new BigDecimal(1);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
		
		//tempVal = Math.pow(tempVal, 3);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount);
		return winningRate;
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
					for(int i = 0; i < betNumBit.length(); i++) {
						String a = betNumBit.substring(i, i + 1);
						Map<String, String> row = new HashMap<String, String>();
						row.put(Constants.KEY_FACADE_BET_NUM, a);
						row.put(Constants.KEY_FACADE_PATTERN, a + "[0-9]{4}");
						row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, a + "0000");				
						betNumList.add(row);
					}
				}
			}
			
			if(betNumBits.length > 1) {
				betNumBit = betNumBits[1];
				if(!StringUtils.isBlank(betNumBit)) {
					for(int i = 0; i < betNumBit.length(); i++) {
						String a = betNumBit.substring(i, i + 1);
						Map<String, String> row = new HashMap<String, String>();
						row.put(Constants.KEY_FACADE_BET_NUM, a);
						row.put(Constants.KEY_FACADE_PATTERN, "[0-9]{1}" + a + "[0-9]{3}");
						row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, "0" + a + "000");				
						betNumList.add(row);
					}
				}				
			}
			
			if(betNumBits.length > 2) {
				betNumBit = betNumBits[2];
				if(!StringUtils.isBlank(betNumBit)) {
					for(int i = 0; i < betNumBit.length(); i++) {
						String a = betNumBit.substring(i, i + 1);
						Map<String, String> row = new HashMap<String, String>();
						row.put(Constants.KEY_FACADE_BET_NUM, a);
						row.put(Constants.KEY_FACADE_PATTERN, "[0-9]{2}" + a + "[0-9]{2}");
						row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, "00" + a + "00");				
						betNumList.add(row);
					}
				}				
			}
			
			if(betNumBits.length > 3) {
				betNumBit = betNumBits[3];
				if(!StringUtils.isBlank(betNumBit)) {
					for(int i = 0; i < betNumBit.length(); i++) {
						String a = betNumBit.substring(i, i + 1);
						Map<String, String> row = new HashMap<String, String>();
						row.put(Constants.KEY_FACADE_BET_NUM, a);
						row.put(Constants.KEY_FACADE_PATTERN, "[0-9]{3}" + a + "[0-9]{1}");
						row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, "000" + a + "0");
						betNumList.add(row);
					}
				}				
			}
			
			if(betNumBits.length > 4) {
				betNumBit = betNumBits[4];
				if(!StringUtils.isBlank(betNumBit)) {
					for(int i = 0; i < betNumBit.length(); i++) {
						String a = betNumBit.substring(i, i + 1);
						Map<String, String> row = new HashMap<String, String>();
						row.put(Constants.KEY_FACADE_BET_NUM, a);
						row.put(Constants.KEY_FACADE_PATTERN, "[0-9]{4}" + a);
						row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, "0000" + a);				
						betNumList.add(row);
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
		
		int bit = random.nextInt(10);
		int bit2 = random.nextInt(10);
		int bit3 = random.nextInt(10);
		int bit4 = random.nextInt(10);
		int bit5 = random.nextInt(10);
		
		betNum.append(Integer.toString(bit)).append(",");
		betNum.append(Integer.toString(bit2)).append(",");
		betNum.append(Integer.toString(bit3)).append(",");
		betNum.append(Integer.toString(bit4)).append(",");
		betNum.append(Integer.toString(bit5));
				
		return betNum.toString();
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
}

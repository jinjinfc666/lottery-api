package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jll.common.constants.Constants;
import com.jll.common.utils.MathUtil;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;

public class ZszuxHhzxPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl  {

	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	private String playTypeDesc = "zszux|中三组选/hhzxds";
	
	@Override
	public String getPlayTypeDesc() {
		return playTypeDesc;
	}
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		// 开奖号码的每一位
		String[] winNumSet = null;
		// 投注号码的每个位的号码，可能多个号码
		String[] betNumSet = new String[3];
		// 每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul = null;
		String betNum = null;
		String winNum = null;

		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(2, 7);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");

		logger.debug("proceed bet number is :: " + Arrays.asList(betNumSet));

		for (String temp : betNumMul) {
			if (temp.contains(winNumSet[0]) 
					&& temp.contains(winNumSet[1]) 
					&& temp.contains(winNumSet[2])) {
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
		String[] betNumMul = betNum.split(";");
		//组三单注奖金
		BigDecimal singleBettingPrizeZs = null;
		//组六单注奖金
		BigDecimal singleBettingPrizeZl = null;
		
		int betTotal = 1;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		String lottoType = (String)params.get("lottoType");
		Float prizePattern = userServ.calPrizePattern(user, lottoType);
		BigDecimal winningRateZs = calWinningRateZs();
		BigDecimal winningRateZl = calWinningRateZl();
		singleBettingPrizeZs =  calSingleBettingPrize(prizePattern, winningRateZs);
		singleBettingPrizeZl = calSingleBettingPrize(prizePattern, winningRateZl);
		
		
		betTotal = betNumMul.length;
		Float singleBetAmount = MathUtil.multiply(1, times, Float.class);
		singleBetAmount = MathUtil.multiply(singleBetAmount, monUnit, Float.class);
		
		for(String tempBettingNum : betNumMul) {
			Float singleWinAmount = 0F;
			boolean isZsBettingNum = isZsBettingNum(tempBettingNum);
			if(isZsBettingNum) {				
				singleWinAmount = MathUtil.multiply(singleBetAmount, 
						singleBettingPrizeZs.floatValue(), 
						Float.class);
			}else {
				singleWinAmount = MathUtil.multiply(singleBetAmount, 
						singleBettingPrizeZl.floatValue(), 
						Float.class);
			}
			
			maxWinAmount = MathUtil.add(maxWinAmount, singleWinAmount, Float.class);
		}
		
		betAmount = MathUtil.multiply(singleBetAmount, betTotal, Float.class);
		
		
		ret.put("playType", playType);
		ret.put("betAmount", betAmount);
		ret.put("maxWinAmount", maxWinAmount);
		ret.put("betTotal", betTotal);
		ret.put("singleBettingPrize", singleBettingPrizeZs);
		ret.put("singleBettingPrizeZl", singleBettingPrizeZl);
		return ret;
	}
	
	private boolean isZsBettingNum(String tempBettingNum) {
		Map<String, String> bettingNumBit = new HashMap<>();
		for(int i = 0;i < tempBettingNum.length(); i++) {
			String numBit = tempBettingNum.substring(i, i+1);
			bettingNumBit.put(numBit, numBit);
		}
		return (bettingNumBit.size() == 2)?true:false;
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
			
			if(betNumTemp.length() != 3
					|| !Utils.validateNum(betNumTemp)) {
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
		Float singleBetAmount = 0F;
		Float maxWinAmount = 0F;
		Integer times = order.getTimes();
		BigDecimal monUnit = order.getPattern();
		//组三单注奖金
		BigDecimal singleBettingPrizeZs = null;
		//组六单注奖金
		BigDecimal singleBettingPrizeZl = null;
		
		//1700 --- 1960
		Float prizePattern = userServ.calPrizePattern(user, issue.getLotteryType());
		BigDecimal winningRateZs = calWinningRateZs();
		BigDecimal winningRateZl = calWinningRateZl();
		singleBettingPrizeZs =  calSingleBettingPrize(prizePattern, winningRateZs);
		singleBettingPrizeZl = calSingleBettingPrize(prizePattern, winningRateZl);
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(2, 7);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");	
		singleBetAmount = MathUtil.multiply(1, times, Float.class);
		singleBetAmount = MathUtil.multiply(singleBetAmount, monUnit, Float.class);
		
		for(String temp : betNumMul) {
			if(temp.contains(winNumSet[0]) 
					&& temp.contains(winNumSet[1])
					&& temp.contains(winNumSet[2])) {
				Float singleWinAmount = 0F;
				boolean isZsBettingNum = isZsBettingNum(temp);
				if(isZsBettingNum) {
					singleWinAmount = MathUtil.multiply(singleBetAmount, singleBettingPrizeZs, Float.class);
				}else {
					singleWinAmount = MathUtil.multiply(singleBetAmount, singleBettingPrizeZl, Float.class);
				}				
				
				maxWinAmount = MathUtil.add(maxWinAmount, singleWinAmount, Float.class);		
			}
		}		
		
		return new BigDecimal(maxWinAmount);
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
	
	public BigDecimal calWinningRateZs() {
		BigDecimal winningRate = null;
		BigDecimal winCount = new BigDecimal(3);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
		
		tempVal = Math.pow(tempVal, 3);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount);
		return winningRate;
	}
	
	public BigDecimal calWinningRateZl() {
		BigDecimal winningRate = null;
		BigDecimal winCount = new BigDecimal(6);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
		
		tempVal = Math.pow(tempVal, 3);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount);
		return winningRate;
	}
	
	
	@Override
	public List<Map<String, String>> parseBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		
		for(String singleBetNumArray : betNumArray) {
			boolean isZx = isZx(singleBetNumArray);
			List<Map<String, String>> partRet = new ArrayList<>();
			if(isZx) {
				Map<String, String> row = new HashMap<String, String>();
				row.put(Constants.KEY_FACADE_BET_NUM, singleBetNumArray);
				row.put(Constants.KEY_FACADE_PATTERN, "[0-9]{1}" + singleBetNumArray + "[0-9]{1}");
				row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, "0" + singleBetNumArray + "0");				
				betNumList.add(row);
				
				StringBuffer buffer = new StringBuffer();
				String firtBit = singleBetNumArray.substring(0, 1);
				String secondBit = singleBetNumArray.substring(1, 2);
				String thirdBit = singleBetNumArray.substring(2,3);
				buffer.append(secondBit).append(firtBit).append(thirdBit);
				
				row = new HashMap<String, String>();
				row.put(Constants.KEY_FACADE_BET_NUM, buffer.toString());
				row.put(Constants.KEY_FACADE_PATTERN, "[0-9]{1}" + buffer.toString() + "[0-9]{1}");
				row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, "0"+ buffer.toString() + "0");
				betNumList.add(row);
				
				buffer = new StringBuffer();
				buffer.append(thirdBit).append(secondBit).append(firtBit);
				row = new HashMap<String, String>();
				row.put(Constants.KEY_FACADE_BET_NUM, buffer.toString());
				row.put(Constants.KEY_FACADE_PATTERN, "[0-9]{1}" + buffer.toString() + "[0-9]{1}");
				row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, "0"+ buffer.toString() + "0");
				betNumList.add(row);
			}else {
				partRet = parseZszuxZLBetNumber(singleBetNumArray);
			}
			
			for(Map<String, String> temp : partRet) {
				betNumList.add(temp);
			}
		}
		
		return betNumList;
	}

	private boolean isZx(String singleBetNumArray) {
		Map<String,String> betNumBits = new HashMap<>();
		for(int i = 0; i < singleBetNumArray.length(); i++) {
			String bit = singleBetNumArray.substring(i, i + 1);
			betNumBits.put(bit, bit);
		}
		
		if(betNumBits.size() == 2) {
			return true;
		}
		
		return false;
	}
	
	private List<Map<String, String>> parseZszuxZLBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		List<String[]> arrangements = new ArrayList<>();
		
		for(String singleBetNumArray : betNumArray) {
			String[] betNumBits = new String[singleBetNumArray.length()];
			for(int i = 0; i < singleBetNumArray.length(); i++) {
				betNumBits[i] = singleBetNumArray.substring(i, i + 1);
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
					row.put(Constants.KEY_FACADE_PATTERN, "[0-9]{1}" + tempStr + "[0-9]{1}");
					row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, "0" + tempStr + "0");				
					betNumList.add(row);
					//betNumList.add(tempStr);
				}
			}
		}
		
		return betNumList;
	}
	
	private boolean isExisting(List<Map<String, String>> betNumList, String tempStr) {
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

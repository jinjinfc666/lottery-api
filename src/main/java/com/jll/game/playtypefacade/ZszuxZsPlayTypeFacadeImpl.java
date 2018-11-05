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

public class ZszuxZsPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {

	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	private String playTypeDesc = "zszux|中三组选/zsfs";

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
		Map<String, String> winNumMap = new HashMap<>();
		//在前三位有2位重复情况下的数位1
		String winNumBit1 = null;
		//
		String winNumBit2 = null;
				
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(2, 7);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		for(String winNumBit : winNumSet) {
			winNumMap.put(winNumBit, winNumBit);
		}
		
		if(winNumMap.size() != 2) {
			return false;
		}
		
		logger.debug("proceed bet number is :: " + Arrays.asList(betNumSet));
		
		Iterator<String> keys = winNumMap.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			if(StringUtils.isBlank(winNumBit1)) {
				winNumBit1 = key;
			}else {
				winNumBit2 = key;
			}
		}
		
		for(String temp : betNumMul) {
			if(temp.contains(winNumBit1) 
					&&temp.contains(winNumBit2)) {
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
			if(StringUtils.isBlank(betNumTemp)) {
				return false;
			}
			
			if(betNumTemp.length() < 2 
					|| betNumTemp.length() > 10
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
		int winningBetAmount = 0;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		Integer times = order.getTimes();
		BigDecimal monUnit = order.getPattern();
		BigDecimal singleBettingPrize = null;
		Map<String, String> winNumMap = new HashMap<>();
		//在前三位有2位重复情况下的数位1
		String winNumBit1 = null;
		//
		String winNumBit2 = null;
		//1700 --- 1960
		Float prizePattern = userServ.calPrizePattern(user, issue.getLotteryType());
		BigDecimal winningRate = calWinningRate();
		singleBettingPrize =  calSingleBettingPrize(prizePattern, winningRate);
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(2, 7);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		for(String winNumBit : winNumSet) {
			winNumMap.put(winNumBit, winNumBit);
		}
		
		Iterator<String> keys = winNumMap.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			if(StringUtils.isBlank(winNumBit1)) {
				winNumBit1 = key;
			}else {
				winNumBit2 = key;
			}
		}
		
		for(String temp : betNumMul) {
			if(temp.contains(winNumBit1) 
					&&temp.contains(winNumBit2)) {
				winningBetAmount++;
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
							threeBits.clear();
							
							threeBits.put(String.valueOf(iiii), String.valueOf(iiii));
							threeBits.put(String.valueOf(ii), String.valueOf(ii));
							threeBits.put(String.valueOf(iii), String.valueOf(iii));
							
							if(threeBits.size() != 2) {
								continue;
							}
							
							Iterator<String> ite = threeBits.keySet().iterator();
							
							if(!singleBetNumArray.contains(ite.next())
									|| !singleBetNumArray.contains(ite.next())) {
								continue;
							}
							
							for(int iiiii = 0; iiiii < 10;iiiii++){
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
		
		int bit = random.nextInt(10);
		int bit2 = -1;
		betNum.append(Integer.toString(bit));
		
		while(true) {
			bit2 = random.nextInt(10);
			if(bit != bit2) {
				betNum.append(Integer.toString(bit2));
				break;
			}
		}
				
		return betNum.toString();
	}
}

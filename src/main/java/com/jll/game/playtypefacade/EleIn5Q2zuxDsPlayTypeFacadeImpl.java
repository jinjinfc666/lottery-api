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

public class EleIn5Q2zuxDsPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl  {

	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	private String playTypeDesc = "em|二码/qezux|前二组选/ds";

	private String betNumOptions = "01,02,03,04,05,06,07,08,09,10,11";
	
	String[] optionsArray = {"01","02","03","04","05","06","07","08","09","10","11"};
	
	@Override
	public String getPlayTypeDesc() {
		return playTypeDesc;
	}
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		//投注号码的每个位的号码，可能多个号码
		//String[] betNumSet = new String[3];
		//每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul = null;
		String betNum = null;
		String winNum = null;
				
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(0, 5);
		betNumMul = betNum.split(";");
		
		for(String temp : betNumMul) {
			temp = temp.replaceAll(" ", "");
			Map<String, String> bits = splitBetNum(winNum.replaceAll(",", ""));
			if(isPatternMatch(bits, temp)) {
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
		String[] betNumSet = null;
		int betTotal = 1;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		int winBetTotal = 0;
		
		/*int len = betNum.length() / 2;
		betTotal = (int)(MathUtil.combination(3, len));*/
		
		betNum = betNum.replaceAll(" ", "");
		betNumSet = betNum.split(";");
		
		winBetTotal = betNumSet.length;
		betTotal = betNumSet.length;
		
		betAmount = MathUtil.multiply(betTotal, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit, Float.class);
		
		maxWinAmount = MathUtil.multiply(winBetTotal, 
				times, 
				Float.class);
		maxWinAmount = MathUtil.multiply(maxWinAmount, 
				monUnit, 
				Float.class);
		maxWinAmount = MathUtil.multiply(maxWinAmount, 
				singleBettingPrize.floatValue(), 
				Float.class);
		
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
		
		//betNum = betNum.replaceAll(" ", "");
		betNumMul = betNum.split(";");
		for(String betNumTemp : betNumMul) {
			if(betNumTemp.split(" ").length != 2) {
				return false;
			}
			
			betNumTemp = betNumTemp.replaceAll(" ", "");
			Map<String, String> tempBits = splitBetNum(betNumTemp);
			if(tempBits.size() != 2
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
		winNum = winNum.substring(0, 5);
		betNumMul = betNum.split(";");
		
		
		for(String temp : betNumMul) {
			temp = temp.replaceAll(" ", "");
			Map<String, String> bits = splitBetNum(winNum.replaceAll(",", ""));
			if(isPatternMatch(bits, temp)) {
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
		BigDecimal winCount = new BigDecimal(6);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 11)));
		
		tempVal = Math.pow(tempVal, 3);
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
	public List<Map<String,String>> parseBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		betNum = betNum.replaceAll(" ", "");
		String[] betNumArray = betNum.split(";");
		StringBuffer buffer = new StringBuffer();
		Map<String, String> threeBits = new HashMap<>();
		
		for(String singleBetNumArray : betNumArray) {
			for(int i = 0; i < 11; i++) {
				for(int ii = 0; ii < 11;ii++){
					threeBits.clear();
					
					threeBits.put(optionsArray[i], optionsArray[i]);
					threeBits.put(optionsArray[ii], optionsArray[ii]);
					
					if(threeBits.size() != 2) {
						continue;
					}
										
					if(!isPatternMatch(threeBits, singleBetNumArray)) {
						continue;
					}
					
					for(int iii = 0; iii < 11;iii++){
						
						for(int iiii = 0; iiii < 11;iiii++){
							for(int iiiii = 0; iiiii < 11;iiiii++){
								buffer.delete(0, buffer.length());
								
								
								buffer.append(optionsArray[i])
								.append(optionsArray[ii])
								.append(optionsArray[iii])
								.append(optionsArray[iiii])
								.append(optionsArray[iiiii]);
								
								
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
	
		
	private boolean isPatternMatch(Map<String, String> threeBits, String singleBetNum) {
		for(int i = 0; i < singleBetNum.length();) {
			String temp = singleBetNum.substring(i, i + 2);
			
			if(threeBits.get(temp) == null) {
				return false;
			}
			
			i += 2;
		}
		
		
		return true;
	}

	@Override
	public String obtainSampleBetNumber(){
		Random random = new Random();
		StringBuffer betNum = new StringBuffer();
		StringBuffer bitBetBum = new StringBuffer();
		int betNums = random.nextInt(5) + 1;

		for (int a = 0; a < betNums; a++) {
			for (int i = 0; i < 2; i++) {
				while (true) {
					int bit = random.nextInt(11);
					String bitStr = optionsArray[bit];
					if (bitBetBum.toString().contains(bitStr)) {
						continue;
					}

					bitBetBum.append(bitStr).append(" ");
					break;
				}
			}
			bitBetBum.delete(bitBetBum.length() - 1, bitBetBum.length());
			
			betNum.append(bitBetBum).append(";");
			
			bitBetBum.delete(0, bitBetBum.length());
		}

		betNum.delete(betNum.length() - 1, betNum.length());

		return betNum.toString();
	}
}
